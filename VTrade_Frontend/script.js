/**
 * VTrade — Shared Script
 * config.js must be loaded before this file on every page.
 */

// ── QUANTITY CONTROLS ─────────────────────────────────────
function handleQtyButtons() {
  document.addEventListener('click', (e) => {
    const btn = e.target.closest('.qty-btn, .qty-b');
    if (!btn) return;
    const wrap = btn.closest('.qty-row, .qty-wrap');
    if (!wrap) return;
    const valEl = wrap.querySelector('.qty-val, .qty-n, .quantity');
    if (!valEl) return;
    let qty = parseInt(valEl.textContent) || 0;
    if (btn.classList.contains('increase') || btn.textContent.trim() === '+') {
      qty = Math.min(10, qty + 1);
    } else {
      qty = Math.max(0, qty - 1);
    }
    valEl.textContent = qty;
  });
}

// ── WISHLIST TOGGLE ───────────────────────────────────────
function handleWishlistButtons() {
  document.addEventListener('click', (e) => {
    const btn = e.target.closest('.product-wishlist, .wishlist-btn');
    if (!btn) return;
    const isFilled = btn.textContent.trim() === '♥';
    btn.textContent = isFilled ? '♡' : '♥';
    btn.style.color = isFilled ? '' : 'var(--red)';
    btn.style.borderColor = isFilled ? '' : 'var(--red)';
  });
}

// ── MOBILE MENU ───────────────────────────────────────────
function handleMobileMenu() {
  const toggle = document.getElementById('menu-toggle');
  const menu   = document.getElementById('mobile-menu');
  if (!toggle || !menu) return;
  toggle.addEventListener('click', () => menu.classList.toggle('open'));
}

// ── BACK TO TOP ───────────────────────────────────────────
function handleBackToTop() {
  const btn = document.getElementById('back-to-top');
  if (!btn) return;
  window.addEventListener('scroll', () => btn.classList.toggle('show', window.scrollY > 400));
  btn.addEventListener('click', () => window.scrollTo({ top: 0, behavior: 'smooth' }));
}

// ── PAGE LOADER ───────────────────────────────────────────
function handleLoader() {
  const loader = document.getElementById('loader');
  if (!loader) return;
  window.addEventListener('load', () => setTimeout(() => loader.classList.add('hide'), 900));
}

// ── NEWSLETTER ────────────────────────────────────────────
function handleNewsletter() {
  const form = document.getElementById('newsletter-form');
  if (!form) return;
  form.addEventListener('submit', (e) => {
    e.preventDefault();
    const input = document.getElementById('newsletter-email');
    const email = input?.value.trim();
    if (!email) { alert('Please enter your email.'); return; }
    const subs = JSON.parse(localStorage.getItem('newsletterSubscribers')) || [];
    if (subs.includes(email)) { alert('You are already subscribed!'); return; }
    subs.push(email);
    localStorage.setItem('newsletterSubscribers', JSON.stringify(subs));
    alert('Subscribed! We\'ll keep you posted.');
    if (input) input.value = '';
  });
}

// ── SHOW MORE (redirect to SeeAll) ────────────────────────
function handleShowMore() {
  document.querySelectorAll('.show-more').forEach(btn => {
    btn.addEventListener('click', () => {
      localStorage.setItem('activeSection', btn.getAttribute('data-section') || 'all');
      window.location.href = 'SeeAll.html';
    });
  });
}

// ── REVIEW SLIDER ─────────────────────────────────────────
function handleReviewSlider() {
  const slider = document.getElementById('review-slider');
  if (!slider) return;
  let iv = makeSliderInterval(slider);
  slider.addEventListener('mouseenter', () => clearInterval(iv));
  slider.addEventListener('mouseleave', () => { iv = makeSliderInterval(slider); });
}
function makeSliderInterval(slider) {
  return setInterval(() => {
    if (slider.scrollLeft + slider.clientWidth >= slider.scrollWidth - 60) {
      slider.scrollTo({ left: 0, behavior: 'smooth' });
    } else {
      slider.scrollBy({ left: 300, behavior: 'smooth' });
    }
  }, 4500);
}

// ── CLEAR INPUT ERRORS ────────────────────────────────────
function clearErrorOnInput() {
  document.querySelectorAll('.vt-input').forEach(el => {
    el.addEventListener('input', () => el.classList.remove('error'));
  });
}

// ── ADD TO CART (shared helper) ───────────────────────────
function addToCart(name, price, quantity, image) {
  if (!name || !price || quantity < 1) return false;
  const cart = JSON.parse(localStorage.getItem('cart')) || [];
  const idx = cart.findIndex(i => i.name === name);
  if (idx >= 0) cart[idx].quantity = Math.min(10, cart[idx].quantity + quantity);
  else cart.push({ name, price, quantity, image: image || '' });
  localStorage.setItem('cart', JSON.stringify(cart));
  updateCartBadge();
  return true;
}

// ── INIT ──────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  // config.js already runs updateAuthNav + updateCartBadge on DOMContentLoaded
  // so just wire up the UI behaviours here
  handleQtyButtons();
  handleWishlistButtons();
  handleMobileMenu();
  handleBackToTop();
  handleLoader();
  handleNewsletter();
  handleShowMore();
  handleReviewSlider();
  clearErrorOnInput();
});
