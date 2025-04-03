document.getElementById('menu-toggle').addEventListener('click', function() {
    const navLinks = document.getElementById('nav-links');
    navLinks.classList.toggle('hidden');
});
let cart = JSON.parse(localStorage.getItem('cart')) || [];
const cartCountElement = document.getElementById('cart-count');
function updateCartCount() {
    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
    cartCountElement.textContent = totalItems;
    cartCountElement.classList.toggle('hidden', totalItems === 0);
    localStorage.setItem('cart', JSON.stringify(cart));
}
document.querySelectorAll('.quantity-btn').forEach(button => {
    button.addEventListener('click', function() {
        const quantityElement = this.parentElement.querySelector('.quantity');
        let quantity = parseInt(quantityElement.textContent);
        
        if (this.textContent === '+' && quantity < 10) {
            quantity++;
        } else if (this.textContent === '-' && quantity > 0) {
            quantity--;
        }
        quantityElement.textContent = quantity;
    });
});
document.querySelectorAll('#best-sellers button').forEach(button => {
    if (button.textContent.trim() === 'Add to Cart') {
        button.addEventListener('click', function() {
            const product = this.closest('div');
            const productName = product.querySelector('h3').textContent;
            const quantity = parseInt(product.querySelector('.quantity').textContent);
            const priceText = product.querySelector('span.text-lg').textContent;
            const price = parseFloat(priceText.replace('₹', ''));
            const imageSrc = product.querySelector('img').src;
            
            if (quantity === 0) {
                alert('Please select at least 1 item');
                return;
            }
            const existingItemIndex = cart.findIndex(item => item.name === productName);
            if (existingItemIndex !== -1) {
                cart[existingItemIndex].quantity += quantity;
            } else {
                cart.push({ name: productName, quantity, price, image: imageSrc });
            }
            updateCartCount();
            product.querySelector('.quantity').textContent = '0';
            alert(`${quantity} ${productName} added to cart`);
        });
    }
});
const backToTopBtn = document.getElementById('back-to-top');
window.addEventListener('scroll', function() {
    backToTopBtn.classList.toggle('hidden', window.scrollY <= 300);
});
backToTopBtn.addEventListener('click', function() {
    window.scrollTo({ top: 0, behavior: 'smooth' });
});
document.querySelectorAll('.show-more').forEach(button => {
    button.addEventListener('click', function() {
        const section = this.getAttribute('data-section');
        localStorage.setItem('activeSection', section);
        window.location.href = 'SeeAll.html';
    });
});
function updateNavigation() {
    const isLoggedIn = localStorage.getItem('userLoggedIn') === 'true';
    const authButtons = document.getElementById('auth-buttons');
    if (isLoggedIn && authButtons) {
        authButtons.innerHTML = `
            <button class="login active"><a href="Account.html">Account</a></button>
        `;
    } else if (authButtons) {
        authButtons.innerHTML = `
            <button class="login active"><a href="login.html">Login</a></button>
            <button class="signup"><a href="SignUp.html">Signup</a></button>
        `;
    }
}
function updateOrderSummary() {
    const subtotal = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    const tax = subtotal * 0.18;
    const total = subtotal + tax;
    const subtotalElement = document.getElementById('subtotal');
    const taxElement = document.getElementById('tax');
    const totalElement = document.getElementById('total');
    const checkoutBtn = document.getElementById('checkout-btn');
    if (subtotalElement) subtotalElement.textContent = `₹${subtotal.toFixed(2)}`;
    if (taxElement) taxElement.textContent = `₹${tax.toFixed(2)}`;
    if (totalElement) totalElement.textContent = `₹${total.toFixed(2)}`;
    if (checkoutBtn) checkoutBtn.disabled = cart.length === 0;
}
function renderCartItems() {
    const cartItemsContainer = document.getElementById('cart-items');
    const emptyCartMessage = document.getElementById('empty-cart-message');
    if (!cartItemsContainer || !emptyCartMessage) return;
    if (cart.length === 0) {
        emptyCartMessage.classList.remove('hidden');
        cartItemsContainer.innerHTML = '';
        cartItemsContainer.appendChild(emptyCartMessage);
        return;
    }
    emptyCartMessage.classList.add('hidden');
    let cartHTML = '';
    cart.forEach((item, index) => {
        cartHTML += `
            <div class="flex flex-col sm:flex-row gap-4 border-b border-gray-200 py-4" data-index="${index}">
                <img src="${item.image}" alt="${item.name}" class="w-24 h-24 object-contain rounded-lg">
                <div class="flex-1">
                    <h3 class="font-medium">${item.name}</h3>
                    <p class="text-gray-600">₹${item.price.toFixed(2)}</p>
                    <div class="flex items-center mt-2">
                        <button class="quantity-btn decrease bg-gray-200 px-2 py-1 rounded hover:bg-gray-300">-</button>
                        <span class="quantity mx-2 w-8 text-center">${item.quantity}</span>
                        <button class="quantity-btn increase bg-gray-200 px-2 py-1 rounded hover:bg-gray-300">+</button>
                        <button class="remove-btn ml-4 text-red-600 hover:text-red-800">Remove</button>
                    </div>
                </div>
                <div class="font-medium sm:text-right">
                    ₹${(item.price * item.quantity).toFixed(2)}
                </div>
            </div>
        `;
    });
    cartItemsContainer.innerHTML = cartHTML;
    document.querySelectorAll('.quantity-btn.increase').forEach(button => {
        button.addEventListener('click', function() {
            const index = this.closest('[data-index]').dataset.index;
            if (cart[index].quantity < 10) {
                cart[index].quantity++;
                renderCartItems();
                updateCartCount();
                updateOrderSummary();
            }
        });
    });
    document.querySelectorAll('.quantity-btn.decrease').forEach(button => {
        button.addEventListener('click', function() {
            const index = this.closest('[data-index]').dataset.index;
            if (cart[index].quantity > 1) {
                cart[index].quantity--;
                renderCartItems();
                updateCartCount();
                updateOrderSummary();
            }
        });
    });
    document.querySelectorAll('.remove-btn').forEach(button => {
        button.addEventListener('click', function() {
            const index = this.closest('[data-index]').dataset.index;
            cart.splice(index, 1);
            renderCartItems();
            updateCartCount();
            updateOrderSummary();
        });
    });
}
document.addEventListener('DOMContentLoaded', function() {
    updateCartCount();
    updateNavigation();
    renderCartItems();
    updateOrderSummary();
});
document.addEventListener('DOMContentLoaded', function() {
    const slider = document.getElementById('review-slider');
    let scrollInterval;
<<<<<<< HEAD
    const scrollAmount = 400;
    
    function startAutoScroll() {
        scrollInterval = setInterval(() => {
=======
    const scrollAmount = 400; // Adjust based on card width
    
    function startAutoScroll() {
        scrollInterval = setInterval(() => {
            // Check if reached end
>>>>>>> 2cc0be7 (Updated website files)
            if (slider.scrollLeft + slider.clientWidth >= slider.scrollWidth - 100) {
                slider.scrollTo({
                    left: 0,
                    behavior: 'smooth'
                });
            } else {
                slider.scrollBy({
                    left: scrollAmount,
                    behavior: 'smooth'
                });
            }
        }, 5000);
    }
    startAutoScroll();
    slider.addEventListener('mouseenter', () => clearInterval(scrollInterval));
    slider.addEventListener('mouseleave', startAutoScroll);
});
function hideLoader() {
    document.querySelector('.loader-container').classList.add('active');
}
window.onload = function() {
    setTimeout(hideLoader, 1000);
}
