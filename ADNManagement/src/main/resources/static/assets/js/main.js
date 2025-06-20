// File intentionally left blank.

// Xử lý sự kiện click cho user dropdown và các chức năng chung
document.addEventListener('DOMContentLoaded', function() {
    // Cập nhật giao diện đăng nhập
    updateAuthUI();

    // Xử lý sự kiện click cho user dropdown
    document.addEventListener('click', function(e) {
        const userDropdown = document.querySelector('.user-name');
        const dropdownContent = document.querySelector('.dropdown-content');
        
        if (!userDropdown || !dropdownContent) return;

        if (userDropdown.contains(e.target)) {
            // Click vào user name
            e.stopPropagation();
            userDropdown.classList.toggle('active');
            dropdownContent.classList.toggle('show');
        } else if (!dropdownContent.contains(e.target)) {
            // Click ra ngoài dropdown
            userDropdown.classList.remove('active');
            dropdownContent.classList.remove('show');
        }
    });
});

// Hàm cập nhật giao diện đăng nhập
function updateAuthUI() {
    const loginButton = document.querySelector(".nav-link.login-button");
    const registerBtn = document.querySelector(".quick-contact-btn.register");
    const authStatus = Auth.checkLoginStatus();

    if (authStatus.isLoggedIn) {
        // Người dùng đã đăng nhập
        const user = authStatus.user;
        const defaultAvatar = "assets/images/user-avatar.jpg";

        // Cập nhật nút đăng nhập thành menu người dùng
        loginButton.innerHTML = `
            <div class="user-dropdown">
                <span class="user-name">
                    <img src="${user.avatar || defaultAvatar}" alt="Avatar" class="user-avatar">
                    ${user.fullName || "Người dùng"}
                    <i class="fas fa-caret-down"></i>
                </span>
                <div class="dropdown-content">
                    <a href="newprofile.html">Tài khoản</a>
                    <a href="appoinments.html">Lịch hẹn</a>
                    <a href="historytest.html">Lịch sử xét nghiệm</a>
                    <a href="#" onclick="handleLogout(); return false;">Đăng xuất</a>
                </div>
            </div>
        `;

        loginButton.classList.add("logged-in");
        loginButton.removeAttribute("href");

        // Cập nhật nút đăng ký ở sidebar (nếu có)
        if (registerBtn) {
            registerBtn.setAttribute("href", "newprofile.html");
            registerBtn.querySelector("span").textContent = "Tài khoản";
        }
    } else {
        // Người dùng chưa đăng nhập
        loginButton.innerHTML = "Đăng nhập";
        loginButton.classList.remove("logged-in");
        loginButton.setAttribute("href", "login.html");

        // Khôi phục nút đăng ký
        if (registerBtn) {
            registerBtn.setAttribute("href", "register.html");
            registerBtn.querySelector("span").textContent = "Đăng ký";
        }
    }
}

// Hàm xử lý đăng xuất
function handleLogout() {
    if (confirm("Bạn có chắc chắn muốn đăng xuất?")) {
        Auth.logout()
            .then(() => {
                updateAuthUI();
                alert("Đăng xuất thành công!");
                window.location.reload();
            })
            .catch((error) => {
                console.error("Lỗi khi đăng xuất:", error);
                alert("Có lỗi xảy ra khi đăng xuất.");
            });
    }
}