/**
 * Admin Dashboard JavaScript
 * File: admin.js
 * Description: Xử lý các chức năng đặc thù của trang Admin
 */

document.addEventListener("DOMContentLoaded", function () {
  // Khởi tạo biểu đồ thống kê người dùng
  initUserStatsChart();

  // Khởi tạo sự kiện cho các nút trong trang Admin
  initAdminEvents();

  // Khởi tạo hiệu ứng loading cho trang khi chuyển menu
  initPageLoading();

  // Tạo hiệu ứng thông báo thời gian thực
  initRealTimeNotifications();

  // Xử lý chuyển đổi trang khi click vào các menu item
  handleMenuNavigation();

  // Cài đặt giao diện người dùng tùy chọn (nếu có)
  loadUserPreferences();
});

/**
 * Khởi tạo biểu đồ thống kê người dùng
 */
function initUserStatsChart() {
  const ctx = document.getElementById("userStatsChart");
  if (!ctx) return;

  const userStatsChart = new Chart(ctx, {
    type: "line",
    data: {
      labels: ["T2", "T3", "T4", "T5", "T6", "T7", "CN"],
      datasets: [
        {
          label: "Người dùng mới",
          data: [15, 25, 18, 30, 25, 35, 20],
          borderColor: "#4caf50",
          backgroundColor: "rgba(76, 175, 80, 0.1)",
          borderWidth: 2,
          tension: 0.4,
          fill: true,
        },
        {
          label: "Người dùng hoạt động",
          data: [65, 59, 80, 81, 56, 85, 90],
          borderColor: "#2196f3",
          backgroundColor: "rgba(33, 150, 243, 0.1)",
          borderWidth: 2,
          tension: 0.4,
          fill: true,
        },
      ],
    },
    options: {
      scales: {
        y: {
          beginAtZero: true,
        },
      },
      responsive: true,
      plugins: {
        legend: {
          position: "top",
        },
      },
    },
  });

  // Xử lý thay đổi khoảng thời gian cho biểu đồ
  document
    .getElementById("userStatsTimeRange")
    .addEventListener("change", function () {
      updateUserStatsChart(this.value, userStatsChart);
    });
}

/**
 * Cập nhật dữ liệu biểu đồ dựa trên khoảng thời gian lựa chọn
 * @param {string} timeRange - Khoảng thời gian lựa chọn
 * @param {object} chart - Đối tượng biểu đồ
 */
function updateUserStatsChart(timeRange, chart) {
  // Giả lập dữ liệu theo các khoảng thời gian khác nhau
  let labels, userData, activeData;

  switch (timeRange) {
    case "7 ngày qua":
      labels = ["T2", "T3", "T4", "T5", "T6", "T7", "CN"];
      userData = [15, 25, 18, 30, 25, 35, 20];
      activeData = [65, 59, 80, 81, 56, 85, 90];
      break;
    case "30 ngày qua":
      labels = ["Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4", "Tuần 5"];
      userData = [75, 90, 85, 95, 110];
      activeData = [210, 250, 240, 260, 280];
      break;
    case "90 ngày qua":
      labels = ["Tháng 1", "Tháng 2", "Tháng 3"];
      userData = [250, 320, 290];
      activeData = [780, 850, 920];
      break;
    case "Năm qua":
      labels = [
        "Th1",
        "Th2",
        "Th3",
        "Th4",
        "Th5",
        "Th6",
        "Th7",
        "Th8",
        "Th9",
        "Th10",
        "Th11",
        "Th12",
      ];
      userData = [150, 180, 220, 250, 300, 320, 310, 340, 360, 380, 400, 420];
      activeData = [500, 550, 600, 650, 700, 750, 780, 820, 840, 880, 910, 950];
      break;
  }

  chart.data.labels = labels;
  chart.data.datasets[0].data = userData;
  chart.data.datasets[1].data = activeData;
  chart.update();
}

/**
 * Khởi tạo sự kiện cho các nút trong trang Admin
 */
function initAdminEvents() {
  // Nút làm mới tình trạng hệ thống
  const refreshSystemBtn = document.getElementById("refreshSystemHealth");
  if (refreshSystemBtn) {
    refreshSystemBtn.addEventListener("click", function () {
      showLoading("Đang cập nhật tình trạng hệ thống...");

      // Giả lập thời gian tải dữ liệu
      setTimeout(function () {
        updateSystemHealth();
        hideLoading();
        showNotification("Đã cập nhật tình trạng hệ thống", "success");
      }, 1000);
    });
  }

  // Nút xem tất cả nhật ký
  const viewAllLogsBtn = document.getElementById("viewAllLogs");
  if (viewAllLogsBtn) {
    viewAllLogsBtn.addEventListener("click", function () {
      const systemLogsLink = document.querySelector(
        'a[data-page="system-logs"]'
      );
      if (systemLogsLink) {
        systemLogsLink.click();
      }
    });
  }

  // Nút xem tất cả hoạt động
  const viewAllActivitiesBtn = document.getElementById("viewAllActivities");
  if (viewAllActivitiesBtn) {
    viewAllActivitiesBtn.addEventListener("click", function () {
      showNotification("Đang tải danh sách hoạt động...", "info");

      // Giả lập thời gian tải dữ liệu
      setTimeout(function () {
        loadAllActivities();
      }, 500);
    });
  }

  // Thêm sự kiện cho các nút khác khi cần
}

/**
 * Cập nhật dữ liệu tình trạng hệ thống
 */
function updateSystemHealth() {
  const systemTable = document.getElementById("systemHealthTable");
  if (!systemTable) return;

  // Cập nhật các giá trị ngẫu nhiên cho các dịch vụ
  const rows = systemTable.querySelectorAll("tr");

  rows.forEach((row) => {
    // Cập nhật phần trăm tải
    const loadCell = row.querySelector("td:nth-child(4)");
    if (loadCell) {
      const progressBar = loadCell.querySelector(".progress-bar");
      if (progressBar) {
        const newLoad = Math.floor(Math.random() * 95) + 5; // 5-99%
        let bgColor = "#4caf50"; // Màu xanh lá cho tải thấp

        if (newLoad > 80) {
          bgColor = "#f44336"; // Màu đỏ cho tải cao
        } else if (newLoad > 60) {
          bgColor = "#ff9800"; // Màu cam cho tải trung bình
        }

        progressBar.style.width = `${newLoad}%`;
        progressBar.style.backgroundColor = bgColor;
        progressBar.textContent = `${newLoad}%`;

        // Cập nhật trạng thái nếu tải quá cao
        const statusCell = row.querySelector("td:nth-child(2)");
        if (statusCell) {
          const statusBadge = statusCell.querySelector(".badge");
          if (statusBadge && newLoad > 90) {
            statusBadge.style.backgroundColor = "#ff9800";
            statusBadge.textContent = "Cảnh báo";
          } else if (statusBadge && newLoad <= 90) {
            statusBadge.style.backgroundColor = "#4caf50";
            statusBadge.textContent = "Hoạt động";
          }
        }
      }
    }

    // Cập nhật thời gian hoạt động
    const uptimeCell = row.querySelector("td:nth-child(3)");
    if (uptimeCell) {
      const parts = uptimeCell.textContent.split(", ");
      if (parts.length === 2) {
        const days = parseInt(parts[0]);
        const hours = parseInt(parts[1]) + 1; // Tăng thêm 1 giờ

        if (hours >= 24) {
          uptimeCell.textContent = `${days + 1} ngày, 0 giờ`;
        } else {
          uptimeCell.textContent = `${days} ngày, ${hours} giờ`;
        }
      }
    }
  });
}

/**
 * Tải tất cả hoạt động hệ thống
 */
function loadAllActivities() {
  const activitiesList = document.getElementById("recentActivitiesList");
  if (!activitiesList) return;

  // Thêm các hoạt động mới vào danh sách
  const newActivities = [
    {
      icon: "fas fa-server",
      name: "Khởi động lại máy chủ",
      time: "Hôm qua, 12:30 PM",
      user: "Hệ thống",
      status: "Thành công",
    },
    {
      icon: "fas fa-shield-alt",
      name: "Cập nhật bảo mật",
      time: "Hôm qua, 10:15 AM",
      user: "Admin Trần",
      status: "Hoàn thành",
    },
    {
      icon: "fas fa-user-times",
      name: "Khóa tài khoản",
      time: "22/05/2025, 3:45 PM",
      user: "Admin Hoàng",
      status: "Hoàn thành",
    },
    {
      icon: "fas fa-chart-line",
      name: "Xuất báo cáo hệ thống",
      time: "22/05/2025, 9:20 AM",
      user: "Admin Lê",
      status: "Hoàn thành",
    },
  ];

  // Tạo các phần tử HTML cho hoạt động mới
  newActivities.forEach((activity) => {
    const activityItem = document.createElement("div");
    activityItem.className = "appointment-item";
    activityItem.innerHTML = `
      <div class="appointment-avatar">
        <i class="${activity.icon}"></i>
      </div>
      <div class="appointment-details">
        <div class="appointment-name">${activity.name}</div>
        <div class="appointment-info">
          <span><i class="fas fa-calendar"></i> ${activity.time}</span>
          <span><i class="fas fa-user"></i> ${activity.user}</span>
        </div>
      </div>
      <div class="appointment-status status-scheduled">
        ${activity.status}
      </div>
    `;

    // Thêm hiệu ứng cho phần tử mới
    activityItem.style.opacity = "0";
    activityItem.style.transform = "translateY(10px)";
    activityItem.style.transition = "opacity 0.3s ease, transform 0.3s ease";

    activitiesList.appendChild(activityItem);

    // Hiển thị animation sau một khoảng thời gian nhỏ
    setTimeout(() => {
      activityItem.style.opacity = "1";
      activityItem.style.transform = "translateY(0)";
    }, 50);
  });

  showNotification("Đã tải thêm 4 hoạt động", "success");
}

/**
 * Khởi tạo hiệu ứng loading cho trang khi chuyển menu
 */
function initPageLoading() {
  // Tạo phần tử loading
  const loadingOverlay = document.createElement("div");
  loadingOverlay.className = "loading-overlay";
  loadingOverlay.innerHTML = `
    <div class="loading-spinner"></div>
    <div class="loading-message">Đang tải...</div>
  `;
  document.body.appendChild(loadingOverlay);

  // Thêm CSS cho loading overlay
  const style = document.createElement("style");
  style.textContent = `
    .loading-overlay {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(255, 255, 255, 0.8);
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      z-index: 9999;
      opacity: 0;
      visibility: hidden;
      transition: opacity 0.3s, visibility 0.3s;
    }
    
    .loading-overlay.active {
      opacity: 1;
      visibility: visible;
    }
    
    .loading-spinner {
      width: 50px;
      height: 50px;
      border: 5px solid rgba(33, 150, 243, 0.2);
      border-radius: 50%;
      border-top-color: #2196f3;
      animation: spin 1s ease-in-out infinite;
    }
    
    .loading-message {
      margin-top: 15px;
      font-size: 16px;
      font-weight: 500;
      color: #333;
    }
    
    @keyframes spin {
      to { transform: rotate(360deg); }
    }
    
    /* Hiệu ứng nhấp nháy cho thông báo */
    .notification {
      position: fixed;
      top: 20px;
      right: 20px;
      padding: 15px 25px;
      background-color: white;
      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
      border-radius: 5px;
      z-index: 10000;
      opacity: 0;
      transform: translateY(-20px);
      transition: opacity 0.3s, transform 0.3s;
      display: flex;
      align-items: center;
      max-width: 400px;
    }
    
    .notification.success {
      border-left: 4px solid #4caf50;
    }
    
    .notification.error {
      border-left: 4px solid #f44336;
    }
    
    .notification.info {
      border-left: 4px solid #2196f3;
    }
    
    .notification.warning {
      border-left: 4px solid #ff9800;
    }
    
    .notification-icon {
      margin-right: 15px;
      font-size: 20px;
    }
    
    .notification.success .notification-icon {
      color: #4caf50;
    }
    
    .notification.error .notification-icon {
      color: #f44336;
    }
    
    .notification.info .notification-icon {
      color: #2196f3;
    }
    
    .notification.warning .notification-icon {
      color: #ff9800;
    }
    
    .notification-content {
      flex-grow: 1;
    }
    
    .notification-title {
      font-weight: 600;
      margin-bottom: 5px;
    }
    
    .notification-message {
      font-size: 14px;
      color: #555;
    }
    
    .notification-close {
      font-size: 18px;
      color: #999;
      cursor: pointer;
      margin-left: 10px;
    }
    
    .notification-close:hover {
      color: #333;
    }
    
    .notification.show {
      opacity: 1;
      transform: translateY(0);
    }
    
    /* CSS cho các progress bar */
    .progress-bar {
      height: 20px;
      border-radius: 3px;
      text-align: center;
      line-height: 20px;
      font-size: 12px;
      font-weight: 600;
      color: white;
      transition: width 0.5s ease, background-color 0.5s ease;
    }
  `;
  document.head.appendChild(style);
}

/**
 * Hiển thị overlay loading
 * @param {string} message - Thông báo hiển thị khi loading
 */
function showLoading(message = "Đang tải...") {
  const loadingOverlay = document.querySelector(".loading-overlay");
  const loadingMessage = document.querySelector(".loading-message");

  if (loadingOverlay && loadingMessage) {
    loadingMessage.textContent = message;
    loadingOverlay.classList.add("active");
  }
}

/**
 * Ẩn overlay loading
 */
function hideLoading() {
  const loadingOverlay = document.querySelector(".loading-overlay");
  if (loadingOverlay) {
    loadingOverlay.classList.remove("active");
  }
}

/**
 * Hiển thị thông báo
 * @param {string} message - Nội dung thông báo
 * @param {string} type - Loại thông báo (success, error, info, warning)
 * @param {string} title - Tiêu đề thông báo (tùy chọn)
 */
function showNotification(message, type = "info", title = "") {
  // Xóa thông báo cũ nếu có
  const oldNotification = document.querySelector(".notification");
  if (oldNotification) {
    oldNotification.remove();
  }

  // Thiết lập tiêu đề mặc định theo loại thông báo
  if (!title) {
    switch (type) {
      case "success":
        title = "Thành công";
        break;
      case "error":
        title = "Lỗi";
        break;
      case "info":
        title = "Thông tin";
        break;
      case "warning":
        title = "Cảnh báo";
        break;
      default:
        title = "Thông báo";
    }
  }

  // Tạo biểu tượng theo loại thông báo
  let icon;
  switch (type) {
    case "success":
      icon = "fas fa-check-circle";
      break;
    case "error":
      icon = "fas fa-exclamation-circle";
      break;
    case "info":
      icon = "fas fa-info-circle";
      break;
    case "warning":
      icon = "fas fa-exclamation-triangle";
      break;
    default:
      icon = "fas fa-bell";
  }

  // Tạo phần tử thông báo
  const notification = document.createElement("div");
  notification.className = `notification ${type}`;
  notification.innerHTML = `
    <div class="notification-icon">
      <i class="${icon}"></i>
    </div>
    <div class="notification-content">
      <div class="notification-title">${title}</div>
      <div class="notification-message">${message}</div>
    </div>
    <div class="notification-close">&times;</div>
  `;

  document.body.appendChild(notification);

  // Hiển thị thông báo sau 10ms để có hiệu ứng
  setTimeout(() => {
    notification.classList.add("show");
  }, 10);

  // Thêm sự kiện đóng thông báo
  const closeBtn = notification.querySelector(".notification-close");
  if (closeBtn) {
    closeBtn.addEventListener("click", () => {
      notification.classList.remove("show");
      setTimeout(() => {
        notification.remove();
      }, 300);
    });
  }

  // Tự động đóng sau 5 giây
  setTimeout(() => {
    if (document.body.contains(notification)) {
      notification.classList.remove("show");
      setTimeout(() => {
        if (document.body.contains(notification)) {
          notification.remove();
        }
      }, 300);
    }
  }, 5000);
}

/**
 * Tạo hiệu ứng thông báo thời gian thực
 */
function initRealTimeNotifications() {
  // Giả lập nhận thông báo theo thời gian
  const notifications = [
    {
      message: "Có 3 tài khoản người dùng mới đăng ký",
      type: "info",
      delay: 30000, // 30 giây
    },
    {
      message: "Cảnh báo: Hệ thống email đang hoạt động với tải cao",
      type: "warning",
      delay: 60000, // 60 giây
    },
    {
      message: "Sao lưu tự động đã hoàn thành",
      type: "success",
      delay: 120000, // 120 giây
    },
  ];

  notifications.forEach((notification) => {
    setTimeout(() => {
      showNotification(notification.message, notification.type);
    }, notification.delay);
  });

  // Cập nhật số lượng thông báo chưa đọc
  setTimeout(() => {
    updateNotificationBadges();
  }, 45000); // 45 giây
}

/**
 * Cập nhật số lượng thông báo chưa đọc
 */
function updateNotificationBadges() {
  const bellBadge = document.querySelector(".fa-bell").nextElementSibling;
  const messageBadge =
    document.querySelector(".fa-envelope").nextElementSibling;

  if (bellBadge) {
    const currentCount = parseInt(bellBadge.textContent);
    bellBadge.textContent = currentCount + 2;

    // Hiệu ứng nhấp nháy cho badge
    bellBadge.style.animation = "none";
    setTimeout(() => {
      bellBadge.style.animation = "pulse 1s infinite";
    }, 10);
  }

  if (messageBadge) {
    const currentCount = parseInt(messageBadge.textContent);
    messageBadge.textContent = currentCount + 1;

    // Hiệu ứng nhấp nháy cho badge
    messageBadge.style.animation = "none";
    setTimeout(() => {
      messageBadge.style.animation = "pulse 1s infinite";
    }, 10);
  }

  // Thêm CSS cho hiệu ứng nhấp nháy
  const style = document.createElement("style");
  style.textContent = `
    @keyframes pulse {
      0% { transform: scale(1); }
      50% { transform: scale(1.2); }
      100% { transform: scale(1); }
    }
  `;
  document.head.appendChild(style);
}

/**
 * Xử lý chuyển đổi trang khi click vào các menu item
 */
function handleMenuNavigation() {
  const menuLinks = document.querySelectorAll(".menu-link");

  menuLinks.forEach((link) => {
    link.addEventListener("click", function (e) {
      e.preventDefault();

      const pageName = this.getAttribute("data-page");
      if (!pageName) return;

      // Kích hoạt trạng thái active cho menu item
      menuLinks.forEach((item) => item.classList.remove("active"));
      this.classList.add("active");

      showLoading(`Đang tải ${this.querySelector("span").textContent}...`);

      // Ẩn tất cả các trang
      const allPages = document.querySelectorAll(".page-content");
      allPages.forEach((page) => (page.style.display = "none"));

      setTimeout(() => {
        // Hiển thị trang được chọn
        const selectedPage = document.getElementById(`${pageName}-page`);
        if (selectedPage) {
          selectedPage.style.display = "block";
        }

        hideLoading();
      }, 500); // Giả lập thời gian tải trang
    });
  });

  // Xử lý cho toggle sidebar
  const toggleBtn = document.querySelector(".toggle-sidebar");
  const sidebar = document.querySelector(".sidebar");
  const mainContent = document.querySelector(".main-content");

  if (toggleBtn && sidebar && mainContent) {
    toggleBtn.addEventListener("click", function () {
      sidebar.classList.toggle("collapsed");
      mainContent.classList.toggle("expanded");
    });
  }
}

/**
 * Cài đặt giao diện người dùng tùy chọn
 */
function loadUserPreferences() {
  // Kiểm tra nếu có lưu trữ tùy chọn trong localStorage
  const savedTheme = localStorage.getItem("adminTheme");
  if (savedTheme) {
    document.body.classList.add(savedTheme);
  }

  // Thêm các tùy chọn khác theo nhu cầu
}

// Các hàm phụ trợ khác có thể được thêm vào sau này khi phát triển thêm các tính năng
