// main.js - load động nội dung
function loadPage(page) {
  fetch(page + '.html')
    .then(res => res.text())
    .then(html => {
      document.getElementById('main-content').innerHTML = html;
      // Nếu là dashboard thì gắn lại sự kiện
      if (page === 'dashboard' && typeof setupDashboardActionButtons === 'function') {
        setupDashboardActionButtons();
      }
    });
}

// đảm bảo modal chỉ được load động vào body, không bao giờ có sẵn trong HTML
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
      if (modal) {
        modal.style.display = 'flex';
        modal.classList.add('modal-animate');
        setTimeout(() => modal.classList.remove('modal-animate'), 400);
      }
      if (modal) {
        modal.addEventListener('click', function(e) {
          if (e.target === modal) wrapper.remove();
        });
      }
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
    });
}

// function setupDashboardActionButtons() {
//   document.querySelectorAll('button.action-btn').forEach(btn => {
//     if (btn.classList.contains('view-btn')) {
//       btn.onclick = function() {
//         const row = btn.closest('tr');
//         if (row) showModal('appointment-modal.html');
//       };
//     } else if (btn.classList.contains('edit-btn')) {
//       btn.onclick = function() {
//         const row = btn.closest('tr');
//         if (row) showModal('status-modal.html');
//       };
//     } else if (btn.classList.contains('result-btn')) {
//       btn.onclick = function() {
//         const row = btn.closest('tr');
//         if (row) alert('Nhập kết quả cho: ' + row.children[0].textContent.trim());
//       };
//     }
//   });
// }

document.addEventListener('DOMContentLoaded', function() {
  loadPage('dashboard');
  setupDashboardActionButtons();
});
