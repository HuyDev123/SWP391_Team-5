// navigation.js
// Chuyển trang, sidebar, menu
// ... existing code ... 

// Navigation JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // ===== Toggle sidebar =====
    const toggleSidebar = document.getElementById('toggleSidebar');
    const sidebar = document.getElementById('sidebar');
    
    if (toggleSidebar && sidebar) {
        toggleSidebar.addEventListener('click', function() {
            sidebar.classList.toggle('active');
            document.body.classList.toggle('sidebar-open');
        });
        
        // Đóng sidebar khi click bên ngoài trên mobile
        document.addEventListener('click', function(e) {
            if (window.innerWidth <= 768 && 
                sidebar.classList.contains('active') && 
                !sidebar.contains(e.target) && 
                e.target !== toggleSidebar) {
                sidebar.classList.remove('active');
                document.body.classList.remove('sidebar-open');
            }
        });
    }
    
    // ===== Chuyển tab khi click vào menu sidebar =====
    const menuLinks = document.querySelectorAll('.menu-link');
    
    menuLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            // Nếu đang ở trang khác, không ngăn chặn chuyển hướng
            if (!this.getAttribute('data-page')) return;
            
            e.preventDefault();
            
            // Xóa active class khỏi tất cả menu links
            menuLinks.forEach(item => item.classList.remove('active'));
            
            // Thêm active class vào link được click
            this.classList.add('active');
            
            // Nếu có data-page, hiển thị tab tương ứng
            const pageId = this.getAttribute('data-page');
            if (pageId) {
                // Ẩn tất cả các tab
                const pages = document.querySelectorAll('.page-content');
                pages.forEach(page => page.classList.remove('active'));
                
                // Hiển thị tab được chọn
                const selectedPage = document.getElementById(pageId + '-page');
                if (selectedPage) selectedPage.classList.add('active');
                
                // Đóng sidebar trên mobile sau khi chọn tab
                if (window.innerWidth <= 768 && sidebar) {
                    sidebar.classList.remove('active');
                    document.body.classList.remove('sidebar-open');
                }
                
                // Cập nhật URL nếu cần
                updateURL(pageId);
            }
        });
    });
    
    // ===== Xử lý logout =====
    const logoutLink = document.querySelector('.logout-link');
    if (logoutLink) {
        logoutLink.addEventListener('click', function(e) {
            e.preventDefault();
            
            if (confirm('Bạn có chắc chắn muốn đăng xuất?')) {
                // Xóa dữ liệu phiên và chuyển hướng đến trang đăng nhập
                // localStorage.removeItem('user');
                // localStorage.removeItem('token');
                window.location.href = 'login.html';
            }
        });
    }
    
    // ===== Xử lý notification =====
    const notificationBtn = document.getElementById('notificationBtn');
    if (notificationBtn) {
        notificationBtn.addEventListener('click', function() {
            alert('Chức năng thông báo đang được phát triển!');
        });
    }
    
    // ===== Xử lý help button =====
    const helpBtn = document.querySelector('.help-btn');
    if (helpBtn) {
        helpBtn.addEventListener('click', function() {
            alert('Chức năng trợ giúp đang được phát triển!');
        });
    }
    
    // ===== Kiểm tra URL và active tab tương ứng khi tải trang =====
    function activateTabFromURL() {
        // Lấy hash từ URL (ví dụ: #appointments)
        const hash = window.location.hash.substring(1);
        
        if (hash) {
            // Tìm menu link tương ứng
            const link = document.querySelector(`.menu-link[data-page="${hash}"]`);
            if (link) {
                // Kích hoạt click event
                link.click();
            }
        } else {
            // Nếu không có hash, active tab đầu tiên
            const firstLink = document.querySelector('.menu-link[data-page]');
            if (firstLink) {
                firstLink.click();
            }
        }
    }
    
    // Cập nhật URL khi chuyển tab
    function updateURL(pageId) {
        if (history.pushState) {
            history.pushState(null, null, `#${pageId}`);
        } else {
            window.location.hash = pageId;
        }
    }
    
    // Kích hoạt tab từ URL khi tải trang
    activateTabFromURL();
    
    // Xử lý khi URL thay đổi (khi người dùng sử dụng nút back/forward)
    window.addEventListener('popstate', activateTabFromURL);
});

// Hàm chuyển trang
function navigateToPage(page) {
    // Ẩn tất cả các trang
    const allPages = document.querySelectorAll('.page-content');
    allPages.forEach(p => p.classList.remove('active'));
    
    // Hiển thị trang được chọn
    const targetPage = document.getElementById(`${page}-page`);
    if (targetPage) {
        targetPage.classList.add('active');
    }
    
    // Cập nhật trạng thái active của menu
    const allMenuLinks = document.querySelectorAll('.menu-link');
    allMenuLinks.forEach(link => link.classList.remove('active'));
    
    const activeLink = document.querySelector(`.menu-link[data-page="${page}"]`);
    if (activeLink) {
        activeLink.classList.add('active');
    }
    
    // Xử lý logic đặc biệt cho từng trang
    if (page === 'kit') {
        // Khởi tạo dữ liệu cho trang kit
        if (typeof renderKitTable === 'function') {
            renderKitTable();
        }
    }
    
    // Cuộn lên đầu trang
    window.scrollTo(0, 0);
} 