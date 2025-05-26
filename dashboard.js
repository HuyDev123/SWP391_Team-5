// dashboard.js - xử lý dashboard
// (Thêm code xử lý dashboard nếu cần)

// Xử lý các nút thao tác trong dashboard
function showAppointmentsPage() {
  // Ẩn dashboard, hiện appointments-page
  document.querySelector('.dashboard').style.display = 'none';
  document.querySelector('.table-container').style.display = 'none';
  const apptPage = document.getElementById('appointments-page');
  if (apptPage) apptPage.style.display = 'block';
}

// Sử dụng fetch để load modal và chèn vào document.body
function showModal(modalFile) {
  let oldModal = document.getElementById('dynamic-modal');
  if (oldModal) oldModal.remove();
  
  fetch(modalFile)
    .then(res => res.text())
    .then(html => {
      const wrapper = document.createElement('div');
      wrapper.id = 'dynamic-modal';
      wrapper.innerHTML = html;
      document.body.appendChild(wrapper);
      
      const modal = wrapper.querySelector('.modal');
      if (!modal) {
        // If the loaded HTML doesn't have a .modal class element, wrap the content
        const content = wrapper.innerHTML;
        wrapper.innerHTML = `
          <div class="modal">
            <div class="modal-content">
              <span class="close-modal">&times;</span>
              ${content}
            </div>
          </div>
        `;
      }
      
      const modal2 = wrapper.querySelector('.modal');
      modal2.style.display = 'flex';
      modal2.classList.add('modal-animate');
      setTimeout(() => modal2.classList.remove('modal-animate'), 400);
      
      modal2.addEventListener('click', function(e) {
        if (e.target === modal2) wrapper.remove();
      });
      
      // Đóng/hủy modal
      const closeBtns = wrapper.querySelectorAll('.close-modal, .cancel-btn');
      closeBtns.forEach(btn => btn.addEventListener('click', () => wrapper.remove()));
      // Nút Chỉnh sửa
      const editBtn = wrapper.querySelector('.edit-appointment-btn');
      if (editBtn) {
        editBtn.addEventListener('click', function() {
          wrapper.remove();
          showModal('edit-appointment-modal.html');
        });
      }
      // Nút Cập nhật trạng thái
      const updateBtn = wrapper.querySelector('.update-status-btn');
      if (updateBtn) {
        updateBtn.addEventListener('click', function() {
          wrapper.remove();
          showModal('status-modal.html');
        });
      }
    })
    .catch(error => console.error('Error loading modal:', error));
}

function setupDashboardActionButtons() {
  // Nút Xem tất cả
  const viewAllBtn = document.querySelector('.section-actions button');
  if (viewAllBtn) {
    viewAllBtn.addEventListener('click', function() {
      showAppointmentsPage();
    });
  }
  // Nút Xem/Cập nhật/Kết quả
  document.querySelectorAll('button.action-btn').forEach(btn => {
    if (btn.classList.contains('view-btn')) {
      btn.addEventListener('click', function() {
        const row = btn.closest('tr');
        if (row) showModal('appointment-modal.html');
      });
    } else if (btn.classList.contains('update-status-btn')) {
      btn.addEventListener('click', function() {
        const row = btn.closest('tr');
        if (row) showModal('status-modal.html');
      });
    } else if (btn.classList.contains('edit-appointment-btn')) {
      btn.addEventListener('click', function() {
        const row = btn.closest('tr');
        if (row) showModal('edit-appointment-modal.html');
      });
    }
  });
}

document.addEventListener('DOMContentLoaded', function() {
  setupDashboardActionButtons();
});
