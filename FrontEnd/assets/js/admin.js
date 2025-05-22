/**
 * Admin Dashboard JavaScript
 * File: admin.js
 * Description: Xử lý các chức năng đặc thù của trang Admin
 */

/**
 * Tải trước các tài nguyên cần thiết cho trang
 */
function preloadResources() {
  return new Promise((resolve) => {
    console.log("Đang tải trước tài nguyên...");

    // Giả lập tải tài nguyên
    setTimeout(() => {
      console.log("Đã tải xong tài nguyên");
      resolve();
    }, 300);
  });
}

document.addEventListener("DOMContentLoaded", function () {
  // Hiển thị loading ban đầu
  showLoading("Đang khởi tạo hệ thống...");

  // Khởi tạo hiệu ứng loading cho trang khi chuyển menu (phải khởi tạo trước)
  initPageLoading();

  // Tải trước các tài nguyên cần thiết
  preloadResources().then(() => {
    // Khởi tạo biểu đồ thống kê người dùng
    initUserStatsChart(); // Khởi tạo sự kiện cho các nút trong trang Admin
    initAdminEvents();

    // Xử lý chuyển đổi trang khi click vào các menu item
    handleMenuNavigation();

    // Cài đặt giao diện người dùng tùy chọn (nếu có)
    loadUserPreferences();

    // Ẩn loading ban đầu
    hideLoading();
  });

  // Đảm bảo loading overlay không bị treo khi người dùng F5
  window.addEventListener("beforeunload", function () {
    hideLoading();
  });

  // Thêm phím tắt để tắt loading overlay nếu bị treo
  document.addEventListener("keydown", function (e) {
    // ESC key
    if (e.key === "Escape" || e.keyCode === 27) {
      hideLoading();
      console.log("Đã ẩn loading bằng phím ESC");
      showNotification("Đã hủy tải trang", "info");
    }
  });

  // Thêm sự kiện click cho overlay để có thể tắt nó nếu bị treo
  const loadingOverlay = document.querySelector(".loading-overlay");
  if (loadingOverlay) {
    loadingOverlay.addEventListener("click", function (e) {
      // Chỉ cho phép click vào phần nền để tắt, không phải vào spinner
      if (e.target === loadingOverlay) {
        hideLoading();
        console.log("Đã ẩn loading bằng cách click vào overlay");
        showNotification("Đã hủy tải trang", "info");
      }
    });
  }

  // Double-check để đảm bảo loading screen được ẩn sau khi trang đã tải xong
  setTimeout(hideLoading, 2000);
});

/**
 * Khởi tạo biểu đồ thống kê người dùng
 */
function initUserStatsChart() {
  try {
    const ctx = document.getElementById("userStatsChart");
    if (!ctx) {
      console.warn("Không tìm thấy phần tử userStatsChart");
      return;
    }

    // Kiểm tra xem thư viện Chart có tồn tại không
    if (typeof Chart === "undefined") {
      console.error("Thư viện Chart.js chưa được tải");
      showNotification("Có lỗi khi tải biểu đồ", "error");
      return;
    }

    // Hủy biểu đồ cũ nếu đã tồn tại
    if (window.userStatsChart instanceof Chart) {
      window.userStatsChart.destroy();
    }

    window.userStatsChart = new Chart(ctx, {
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
    const timeRangeSelect = document.getElementById("userStatsTimeRange");
    if (timeRangeSelect) {
      timeRangeSelect.addEventListener("change", function () {
        updateUserStatsChart(this.value, window.userStatsChart);
      });
    } else {
      console.warn("Không tìm thấy phần tử userStatsTimeRange");
    }
  } catch (error) {
    console.error("Lỗi khi khởi tạo biểu đồ:", error);
    showNotification("Có lỗi khi tạo biểu đồ thống kê", "error");
  }
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

// --- Tìm kiếm và lọc danh sách admin user ---
document.addEventListener("DOMContentLoaded", function () {
  const searchInput = document.getElementById("adminUserSearch");
  const statusFilter = document.getElementById("adminUserStatusFilter");
  const userTable = document.querySelector("#user-management-page table tbody");

  if (searchInput && statusFilter && userTable) {
    function filterAdminUsers() {
      const search = searchInput.value.trim().toLowerCase();
      const status = statusFilter.value;
      const rows = userTable.querySelectorAll("tr");
      rows.forEach((row) => {
        const name = row.children[1].textContent.toLowerCase();
        const email = row.children[2].textContent.toLowerCase();
        const statusText = row.children[5].textContent.trim();
        let match = true;
        if (search && !(name.includes(search) || email.includes(search)))
          match = false;
        if (status) {
          if (status === "active" && statusText !== "Đang hoạt động")
            match = false;
          if (status === "inactive" && statusText !== "Không hoạt động")
            match = false;
          if (status === "suspended" && statusText !== "Tạm khóa")
            match = false;
        }
        row.style.display = match ? "" : "none";
      });
    }
    searchInput.addEventListener("input", filterAdminUsers);
    statusFilter.addEventListener("change", filterAdminUsers);
  }

  // --- Tìm kiếm và lọc danh sách nhân viên ---
  const staffSearchInput = document.getElementById("staffSearch");
  const staffStatusFilter = document.getElementById("staffStatusFilter");
  const staffTable = document.querySelector(
    "#staff-management-page table tbody"
  );

  if (staffSearchInput && staffStatusFilter && staffTable) {
    function filterStaff() {
      const search = staffSearchInput.value.trim().toLowerCase();
      const status = staffStatusFilter.value;
      const rows = staffTable.querySelectorAll("tr");
      rows.forEach((row) => {
        const name = row.children[1].textContent.toLowerCase();
        const email = row.children[2].textContent.toLowerCase();
        const statusText = row.children[6].textContent.trim();
        let match = true;
        if (search && !(name.includes(search) || email.includes(search)))
          match = false;
        if (status) {
          if (status === "active" && statusText !== "Đang làm việc")
            match = false;
          if (status === "leave" && statusText !== "Nghỉ phép") match = false;
          if (status === "quit" && statusText !== "Đã nghỉ việc") match = false;
        }
        row.style.display = match ? "" : "none";
      });
    }
    staffSearchInput.addEventListener("input", filterStaff);
    staffStatusFilter.addEventListener("change", filterStaff);
  }
});

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
  // Xóa loading overlay cũ nếu có
  const existingOverlay = document.querySelector(".loading-overlay");
  if (existingOverlay) {
    existingOverlay.remove();
  }

  // Tạo phần tử loading mới
  const loadingOverlay = document.createElement("div");
  loadingOverlay.className = "loading-overlay";
  loadingOverlay.id = "loadingOverlay";
  loadingOverlay.innerHTML = `
    <div class="loading-spinner"></div>
    <div class="loading-message">Đang tải...</div>
  `;
  document.body.appendChild(loadingOverlay); // CSS đã được chuyển vào file common.css
}

/**
 * Hiển thị overlay loading
 * @param {string} message - Thông báo hiển thị khi loading
 */
function showLoading(message = "Đang tải...") {
  const loadingOverlay = document.querySelector(".loading-overlay");
  const loadingMessage = document.querySelector(".loading-message");

  if (loadingOverlay && loadingMessage) {
    // Reset các style có thể đã bị thay đổi
    loadingOverlay.style.opacity = "";
    loadingOverlay.style.visibility = "";

    // Cập nhật nội dung
    loadingMessage.textContent = message;
    loadingOverlay.classList.add("active");

    // Đặt timeout để tự động ẩn loading sau thời gian chờ
    clearTimeout(window.loadingTimeout);
    window.loadingTimeout = setTimeout(() => {
      if (loadingOverlay.classList.contains("active")) {
        console.warn("Loading timeout - auto hiding");
        hideLoading();
        showNotification("Đã hoàn thành tải trang", "info");
      }
    }, 5000); // Tự động ẩn sau 5 giây nếu bị treo

    console.log("Hiển thị loading overlay:", message);
  } else {
    console.error("Không tìm thấy loading overlay");
  }
}

/**
 * Ẩn overlay loading
 */
function hideLoading() {
  const loadingOverlay = document.querySelector(".loading-overlay");
  if (loadingOverlay) {
    loadingOverlay.classList.remove("active");

    // Đảm bảo loading không kẹt vì vấn đề CSS
    loadingOverlay.style.opacity = "0";
    loadingOverlay.style.visibility = "hidden";

    console.log("Đã ẩn loading overlay");
  }
}

/**
 * Hiển thị thông báo
 * @param {string} message - Nội dung thông báo
 * @param {string} type - Loại thông báo (success, error, info, warning)
 * @param {string} title - Tiêu đề thông báo (tùy chọn)
 * @param {Object} position - Vị trí hiển thị thông báo (tùy chọn)
 */
function showNotification(
  message,
  type = "info",
  title = "",
  position = { bottom: "20px", right: "20px" }
) {
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
  // Áp dụng vị trí
  if (position) {
    if (position.top) notification.style.top = position.top;
    if (position.bottom) notification.style.bottom = position.bottom;
    if (position.left) notification.style.left = position.left;
    if (position.right) notification.style.right = position.right;
  }

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
  const notificationTimeout = setTimeout(() => {
    if (document.body.contains(notification)) {
      notification.classList.remove("show");
      setTimeout(() => {
        if (document.body.contains(notification)) {
          notification.remove();
        }
      }, 300);
    }
  }, 5000);

  // Tạm dừng đếm ngược khi hover vào thông báo
  notification.addEventListener("mouseenter", () => {
    clearTimeout(notificationTimeout);
  });

  // Tiếp tục đếm ngược khi không hover nữa
  notification.addEventListener("mouseleave", () => {
    setTimeout(() => {
      if (document.body.contains(notification)) {
        notification.classList.remove("show");
        setTimeout(() => {
          if (document.body.contains(notification)) {
            notification.remove();
          }
        }, 300);
      }
    }, 2000); // Tự động đóng sau 2 giây khi rời chuột
  });
}

/**
 * Empty function to maintain compatibility with existing code
 */
function initRealTimeNotifications() {
  // Function removed as notifications feature has been disabled
  console.log("Notification functionality has been disabled");
}

/**
 * Empty function to maintain compatibility with existing code
 */
function updateNotificationBadges() {
  // Function removed as notifications feature has been disabled
}

/**
 * Empty function to maintain compatibility with existing code
 */
function setupNotificationSystem() {
  // Function removed as notifications feature has been disabled
}

/**
 * Empty function to maintain compatibility with existing code
 */
function addNotificationToDropdown({
  userName,
  content,
  time,
  isNew,
  isSystem,
}) {
  // Do nothing - notifications feature is disabled
}

/**
 * Xử lý chuyển đổi trang khi click vào các menu item
 */
function handleMenuNavigation() {
  const menuLinks = document.querySelectorAll(".menu-link");
  const pages = document.querySelectorAll(".page-content");
  const dashboard = document.querySelector(".dashboard");

  // Khởi tạo biến để lưu trạng thái trang hiện tại
  let currentPage = "dashboard";

  menuLinks.forEach((link) => {
    link.addEventListener("click", function (e) {
      e.preventDefault();

      // Xóa class active từ tất cả các liên kết menu
      menuLinks.forEach((item) => {
        item.classList.remove("active");
      });

      // Thêm class active cho liên kết được click
      this.classList.add("active");

      const pageId = this.getAttribute("data-page");
      if (pageId) {
        showAdminPage(pageId);
      }
    });
  });

  // Thêm sự kiện cho các nút xem tất cả log
  const viewAllLogsBtn = document.getElementById("viewAllLogs");
  if (viewAllLogsBtn) {
    viewAllLogsBtn.addEventListener("click", function () {
      showAdminPage("system-logs");
    });
  }
}

/**
 * Hiển thị trang dựa trên ID
 * @param {string} pageId - ID của trang cần hiển thị (không bao gồm suffix "-page")
 */
function showAdminPage(pageId) {
  // Ẩn tất cả các trang
  const allPages = document.querySelectorAll(".page-content");
  const dashboard = document.querySelector(".dashboard");

  // Ẩn dashboard và tất cả các trang
  if (dashboard) {
    dashboard.style.display = "none";
  }

  allPages.forEach((page) => {
    page.style.display = "none";
  });

  // Hiển thị trang yêu cầu
  const targetPageId = pageId === "dashboard" ? "dashboard" : pageId + "-page";

  if (pageId === "dashboard") {
    if (dashboard) {
      dashboard.style.display = "block";
    }
  } else {
    const targetPage = document.getElementById(targetPageId);
    if (targetPage) {
      targetPage.style.display = "block";
    } else {
      console.error("Không tìm thấy trang:", targetPageId);
      // Fallback to dashboard if page not found
      if (dashboard) {
        dashboard.style.display = "block";
      }
    }
  }

  // Cập nhật URL (tùy chọn)
  history.pushState({ page: pageId }, "", "#" + pageId);
}
