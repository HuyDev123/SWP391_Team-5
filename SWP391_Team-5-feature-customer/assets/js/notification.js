// notification.js - Quản lý thông báo

// Hàm hiển thị thông báo
function showNotification(message, type = 'info') {
    const notification = document.getElementById('notification');
    if (!notification) return;
    
    const icon = notification.querySelector('.notification-icon');
    const msg = notification.querySelector('.notification-message');
    
    // Thiết lập icon dựa trên loại thông báo
    icon.className = 'notification-icon';
    if (type === 'success') {
        icon.classList.add('fas', 'fa-check-circle');
        notification.className = 'notification success';
    } else if (type === 'error') {
        icon.classList.add('fas', 'fa-times-circle');
        notification.className = 'notification error';
    } else if (type === 'warning') {
        icon.classList.add('fas', 'fa-exclamation-triangle');
        notification.className = 'notification warning';
    } else {
        icon.classList.add('fas', 'fa-info-circle');
        notification.className = 'notification info';
    }
    
    // Thiết lập nội dung thông báo
    msg.textContent = message;
    
    // Hiển thị thông báo
    notification.classList.add('show');
    
    // Tự động ẩn sau 3 giây
    setTimeout(() => {
        notification.classList.remove('show');
    }, 3000);
}

// Khởi tạo sự kiện đóng thông báo khi click vào nút đóng
document.addEventListener('DOMContentLoaded', function() {
    const closeBtn = document.querySelector('.notification-close');
    if (closeBtn) {
        closeBtn.addEventListener('click', function() {
            const notification = document.getElementById('notification');
            if (notification) {
                notification.classList.remove('show');
            }
        });
    }
    
    // Thêm thông báo vào window để có thể gọi từ bất kỳ file JS nào
    window.showNotification = showNotification;
}); 