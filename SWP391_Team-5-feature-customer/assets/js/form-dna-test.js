// form-dna-test.js
// Form đăng ký xét nghiệm ADN

document.addEventListener('DOMContentLoaded', function() {
  // Ràng buộc: Nếu chọn Hành chính thì chỉ được chọn lấy mẫu tại cơ sở
  const civilRadio = document.getElementById('civil');
  const legalRadio = document.getElementById('legal');
  const homeRadio = document.getElementById('home');
  const centerRadio = document.getElementById('center');
  const kitTypeInput = document.getElementById('kit-type');

  function updateKitType() {
    if (centerRadio.checked) {
      kitTypeInput.value = 'Kit tại cơ sở';
    } else {
      kitTypeInput.value = 'Kit tại nhà';
    }
  }

  function handleServiceModeChange() {
    if (legalRadio.checked) {
      centerRadio.checked = true;
      centerRadio.disabled = false;
      homeRadio.checked = false;
      homeRadio.disabled = true;
    } else {
      homeRadio.disabled = false;
      // Nếu trước đó là hành chính thì trả lại lựa chọn lấy mẫu tại nhà
      if (!centerRadio.checked && !homeRadio.checked) homeRadio.checked = true;
    }
    updateKitType();
  }

  civilRadio.addEventListener('change', handleServiceModeChange);
  legalRadio.addEventListener('change', handleServiceModeChange);
  homeRadio.addEventListener('change', updateKitType);
  centerRadio.addEventListener('change', updateKitType);

  // Khởi tạo trạng thái ban đầu
  handleServiceModeChange();

  // Thêm/xóa mẫu động
  const samplesContainer = document.getElementById('samples-container');
  const addSampleBtn = document.getElementById('add-sample');

  addSampleBtn.addEventListener('click', function() {
    const sampleCount = samplesContainer.querySelectorAll('.sample-item').length + 1;
    const sampleDiv = document.createElement('div');
    sampleDiv.className = 'sample-item';
    sampleDiv.innerHTML = `
      <div class="form-row">
        <div class="form-group">
          <label class="form-label required-label">Tên người lấy mẫu</label>
          <input type="text" class="form-control" name="sample_name[]" required>
        </div>
        <div class="form-group">
          <label class="form-label required-label">Quan hệ</label>
          <select class="form-select" name="sample_relation[]" required>
            <option value="">-- Chọn --</option>
            <option value="father">Cha</option>
            <option value="mother">Mẹ</option>
            <option value="child">Con</option>
            <option value="sibling">Anh/Chị/Em</option>
          </select>
        </div>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label class="form-label required-label">Loại mẫu</label>
          <select class="form-select" name="sample_type[]" required>
            <option value="">-- Chọn --</option>
            <option value="niêm mạc miệng">Niêm mạc miệng</option>
            <option value="máu">Máu</option>
            <option value="tóc">Tóc</option>
            <option value="móng tay">Móng tay</option>
          </select>
        </div>
        <div class="form-group">
          <label class="form-label">Hình ảnh mẫu</label>
          <input type="file" class="form-control" name="sample_image[]" accept="image/*">
        </div>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label class="form-label required-label">Ngày lấy mẫu</label>
          <input type="date" class="form-control" name="sample_date[]" required>
        </div>
        <div class="form-group">
          <label class="form-label">Giờ lấy mẫu</label>
          <input type="time" class="form-control" name="sample_time[]">
        </div>
        <div class="form-group">
          <button type="button" class="btn btn-danger remove-sample" style="margin-top:28px"><i class="fas fa-trash"></i> Xóa mẫu</button>
        </div>
      </div>
    `;
    samplesContainer.appendChild(sampleDiv);
    // Thêm sự kiện xóa mẫu
    sampleDiv.querySelector('.remove-sample').addEventListener('click', function() {
      sampleDiv.remove();
    });
  });

  // Xóa mẫu đầu tiên nếu có nhiều hơn 1 mẫu
  samplesContainer.addEventListener('click', function(e) {
    if (e.target.classList.contains('remove-sample')) {
      const items = samplesContainer.querySelectorAll('.sample-item');
      if (items.length > 1) {
        e.target.closest('.sample-item').remove();
      }
    }
  });

  // Validate form trước khi submit
  const form = document.getElementById('dna-test-form');
  form.addEventListener('submit', function(e) {
    // Có thể bổ sung validate nâng cao ở đây
    // Ví dụ: kiểm tra nếu là hành chính thì phải chọn lấy mẫu tại cơ sở
    if (legalRadio.checked && !centerRadio.checked) {
      alert('Dịch vụ hành chính bắt buộc phải lấy mẫu tại cơ sở xét nghiệm!');
      e.preventDefault();
      return false;
    }
    // Validate các trường khác nếu cần
  });

  // Lấy các phần tử form
  const dnaTestForm = document.getElementById('dna-test-form');
  const resetFormBtn = document.getElementById('resetFormBtn');
  const applyCouponBtn = document.getElementById('applyCouponBtn');
  
  // Các phần tử thu mẫu
  const collectionMethodRadios = document.getElementsByName('collection_method');
  const centerCollectionSection = document.getElementById('center-collection-section');
  const homeCollectionSection = document.getElementById('home-collection-section');
  const selfCollectionSection = document.getElementById('self-collection-section');
  
  // Các phần tử giá
  const serviceRadios = document.getElementsByName('service');
  const servicePrice = document.getElementById('service-price');
  const collectionFeeRow = document.getElementById('collection-fee-row');
  const collectionFee = document.getElementById('collection-fee');
  const discountRow = document.getElementById('discount-row');
  const discountAmount = document.getElementById('discount-amount');
  const totalAmount = document.getElementById('total-amount');
  
  // Các phần tử loại dịch vụ
  const serviceTypeSelect = document.querySelector('select[name="service_type"]');
  const selfCollectionRadio = document.getElementById('method-self');
  
  // Thiết lập ngày tối thiểu cho các trường ngày
  const today = new Date();
  const tomorrow = new Date(today);
  tomorrow.setDate(tomorrow.getDate() + 1);
  
  const dateInputs = document.querySelectorAll('input[type="date"]');
  dateInputs.forEach(input => {
      input.min = formatDate(tomorrow);
  });
  
  // Xử lý sự kiện thay đổi phương thức thu mẫu
  collectionMethodRadios.forEach(radio => {
      radio.addEventListener('change', function() {
          // Ẩn tất cả các phần
          centerCollectionSection.classList.add('hidden');
          homeCollectionSection.classList.add('hidden');
          selfCollectionSection.classList.add('hidden');
          
          // Hiển thị phần tương ứng
          if (this.value === 'center') {
              centerCollectionSection.classList.remove('hidden');
              collectionFeeRow.style.display = 'none';
              collectionFee.textContent = '0 VNĐ';
              updateTotalAmount();
          } else if (this.value === 'home') {
              homeCollectionSection.classList.remove('hidden');
              collectionFeeRow.style.display = 'flex';
              collectionFee.textContent = '300.000 VNĐ';
              updateTotalAmount();
          } else if (this.value === 'self') {
              selfCollectionSection.classList.remove('hidden');
              collectionFeeRow.style.display = 'none';
              collectionFee.textContent = '0 VNĐ';
              updateTotalAmount();
          }
      });
  });
  
  // Xử lý sự kiện thay đổi loại dịch vụ
  serviceTypeSelect.addEventListener('change', function() {
      // Nếu chọn dịch vụ hành chính, không cho phép tự thu mẫu
      if (this.value === 'admin') {
          selfCollectionRadio.disabled = true;
          if (selfCollectionRadio.checked) {
              document.getElementById('method-center').checked = true;
              // Kích hoạt sự kiện change để cập nhật giao diện
              document.getElementById('method-center').dispatchEvent(new Event('change'));
          }
      } else {
          selfCollectionRadio.disabled = false;
      }
  });
  
  // Xử lý sự kiện thay đổi dịch vụ xét nghiệm
  serviceRadios.forEach(radio => {
      radio.addEventListener('change', function() {
          if (this.value === 'father_child' || this.value === 'mother_child') {
              servicePrice.textContent = '2.500.000 VNĐ';
          } else if (this.value === 'family') {
              servicePrice.textContent = '4.000.000 VNĐ';
          }
          updateTotalAmount();
      });
  });
  
  // Xử lý sự kiện áp dụng mã giảm giá
  applyCouponBtn.addEventListener('click', function() {
      const couponCode = document.querySelector('input[name="coupon_code"]').value.trim();
      if (couponCode) {
          // Giả lập kiểm tra mã giảm giá
          if (couponCode === 'DNA10') {
              discountRow.style.display = 'flex';
              discountAmount.textContent = '250.000 VNĐ';
              showNotification('success', 'Đã áp dụng mã giảm giá thành công!');
          } else if (couponCode === 'DNA20') {
              discountRow.style.display = 'flex';
              discountAmount.textContent = '500.000 VNĐ';
              showNotification('success', 'Đã áp dụng mã giảm giá thành công!');
          } else {
              showNotification('error', 'Mã giảm giá không hợp lệ hoặc đã hết hạn!');
          }
          updateTotalAmount();
      } else {
          showNotification('warning', 'Vui lòng nhập mã giảm giá!');
      }
  });
  
  // Xử lý sự kiện nút reset form
  resetFormBtn.addEventListener('click', function() {
      dnaTestForm.reset();
      // Kích hoạt sự kiện change để cập nhật giao diện
      document.getElementById('method-center').dispatchEvent(new Event('change'));
      document.querySelector('input[name="service"][value="father_child"]').dispatchEvent(new Event('change'));
      
      // Reset các giá trị khác
      discountRow.style.display = 'none';
      discountAmount.textContent = '0 VNĐ';
      
      showNotification('info', 'Đã xóa tất cả thông tin nhập!');
  });
  
  // Xử lý sự kiện submit form
  dnaTestForm.addEventListener('submit', function(e) {
      e.preventDefault();
      
      // Kiểm tra trường bắt buộc dựa trên phương thức thu mẫu
      const collectionMethod = document.querySelector('input[name="collection_method"]:checked').value;
      
      let isValid = true;
      let errorMessage = '';
      
      if (collectionMethod === 'center') {
          const centerLocation = document.querySelector('select[name="center_location"]').value;
          const appointmentDate = document.querySelector('input[name="appointment_date"]').value;
          const appointmentTime = document.querySelector('select[name="appointment_time"]').value;
          
          if (!centerLocation || !appointmentDate || !appointmentTime) {
              isValid = false;
              errorMessage = 'Vui lòng điền đầy đủ thông tin lịch hẹn tại trung tâm!';
          }
      } else if (collectionMethod === 'home') {
          const homeAddress = document.querySelector('input[name="home_address"]').value;
          const homeAppointmentDate = document.querySelector('input[name="home_appointment_date"]').value;
          const homeAppointmentTime = document.querySelector('select[name="home_appointment_time"]').value;
          
          if (!homeAddress || !homeAppointmentDate || !homeAppointmentTime) {
              isValid = false;
              errorMessage = 'Vui lòng điền đầy đủ thông tin lấy mẫu tại nhà!';
          }
      } else if (collectionMethod === 'self') {
          const kitAddress = document.querySelector('input[name="kit_address"]').value;
          
          if (!kitAddress) {
              isValid = false;
              errorMessage = 'Vui lòng điền địa chỉ nhận kit!';
          }
      }
      
      // Kiểm tra thông tin liên hệ
      const fullname = document.querySelector('input[name="fullname"]').value;
      const phone = document.querySelector('input[name="phone"]').value;
      const email = document.querySelector('input[name="email"]').value;
      
      if (!fullname || !phone || !email) {
          isValid = false;
          errorMessage = 'Vui lòng điền đầy đủ thông tin liên hệ!';
      }
      
      if (isValid) {
          // Giả lập gửi form thành công
          showNotification('success', 'Đăng ký xét nghiệm thành công! Chúng tôi sẽ liên hệ với bạn trong thời gian sớm nhất.');
          
          // Sau 2 giây, chuyển hướng đến trang theo dõi tiến trình
          setTimeout(function() {
              navigateToPage('tracking');
          }, 2000);
      } else {
          showNotification('error', errorMessage);
      }
  });
  
  // Khởi tạo ban đầu
  document.getElementById('method-center').dispatchEvent(new Event('change'));
  
  // Hàm hỗ trợ
  function formatDate(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
  }
  
  function updateTotalAmount() {
      // Lấy giá dịch vụ
      const servicePriceText = servicePrice.textContent;
      const servicePriceValue = parseInt(servicePriceText.replace(/[^\d]/g, ''));
      
      // Lấy phí thu mẫu
      const collectionFeeText = collectionFee.textContent;
      const collectionFeeValue = parseInt(collectionFeeText.replace(/[^\d]/g, '')) || 0;
      
      // Lấy giảm giá
      const discountAmountText = discountAmount.textContent;
      const discountAmountValue = parseInt(discountAmountText.replace(/[^\d]/g, '')) || 0;
      
      // Tính tổng
      const total = servicePriceValue + collectionFeeValue - discountAmountValue;
      
      // Định dạng số
      const formattedTotal = new Intl.NumberFormat('vi-VN').format(total);
      
      // Cập nhật tổng
      totalAmount.textContent = `${formattedTotal} VNĐ`;
  }
  
  function showNotification(type, message) {
      const notification = document.getElementById('notification');
      const notificationIcon = notification.querySelector('.notification-icon');
      const notificationMessage = notification.querySelector('.notification-message');
      
      // Thiết lập icon và màu sắc theo loại thông báo
      if (type === 'success') {
          notificationIcon.className = 'notification-icon fas fa-check-circle';
          notification.className = 'notification notification-success show';
      } else if (type === 'error') {
          notificationIcon.className = 'notification-icon fas fa-exclamation-circle';
          notification.className = 'notification notification-error show';
      } else if (type === 'warning') {
          notificationIcon.className = 'notification-icon fas fa-exclamation-triangle';
          notification.className = 'notification notification-warning show';
      } else if (type === 'info') {
          notificationIcon.className = 'notification-icon fas fa-info-circle';
          notification.className = 'notification notification-info show';
      }
      
      // Thiết lập nội dung
      notificationMessage.textContent = message;
      
      // Hiển thị thông báo
      notification.classList.add('show');
      
      // Tự động ẩn sau 5 giây
      setTimeout(function() {
          notification.classList.remove('show');
      }, 5000);
  }
  
  // Xử lý sự kiện đóng thông báo
  document.querySelector('.notification-close').addEventListener('click', function() {
      document.getElementById('notification').classList.remove('show');
  });
});

// Hàm xử lý FAQ
function toggleFAQ(element) {
    element.classList.toggle('active');
} 