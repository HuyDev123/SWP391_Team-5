// API module for handling backend calls
const API = (function () {
  // Base API URL - change to real API endpoint in production
  const API_BASE_URL = "https://api.genx-example.com";

  // Helper to get auth token
  function getAuthToken() {
    const user = JSON.parse(localStorage.getItem("currentUser") || "{}");
    return user.token;
  }

  // Helper for API calls
  async function apiCall(endpoint, method = "GET", data = null) {
    const url = `${API_BASE_URL}${endpoint}`;

    // Default headers
    const headers = {
      "Content-Type": "application/json",
    };

    // Add auth token if available
    const token = getAuthToken();
    if (token) {
      headers["Authorization"] = `Bearer ${token}`;
    }

    // Prepare request options
    const options = {
      method,
      headers,
      credentials: "include",
    };

    // Add body if provided
    if (data && (method === "POST" || method === "PUT" || method === "PATCH")) {
      options.body = JSON.stringify(data);
    }

    // For development, log the request
    console.log(`API ${method} request to ${endpoint}`);

    try {
      // In development mode, simulate API with mock data
      if (
        window.location.hostname === "localhost" ||
        window.location.hostname === "127.0.0.1"
      ) {
        return await simulateApiResponse(endpoint, method, data);
      }

      // Real API call
      const response = await fetch(url, options);

      // Parse JSON response
      const responseData = await response.json();

      // Check for error status
      if (!response.ok) {
        throw new Error(responseData.message || "API call failed");
      }

      return responseData;
    } catch (error) {
      console.error("API call error:", error);

      // For now, use mock data when API fails
      return await simulateApiResponse(endpoint, method, data);
    }
  }

  // Simulate API responses for development
  async function simulateApiResponse(endpoint, method, data) {
    // Add artificial delay to simulate network
    await new Promise((resolve) => setTimeout(resolve, 800));

    // Mock responses based on endpoint
    console.log("Using simulated API response for", endpoint);

    // Return different mock data based on endpoint
    switch (true) {
      case endpoint.includes("/auth/login"):
        return mockAuthResponse(data);
      case endpoint.includes("/dashboard/stats"):
        return mockDashboardStats();
      case endpoint.includes("/tasks"):
        if (method === "GET") return mockTasks();
        if (method === "POST")
          return {
            success: true,
            task: { ...data, id: "new-task-" + Date.now() },
          };
        break;
      default:
        return { message: "Mock data not available for this endpoint" };
    }
  }

  // Mock auth response
  function mockAuthResponse(data) {
    // Simulate login response
    return {
      success: true,
      user: {
        id: "user-" + Math.random().toString(36).substring(2, 10),
        email: data.email || "user@example.com",
        fullName: "Test User",
        role: "staff",
        token: "mock-jwt-token-" + Math.random().toString(36).substring(2, 15),
      },
    };
  }

  // Mock dashboard stats
  function mockDashboardStats() {
    return {
      todayAppointments: Math.floor(Math.random() * 20),
      newSamples: Math.floor(Math.random() * 15),
      processingSamples: Math.floor(Math.random() * 40) + 10,
      completedSamples: Math.floor(Math.random() * 100) + 50,
      appointmentsTrend: Math.floor(Math.random() * 40) - 20,
      samplesTrend: Math.floor(Math.random() * 40) - 20,
      processingTrend: Math.floor(Math.random() * 40) - 20,
      completedTrend: Math.floor(Math.random() * 40) - 20,
    };
  }

  // Mock tasks
  function mockTasks() {
    return [
      {
        id: "task-1",
        title: "Xử lý mẫu #ADN123",
        customer: "Nguyễn Văn A",
        deadline: new Date(Date.now() + 5 * 3600000).toISOString(),
        priority: "urgent",
        progress: 75,
      },
      {
        id: "task-2",
        title: "Kiểm tra kit #KIT456",
        customer: "Trần Thị B",
        deadline: new Date(Date.now() + 24 * 3600000).toISOString(),
        priority: "normal",
        progress: 30,
      },
      {
        id: "task-3",
        title: "Cập nhật kết quả #KQ789",
        customer: "Lê Văn C",
        deadline: new Date(Date.now() + 48 * 3600000).toISOString(),
        priority: "normal",
        progress: 50,
      },
    ];
  }

  // Auth methods
  async function loginWithGoogle(googleToken) {
    return apiCall("/auth/google-login", "POST", { token: googleToken });
  }

  async function loginWithCredentials(email, password) {
    return apiCall("/auth/login", "POST", { email, password });
  }

  async function logout() {
    return apiCall("/auth/logout", "POST");
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
    // Auth
    loginWithGoogle,
    loginWithCredentials,
    logout,

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
