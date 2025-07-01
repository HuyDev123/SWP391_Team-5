// Main.js - Logic chính cho dashboard
// Hàm renderStaffSidebarInfo toàn cục, chỉ export, không tự động gọi
window.renderStaffSidebarInfo = function () {
  const staff = window.serverStaff || {};
  const container = document.getElementById("user-info-container");
  if (!container) return;
  container.innerHTML = `
    <div class="user-info-block" id="user-info-block" style="position:relative;">
      <div class="user-info-main" id="user-info-main" style="display:flex;align-items:center;gap:12px;cursor:pointer;">
        <img id="user-avatar" src="/assets/images/user-avatar.png" alt="User Avatar" style="width:44px;height:44px;border-radius:50%;object-fit:cover;" />
        <div style="display:flex;flex-direction:column;align-items:flex-start;">
          <div id="avatar-fullname" class="user-name" style="font-weight:600;font-size:15px;">${staff.fullName}</div>
          <div id="avatar-role" class="user-role" style="font-size:13px;color:#888;">Nhân viên</div>
        </div>
        <i class="fas fa-chevron-down" style="margin-left:8px;color:#888;font-size:16px;"></i>
      </div>
      <div class="user-dropdown" id="user-dropdown" style="display:none;position:absolute;right:0;top:44px;min-width:160px;background:#fff!important;opacity:1!important;backdrop-filter:none!important;border-radius:10px;box-shadow:0 4px 16px rgba(0,0,0,0.13)!important;z-index:9999;overflow:hidden;border:1px solid #e0e0e0!important;">
        <a href="/profile" style="display:flex;align-items:center;padding:12px 18px;text-decoration:none;color:#333;font-size:14px;gap:8px;transition:background 0.2s;">
          <i class="fas fa-user"></i> Hồ sơ cá nhân
        </a>
        <a href="#" onclick="window.handleLogout();return false;" style="display:flex;align-items:center;padding:12px 18px;text-decoration:none;color:#f44336;font-size:14px;gap:8px;transition:background 0.2s;">
          <i class="fas fa-sign-out-alt"></i> Đăng xuất
        </a>
      </div>
    </div>
  `;
  // Toggle dropdown logic
  const block = document.getElementById("user-info-block");
  const dropdown = document.getElementById("user-dropdown");
  if (block && dropdown) {
    let dropdownTimeout;
    block.addEventListener("mouseenter", function() {
      clearTimeout(dropdownTimeout);
      dropdown.style.display = "block";
    });
    block.addEventListener("mouseleave", function() {
      dropdownTimeout = setTimeout(() => { dropdown.style.display = "none"; }, 120);
    });
  }
  // Thêm CSS hover cho dropdown nếu chưa có
  if (!document.getElementById('user-dropdown-style')) {
    const style = document.createElement('style');
    style.id = 'user-dropdown-style';
    style.textContent = `
      .user-dropdown a:hover { background: #f5f5f5 !important; }
    `;
    document.head.appendChild(style);
  }
};

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

// Hàm logout dùng chung cho staff
window.handleLogout = function () {
  if (window.Auth && typeof window.Auth.logout === "function") {
    window.Auth.logout()
      .then(() => {
        window.location.reload();
      })
      .catch((error) => {
        alert("Có lỗi xảy ra khi đăng xuất.");
      });
  } else {
    alert("Không tìm thấy module đăng xuất.");
  }
};
