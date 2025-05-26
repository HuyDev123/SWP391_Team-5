// menu.js - sự kiện menu
window.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('.menu-link[data-page]').forEach(link => {
    link.addEventListener('click', function(e) {
      e.preventDefault();
      document.querySelectorAll('.menu-link').forEach(l => l.classList.remove('active'));
      this.classList.add('active');
      loadPage(this.getAttribute('data-page'));
    });
  });
});
