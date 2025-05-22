// DNA Testing Management System
// Main JavaScript File

document.addEventListener("DOMContentLoaded", function () {
  // Initialize loading overlay
  initPageLoading(); // Initialize all components
  initSidebar();
  initNavigation();
  initCharts();
  initModals();
  initTestHistory();
  initEventHandlers();
  // Move dashboard content to its container on load
  moveDashboardContent();
});

/**
 * Khởi tạo hiệu ứng loading cho trang
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
  document.body.appendChild(loadingOverlay);

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
  loadingOverlay.addEventListener("click", function (e) {
    // Chỉ cho phép click vào phần nền để tắt, không phải vào spinner
    if (e.target === loadingOverlay) {
      hideLoading();
      console.log("Đã ẩn loading bằng cách click vào overlay");
      showNotification("Đã hủy tải trang", "info");
    }
  });

  // Double-check để đảm bảo loading screen được ẩn sau khi trang đã tải xong
  setTimeout(hideLoading, 2000);
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

// Initialize Sidebar
function initSidebar() {
  document
    .querySelector(".toggle-sidebar")
    .addEventListener("click", function () {
      document.querySelector(".sidebar").classList.toggle("active");
    });
}

// Initialize Navigation
function initNavigation() {
  // Page navigation
  document.querySelectorAll(".menu-link").forEach((link) => {
    link.addEventListener("click", function (e) {
      e.preventDefault();
      const pageId = this.getAttribute("data-page");
      if (pageId) {
        showPage(pageId + "-page");
      }
    });
  });

  // View All Feedback button
  const viewAllFeedbackBtn = document.getElementById("viewAllFeedback");
  if (viewAllFeedbackBtn) {
    viewAllFeedbackBtn.addEventListener("click", function () {
      showPage("feedback-management-page");
    });
  }
}

// Show a specific page
function showPage(pageId) {
  // Hiển thị hiệu ứng loading
  const pageTitle = document.querySelector(
    `[data-page="${pageId.replace("-page", "")}"] span`
  );
  showLoading(`Đang tải ${pageTitle ? pageTitle.textContent : "trang"}...`);

  try {
    // Hide all pages
    document.querySelectorAll(".page-content").forEach((page) => {
      page.style.display = "none";
    });

    // Get the selected page
    const selectedPage = document.getElementById(pageId);

    if (selectedPage) {
      // Thêm hiệu ứng fade in
      selectedPage.style.opacity = "0";
      selectedPage.style.display = "block";

      // Hiệu ứng fade in
      setTimeout(() => {
        selectedPage.style.opacity = "1";
        selectedPage.style.transition = "opacity 0.3s ease";
      }, 10);
    }

    // Update active menu item
    document.querySelectorAll(".menu-link").forEach((link) => {
      link.classList.remove("active");
      if (link.getAttribute("data-page") === pageId.replace("-page", "")) {
        link.classList.add("active");
      }
    });

    // Ẩn loading sau 300ms
    setTimeout(() => {
      hideLoading();
    }, 300);
  } catch (error) {
    console.error("Lỗi khi chuyển trang:", error);
    hideLoading(); // Đảm bảo ẩn loading nếu có lỗi
    showNotification("Có lỗi khi chuyển trang", "error");
  }
}

// Move dashboard content to its container
function moveDashboardContent() {
  const dashboardContent = document.querySelector(".dashboard");
  const dashboardContainer = document.getElementById("dashboard-page");

  if (dashboardContent && dashboardContainer) {
    dashboardContainer.appendChild(dashboardContent);
  }
}

// Initialize Charts
function initCharts() {
  // Patient Statistics Chart
  const patientStatsCtx = document.getElementById("patientStatsChart");
  if (patientStatsCtx) {
    new Chart(patientStatsCtx.getContext("2d"), {
      type: "line",
      data: {
        labels: ["Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5"],
        datasets: [
          {
            label: "Bệnh nhân mới",
            data: [65, 78, 90, 85, 97],
            borderColor: "#1a73e8",
            backgroundColor: "rgba(26, 115, 232, 0.1)",
            tension: 0.4,
            fill: true,
          },
          {
            label: "Xét nghiệm đã thực hiện",
            data: [85, 100, 115, 110, 120],
            borderColor: "#4caf50",
            backgroundColor: "rgba(76, 175, 80, 0.1)",
            tension: 0.4,
            fill: true,
          },
        ],
      },
      options: {
        responsive: true,
        plugins: {
          legend: {
            position: "top",
          },
        },
        scales: {
          y: {
            beginAtZero: true,
          },
        },
      },
    });
  }

  // Rating Distribution Chart (on feedback page)
  const ratingDistributionCtx = document.getElementById(
    "ratingDistributionChart"
  );
  if (ratingDistributionCtx) {
    new Chart(ratingDistributionCtx.getContext("2d"), {
      type: "bar",
      data: {
        labels: ["5 Sao", "4 Sao", "3 Sao", "2 Sao", "1 Sao"],
        datasets: [
          {
            label: "Số lượng đánh giá",
            data: [250, 120, 40, 12, 5],
            backgroundColor: [
              "#4caf50",
              "#8bc34a",
              "#ffeb3b",
              "#ff9800",
              "#f44336",
            ],
            borderWidth: 0,
          },
        ],
      },
      options: {
        responsive: true,
        plugins: {
          legend: {
            display: false,
          },
        },
        scales: {
          y: {
            beginAtZero: true,
          },
        },
      },
    });
  }

  // Feedback Trend Chart
  const feedbackTrendCtx = document.getElementById("feedbackTrendChart");
  if (feedbackTrendCtx) {
    new Chart(feedbackTrendCtx.getContext("2d"), {
      type: "line",
      data: {
        labels: ["Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5"],
        datasets: [
          {
            label: "Đánh giá trung bình",
            data: [4.5, 4.6, 4.5, 4.7, 4.6],
            borderColor: "#ff9800",
            backgroundColor: "rgba(255, 152, 0, 0.1)",
            tension: 0.4,
            fill: true,
            yAxisID: "y",
          },
          {
            label: "Số lượng đánh giá",
            data: [45, 52, 60, 65, 72],
            borderColor: "#1a73e8",
            borderDash: [5, 5],
            fill: false,
            tension: 0.4,
            yAxisID: "y1",
          },
        ],
      },
      options: {
        responsive: true,
        plugins: {
          legend: {
            position: "top",
          },
        },
        scales: {
          y: {
            type: "linear",
            display: true,
            position: "left",
            min: 1,
            max: 5,
            title: {
              display: true,
              text: "Đánh giá",
            },
          },
          y1: {
            type: "linear",
            display: true,
            position: "right",
            min: 0,
            title: {
              display: true,
              text: "Số lượng đánh giá",
            },
            grid: {
              drawOnChartArea: false,
            },
          },
        },
      },
    });
  }
}

// Initialize Modals
function initModals() {
  // Close modal when clicking on the X
  document.querySelectorAll(".close-modal").forEach((closeBtn) => {
    closeBtn.addEventListener("click", function () {
      this.closest(".modal").style.display = "none";
    });
  });

  // Close modal when clicking outside
  window.addEventListener("click", function (event) {
    document.querySelectorAll(".modal").forEach((modal) => {
      if (event.target == modal) {
        modal.style.display = "none";
      }
    });
  });
}

// Initialize Test History Data
function initTestHistory() {
  // Sample data - In a real application, this would come from an API
  const testData = [
    {
      id: "XN-0001",
      patientName: "Nguyễn Văn An",
      patientId: "BN-0001",
      testType: "Xét nghiệm ADN xác định huyết thống",
      date: "18/05/2025",
      doctor: "BS. Tuấn",
      status: "Hoàn thành",
    },
    {
      id: "XN-0002",
      patientName: "Trần Thị Bình",
      patientId: "BN-0010",
      testType: "Sàng lọc di truyền",
      date: "17/05/2025",
      doctor: "BS. Hương",
      status: "Đang xử lý",
    },
    {
      id: "XN-0003",
      patientName: "Lê Văn Cường",
      patientId: "BN-0003",
      testType: "Xét nghiệm ADN tổ tiên",
      date: "16/05/2025",
      doctor: "BS. Minh",
      status: "Hoàn thành",
    },
    {
      id: "XN-0004",
      patientName: "Phạm Thị Dung",
      patientId: "BN-0008",
      testType: "Xét nghiệm dự đoán di truyền",
      date: "15/05/2025",
      doctor: "BS. Thành",
      status: "Đợi mẫu",
    },
    {
      id: "XN-0005",
      patientName: "Hoàng Văn Em",
      patientId: "BN-0005",
      testType: "Xét nghiệm ADN xác định huyết thống",
      date: "14/05/2025",
      doctor: "BS. Linh",
      status: "Hoàn thành",
    },
  ];

  loadTestHistoryData(testData);
}

// Load Test History Data
function loadTestHistoryData(testData) {
  const testTable = document.getElementById("testHistoryTable");
  if (!testTable) return;

  // Clear existing data
  testTable.innerHTML = "";

  // Add each test to the table
  testData.forEach((test) => {
    const row = document.createElement("tr");

    // Define status class based on status
    let statusClass = "";
    switch (test.status) {
      case "Hoàn thành":
        statusClass = "background-color: #4caf50; color: white;";
        break;
      case "Đang xử lý":
        statusClass = "background-color: #ff9800; color: white;";
        break;
      case "Đợi mẫu":
        statusClass = "background-color: #1e88e5; color: white;";
        break;
      case "Đã hủy":
        statusClass = "background-color: #f44336; color: white;";
        break;
      default:
        statusClass = "background-color: #f44336; color: white;";
    }

    row.innerHTML = `
            <td>${test.id}</td>
            <td>${test.patientName} (${test.patientId})</td>
            <td>${test.testType}</td>
            <td>${test.date}</td>
            <td>${test.doctor}</td>
            <td>
                <span class="badge" style="${statusClass} padding: 3px 8px; border-radius: 3px;">
                    ${test.status}
                </span>
            </td>
            <td>
                <a href="#" class="action-btn view-btn" onclick="viewTestResults('${test.id}')">Xem kết quả</a>
                <a href="#" class="action-btn edit-btn">In</a>
            </td>
        `;

    testTable.appendChild(row);
  });
}

// View Test Results
function viewTestResults(testId) {
  document.getElementById("testResultsModal").style.display = "block";
  document.getElementById("testId").textContent = testId;

  // In a real application, you would fetch the test results data from the server
  // For this example, we're using static data

  // You can customize this function to load different data based on testId
  if (testId === "XN-0001") {
    document.getElementById("patientName").textContent = "Nguyễn Văn An";
    document.getElementById("patientId").textContent = "BN-0001";
    document.getElementById("patientAge").textContent = "35";
    document.getElementById("patientGender").textContent = "Nam";
    document.getElementById("testDate").textContent = "18/05/2025";
    document.getElementById("testType").textContent =
      "Xét nghiệm ADN xác định huyết thống";
    document.getElementById("testDoctor").textContent = "BS. Tuấn";
    document.getElementById("doctorNotes").textContent =
      "Kết quả xét nghiệm cho thấy xác suất 99,9999% là quan hệ huyết thống. Người cha được xét nghiệm không thể bị loại trừ là cha đẻ của đứa trẻ.";
  } else if (testId === "XN-0002") {
    document.getElementById("patientName").textContent = "Trần Thị Bình";
    document.getElementById("patientId").textContent = "BN-0010";
    document.getElementById("patientAge").textContent = "42";
    document.getElementById("patientGender").textContent = "Nữ";
    document.getElementById("testDate").textContent = "17/05/2025";
    document.getElementById("testType").textContent = "Sàng lọc di truyền";
    document.getElementById("testDoctor").textContent = "BS. Hương";
    document.getElementById("doctorNotes").textContent =
      "Kết quả sàng lọc di truyền đang chờ xử lý. Mẫu đang được phân tích trong phòng thí nghiệm của chúng tôi.";
  } else if (testId === "XN-0004") {
    document.getElementById("patientName").textContent = "Phạm Thị Dung";
    document.getElementById("patientId").textContent = "BN-0008";
    document.getElementById("patientAge").textContent = "29";
    document.getElementById("patientGender").textContent = "Nữ";
    document.getElementById("testDate").textContent = "15/05/2025";
    document.getElementById("testType").textContent =
      "Xét nghiệm dự đoán di truyền";
    document.getElementById("testDoctor").textContent = "BS. Thành";
    document.getElementById("doctorNotes").textContent =
      "Xét nghiệm dự đoán di truyền cho thấy nguy cơ bệnh tim mạch cao hơn một chút. Khuyến nghị điều chỉnh lối sống và kiểm tra sức khỏe định kỳ.";
  }
}

// Close Test Results Modal
function closeTestResultsModal() {
  document.getElementById("testResultsModal").style.display = "none";
}

// Initialize Event Handlers
function initEventHandlers() {
  // Apply Filters Button
  const applyFiltersBtn = document.getElementById("applyFilters");
  if (applyFiltersBtn) {
    applyFiltersBtn.addEventListener("click", function () {
      // In a real application, you would fetch filtered data from the server
      const testType = document.getElementById("testTypeFilter").value;
      const startDate = document.getElementById("startDate").value;
      const endDate = document.getElementById("endDate").value;
      const searchTerm = document.getElementById("testHistorySearch").value;
      showLoading("Đang áp dụng bộ lọc...");

      // Giả lập thời gian tải dữ liệu
      setTimeout(() => {
        hideLoading();
        showNotification(
          `Đã áp dụng bộ lọc! Loại xét nghiệm: ${
            testType || "Tất cả"
          }, Khoảng ngày: ${startDate || "Bất kỳ"} đến ${
            endDate || "Bất kỳ"
          }, Tìm kiếm: ${searchTerm || "Không"}`,
          "success"
        );
      }, 500);
    });
  }

  // Feedback Status Filter
  const feedbackStatusFilter = document.getElementById("feedbackStatusFilter");
  if (feedbackStatusFilter) {
    feedbackStatusFilter.addEventListener("change", filterFeedback);
  }

  // Rating Filter
  const ratingFilter = document.getElementById("ratingFilter");
  if (ratingFilter) {
    ratingFilter.addEventListener("change", filterFeedback);
  }
  // Export Feedback Button
  const exportFeedbackBtn = document.getElementById("exportFeedback");
  if (exportFeedbackBtn) {
    exportFeedbackBtn.addEventListener("click", function () {
      showLoading("Đang xuất dữ liệu phản hồi ra file CSV...");

      // Giả lập thời gian xử lý
      setTimeout(() => {
        hideLoading();
        showNotification("Đã xuất dữ liệu phản hồi thành công", "success");
      }, 800);
      // In a real application, you would generate and download a CSV file here
    });
  }
}

// Filter Feedback
function filterFeedback() {
  // In a real application, you would fetch filtered data from the server
  const status = document.getElementById("feedbackStatusFilter").value;
  const rating = document.getElementById("ratingFilter").value;

  showLoading("Đang lọc phản hồi...");

  // Giả lập thời gian tải dữ liệu
  setTimeout(() => {
    hideLoading();
    showNotification(
      `Đã áp dụng bộ lọc phản hồi! Trạng thái: ${
        status || "Tất cả"
      }, Đánh giá: ${rating || "Tất cả"}`,
      "info"
    );
  }, 300);
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
 * ========== NOTIFICATION SYSTEM ==========
 * Xử lý hiển thị dropdown thông báo giống Facebook
 */

// Khởi tạo hệ thống thông báo khi trang đã tải xong
function initNotificationSystem() {
  // Lấy các phần tử DOM liên quan đến thông báo
  const notificationBell = document.getElementById("notificationBell");
  const notificationDropdown = document.getElementById("notificationDropdown");

  // Thêm sự kiện click cho biểu tượng thông báo
  if (notificationBell && notificationDropdown) {
    notificationBell.addEventListener("click", function (e) {
      e.stopPropagation(); // Ngăn sự kiện lan ra ngoài
      toggleDropdown(notificationDropdown);
    });
  }

  // Xử lý đóng dropdown khi click ra ngoài
  document.addEventListener("click", function (e) {
    if (
      notificationDropdown &&
      !notificationDropdown.contains(e.target) &&
      !notificationBell.contains(e.target)
    ) {
      notificationDropdown.classList.remove("active");
    }
  });

  // Thêm sự kiện cho các tab trong notification dropdown
  const notificationTabs = document.querySelectorAll(".notification-tab");
  if (notificationTabs && notificationTabs.length > 0) {
    notificationTabs.forEach((tab) => {
      tab.addEventListener("click", function () {
        // Xóa active từ tất cả các tab
        notificationTabs.forEach((t) => t.classList.remove("active"));
        // Thêm active cho tab được click
        this.classList.add("active");

        // Lọc thông báo dựa trên tab (trong thực tế, sẽ cần chức năng lọc)
        if (this.textContent === "Chưa đọc") {
          // Chỉ hiển thị thông báo chưa đọc
          showUnreadNotifications();
        } else {
          // Hiển thị tất cả thông báo
          showAllNotifications();
        }
      });
    });
  }

  // Thêm sự kiện click cho notification-item
  const notificationItems = document.querySelectorAll(".notification-item");
  if (notificationItems && notificationItems.length > 0) {
    notificationItems.forEach((item) => {
      item.addEventListener("click", function () {
        // Đánh dấu là đã đọc
        this.classList.remove("new");

        // Xóa dấu chấm màu xanh
        const statusDot = this.querySelector(".status-dot");
        if (statusDot) {
          statusDot.style.display = "none";
        }

        // Cập nhật số lượng thông báo chưa đọc
        updateNotificationCount();
      });
    });
  }
}

// Hàm chuyển đổi trạng thái hiển thị của dropdown
function toggleDropdown(dropdown) {
  if (dropdown.classList.contains("active")) {
    dropdown.classList.remove("active");
  } else {
    dropdown.classList.add("active");
  }
}

/**
 * Hàm tạo avatar từ tên người dùng
 * Lấy chữ cái đầu tiên của tên người dùng làm avatar
 * @param {string} fullName - Tên đầy đủ của người dùng
 * @returns {string} - HTML cho avatar người dùng
 */
function createUserAvatar(fullName) {
  if (!fullName)
    return '<div class="notification-item-avatar user"><span>U</span></div>';

  // Lấy chữ cái đầu tiên của tên
  const initial = fullName.charAt(0).toUpperCase();
  return `<div class="notification-item-avatar user"><span>${initial}</span></div>`;
}

// Hàm chỉ hiển thị thông báo chưa đọc
function showUnreadNotifications() {
  const allNotifications = document.querySelectorAll(".notification-item");
  if (allNotifications && allNotifications.length > 0) {
    allNotifications.forEach((item) => {
      if (item.classList.contains("new")) {
        item.style.display = "flex";
      } else {
        item.style.display = "none";
      }
    });
  }
}

// Hàm hiển thị tất cả thông báo
function showAllNotifications() {
  const allNotifications = document.querySelectorAll(".notification-item");
  if (allNotifications && allNotifications.length > 0) {
    allNotifications.forEach((item) => {
      item.style.display = "flex";
    });
  }
}

// Hàm cập nhật số lượng thông báo chưa đọc
function updateNotificationCount() {
  // Đếm số lượng thông báo chưa đọc
  const unreadNotifications = document.querySelectorAll(
    ".notification-item.new"
  ).length;

  // Cập nhật badge
  const notificationBadge = document.querySelector(
    "#notificationBell + .notification-badge"
  );

  if (notificationBadge) {
    notificationBadge.textContent = unreadNotifications;
    if (unreadNotifications === 0) {
      notificationBadge.style.display = "none";
    } else {
      notificationBadge.style.display = "flex";
    }
  }
}

/**
 * Thêm thông báo vào dropdown
 * @param {Object} notificationData - Dữ liệu thông báo
 * @param {string} notificationData.userName - Tên người dùng
 * @param {string} notificationData.content - Nội dung thông báo
 * @param {string} notificationData.time - Thời gian thông báo
 * @param {boolean} notificationData.isNew - Có phải là thông báo mới không
 * @param {boolean} notificationData.isSystem - Có phải là thông báo hệ thống không
 */
function addNotificationToDropdown({
  userName,
  content,
  time,
  isNew,
  isSystem,
}) {
  const notificationDropdown = document.getElementById("notificationDropdown");
  if (!notificationDropdown) return;

  // Tạo phần tử thông báo mới
  const notificationItem = document.createElement("div");
  notificationItem.className = "notification-item";
  if (isNew) {
    notificationItem.classList.add("new");
  }

  // Xác định loại avatar (người dùng hoặc hệ thống)
  let avatarContent = "";
  if (isSystem) {
    const iconClass = content.toLowerCase().includes("cảnh báo")
      ? "fa-exclamation-triangle"
      : "fa-server";
    avatarContent = `<div class="notification-item-avatar system">
      <i class="fas ${iconClass}"></i>
    </div>`;
    // Nếu là thông báo hệ thống, userName sẽ là loại thông báo
  } else {
    avatarContent = `<div class="notification-item-avatar user">
      <span>${userName.charAt(0).toUpperCase()}</span>
    </div>`;
  }

  notificationItem.innerHTML = `
    ${avatarContent}
    <div class="notification-item-content">
      <div class="notification-item-user">${userName}</div>
      <div class="notification-item-text">${content}</div>
      <div class="notification-item-time">${time}</div>
    </div>
    ${isNew ? '<div class="status-dot"></div>' : ""}
  `;

  // Thêm thông báo mới vào đầu danh sách
  notificationDropdown.insertBefore(
    notificationItem,
    notificationDropdown.querySelector(".notification-item")
  );

  // Cập nhật số lượng thông báo chưa đọc
  updateNotificationCount();
}

/**
 * Tạo hiệu ứng thông báo thời gian thực
 */
function initRealTimeNotifications() {
  // Thiết lập sự kiện để thêm thông báo mới
  updateNotificationCount();

  // Kiểm tra xem đã khởi tạo thông báo chưa để tránh trùng lặp
  if (window.notificationsInitialized) {
    return;
  }
  window.notificationsInitialized = true;

  console.log("Initializing real-time notifications in manager.js");

  // Thêm thông báo người dùng demo trễ 20 giây
  setTimeout(() => {
    addNotificationToDropdown({
      userName: "Nguyễn Văn F",
      content: "đã đặt lịch xét nghiệm mới",
      time: "Vừa xong",
      isNew: true,
      isSystem: false,
    });
    showNotification("Có thông báo mới từ người dùng", "info");
  }, 25000);

  // Thêm thông báo người dùng demo trễ 45 giây
  setTimeout(() => {
    addNotificationToDropdown({
      userName: "Hệ thống",
      content: "Cập nhật báo cáo xét nghiệm hàng tuần đã hoàn thành",
      time: "Vừa xong",
      isNew: true,
      isSystem: true,
    });
    showNotification("Báo cáo đã được cập nhật", "success");
  }, 45000);
}

// Expose functions that need to be called from HTML
window.viewTestResults = viewTestResults;
window.closeTestResultsModal = closeTestResultsModal;

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
function initNotificationSystem() {
  // Function removed as notifications feature has been disabled
}

/**
 * Empty function to maintain compatibility with existing code
 */
function showNotification() {
  // Function removed as notifications feature has been disabled
}
