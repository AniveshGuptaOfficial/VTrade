document.getElementById('menu-toggle').addEventListener('click', function() {
    const navLinks = document.getElementById('nav-links');
    navLinks.classList.toggle('hidden');
});
let cart = JSON.parse(localStorage.getItem('cart')) || [];
const cartCountElement = document.getElementById('cart-count');
function updateCartCount() {
    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
    cartCountElement.textContent = totalItems;
    if (totalItems > 0) {
        cartCountElement.classList.remove('hidden');
    } else {
        cartCountElement.classList.add('hidden');
    }
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
            } 
            else {
                cart.push({
                    name: productName,
                    quantity: quantity,
                    price: price,
                    image: imageSrc
                });
            }
            updateCartCount();
            product.querySelector('.quantity').textContent = '0';
            alert(`${quantity} ${productName} added to cart`);
        });
    }
});
document.addEventListener('DOMContentLoaded', () => {
    updateCartCount();
});
document.addEventListener('DOMContentLoaded', function() {
    const backToTopBtn = document.getElementById('back-to-top');
    window.addEventListener('scroll', function() {
        if (window.scrollY > 300) {
            backToTopBtn.classList.remove('hidden');
        } else {
            backToTopBtn.classList.add('hidden');
        }
    });
    backToTopBtn.addEventListener('click', function() {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    });
});
document.getElementById('show-more').addEventListener('click', function() {
    const hiddenItems = document.querySelectorAll('[data-hidden]');
    hiddenItems.forEach(item => {
        item.classList.toggle('hidden');
    });
    this.textContent = this.textContent === 'Show More' ? 'Show Less' : 'Show More';
});