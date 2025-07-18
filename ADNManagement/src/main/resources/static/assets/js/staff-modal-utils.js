// staff-modal-utils.js

// Hàm mở modal nhập thông tin người tham gia
function openParticipantModal(options = {}) {
  // Xóa modal cũ nếu có
  const oldModal = document.getElementById('global-participant-modal');
  if (oldModal) oldModal.remove();

  // Tạo modal
  const modal = document.createElement('div');
  modal.id = 'global-participant-modal';
  modal.className = 'modal';
  modal.style.display = 'flex';
  modal.innerHTML = `
    <div class="modal-content" style="max-width: 700px;">
      <div class="modal-header">
        <h2 class="modal-title">${options.title || 'Nhập Thông Tin Người Tham Gia'}</h2>
        <span class="close" onclick="document.getElementById('global-participant-modal').remove()">&times;</span>
      </div>
      <form id="global-participant-form">
        <div class="form-grid">
          <div class="form-group">
            <label for="gp-fullName">Họ và tên <span class="required">*</span></label>
            <input type="text" id="gp-fullName" name="fullName" class="form-control" required value="${options.fullName || ''}">
          </div>
          <div class="form-group">
            <label for="gp-phone">Số điện thoại <span class="required">*</span></label>
            <input type="tel" id="gp-phone" name="phone" class="form-control" required value="${options.phone || ''}">
          </div>
          <div class="form-group">
            <label for="gp-email">Email <span class="required">*</span></label>
            <input type="email" id="gp-email" name="email" class="form-control" required value="${options.email || ''}">
          </div>
          <div class="form-group">
            <label for="gp-address">Địa chỉ <span class="required">*</span></label>
            <input type="text" id="gp-address" name="address" class="form-control" required value="${options.address || ''}">
          </div>
          <div class="form-group">
            <label for="gp-birthDate">Ngày sinh <span class="required">*</span></label>
            <input type="date" id="gp-birthDate" name="birthDate" class="form-control" required value="${options.birthDate || ''}">
          </div>
          <div class="form-group">
            <label for="gp-gender">Giới tính <span class="required">*</span></label>
            <select id="gp-gender" name="gender" class="form-control" required>
              <option value="">Chọn giới tính</option>
              <option value="male" ${options.gender === 'male' ? 'selected' : ''}>Nam</option>
              <option value="female" ${options.gender === 'female' ? 'selected' : ''}>Nữ</option>
              <option value="other" ${options.gender === 'other' ? 'selected' : ''}>Khác</option>
            </select>
          </div>
          <div class="form-group">
            <label for="gp-relationship">Mối quan hệ <span class="required">*</span></label>
            <input type="text" id="gp-relationship" name="relationship" class="form-control" required value="${options.relationship || ''}">
          </div>
          <div class="form-group">
            <label for="gp-note">Ghi chú</label>
            <textarea id="gp-note" name="note" class="form-control" rows="2">${options.note || ''}</textarea>
          </div>
        </div>
        <div class="form-actions" style="text-align:right; margin-top:20px;">
          <button type="button" class="btn" style="background-color:#6c757d;color:white;margin-right:10px;" onclick="document.getElementById('global-participant-modal').remove()">Hủy</button>
          <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Xác nhận</button>
        </div>
      </form>
    </div>
  `;
  document.body.appendChild(modal);
  // Đóng modal khi click ra ngoài
  modal.addEventListener('click', function(e) { if (e.target === modal) modal.remove(); });
  // Submit event
  if (typeof options.onSubmit === 'function') {
    document.getElementById('global-participant-form').onsubmit = function(e) {
      e.preventDefault();
      const data = {
        fullName: document.getElementById('gp-fullName').value,
        phone: document.getElementById('gp-phone').value,
        email: document.getElementById('gp-email').value,
        address: document.getElementById('gp-address').value,
        birthDate: document.getElementById('gp-birthDate').value,
        gender: document.getElementById('gp-gender').value,
        relationship: document.getElementById('gp-relationship').value,
        note: document.getElementById('gp-note').value
      };
      options.onSubmit(data, modal);
    };
  }
}

// Hàm mở modal nhập thông tin mẫu xét nghiệm
function openSampleModal(options = {}) {
  // Xóa modal cũ nếu có
  const oldModal = document.getElementById('global-sample-modal');
  if (oldModal) oldModal.remove();

  // Tạo modal
  const modal = document.createElement('div');
  modal.id = 'global-sample-modal';
  modal.className = 'modal';
  modal.style.display = 'flex';
  
  // Nếu có participantList thì tạo dropdown, ngược lại dùng input như cũ
  let participantField = '';
  if (Array.isArray(options.participantList) && options.participantList.length > 0) {
    participantField = `
      <label for="gs-participant">Người cung cấp mẫu <span class="required">*</span></label>
      <select id="gs-participant" name="participant" class="form-control" required>
        <option value="">Chọn người tham gia</option>
        ${options.participantList.map(p => `<option value="${p.id}">${p.fullName}</option>`).join('')}
      </select>
    `;
  } else {
    participantField = `
      <label for="gs-participant">Tên người cung cấp <span class="required">*</span></label>
      <input type="text" id="gs-participant" name="participant" class="form-control" required value="${options.participant || ''}">
    `;
  }
  
  // Tạo dropdown cho staff (người thực hiện)
  let collectedByField = '';
  if (Array.isArray(options.staffList) && options.staffList.length > 0) {
    collectedByField = `
      <label for="gs-collectedBy">Người thực hiện <span class="required">*</span></label>
      <select id="gs-collectedBy" name="collectedBy" class="form-control" required>
        <option value="">Chọn nhân viên</option>
        ${options.staffList.map(s => `<option value="${s.id}">${s.fullName}</option>`).join('')}
      </select>
    `;
  } else {
    collectedByField = `
      <label for="gs-collectedBy">Người thực hiện <span class="required">*</span></label>
      <input type="text" id="gs-collectedBy" name="collectedBy" class="form-control" required value="${options.collectedBy || ''}">
    `;
  }
  
  // Tạo dropdown cho services
  let serviceField = '';
  if (Array.isArray(options.serviceList) && options.serviceList.length > 0) {
    const selectedServiceId = options.selectedServiceId || '';
    serviceField = `
      <label for="gs-service">Dịch vụ <span class="required">*</span></label>
      <select id="gs-service" name="service" class="form-control" required>
        <option value="">Chọn dịch vụ</option>
        ${options.serviceList.map(s => `<option value="${s.id}" ${s.id == selectedServiceId ? 'selected' : ''}>${s.name}</option>`).join('')}
      </select>
    `;
  } else {
    serviceField = `
      <label for="gs-service">Dịch vụ <span class="required">*</span></label>
      <input type="text" id="gs-service" name="service" class="form-control" required value="${options.service || ''}">
    `;
  }
  modal.innerHTML = `
    <div class="modal-content" style="max-width: 700px;">
      <div class="modal-header">
        <h2 class="modal-title">${options.title || 'Nhập Thông Tin Mẫu Xét Nghiệm'}</h2>
        <span class="close" onclick="document.getElementById('global-sample-modal').remove()">&times;</span>
      </div>
      <form id="global-sample-form">
        <div class="form-grid">
          <div class="form-group">
            <label for="gs-sampleCode">Mã mẫu <span class="required">*</span></label>
            <input type="text" id="gs-sampleCode" name="sampleCode" class="form-control" required value="${options.sampleCode || ''}">
          </div>
          <div class="form-group">
            ${participantField}
          </div>
          <div class="form-group">
            ${serviceField}
          </div>
          <div class="form-group">
            ${collectedByField}
          </div>
          <div class="form-group">
            <label for="gs-collectedAt">Thời gian lấy <span class="required">*</span></label>
            <input type="datetime-local" id="gs-collectedAt" name="collectedAt" class="form-control" required value="${options.collectedAt || ''}">
          </div>
          <div class="form-group">
            <label for="gs-sampleType">Loại mẫu <span class="required">*</span></label>
            <select id="gs-sampleType" name="sampleType" class="form-control" required>
              <option value="">Chọn loại mẫu</option>
              <option value="blood" ${options.sampleType === 'blood' ? 'selected' : ''}>Máu</option>
              <option value="saliva" ${options.sampleType === 'saliva' ? 'selected' : ''}>Nước bọt</option>
              <option value="hair" ${options.sampleType === 'hair' ? 'selected' : ''}>Tóc</option>
              <option value="nail" ${options.sampleType === 'nail' ? 'selected' : ''}>Móng tay</option>
            </select>
          </div>
          <div class="form-group">
            <label for="gs-note">Ghi chú</label>
            <textarea id="gs-note" name="note" class="form-control" rows="2">${options.note || ''}</textarea>
          </div>
        </div>
        <div class="form-actions" style="text-align:right; margin-top:20px;">
          <button type="button" class="btn" style="background-color:#6c757d;color:white;margin-right:10px;" onclick="document.getElementById('global-sample-modal').remove()">Hủy</button>
          <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Xác nhận</button>
        </div>
      </form>
    </div>
  `;
  document.body.appendChild(modal);
  // Đóng modal khi click ra ngoài
  modal.addEventListener('click', function(e) { if (e.target === modal) modal.remove(); });
  // Submit event
  if (typeof options.onSubmit === 'function') {
    document.getElementById('global-sample-form').onsubmit = function(e) {
      e.preventDefault();
      let participantValue = '';
      if (Array.isArray(options.participantList) && options.participantList.length > 0) {
        participantValue = document.getElementById('gs-participant').value;
      } else {
        participantValue = document.getElementById('gs-participant').value;
      }
      const data = {
        sampleCode: document.getElementById('gs-sampleCode').value,
        participant: participantValue,
        service: document.getElementById('gs-service').value,
        collectedBy: document.getElementById('gs-collectedBy').value,
        collectedAt: document.getElementById('gs-collectedAt').value,
        sampleType: document.getElementById('gs-sampleType').value,
        note: document.getElementById('gs-note').value
      };
      options.onSubmit(data, modal);
    };
  }
}

// Hàm kiểm tra và thực hiện quy trình lấy mẫu cho 1 booking (gọi từ mọi nơi)
async function checkAndHandleSampleCollection(bookingId) {
  try {
    // Gọi API backend lấy trạng thái thực tế
    const res = await fetch(`/booking/${bookingId}/sample-collection-status`);
    if (!res.ok) throw new Error(await res.text());
    const status = await res.json();
    // 1. Kiểm tra số lượng participant
    if (status.numParticipantsActual < status.numParticipantsRequired) {
      // Mở modal thêm participant
      openParticipantModal({
        title: 'Thêm người tham gia',
        onSubmit: async (participantData, modal) => {
          try {
            // Thêm booking ID vào participantData
            const dataToSend = {
              ...participantData,
              booking: { id: bookingId }
            };
            
            const response = await fetch(`/participants`, {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              credentials: 'include',
              body: JSON.stringify(dataToSend)
            });
            if (!response.ok) throw new Error(await response.text());
            modal.remove();
            // Sau khi thêm, kiểm tra lại quy trình
            checkAndHandleSampleCollection(bookingId);
          } catch (err) {
            alert('Lỗi khi thêm người tham gia: ' + err.message);
          }
        }
      });
      return;
    }
    // 2. Kiểm tra từng dịch vụ
    const nextService = (status.services || []).find(s => s.sampleQuantityActual < s.sampleQuantityRequired);
    if (nextService) {
      // Mở modal thêm mẫu cho dịch vụ này
      // Lấy danh sách participant thực tế từ booking
      const partRes = await fetch(`/participants/booking/${bookingId}/participants`);
      const partList = partRes.ok ? await partRes.json() : [];
      
      // Lấy danh sách staff
      const staffRes = await fetch(`/participants/staff`);
      const staffList = staffRes.ok ? await staffRes.json() : [];
      
      // Lấy danh sách services của booking
      const serviceRes = await fetch(`/participants/booking/${bookingId}/services`);
      const serviceList = serviceRes.ok ? await serviceRes.json() : [];
      
      openSampleModal({
        title: 'Thêm mẫu cho dịch vụ',
        participantList: Array.isArray(partList) ? partList : [],
        staffList: Array.isArray(staffList) ? staffList : [],
        serviceList: Array.isArray(serviceList) ? serviceList : [],
        selectedServiceId: nextService.serviceId, // Dịch vụ được chọn mặc định
        onSubmit: async (sampleData, modal) => {
          try {
            // Chuẩn bị dữ liệu cho API
            const dataToSend = {
              sampleCode: sampleData.sampleCode,
              sampleType: sampleData.sampleType,
              collectedAt: sampleData.collectedAt,
              note: sampleData.note,
              participant: { id: parseInt(sampleData.participant) },
              service: { id: parseInt(sampleData.service) },
              collectedBy: { id: parseInt(sampleData.collectedBy) }
            };
            
            const response = await fetch(`/test-samples`, {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              credentials: 'include',
              body: JSON.stringify(dataToSend)
            });
            if (!response.ok) throw new Error(await response.text());
            modal.remove();
            // Sau khi thêm, kiểm tra lại quy trình
            checkAndHandleSampleCollection(bookingId);
          } catch (err) {
            alert('Lỗi khi thêm mẫu: ' + err.message);
          }
        }
      });
      return;
    }
    // 3. Nếu đủ hết, cập nhật trạng thái
    updateBookingStatusSampled(bookingId, () => {
      if (typeof window.showSuccessToast === 'function') window.showSuccessToast('Đã cập nhật trạng thái thành "Đã lấy mẫu"!');
      if (typeof window.fetchAppointments === 'function') window.fetchAppointments();
    });
  } catch (err) {
    alert('Lỗi kiểm tra quy trình lấy mẫu: ' + err.message);
  }
}

// Hàm gọi API cập nhật trạng thái booking sang "Đã lấy mẫu"
async function updateBookingStatusSampled(bookingId, callback) {
  try {
    const response = await fetch(`/booking/staff/appointments/${bookingId}/status`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ status: 'Đã lấy mẫu' })
    });
    if (!response.ok) throw new Error(await response.text());
    if (typeof callback === 'function') callback();
  } catch (err) {
    alert('Lỗi khi cập nhật trạng thái: ' + err.message);
  }
}

// Hàm mở modal thêm thanh toán mới cho 1 booking đã chọn
function openAddPaymentModal(options = {}) {
  // Xóa modal cũ nếu có
  const oldModal = document.getElementById('global-add-payment-modal');
  if (oldModal) oldModal.remove();

  const booking = options.booking || {};
  // booking: { id, customerName, remainingAmount, paymentMethod, method }
  const formattedAmount = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(booking.remainingAmount || 0);
  let paymentMethodOptions = '';
  if (booking.method === 'Tại nhà') {
    paymentMethodOptions += '<option value="COD">COD</option>';
    paymentMethodOptions += '<option value="BANK_TRANSFER">Chuyển khoản ngân hàng</option>';
  } else {
    paymentMethodOptions += '<option value="CASH">Tiền mặt</option>';
    paymentMethodOptions += '<option value="BANK_TRANSFER">Chuyển khoản ngân hàng</option>';
  }

  // Modal HTML
  const modal = document.createElement('div');
  modal.id = 'global-add-payment-modal';
  modal.className = 'modal';
  modal.style.display = 'flex';
  modal.innerHTML = `
    <div class="modal-content" style="max-width:600px;">
      <div class="modal-header">
        <h2 class="modal-title">Thêm thanh toán mới</h2>
        <span class="close" onclick="document.getElementById('global-add-payment-modal').remove()">&times;</span>
      </div>
      <form id="global-add-payment-form">
        <div style="background:#f5f5f5;padding:15px;border-radius:5px;margin-bottom:18px;">
          <div style="margin-bottom:8px;"><b>Mã lịch hẹn:</b> <span>${booking.id || ''}</span></div>
          <div style="margin-bottom:8px;"><b>Khách hàng:</b> <span>${booking.customerName || ''}</span></div>
          <div style="margin-bottom:8px;"><b>Số tiền còn lại:</b> <span>${formattedAmount}</span></div>
        </div>
        <div class="form-group" style="margin-bottom:15px;">
          <label for="ap-amount">Số tiền (VNĐ) <span class="required">*</span></label>
          <input type="number" id="ap-amount" name="amount" class="form-control" required min="1" value="${booking.remainingAmount || ''}">
        </div>
        <div class="form-group" style="margin-bottom:15px;">
          <label for="ap-method">Phương thức thanh toán <span class="required">*</span></label>
          <select id="ap-method" name="paymentMethod" class="form-control" required>${paymentMethodOptions}</select>
        </div>
        <div class="form-group" style="margin-bottom:15px;">
          <label for="ap-receipt">Ảnh biên lai <span style="color:#999;font-weight:normal;">(không bắt buộc)</span></label>
          <input type="file" id="ap-receipt" name="receiptImage" accept="image/*" style="display:block;">
          <div id="ap-receipt-preview" style="display:none;margin-top:10px;"></div>
        </div>
        <div class="form-group" style="margin-bottom:20px;">
          <label for="ap-notes">Ghi chú</label>
          <textarea id="ap-notes" name="notes" class="form-control" rows="3"></textarea>
        </div>
        <div style="text-align:right;">
          <button type="button" class="btn" style="background:#6c757d;color:white;margin-right:10px;" onclick="document.getElementById('global-add-payment-modal').remove()">Hủy</button>
          <button type="submit" class="btn btn-primary">Tạo thanh toán</button>
        </div>
      </form>
    </div>
  `;
  document.body.appendChild(modal);
  // Đóng modal khi click ra ngoài
  modal.addEventListener('click', function(e) { if (e.target === modal) modal.remove(); });

  // Ảnh preview
  const fileInput = document.getElementById('ap-receipt');
  const previewDiv = document.getElementById('ap-receipt-preview');
  fileInput.addEventListener('change', function(e) {
    const file = e.target.files[0];
    if (!file) { previewDiv.style.display = 'none'; previewDiv.innerHTML = ''; return; }
    const allowedTypes = ['image/jpeg','image/jpg','image/png','image/gif'];
    if (!allowedTypes.includes(file.type)) {
      alert('Chỉ hỗ trợ file ảnh: JPG, PNG, GIF');
      fileInput.value = '';
      previewDiv.style.display = 'none';
      previewDiv.innerHTML = '';
      return;
    }
    const maxSize = 5 * 1024 * 1024;
    if (file.size > maxSize) {
      alert('Kích thước file không được vượt quá 5MB');
      fileInput.value = '';
      previewDiv.style.display = 'none';
      previewDiv.innerHTML = '';
      return;
    }
    const reader = new FileReader();
    reader.onload = function(ev) {
      previewDiv.innerHTML = `<img src="${ev.target.result}" style="max-width:200px;max-height:150px;border-radius:8px;box-shadow:0 2px 8px rgba(0,0,0,0.1);">`;
      previewDiv.style.display = 'block';
    };
    reader.readAsDataURL(file);
  });

  // Submit event
  document.getElementById('global-add-payment-form').onsubmit = async function(e) {
    e.preventDefault();
    const amount = parseFloat(document.getElementById('ap-amount').value);
    const paymentMethod = document.getElementById('ap-method').value;
    const notes = document.getElementById('ap-notes').value;
    const receiptFile = document.getElementById('ap-receipt').files[0] || null;
    if (!amount || isNaN(amount) || amount <= 0) {
      alert('Vui lòng nhập số tiền hợp lệ!');
      return;
    }
    if (!paymentMethod) {
      alert('Vui lòng chọn phương thức thanh toán!');
      return;
    }
    const data = {
      bookingId: booking.id,
      amount,
      paymentMethod,
      notes,
      receiptFile
    };
    
    // --- Bổ sung logic cập nhật trạng thái booking ---
    try {
      // Gọi API tạo thanh toán
      let paymentResult;
      if (receiptFile) {
        const formData = new FormData();
        formData.append('bookingId', booking.id);
        formData.append('amount', amount);
        formData.append('paymentMethod', paymentMethod);
        formData.append('notes', notes);
        formData.append('receiptImage', receiptFile);
        paymentResult = await STAFF_API.PAYMENT.createPaymentWithReceipt(formData);
      } else {
        paymentResult = await STAFF_API.PAYMENT.createPayment(booking.id, amount, paymentMethod, notes);
      }
      // Sau khi tạo thanh toán thành công:
      if (typeof options.onSubmit === 'function') {
        options.onSubmit(data, modal);
      } else {
        if (typeof window.showSuccessToast === 'function') window.showSuccessToast('Tạo thanh toán thành công!');
        modal.remove();
      }
    } catch (err) {
      if (typeof window.showErrorToast === 'function') window.showErrorToast('Lỗi khi tạo thanh toán hoặc cập nhật trạng thái: ' + err.message);
    }
  };
}

/**
 * Modal thêm/sửa kết quả cho single/multi service (universal)
 * options: {
 *   bookingId: string|number,
 *   services: [{ serviceId, serviceName, resultFile }],
 *   onSubmit: (data, modal) => void,
 *   onClose: (modal) => void
 * }
 */
function openResultModalUniversal(options = {}) {
  // Xóa modal cũ nếu có
  const oldModal = document.getElementById('global-result-modal');
  if (oldModal) oldModal.remove();

  const { bookingId, services = [], onSubmit, onClose } = options;
  let selectedServiceIdx = null;
  let selectedFile = null;

  // Tạo modal
  const modal = document.createElement('div');
  modal.id = 'global-result-modal';
  modal.className = 'modal';
  modal.style.display = 'flex';

  // Render danh sách dịch vụ
  function renderServiceList() {
    // Hiển thị thông tin booking cơ bản
    let bookingInfoHtml = '';
    if (options.bookingInfo) {
      const b = options.bookingInfo;
      bookingInfoHtml = `
        <div style="background:#f5f5f5;padding:12px 18px;border-radius:8px;margin-bottom:18px;">
          <b>Mã lịch hẹn:</b> <span>${b.id || ''}</span> &nbsp; | &nbsp;
          <b>Khách hàng:</b> <span>${b.customerName || ''}</span> &nbsp; | &nbsp;
          <b>Ngày đặt:</b> <span>${b.bookingDate ? b.bookingDate.substring(0,10) : ''}</span> &nbsp; | &nbsp;
          <b>Phương thức:</b> <span>${b.method || ''}</span>
        </div>
      `;
    }
    // Kiểm tra tất cả dịch vụ đã có resultFile
    const allCompleted = services.length > 0 && services.every(s => !!s.resultFile);
    
    let returnResultBtn = '';
    if (allCompleted) {
      returnResultBtn = `
        <div style="text-align:right;margin-top:24px;">
          <button type="button" class="btn btn-success" id="btn-return-result"><i class="fas fa-share"></i> Trả kết quả</button>
        </div>
      `;
    }
    return `
      ${bookingInfoHtml}
      <div class="form-group full-width">
        <label class="form-label">Chọn dịch vụ cần thêm/sửa kết quả</label>
        <div style="display:flex;flex-direction:column;gap:12px;">
          ${services.map((s, idx) => `
            <div style="display:flex;align-items:center;gap:16px;background:#f8f9fa;padding:12px 18px;border-radius:10px;">
              <span style="flex:1;font-weight:600;">${s.serviceName}</span>
              ${s.resultFile ?
                `<span class="status-badge status-completed" style="margin-right:10px;">Đã có KQ</span>
                 <button class="btn btn-info" onclick="window._openResultServiceForm(${idx})"><i class='fas fa-edit'></i> Sửa</button>
                 <button class="btn btn-secondary" onclick="(async function() { const exists = await testFileAccess('${s.resultFile}'); if (exists) { openPdfViewerModal('${s.resultFile}', 'Kết quả: ${s.serviceName}'); } else { alert('File không tồn tại hoặc không thể truy cập!'); } })()" title="Xem file"><i class='fas fa-eye'></i> Xem</button>` :
                `<span class="status-badge status-pending" style="margin-right:10px;">Chưa có KQ</span>
                 <button class="btn btn-primary" onclick="window._openResultServiceForm(${idx})"><i class='fas fa-plus'></i> Thêm KQ</button>`
              }
              ${s.resultFile ? `<span style='font-size:13px;color:#666;margin-left:10px;'><i class='fas fa-file-pdf' style='color:#dc3545;'></i> ${s.resultFile}</span>` : ''}
            </div>
          `).join('')}
        </div>
      </div>
      ${returnResultBtn}
    `;
  }

  // Render form cho 1 dịch vụ
  function renderServiceForm(idx) {
    const s = services[idx];
    return `
      <div class="form-group full-width">
        <label class="form-label">Dịch vụ: <b>${s.serviceName}</b></label>
        ${s.resultFile ? `
          <div style='margin-bottom:15px;background:#f8f9fa;padding:12px;border-radius:8px;'>
            <div style='margin-bottom:8px;'><span class='status-badge status-completed'>File hiện tại:</span></div>
            <div style='display:flex;align-items:center;gap:10px;'>
              <span style='font-size:13px;color:#666;'><i class='fas fa-file-pdf' style='color:#dc3545;'></i> ${s.resultFile}</span>
              <button type='button' class='btn btn-sm btn-secondary' onclick="(async function() { const exists = await testFileAccess('${s.resultFile}'); if (exists) { openPdfViewerModal('${s.resultFile}', 'Kết quả: ${s.serviceName}'); } else { alert('File không tồn tại hoặc không thể truy cập!'); } })()" title="Xem file"><i class='fas fa-eye'></i> Xem</button>
            </div>
          </div>
        ` : ''}
        <label class="form-label">Upload file kết quả PDF <span class="required">*</span></label>
        <div class="file-upload" id="result-file-upload" style="margin-bottom:10px;cursor:pointer;" onclick="document.getElementById('result-file-input').click()">
          <div class="file-upload-icon"><i class="fas fa-cloud-upload-alt"></i></div>
          <div class="file-upload-text">Click để chọn file PDF hoặc kéo thả file vào đây</div>
          <div style="font-size:12px;color:#6c757d;">Chỉ hỗ trợ file PDF (Tối đa 10MB)</div>
        </div>
        <input type="file" id="result-file-input" style="display:none;" accept=".pdf">
        <div class="file-list" id="result-file-list"></div>
      </div>
      <div class="form-actions">
        <button type="button" class="btn" style="background:#6c757d;color:white;margin-right:10px;" id="result-cancel-btn"><i class="fas fa-arrow-left"></i> Quay lại</button>
        <button type="button" class="btn btn-primary" id="result-submit-btn"><i class="fas fa-save"></i> ${s.resultFile ? 'Cập nhật' : 'Lưu kết quả'}</button>
      </div>
    `;
  }

  // Hàm render modal nội dung
  function renderModalContent() {
    modal.innerHTML = `
      <div class="modal-content" style="max-width:650px;">
        <div class="modal-header">
          <h2 class="modal-title">Quản lý kết quả xét nghiệm</h2>
          <span class="close" onclick="document.getElementById('global-result-modal').remove()">&times;</span>
        </div>
        <div id="result-modal-body">
          ${selectedServiceIdx === null ? renderServiceList() : renderServiceForm(selectedServiceIdx)}
        </div>
      </div>
    `;
    document.body.appendChild(modal);
    
    // Đóng modal khi click ra ngoài
    modal.addEventListener('click', function(e) { 
      if (e.target === modal) { 
        modal.remove(); 
        if (typeof onClose === 'function') onClose(modal); 
      } 
    });
    
    // Gán lại window fn để gọi từ HTML string
    window._openResultServiceForm = (idx) => {
      selectedServiceIdx = idx;
      renderModalContent();
      setupFileUpload();
    };
    
    if (selectedServiceIdx !== null) setupFileUpload();
    
    // Gán nút cancel/quay lại
    setTimeout(() => {
      const cancelBtn = document.getElementById('result-cancel-btn');
      if (cancelBtn) {
        cancelBtn.onclick = () => { 
          selectedServiceIdx = null; 
          renderModalContent(); 
        };
      }
      
      const submitBtn = document.getElementById('result-submit-btn');
      if (submitBtn) {
        submitBtn.onclick = () => {
          submitResult();
        };
      }
      
      // Gán nút trả kết quả nếu có
      const btnReturn = document.getElementById('btn-return-result');
      if (btnReturn) {
        btnReturn.onclick = async function() {
          btnReturn.disabled = true;
          btnReturn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang trả kết quả...';
          try {
            const res = await fetch(`/booking/staff/appointments/${bookingId}/status`, {
              method: 'PUT',
              headers: { 'Content-Type': 'application/json' },
              credentials: 'include',
              body: JSON.stringify({ status: 'Đã trả kết quả' })
            });
            if (!res.ok) throw new Error(await res.text());
            if (typeof window.showSuccessToast === 'function') window.showSuccessToast('Đã trả kết quả thành công!');
            modal.remove();
            // Refresh trang sau khi trả kết quả thành công
            setTimeout(() => {
              window.location.reload();
            }, 1000);
          } catch (err) {
            if (typeof window.showErrorToast === 'function') window.showErrorToast('Lỗi khi trả kết quả: ' + err.message);
            btnReturn.disabled = false;
            btnReturn.innerHTML = '<i class="fas fa-share"></i> Trả kết quả';
          }
        };
      }
    }, 0);
  }

  // File upload logic
  function setupFileUpload() {
    const fileUpload = document.getElementById('result-file-upload');
    const fileInput = document.getElementById('result-file-input');
    const fileList = document.getElementById('result-file-list');
    
    if (!fileUpload || !fileInput) {
      return;
    }
    
    // Drag & drop
    fileUpload.addEventListener('dragover', (e) => { e.preventDefault(); fileUpload.classList.add('dragover'); });
    fileUpload.addEventListener('dragleave', () => { fileUpload.classList.remove('dragover'); });
    fileUpload.addEventListener('drop', (e) => {
      e.preventDefault(); 
      fileUpload.classList.remove('dragover');
      const files = e.dataTransfer.files;
      handleFiles(files);
    });
    
    fileInput.addEventListener('change', (e) => { 
      handleFiles(e.target.files); 
    });
    
    function handleFiles(files) {
      if (!files || !files[0]) return;
      const file = files[0];
      
      if (file.type !== 'application/pdf') { 
        alert('Chỉ chấp nhận file PDF!'); 
        fileInput.value = ''; 
        return; 
      }
      if (file.size > 10 * 1024 * 1024) { 
        alert('File quá lớn, tối đa 10MB!'); 
        fileInput.value = ''; 
        return; 
      }
      selectedFile = file;
      fileList.innerHTML = `<div class='file-item'><i class='fas fa-file-pdf' style='color:#dc3545;'></i> <span>${file.name}</span> <small style='color:#6c757d;margin-left:10px;'>(${(file.size/1024/1024).toFixed(2)} MB)</small> <button type='button' class='file-remove' onclick='window._removeResultFile()' title='Xóa file'>×</button></div>`;
    }
    
    window._removeResultFile = () => {
      selectedFile = null;
      fileInput.value = '';
      fileList.innerHTML = '';
    };
    
    // Reset file list khi vào form mới
    if (selectedServiceIdx !== null) {
      selectedFile = null;
      fileList.innerHTML = '';
    }
  }

  // Function refresh services data from backend
  async function refreshServicesData() {
    try {
      // Gọi API để lấy dữ liệu services mới nhất từ backend
      const response = await fetch(`/results/booking/${bookingId}/services`);
      
      if (response.ok) {
        const updatedServices = await response.json();

        // Cập nhật lại services array với dữ liệu mới, đảm bảo mapping đúng field
        updatedServices.forEach(newService => {
          const existingIndex = services.findIndex(s => s.serviceId === newService.serviceId);
          
          if (existingIndex !== -1) {
            // Map dữ liệu từ backend format sang format mà frontend expect
            services[existingIndex] = {
              ...services[existingIndex],
              serviceId: newService.serviceId,
              serviceName: newService.serviceName,
              resultFile: newService.resultFile, // Đây là field quan trọng
              hasResult: newService.hasResult
            };
          }
        });
        return true;
      } else {
        return false;
      }
    } catch (error) {
      console.error('Error refreshing services data:', error);
      return false;
    }
  }

  // Function test file access
  async function testFileAccess(fileName) {
    try {
      const response = await fetch(`/results/test-file/${fileName}`);
      
      if (response.ok) {
        const result = await response.json();
        return result.exists;
      }
      return false;
    } catch (error) {
      console.error('Error testing file access:', error);
      return false;
    }
  }

  // Function open PDF viewer modal
  function openPdfViewerModal(fileName, title = 'Xem kết quả') {
    const oldModal = document.getElementById('pdf-viewer-modal');
    if (oldModal) oldModal.remove();

    const modal = document.createElement('div');
    modal.id = 'pdf-viewer-modal';
    modal.className = 'modal';
    modal.style.display = 'flex';
    modal.innerHTML = `
      <div class="modal-content" style="max-width:90%;max-height:90%;width:90%;height:90%;">
        <div class="modal-header">
          <h2 class="modal-title">${title}</h2>
          <span class="close" onclick="document.getElementById('pdf-viewer-modal').remove()">&times;</span>
        </div>
        <div style="height:calc(100% - 60px);overflow:hidden;position:relative;">
          <div class="pdf-loading" id="pdf-loading">
            <i class="fas fa-spinner"></i> Đang tải file PDF...
          </div>
          <iframe src="/results/file/${fileName}" style="width:100%;height:100%;border:none;display:none;" id="pdf-iframe" onload="document.getElementById('pdf-loading').style.display='none';this.style.display='block';"></iframe>
        </div>
      </div>
    `;
    document.body.appendChild(modal);
    
    // Đóng modal khi click ra ngoài
    modal.addEventListener('click', function(e) { 
      if (e.target === modal) modal.remove(); 
    });

    // Timeout để ẩn loading nếu iframe không load được
    setTimeout(() => {
      const loading = document.getElementById('pdf-loading');
      const iframe = document.getElementById('pdf-iframe');
      if (loading && iframe.style.display === 'none') {
        loading.innerHTML = '<i class="fas fa-exclamation-triangle"></i> Không thể tải file PDF. Vui lòng thử lại.';
        loading.style.color = '#dc3545';
      }
    }, 10000);
  }

  // Make functions globally available
  window.testFileAccess = testFileAccess;
  window.openPdfViewerModal = openPdfViewerModal;

  // Submit result
  async function submitResult() {
    if (selectedServiceIdx === null) {
      return;
    }
    if (!selectedFile) { 
      alert('Vui lòng chọn file PDF kết quả!'); 
      return; 
    }
    
    const s = services[selectedServiceIdx];
    
    try {
      // Upload file trực tiếp vào server
      const formData = new FormData();
      formData.append('bookingId', bookingId);
      formData.append('serviceId', s.serviceId);
      formData.append('file', selectedFile);
      
      const response = await fetch('/results/upload', {
        method: 'POST',
        credentials: 'include',
        body: formData
      });
      
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }
      
      const result = await response.json();
      
      // Hiển thị thông báo thành công
      if (typeof window.showSuccessToast === 'function') {
        window.showSuccessToast(`Đã lưu kết quả thành công! File: ${result.fileName}`);
      } else {
        alert(`Đã lưu kết quả thành công! File: ${result.fileName}`);
      }
      
      // Cập nhật services array với file mới
      services[selectedServiceIdx].resultFile = result.fileName;
      
      // Reset file input và list
      const fileInput = document.getElementById('result-file-input');
      const fileList = document.getElementById('result-file-list');
      if (fileInput) fileInput.value = '';
      if (fileList) fileList.innerHTML = '';
      selectedFile = null;
      
      // Refresh dữ liệu services từ backend
      const refreshSuccess = await refreshServicesData();

      // Gọi callback nếu có
      if (typeof onSubmit === 'function') {
        onSubmit({
          bookingId,
          serviceId: s.serviceId,
          fileName: result.fileName,
          fileUrl: result.fileUrl,
          testResultId: result.testResultId
        }, modal, selectedServiceIdx);
      } else {
        // Quay lại danh sách services và refresh modal với dữ liệu mới
        selectedServiceIdx = null;
        renderModalContent();

        // Refresh lại danh sách appointments nếu có function
        if (typeof window.fetchAppointments === 'function') {
          window.fetchAppointments();
        }
      }
    } catch (err) {
      console.error('Upload error:', err);
      if (typeof window.showErrorToast === 'function') {
        window.showErrorToast('Lỗi khi upload kết quả: ' + err.message);
      } else {
        alert('Lỗi khi upload kết quả: ' + err.message);
      }
    }
  }

  renderModalContent();
}

window.openSampleModal = openSampleModal;
window.openParticipantModal = openParticipantModal;
