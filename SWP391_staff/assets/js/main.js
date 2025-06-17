// Main.js - Logic chính cho dashboard
// Khi DOM đã sẵn sàng, thực hiện các thao tác khởi tạo
document.addEventListener("DOMContentLoaded", function () {
  // Kiểm tra xem module Auth đã được load chưa
  if (window.Auth) {
    console.log("Auth module loaded successfully");
  } else {
    // Nếu chưa load được Auth, báo lỗi
    console.error(
      "Auth module not loaded. Make sure auth.js is included before main.js"
    );
  }

  // Hàm thiết lập giao diện theo vai trò người dùng
  window.setupUIByRole = function (user) {
    if (!user || !user.role) return;

    console.log("Setting up UI for role:", user.role);

    // Lấy tất cả các phần tử chỉ dành cho từng vai trò
    const staffElements = document.querySelectorAll(".staff-only");
    const managerElements = document.querySelectorAll(".manager-only");
    const adminElements = document.querySelectorAll(".admin-only");

    // Ẩn toàn bộ các phần tử phân quyền trước
    staffElements.forEach((el) => (el.style.display = "none"));
    managerElements.forEach((el) => (el.style.display = "none"));
    adminElements.forEach((el) => (el.style.display = "none"));

    // Hiển thị các phần tử phù hợp với vai trò
    if (user.role === "staff") {
      staffElements.forEach((el) => (el.style.display = ""));
    } else if (user.role === "manager") {
      staffElements.forEach((el) => (el.style.display = ""));
      managerElements.forEach((el) => (el.style.display = ""));
    } else if (user.role === "admin") {
      staffElements.forEach((el) => (el.style.display = ""));
      managerElements.forEach((el) => (el.style.display = ""));
      adminElements.forEach((el) => (el.style.display = ""));
    }

    // Cập nhật thông điệp chào mừng nếu có
    const welcomeMessage = document.getElementById("welcome-message");
    if (welcomeMessage) {
      let roleDisplay = "nhân viên";
      if (user.role === "manager") roleDisplay = "quản lý";
      if (user.role === "admin") roleDisplay = "quản trị viên";

      welcomeMessage.textContent = `Chào mừng ${user.fullName}, ${roleDisplay} đã quay trở lại!`;
    }
  };

  // Kiểm tra trạng thái đăng nhập và cập nhật UI
  if (window.Auth && typeof window.Auth.checkLoginStatus === "function") {
    const { isLoggedIn, user } = window.Auth.checkLoginStatus();

    if (isLoggedIn && user) {
      console.log("User is logged in:", user);

      // Thiết lập UI theo vai trò nếu đã đăng nhập
      if (typeof window.setupUIByRole === "function") {
        window.setupUIByRole(user);
      }
    } else {
      console.log("User is not logged in");
    }
  }
});

// Hàm hiển thị thông báo (đã có sẵn trong HTML)
function showNotification(message, type = "success") {
  const notification = document.getElementById("notification");
  if (notification) {
    const notificationTitle = document.getElementById("notification-title");
    const notificationMessage = document.getElementById("notification-message");
    const notificationIcon = document.getElementById("notification-icon");

    // Xử lý loại thông báo
    if (type === "success") {
      notificationTitle.textContent = "Thành công";
      notificationIcon.textContent = "✓";
      notificationIcon.style.color = "#4caf50";
    } else if (type === "error") {
      notificationTitle.textContent = "Lỗi";
      notificationIcon.textContent = "❌";
      notificationIcon.style.color = "#f44336";
    } else if (type === "info") {
      notificationTitle.textContent = "Thông báo";
      notificationIcon.textContent = "ℹ️";
      notificationIcon.style.color = "#2196F3";
    }

    // Gán nội dung thông báo và hiển thị
    notificationMessage.textContent = message;
    notification.style.display = "flex";
  }
}
