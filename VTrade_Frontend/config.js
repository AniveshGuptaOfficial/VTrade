/**
 * VTrade Frontend Config
 * Updated for Spring Boot Backend (Port 8080)
 */

const VTRADE_CONFIG = {
  BACKEND_URL: 'http://localhost:8080',        // ← Changed to Spring Boot
  API_BASE:    'http://localhost:8080/api',     // ← Updated
  UPLOADS_URL: 'http://localhost:8080/uploads',
};

// Helper: build full URL for avatar/image
function assetURL(path) {
  if (!path) return null;
  if (path.startsWith('http')) return path;
  return VTRADE_CONFIG.BACKEND_URL + path;
}

// Helper: get saved JWT token
function getToken() {
  return localStorage.getItem('vtrade_token') || null;
}

// Helper: save token + user data
function saveAuthData(token, user) {
  if (token) localStorage.setItem('vtrade_token', token);
  if (user) localStorage.setItem('userData', JSON.stringify(user));
  localStorage.setItem('userLoggedIn', 'true');
}

// Clear auth data
function clearAuthData() {
  ['vtrade_token','userData','userLoggedIn','userName','userEmail',
   'userRole','phoneNumber','generatedOTP','vtrade_slots'].forEach(k => localStorage.removeItem(k));
}

// Check login status
function isLoggedIn() {
  return !!getToken() || localStorage.getItem('userLoggedIn') === 'true';
}

// Central fetch wrapper
async function vtFetch(method, path, body, isFormData = false) {
  const token = getToken();
  const headers = {};
  if (token) headers['Authorization'] = `Bearer ${token}`;
  if (!isFormData && body) headers['Content-Type'] = 'application/json';

  const opts = { method, headers };
  if (body) opts.body = isFormData ? body : JSON.stringify(body);

  try {
    const res = await fetch(VTRADE_CONFIG.API_BASE + path, opts);
    const data = await res.json().catch(() => ({}));

    if (!res.ok) {
      throw new Error(data.message || `HTTP Error ${res.status}`);
    }
    return data;
  } catch (err) {
    console.error("vtFetch Error:", err);
    throw err;
  }
}

// Ping backend
async function pingBackend() {
  try {
    const res = await fetch(VTRADE_CONFIG.BACKEND_URL + '/api/health', {
      signal: AbortSignal.timeout(3000)
    });
    return res.ok;
  } catch (_) {
    return false;
  }
}

// Update navigation
function updateAuthNav() {
  const area = document.getElementById('auth-area');
  if (!area) return;

  if (isLoggedIn()) {
    const ud = JSON.parse(localStorage.getItem('userData') || '{}');
    const name = ud.firstName || ud.first_name || ud.name || 'Account';
    area.innerHTML = `<a href="Account.html" class="btn btn-outline btn-sm" style="border-color:rgba(255,255,255,.35);color:white">👤 ${name}</a>`;
  } else {
    area.innerHTML = '<a href="login.html" class="btn btn-gold btn-sm">Login / Register</a>';
  }
}

// Update cart badge
function updateCartBadge() {
  const cart = JSON.parse(localStorage.getItem('cart')) || [];
  const total = cart.reduce((s, i) => s + (i.quantity || 0), 0);
  const el = document.getElementById('cart-count');
  if (el) el.textContent = total > 0 ? `(${total})` : '';
}

// Require login
function requireAuth() {
  if (!isLoggedIn()) window.location.href = 'login.html';
}

// Auto run on page load
document.addEventListener('DOMContentLoaded', () => {
  updateAuthNav();
  updateCartBadge();
});