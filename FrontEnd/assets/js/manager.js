// DNA Testing Management System
// Main JavaScript File

document.addEventListener("DOMContentLoaded", function () {
  // Initialize all components
  initSidebar();
  initNavigation();
  initCharts();
  initModals();
  initTestHistory();
  initEventHandlers();

  // Move dashboard content to its container on load
  moveDashboardContent();
});

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
  // Hide all pages
  document.querySelectorAll(".page-content").forEach((page) => {
    page.style.display = "none";
  });

  // Show the selected page
  document.getElementById(pageId).style.display = "block";

  // Update active menu item
  document.querySelectorAll(".menu-link").forEach((link) => {
    link.classList.remove("active");
    if (link.getAttribute("data-page") === pageId.replace("-page", "")) {
      link.classList.add("active");
    }
  });
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

      alert(
        `Đã áp dụng bộ lọc! Loại xét nghiệm: ${
          testType || "Tất cả"
        }, Khoảng ngày: ${startDate || "Bất kỳ"} đến ${
          endDate || "Bất kỳ"
        }, Tìm kiếm: ${searchTerm || "Không"}`
      );
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
      alert("Đang xuất dữ liệu phản hồi ra file CSV...");
      // In a real application, you would generate and download a CSV file here
    });
  }
}

// Filter Feedback
function filterFeedback() {
  // In a real application, you would fetch filtered data from the server
  const status = document.getElementById("feedbackStatusFilter").value;
  const rating = document.getElementById("ratingFilter").value;

  alert(
    `Đã áp dụng bộ lọc phản hồi! Trạng thái: ${status || "Tất cả"}, Đánh giá: ${
      rating || "Tất cả"
    }`
  );
}

// Expose functions that need to be called from HTML
window.viewTestResults = viewTestResults;
window.closeTestResultsModal = closeTestResultsModal;
