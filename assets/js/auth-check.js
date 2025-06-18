// auth-check.js - Script đơn giản để kiểm tra xác thực trên các trang không phải dashboard chính
document.addEventListener("DOMContentLoaded", function () {
  // Kiểm tra xem auth.js đã được load và đối tượng Auth đã tồn tại chưa
  if (window.Auth && typeof window.Auth.checkLoginStatus === "function") {
    const { isLoggedIn, user } = window.Auth.checkLoginStatus();

    if (!isLoggedIn) {
      // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập và lưu lại đường dẫn hiện tại
      window.location.href =
        "login.html?redirect=" + encodeURIComponent(window.location.href);
    } else {
      // Nếu đã đăng nhập, in ra thông tin người dùng (chủ yếu để debug)
      console.log("User authenticated:", user.fullName);

      // Cập nhật các phần tử giao diện có class .current-username với tên người dùng
      const usernameElements = document.querySelectorAll(".current-username");
      if (usernameElements.length > 0) {
        usernameElements.forEach((el) => {
          el.textContent = user.fullName;
        });
      }

      // Nếu có hàm setupUIByRole, gọi hàm này để cập nhật giao diện theo vai trò người dùng
      if (typeof window.setupUIByRole === "function") {
        window.setupUIByRole(user);
      }

      // Kiểm tra và gọi hàm render lịch hẹn nếu có
      setTimeout(function () {
        if (window.forceRenderAppointments) {
          console.log("Auth-check: Calling forceRenderAppointments");
          window.forceRenderAppointments();
        }
      }, 200);
    }
  } else {
    // Nếu chưa load được Auth, báo lỗi trên console
    console.error(
      "Chưa load được module Auth. Hãy chắc chắn đã nhúng auth.js trước auth-check.js"
    );
  }
});
