/**
 * Module quản lý xác thực người dùng
 * Hỗ trợ cả dữ liệu giả lập cho test và tích hợp API thực tế
 */

// Cấu hình - đổi sang false khi sử dụng API thật
const USE_MOCK_DATA = true;
const API_URL = "https://api.genx.com/api"; // Thay đổi thành URL API thực tế của bạn

// Dữ liệu người dùng mẫu cho việc test
const mockUsers = [
  {
    id: 1,
    email: "user@example.com",
    password: "password123",
    fullName: "Nguyễn Văn A",
    phone: "0987654321",
    avatar: "assets/images/auth/user-avatar.jpg",
  },
  {
    id: 2,
    email: "admin@example.com",
    password: "admin123",
    fullName: "Trần Thị B",
    phone: "0123456789",
    avatar: "assets/images/admin-avatar.jpg",
  },
];

/**
 * Đăng nhập người dùng
 * @param {string} email - Email đăng nhập
 * @param {string} password - Mật khẩu
 * @returns {Promise} - Promise với kết quả đăng nhập
 */
function login(email, password) {
  return new Promise((resolve, reject) => {
    if (USE_MOCK_DATA) {
      // Sử dụng dữ liệu giả lập
      setTimeout(() => {
        const user = mockUsers.find(
          (u) => u.email === email && u.password === password
        );

        if (user) {
          // Tạo bản sao user không bao gồm mật khẩu
          const { password, ...userWithoutPassword } = user;

          // Tạo token giả lập
          const token = btoa(
            JSON.stringify({
              userId: user.id,
              exp: new Date().getTime() + 24 * 60 * 60 * 1000, // Hết hạn sau 1 ngày
            })
          );

          // Lưu thông tin đăng nhập
          storeAuthData(token, userWithoutPassword);

          resolve({
            success: true,
            user: userWithoutPassword,
            token: token,
          });
        } else {
          reject({
            success: false,
            message: "Email hoặc mật khẩu không đúng",
          });
        }
      }, 500); // Giả lập độ trễ mạng
    } else {
      // Sử dụng API thực tế
      fetch(`${API_URL}/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error("Đăng nhập thất bại");
          }
          return response.json();
        })
        .then((data) => {
          storeAuthData(data.token, data.user);
          resolve({
            success: true,
            user: data.user,
            token: data.token,
          });
        })
        .catch((error) => {
          reject({
            success: false,
            message: error.message || "Đăng nhập thất bại",
          });
        });
    }
  });
}

/**
 * Đăng nhập với Google
 * @param {Object} googleUser - Thông tin người dùng Google (trong trường hợp thực tế)
 * @returns {Promise} - Promise với kết quả đăng nhập
 */
function loginWithGoogle(googleUser = null) {
  return new Promise((resolve, reject) => {
    if (USE_MOCK_DATA) {
      // Sử dụng dữ liệu giả lập
      setTimeout(() => {
        // Tạo thông tin người dùng Google giả lập
        const user = {
          id: "google-123456",
          fullName: "Lê Thị C",
          email: "user.google@gmail.com",
          avatar: "assets/images/google-avatar.jpg",
        };

        // Tạo token giả lập
        const token = btoa(
          JSON.stringify({
            userId: user.id,
            provider: "google",
            exp: new Date().getTime() + 24 * 60 * 60 * 1000,
          })
        );

        // Lưu thông tin đăng nhập
        storeAuthData(token, user);

        resolve({
          success: true,
          user: user,
          token: token,
        });
      }, 800); // Giả lập độ trễ mạng
    } else {
      // Sử dụng API thực tế với thông tin từ googleUser
      if (!googleUser || !googleUser.getAuthResponse) {
        reject({
          success: false,
          message: "Đăng nhập Google thất bại",
        });
        return;
      }

      const idToken = googleUser.getAuthResponse().id_token;

      fetch(`${API_URL}/auth/google-login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ idToken: idToken }),
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error("Đăng nhập Google thất bại");
          }
          return response.json();
        })
        .then((data) => {
          storeAuthData(data.token, data.user);
          resolve({
            success: true,
            user: data.user,
            token: data.token,
          });
        })
        .catch((error) => {
          reject({
            success: false,
            message: error.message || "Đăng nhập Google thất bại",
          });
        });
    }
  });
}

/**
 * Lưu thông tin xác thực vào localStorage
 * @param {string} token - Token xác thực
 * @param {Object} user - Thông tin người dùng
 */
function storeAuthData(token, user) {
  localStorage.setItem("accessToken", token);
  localStorage.setItem("user", JSON.stringify(user));
}

/**
 * Kiểm tra trạng thái đăng nhập
 * @returns {Object} - Trạng thái đăng nhập và thông tin người dùng nếu đã đăng nhập
 */
function checkLoginStatus() {
  const token = localStorage.getItem("accessToken");
  const user = JSON.parse(localStorage.getItem("user") || "null");

  if (!token || !user) {
    return {
      isLoggedIn: false,
    };
  }

  if (USE_MOCK_DATA) {
    // Kiểm tra token giả lập có hết hạn không
    try {
      const tokenData = JSON.parse(atob(token));
      if (tokenData.exp < new Date().getTime()) {
        // Token hết hạn
        logout();
        return {
          isLoggedIn: false,
          message: "Phiên đăng nhập đã hết hạn",
        };
      }

      return {
        isLoggedIn: true,
        user: user,
      };
    } catch (error) {
      logout();
      return {
        isLoggedIn: false,
        message: "Phiên đăng nhập không hợp lệ",
      };
    }
  } else {
    // Trong trường hợp thực tế, có thể thêm gọi API để validate token
    return {
      isLoggedIn: true,
      user: user,
    };
  }
}

/**
 * Đăng xuất người dùng
 * @returns {Promise} - Promise khi đăng xuất hoàn tất
 */
function logout() {
  return new Promise((resolve) => {
    if (!USE_MOCK_DATA) {
      // Trong trường hợp thực tế, có thể gọi API logout
      const token = localStorage.getItem("accessToken");
      if (token) {
        fetch(`${API_URL}/auth/logout`, {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }).catch((error) => {
          console.error("Lỗi khi gọi API logout:", error);
        });
      }
    }

    // Xóa dữ liệu đăng nhập
    localStorage.removeItem("accessToken");
    localStorage.removeItem("user");

    setTimeout(() => {
      resolve({
        success: true,
        message: "Đăng xuất thành công",
      });
    }, 300); // Giả lập độ trễ mạng
  });
}

// Export các hàm public để sử dụng từ bên ngoài
window.Auth = {
  login,
  loginWithGoogle,
  logout,
  checkLoginStatus,
};
