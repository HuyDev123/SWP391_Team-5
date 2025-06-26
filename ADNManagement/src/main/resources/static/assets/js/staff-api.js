// staff-api.js: Chỉ giữ lại các hàm gọi API cho dashboard, tasks, activities nếu cần. Loại bỏ mock data và xác thực, xác thực đã do backend xử lý.

const API = (function () {
  // Base API URL - change to real API endpoint in production
  const API_BASE_URL = "https://api.genx-example.com";

  // Helper for API calls
  async function apiCall(endpoint, method = "GET", data = null) {
    const url = `${API_BASE_URL}${endpoint}`;
    const headers = {
      "Content-Type": "application/json",
    };
    const options = {
      method,
      headers,
      credentials: "include",
    };
    if (data && (method === "POST" || method === "PUT" || method === "PATCH")) {
      options.body = JSON.stringify(data);
    }
    const response = await fetch(url, options);
    if (!response.ok) {
      throw new Error("API call failed");
    }
    return await response.json();
  }

  // Dashboard methods
  async function getDashboardStats() {
    return apiCall("/dashboard/stats");
  }

  // Tasks methods
  async function getTasks() {
    return apiCall("/tasks");
  }
  async function getTaskDetail(taskId) {
    return apiCall(`/tasks/${taskId}`);
  }
  async function createTask(taskData) {
    return apiCall("/tasks", "POST", taskData);
  }
  async function updateTask(taskId, taskData) {
    return apiCall(`/tasks/${taskId}`, "PUT", taskData);
  }
  async function completeTask(taskId) {
    return apiCall(`/tasks/${taskId}/complete`, "POST");
  }

  // Activities methods
  async function getRecentActivities() {
    return apiCall("/activities/recent");
  }
  async function getMoreActivities(offset) {
    return apiCall(`/activities?offset=${offset}`);
  }

  // Public API
  return {
    // Dashboard
    getDashboardStats,
    // Tasks
    getTasks,
    getTaskDetail,
    createTask,
    updateTask,
    completeTask,
    // Activities
    getRecentActivities,
    getMoreActivities,
  };
})();
