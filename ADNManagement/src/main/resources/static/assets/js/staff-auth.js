// Sửa lại toàn bộ authentication logic
const Auth = (function () {

  // Thay vì lưu localStorage, gọi API để tạo session
  function signInWithGoogle() {
    const fakeGoogleUser = {
      email: "testuser@genx.com",
      fullName: "Test User",
      role: "staff"
    };

    // Gọi API để tạo session backend
    loginToBackend(fakeGoogleUser);
  }

  // THÊM: Function gọi API login backend
  function loginToBackend(userData) {
    fetch('/api/staff/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userData)
    })
        .then(response => {
          if (response.ok) {
            // Backend đã tạo session, redirect về staff-home
            window.location.href = '/staff-home';
          } else {
            console.error('Login failed');
          }
        })
        .catch(error => {
          console.error('Login error:', error);
        });
  }

  // THÊM: Function kiểm tra session từ backend
  function checkBackendSession() {
    return fetch('/api/staff/check-session')
        .then(response => response.json())
        .then(data => data.isLoggedIn)
        .catch(() => false);
  }

  // SỬA: updateAuthUI để kiểm tra backend session
  async function updateAuthUI() {
    const isLoggedIn = await checkBackendSession();

    // Lấy các phần tử giao diện
    const userInfoContainer = document.getElementById("user-info-container");
    const loginButtons = document.getElementById("login-buttons");

    if (isLoggedIn) {
      // Đã login
      if (userInfoContainer) userInfoContainer.style.display = "flex";
      if (loginButtons) loginButtons.style.display = "none";
    } else {
      // Chưa login
      if (userInfoContainer) userInfoContainer.style.display = "none";
      if (loginButtons) loginButtons.style.display = "flex";

      // Redirect nếu không phải trang login
      if (!window.location.pathname.includes("internal-login")) {
        window.location.href = "/internal-login?redirect=" + encodeURIComponent(window.location.href);
      }
    }
  }

  // SỬA: Logout gọi API backend
  function logout() {
    fetch('/api/staff/logout', { method: 'POST' })
        .then(() => {
          window.location.href = '/internal-login';
        });
  }

  return {
    init: function() {
      console.log("Auth initialized");
      updateAuthUI();
    },
    signInWithGoogle,
    logout,
    updateAuthUI
  };
})();