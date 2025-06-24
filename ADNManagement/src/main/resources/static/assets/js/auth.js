/**
 * Module quản lý xác thực người dùng
 * Hỗ trợ cả dữ liệu giả lập cho test và tích hợp API thực tế
 */

// Dữ liệu người dùng mẫu cho việc test
// const mockUsers = [
//   {
//     id: 1,
//     email: "user@example.com",
//     password: "password123",
//     fullName: "Nguyễn Văn A",
//     phone: "0987654321",
//     avatar: "assets/images/auth/user-avatar.jpg",
//   },
//   {
//     id: 2,
//     email: "admin@example.com",
//     password: "admin123",
//     fullName: "Trần Thị B",
//     phone: "0123456789",
//     avatar: "assets/images/admin-avatar.jpg",
//   },
// ];

/**
 * Kiểm tra trạng thái đăng nhập
 * @returns {Object} - Trạng thái đăng nhập và thông tin người dùng nếu đã đăng nhập
 */
function checkLoginStatus() {
  // Ưu tiên kiểm tra biến user từ server (window.serverUser)
  if (typeof window.serverUser !== 'undefined' && window.serverUser) {
    return {
      isLoggedIn: true,
      user: window.serverUser,
    };
  }
  // Nếu không có, trả về chưa đăng nhập
  return { isLoggedIn: false };
}

/**
 * Cập nhật giao diện xác thực (menu đăng nhập/đăng xuất)
 */
function updateAuthUI() {
  const loginButton = document.querySelector(".nav-link.login-button");
  if (!loginButton) return;
  const authStatus = Auth.checkLoginStatus();
  if (authStatus.isLoggedIn) {
    const user = authStatus.user;
    const defaultAvatar = "/assets/images/user-avatar.png";
    loginButton.innerHTML = `
      <div class="user-dropdown">
        <span class="user-name">
          <img src="${defaultAvatar}" alt="Avatar" class="user-avatar">
          ${user.fullName}
          <i class="fas fa-caret-down"></i>
        </span>
        <div class="dropdown-content">
          <a href="/profile">Tài khoản</a>
          <a href="/appoinments-list">Lịch hẹn</a>
          <a href="/test-history">Lịch sử xét nghiệm</a>
          <a href="#" id="logout-link">Đăng xuất</a>
        </div>
      </div>
    `;
    loginButton.classList.add("logged-in");
    loginButton.removeAttribute("href");
    // Dropdown event
    const userDropdown = loginButton.querySelector('.user-name');
    const dropdownContent = loginButton.querySelector('.dropdown-content');
    if (userDropdown && dropdownContent) {
      userDropdown.addEventListener('click', function(e) {
        e.stopPropagation();
        this.classList.toggle('active');
        dropdownContent.classList.toggle('show');
      });
      document.addEventListener('click', function(e) {
        if (!userDropdown.contains(e.target)) {
          userDropdown.classList.remove('active');
          dropdownContent.classList.remove('show');
        }
      });
    }
    // Đăng xuất
    const logoutLink = loginButton.querySelector('#logout-link');
    if (logoutLink) {
      logoutLink.addEventListener('click', function(e) {
        e.preventDefault();
        handleLogout();
      });
    }
  } else {
    loginButton.innerHTML = "Đăng nhập";
    loginButton.classList.remove("logged-in");
    loginButton.setAttribute("href", "/login");
  }
}

/**
 * Xử lý đăng xuất
 */
function handleLogout() {
  if (confirm("Bạn có chắc chắn muốn đăng xuất?")) {
    // sessionStorage.removeItem("user");
    // updateAuthUI();
    // alert("Đăng xuất thành công!");
  }
}

// Tự động cập nhật UI khi load trang
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', updateAuthUI);
} else {
  updateAuthUI();
}

// Export các hàm public để sử dụng từ bên ngoài
window.Auth = {
  checkLoginStatus,
  updateAuthUI,
  handleLogout
};
