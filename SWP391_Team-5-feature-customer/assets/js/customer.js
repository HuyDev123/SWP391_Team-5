// customer.js - Quản lý giao diện khách hàng xét nghiệm ADN (tối ưu cho layout mới)

document.addEventListener('DOMContentLoaded', function() {
    // --- Chuyển tab động sidebar ---
    const menuLinks = document.querySelectorAll('.menu-link[data-page]');
    const pages = document.querySelectorAll('.page-content');
    menuLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            menuLinks.forEach(l => l.classList.remove('active'));
            pages.forEach(page => page.classList.remove('active'));
            this.classList.add('active');
            const pageId = this.getAttribute('data-page');
            const page = document.getElementById(pageId + '-page');
            if (page) page.classList.add('active');
        });
    });

    // --- Sidebar mobile toggle ---
    const toggleSidebar = document.getElementById('toggleSidebar');
    const sidebar = document.getElementById('sidebar');
    if (toggleSidebar && sidebar) {
        toggleSidebar.addEventListener('click', function() {
            sidebar.classList.toggle('active');
        });
    }

    // --- Notification ---
    function showNotification(message, type = 'info') {
        const notification = document.getElementById('notification');
        if (!notification) return;
        const icon = notification.querySelector('.notification-icon');
        const msg = notification.querySelector('.notification-message');
        icon.className = 'notification-icon';
        if (type === 'success') icon.classList.add('fas', 'fa-check-circle');
        else if (type === 'error') icon.classList.add('fas', 'fa-times-circle');
        else icon.classList.add('fas', 'fa-info-circle');
        msg.textContent = message;
        notification.classList.add('show');
        setTimeout(() => notification.classList.remove('show'), 3000);
    }
    document.querySelector('.notification-close')?.addEventListener('click', function() {
        document.getElementById('notification').classList.remove('show');
    });

    // --- Dashboard: render dữ liệu mẫu ---
    document.getElementById('stat-appointments').textContent = '2';
    document.getElementById('stat-kits').textContent = '1';
    document.getElementById('stat-results').textContent = '3';
    // Lịch hẹn gần nhất
    document.getElementById('dashboard-appointments-list').innerHTML = `
        <ul>
            <li>12/06/2024 - Xét nghiệm cha con - Trung tâm 1</li>
            <li>15/06/2024 - Xét nghiệm mẹ con - Tại nhà</li>
        </ul>
    `;
    // Kết quả mới nhất
    document.getElementById('dashboard-results-list').innerHTML = `
        <ul>
            <li>11/06/2024 - Xét nghiệm cha con - <b class="positive">Dương tính</b></li>
            <li>10/06/2024 - Xét nghiệm mẹ con - <b class="negative">Âm tính</b></li>
        </ul>
    `;
    document.getElementById('viewAllAppointments')?.addEventListener('click', () => navigateToPage('appointments'));
    document.getElementById('viewAllResults')?.addEventListener('click', () => navigateToPage('results'));

    // --- Appointments: render dữ liệu mẫu ---
    const appointmentsTable = document.querySelector('.appointments-table tbody');
    if (appointmentsTable) {
        appointmentsTable.innerHTML = `
            <tr><td>1</td><td>Nguyễn Văn A</td><td>Xét nghiệm cha con</td><td>12/06/2024</td><td>Trung tâm</td><td><span class="status-badge completed">Đã hoàn thành</span></td><td><button class="btn btn-sm btn-info">Xem</button></td></tr>
            <tr><td>2</td><td>Nguyễn Văn A</td><td>Xét nghiệm mẹ con</td><td>15/06/2024</td><td>Tại nhà</td><td><span class="status-badge processing">Đang xử lý</span></td><td><button class="btn btn-sm btn-info">Xem</button></td></tr>
        `;
    }

    // --- Results: render dữ liệu mẫu ---
    const resultsTable = document.querySelector('.results-table tbody');
    if (resultsTable) {
        resultsTable.innerHTML = `
            <tr><td>1</td><td>Xét nghiệm cha con</td><td>11/06/2024</td><td><span class="status-badge completed">Đã trả</span></td><td><span class="positive">Dương tính</span></td><td><button class="btn btn-sm btn-info">Xem</button></td></tr>
            <tr><td>2</td><td>Xét nghiệm mẹ con</td><td>10/06/2024</td><td><span class="status-badge completed">Đã trả</span></td><td><span class="negative">Âm tính</span></td><td><button class="btn btn-sm btn-info">Xem</button></td></tr>
        `;
    }

    // --- Feedback: rating sao ---
    const ratingStars = document.querySelectorAll('.rating .star');
    if (ratingStars.length > 0) {
        ratingStars.forEach(star => {
            star.addEventListener('click', function() {
                const value = parseInt(this.getAttribute('data-value'));
                ratingStars.forEach((s, idx) => {
                    if (idx < value) s.classList.add('active');
                    else s.classList.remove('active');
                });
                document.getElementById('rating-value').value = value;
            });
        });
    }

    // --- Feedback: submit form ---
    document.getElementById('feedback-form')?.addEventListener('submit', function(e) {
        e.preventDefault();
        const service = this.elements['service'].value;
        const rating = this.elements['rating'].value;
        if (!service || !rating) {
            showNotification('Vui lòng chọn dịch vụ và đánh giá sao!', 'error');
            return;
        }
        showNotification('Cảm ơn bạn đã gửi đánh giá!', 'success');
            this.reset();
        ratingStars.forEach(star => star.classList.remove('active'));
});

    // --- Profile: submit form ---
    document.getElementById('profile-form')?.addEventListener('submit', function(e) {
        e.preventDefault();
        const fullname = this.elements['fullname'].value.trim();
        const email = this.elements['email'].value.trim();
        const phone = this.elements['phone'].value.trim();
        if (!fullname || !email || !phone) {
            showNotification('Vui lòng nhập đầy đủ thông tin!', 'error');
            return;
        }
        showNotification('Thông tin cá nhân đã được cập nhật!', 'success');
    });

    // --- Helper: chuyển tab bằng code ---
    function navigateToPage(pageId) {
        const link = document.querySelector(`.menu-link[data-page="${pageId}"]`);
        if (link) link.click();
    }
});
