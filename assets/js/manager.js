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
        labels: ["Jan", "Feb", "Mar", "Apr", "May"],
        datasets: [
          {
            label: "New Patients",
            data: [65, 78, 90, 85, 97],
            borderColor: "#1a73e8",
            backgroundColor: "rgba(26, 115, 232, 0.1)",
            tension: 0.4,
            fill: true,
          },
          {
            label: "Tests Performed",
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
        labels: ["5 Stars", "4 Stars", "3 Stars", "2 Stars", "1 Star"],
        datasets: [
          {
            label: "Number of Ratings",
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
        labels: ["Jan", "Feb", "Mar", "Apr", "May"],
        datasets: [
          {
            label: "Avg. Rating",
            data: [4.5, 4.6, 4.5, 4.7, 4.6],
            borderColor: "#ff9800",
            backgroundColor: "rgba(255, 152, 0, 0.1)",
            tension: 0.4,
            fill: true,
            yAxisID: "y",
          },
          {
            label: "Number of Reviews",
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
              text: "Rating",
            },
          },
          y1: {
            type: "linear",
            display: true,
            position: "right",
            min: 0,
            title: {
              display: true,
              text: "Number of Reviews",
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
      id: "T-0001",
      patientName: "John Doe",
      patientId: "P-0001",
      testType: "DNA Paternity",
      date: "May 18, 2025",
      doctor: "Dr. Smith",
      status: "Completed",
    },
    {
      id: "T-0002",
      patientName: "Mary Smith",
      patientId: "P-0002",
      testType: "Genetic Screening",
      date: "May 17, 2025",
      doctor: "Dr. Johnson",
      status: "Processing",
    },
    {
      id: "T-0003",
      patientName: "Robert Johnson",
      patientId: "P-0003",
      testType: "Ancestry DNA",
      date: "May 16, 2025",
      doctor: "Dr. Williams",
      status: "Completed",
    },
    {
      id: "T-0004",
      patientName: "Sarah Williams",
      patientId: "P-0004",
      testType: "Genetic Predisposition",
      date: "May 15, 2025",
      doctor: "Dr. Brown",
      status: "Awaiting Sample",
    },
    {
      id: "T-0005",
      patientName: "James Brown",
      patientId: "P-0005",
      testType: "DNA Paternity",
      date: "May 14, 2025",
      doctor: "Dr. Taylor",
      status: "Completed",
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
      case "Completed":
        statusClass = "background-color: #4caf50; color: white;";
        break;
      case "Processing":
        statusClass = "background-color: #ff9800; color: white;";
        break;
      case "Awaiting Sample":
        statusClass = "background-color: #2196f3; color: white;";
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
                <a href="#" class="action-btn view-btn" onclick="viewTestResults('${test.id}')">View Results</a>
                <a href="#" class="action-btn edit-btn">Print</a>
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
  if (testId === "T-0001") {
    document.getElementById("patientName").textContent = "John Doe";
    document.getElementById("patientId").textContent = "P-0001";
    document.getElementById("patientAge").textContent = "35";
    document.getElementById("patientGender").textContent = "Male";
    document.getElementById("testDate").textContent = "May 18, 2025";
    document.getElementById("testType").textContent = "DNA Paternity";
    document.getElementById("testDoctor").textContent = "Dr. Smith";
    document.getElementById("doctorNotes").textContent =
      "Paternity test results show 99.9999% probability of paternity. The alleged father cannot be excluded as the biological father of the tested child.";
  } else if (testId === "T-0002") {
    document.getElementById("patientName").textContent = "Mary Smith";
    document.getElementById("patientId").textContent = "P-0002";
    document.getElementById("patientAge").textContent = "42";
    document.getElementById("patientGender").textContent = "Female";
    document.getElementById("testDate").textContent = "May 17, 2025";
    document.getElementById("testType").textContent = "Genetic Screening";
    document.getElementById("testDoctor").textContent = "Dr. Johnson";
    document.getElementById("doctorNotes").textContent =
      "Genetic screening results pending. Sample is being processed in our laboratory.";
  } else if (testId === "T-0004") {
    document.getElementById("patientName").textContent = "Sarah Williams";
    document.getElementById("patientId").textContent = "P-0004";
    document.getElementById("patientAge").textContent = "29";
    document.getElementById("patientGender").textContent = "Female";
    document.getElementById("testDate").textContent = "May 15, 2025";
    document.getElementById("testType").textContent = "Genetic Predisposition";
    document.getElementById("testDoctor").textContent = "Dr. Brown";
    document.getElementById("doctorNotes").textContent =
      "Genetic predisposition test shows slightly elevated risk for cardiovascular conditions. Recommend lifestyle modifications and regular check-ups.";
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
        `Filters applied! Test Type: ${testType || "All"}, Date Range: ${
          startDate || "Any"
        } to ${endDate || "Any"}, Search: ${searchTerm || "None"}`
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
      alert("Exporting feedback data to CSV...");
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
    `Feedback filters applied! Status: ${status || "All"}, Rating: ${
      rating || "All"
    }`
  );
}

// Expose functions that need to be called from HTML
window.viewTestResults = viewTestResults;
window.closeTestResultsModal = closeTestResultsModal;
