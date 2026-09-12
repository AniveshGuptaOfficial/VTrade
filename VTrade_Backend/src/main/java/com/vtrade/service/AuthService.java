package com.vtrade.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.vtrade.dto.*;
import com.vtrade.model.User;
import com.vtrade.repository.UserRepository;
import com.vtrade.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.regex.Pattern;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${vtrade.google.client-id}")
    private String googleClientId;

    /** firstname.lastname + 4-digit batch year — VIT student email */
    private static final Pattern VIT_STUDENT_EMAIL =
            Pattern.compile("^[a-z]+\\.[a-z]+[0-9]{4}@vitstudent\\.ac\\.in$");

    /** firstname.lastname — VIT staff/faculty email */
    private static final Pattern VIT_STAFF_EMAIL =
            Pattern.compile("^[a-z]+\\.[a-z]+@vit\\.ac\\.in$");

    /** Temporary: Delivery Staff & Partner Store sign-in restricted to plain Gmail accounts. */
    private static final Pattern GMAIL_EMAIL =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@gmail\\.com$");

    public AuthService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Self-registration (email/password) for the Student portal only. Enforces the
     * same official-VIT-email rule as Student Google Sign-In, so a random Gmail or
     * other outside address can't be used to create a buyer/student account.
     */
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByPhone(req.getPhone())) {
            throw new IllegalArgumentException("An account with this phone number already exists.");
        }

        String normalizedEmail = null;
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            normalizedEmail = req.getEmail().trim().toLowerCase();

            boolean isVitStudent = VIT_STUDENT_EMAIL.matcher(normalizedEmail).matches();
            boolean isVitStaff = VIT_STAFF_EMAIL.matcher(normalizedEmail).matches();
            if (!isVitStudent && !isVitStaff) {
                throw new IllegalArgumentException(
                        "Please register with your official VIT email (@vitstudent.ac.in or @vit.ac.in).");
            }

            if (userRepository.existsByEmail(normalizedEmail)) {
                throw new IllegalArgumentException("An account with this email already exists.");
            }
        }

        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(normalizedEmail);
        user.setPhone(req.getPhone());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole("buyer");

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRole());
        return new AuthResponse(token, user);
    }

    public AuthResponse login(LoginRequest req) {
        User user;
        if (req.getStudentId() != null && !req.getStudentId().isBlank()) {
            user = userRepository.findByStudentId(req.getStudentId())
                    .orElseThrow(() -> new IllegalArgumentException("No account found with this registration number. Please register first."));
        } else if (req.getEmail() != null && !req.getEmail().isBlank()) {
            user = userRepository.findByEmail(req.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("No account found with this email."));
        } else if (req.getPhone() != null && !req.getPhone().isBlank()) {
            user = userRepository.findByPhone(req.getPhone())
                    .orElseThrow(() -> new IllegalArgumentException("No account found with this phone number."));
        } else {
            throw new IllegalArgumentException("Email, phone, or registration number is required.");
        }

        if (user.getPasswordHash() == null || !passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect password.");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRole());
        return new AuthResponse(token, user);
    }

    /**
     * Verifies a Google ID token, then applies domain restrictions based on which
     * portal ("student" | "worker" | "store") the person signed in from:
     *   - student: must be an official VIT email (@vitstudent.ac.in or @vit.ac.in);
     *     new accounts are created automatically on first sign-in.
     *   - worker / store: temporarily restricted to plain @gmail.com accounts.
     *     These accounts are NEVER auto-created here — an admin must provision
     *     them first via POST /api/admin/provision-user. If no matching account
     *     exists, sign-in is refused with a clear message.
     * Additionally, an admin-provisioned account (mustResetPassword == true) must
     * complete its first login using the temporary email + password issued by the
     * admin before Google Sign-In becomes available — this prevents someone from
     * bypassing the forced password setup simply by clicking "Sign in with Google".
     * Returns the same AuthResponse shape as every other login path.
     */
    public AuthResponse googleLogin(String idTokenString, String intendedRole) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not verify Google token.");
        }
        if (idToken == null) {
            throw new IllegalArgumentException("Invalid or expired Google token.");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Your Google account has no email address on file.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        String normalizedRole = intendedRole == null ? "student" : intendedRole.trim().toLowerCase();

        String assignedRole = resolveAndValidate(normalizedEmail, normalizedRole);

        User existing = userRepository.findByGoogleId(googleId)
                .or(() -> userRepository.findByEmail(normalizedEmail))
                .orElse(null);

        User user;
        if (existing != null) {
            // Admin-provisioned accounts (Delivery Staff / Partner Store) must sign in at
            // least once using the temporary email + password issued by the admin, which
            // clears mustResetPassword, before Google Sign-In is allowed for that account.
            if (existing.isMustResetPassword()) {
                throw new IllegalArgumentException(
                        "Please sign in first using the email and temporary password provided by your admin. " +
                        "You'll be asked to set your own password, after which Google Sign-In will work too.");
            }
            user = existing;
        } else {
            // No existing account. Only the Student portal is allowed to self-provision
            // via Google Sign-In. Delivery Staff / Partner Store accounts must already
            // exist — created by an admin through /api/admin/provision-user.
            if (!"student".equals(normalizedRole)) {
                String portalLabel = "worker".equals(normalizedRole) ? "Delivery Staff" : "Partner Store";
                throw new IllegalArgumentException(
                        "No " + portalLabel + " account found for this email. Please contact the admin to get access.");
            }
            user = new User();
            user.setFirstName(firstName != null ? firstName : "User");
            user.setLastName(lastName != null ? lastName : "");
            user.setEmail(normalizedEmail);
            user.setRole(assignedRole);
        }

        user.setGoogleId(googleId);
        if (user.getEmail() == null) {
            user.setEmail(normalizedEmail);
        }
        user = userRepository.save(user);

        String jwt = jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRole());
        return new AuthResponse(jwt, user);
    }

    /** Checks the email against the domain rule for the requested portal and returns the role to assign new users. */
    private String resolveAndValidate(String normalizedEmail, String normalizedRole) {
        boolean isVitStudent = VIT_STUDENT_EMAIL.matcher(normalizedEmail).matches();
        boolean isVitStaff = VIT_STAFF_EMAIL.matcher(normalizedEmail).matches();
        boolean isGmail = GMAIL_EMAIL.matcher(normalizedEmail).matches();

        switch (normalizedRole) {
            case "worker":
                if (!isGmail) {
                    throw new IllegalArgumentException(
                            "Delivery Staff sign-in currently requires a Gmail account (@gmail.com).");
                }
                return "delivery_staff";

            case "store":
                if (!isGmail) {
                    throw new IllegalArgumentException(
                            "Partner Store sign-in currently requires a Gmail account (@gmail.com).");
                }
                return "store";

            case "student":
            default:
                if (!isVitStudent && !isVitStaff) {
                    throw new IllegalArgumentException(
                            "Please sign in with your official VIT email (@vitstudent.ac.in or @vit.ac.in).");
                }
                return "buyer";
        }
    }
}