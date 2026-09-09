package com.vtrade.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(unique = true)
    private String email;

    /** No longer required — Google Sign-In users may not have a phone on record. */
    @Column(unique = true)
    private String phone;

    @JsonIgnore
    @Column(name = "password_hash")
    private String passwordHash;

    /** buyer | student_worker | delivery_staff | store | admin */
    @Column(nullable = false)
    private String role = "buyer";

    private String hostelBlock;

    private String roomNumber;

    @Column(name = "student_id", unique = true)
    private String studentId;

    @Column(length = 1000)
    private String bio;

    /** student | delivery_staff — set when a buyer applies to become a worker */
    private String workerType;

    /** pending | approved | rejected */
    private String verificationStatus;

    private String avatarUrl;

    /** Google account subject ID — set when the user signs in/up via Google. */
    @Column(name = "google_id", unique = true)
    private String googleId;

    /** Whether this user has opted into the worker program */
    private boolean worker = false;

    /** Online/available toggle for workers */
    private boolean workerOnline = false;

    /**
     * True for admin-provisioned Delivery Staff / Partner Store accounts that must
     * set their own password before normal use (a temporary password is issued
     * out-of-band by the admin). Defaults to false for all normal self-service
     * accounts (buyers/students via register or Google Sign-In).
     */
    private boolean mustResetPassword = false;

    /** Accumulated bounty points */
    private Integer bountyPoints = 0;

    /** Total completed deliveries (workers) */
    private Integer totalDeliveries = 0;

    /** Sum of all ratings received (workers) — divide by ratingCount for average */
    private Double ratingSum = 0.0;

    /** Number of ratings received (workers) */
    private Integer ratingCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    /** Computed — true if passwordHash is set. Exposed in API so frontend knows login options. */
    public boolean isHasPassword() {
        return passwordHash != null && !passwordHash.isBlank();
    }
}