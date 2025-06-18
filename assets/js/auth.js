// Module Auth xử lý xác thực người dùng
const Auth = (function () {
  // Khởi tạo xác thực (gán sự kiện cho nút đăng nhập, kiểm tra trạng thái đăng nhập)
  function initAuth() {
    console.log("Auth initialized");

    // Gán sự kiện cho nút đăng nhập Google
    const googleLoginBtn = document.getElementById("google-login-btn");
    if (googleLoginBtn) {
      googleLoginBtn.addEventListener("click", signInWithGoogle);
    }

    // Kiểm tra trạng thái đăng nhập khi load trang
    updateAuthUI();
  } // Đăng nhập với Google (giả lập, chỉ 1 tài khoản fake)
  function signInWithGoogle() {
    // Giả lập đăng nhập Google với 1 tài khoản duy nhất
    const fakeGoogleUser = {
      id: "google-fake-001",
      email: "testuser@genx.com",
      fullName: "Test User",
      role: "staff", // Có thể đổi thành "manager" hoặc "admin" nếu muốn test quyền khác
      avatar: null,
      firstLetter: "TU",
      avatarColor: "#ffca28",
      provider: "google",
    };
    saveUserToLocalStorage(fakeGoogleUser);
    updateAuthUI();
    // Chuyển hướng nếu đang ở trang login
    if (window.location.pathname.includes("login.html")) {
      const redirectUrl = new URLSearchParams(window.location.search).get(
        "redirect"
      );
      window.location.href = redirectUrl || "index.html";
    }
  }

  // Hiện/ẩn dropdown chọn tài khoản demo
  function toggleDemoAccountsDropdown() {
    const dropdown = document.getElementById("demo-accounts-dropdown");
    if (dropdown.style.display === "block") {
      dropdown.style.display = "none";
    } else {
      dropdown.style.display = "block";
    }
  }

  // Đăng nhập với tài khoản demo
  function loginWithDemoAccount(accountType) {
    if (!demoAccounts[accountType]) {
      console.error("Invalid account type:", accountType);
      return;
    }

    // Lấy thông tin tài khoản demo
    const user = demoAccounts[accountType];
    user.provider = "demo";

    // Lưu vào localStorage
    saveUserToLocalStorage(user);

    // Cập nhật UI
    updateAuthUI();

    // Ẩn dropdown
    document.getElementById("demo-accounts-dropdown").style.display = "none";

    // Chuyển hướng hoặc reload lại trang để áp dụng phân quyền
    if (window.location.pathname.includes("login.html")) {
      const redirectUrl = new URLSearchParams(window.location.search).get(
        "redirect"
      );
      window.location.href = redirectUrl || "index.html";
    } else {
      // Reload lại trang hiện tại
      window.location.reload();
    }
  }

  // Lưu user vào localStorage
  function saveUserToLocalStorage(user) {
    localStorage.setItem("currentUser", JSON.stringify(user));

    // Thực tế sẽ gọi API để tạo/cập nhật user và lấy role/permission
    console.log("User saved to localStorage:", user);
  }

  // Kiểm tra trạng thái đăng nhập
  function checkLoginStatus() {
    const userJson = localStorage.getItem("currentUser");
    if (!userJson) {
      return { isLoggedIn: false, user: null };
    }

    try {
      const user = JSON.parse(userJson);
      return { isLoggedIn: true, user };
    } catch (e) {
      console.error("Error parsing user from localStorage:", e);
      localStorage.removeItem("currentUser");
      return { isLoggedIn: false, user: null };
    }
  }

  // Cập nhật UI theo trạng thái đăng nhập
  function updateAuthUI() {
    const { isLoggedIn, user } = checkLoginStatus();

    // Lấy các phần tử giao diện
    const userInfoContainer = document.getElementById("user-info-container");
    const loginButtons = document.getElementById("login-buttons");
    const logoutBtn = document.getElementById("logout-btn");
    const avatarLetter = document.getElementById("avatar-letter");
    const userAvatar = document.getElementById("user-avatar");
    const fullNameElement = document.getElementById("avatar-fullname");
    const roleElement = document.getElementById("avatar-role");

    if (isLoggedIn && user) {
      // Nếu đã đăng nhập
      if (userInfoContainer) userInfoContainer.style.display = "flex";
      if (loginButtons) loginButtons.style.display = "none";
      if (logoutBtn) logoutBtn.style.display = "block"; // Cập nhật thông tin user
      if (fullNameElement) fullNameElement.textContent = user.fullName;
      if (roleElement) {
        let roleDisplay = "Nhân Viên";
        if (user.role === "manager") roleDisplay = "Quản lý";
        if (user.role === "admin") roleDisplay = "Quản trị viên";
        roleElement.textContent = roleDisplay;
      } // Cập nhật avatar
      if (avatarLetter && userAvatar) {
        if (user.avatar) {
          userAvatar.src = user.avatar;
          userAvatar.style.display = "block";
          avatarLetter.style.display = "none";
        } else {
          if (user.fullName === "Test User") {
            avatarLetter.textContent = "TU";
            avatarLetter.style.color = "#333";
            // Explicitly set the background color
            if (avatarLetter.parentElement) {
              avatarLetter.parentElement.classList.add("avatar");
              avatarLetter.parentElement.style.backgroundColor = "#ffca28";
            }
          } else {
            avatarLetter.textContent =
              user.firstLetter || user.fullName.charAt(0).toUpperCase();
            avatarLetter.style.color = "#333";
            if (avatarLetter.parentElement) {
              avatarLetter.parentElement.classList.add("avatar");
              avatarLetter.parentElement.style.backgroundColor =
                user.avatarColor || "#ffca28";
            }
          }
          userAvatar.style.display = "none";
          avatarLetter.style.display = "flex";
        }
      }

      // Nếu có hàm setupUIByRole thì gọi để phân quyền UI
      if (window.setupUIByRole && typeof window.setupUIByRole === "function") {
        window.setupUIByRole(user);
      }

      // Cập nhật tên nhân viên ở khu vực chào mừng nếu có
      const staffNameElement = document.getElementById("staff-name");
      if (staffNameElement) {
        staffNameElement.textContent = user.fullName;
      }
    } else {
      // Nếu chưa đăng nhập
      if (userInfoContainer) userInfoContainer.style.display = "none";
      if (loginButtons) loginButtons.style.display = "flex";
      if (logoutBtn) logoutBtn.style.display = "none";

      // Nếu không phải trang login thì chuyển hướng về login
      if (!window.location.pathname.includes("login.html")) {
        window.location.href =
          "login.html?redirect=" + encodeURIComponent(window.location.href);
      }
    }
  }

  // Đăng xuất
  function logout() {
    // Xóa dữ liệu user
    localStorage.removeItem("currentUser");

    // Thực tế sẽ gọi API logout
    console.log("User logged out");

    // Cập nhật UI
    updateAuthUI();

    // Chuyển hướng về trang đăng nhập
    window.location.href = "login.html";
  }

  // Public API
  return {
    init: initAuth, // Khởi tạo module
    signInWithGoogle, // Đăng nhập Google
    checkLoginStatus, // Kiểm tra trạng thái
    logout, // Đăng xuất
    updateAuthUI, // Cập nhật UI
  };
})();

// Khởi tạo Auth khi DOM đã sẵn sàng
document.addEventListener("DOMContentLoaded", function () {
  Auth.init();
});

// Export các hàm toàn cục cho HTML sử dụng
window.handleLogout = Auth.logout;
