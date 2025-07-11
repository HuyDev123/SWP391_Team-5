// Script to update main menu across all pages with glassmorphism design
// Run this in browser console or add to each page

const GLASSMORPHISM_MENU_STYLES = `
.menu-title {
  padding: 20px 20px 10px 20px;
  color: #666;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1.5px;
  position: relative;
}

.menu-title::after {
  content: '';
  position: absolute;
  bottom: 5px;
  left: 20px;
  width: 30px;
  height: 2px;
  background: linear-gradient(90deg, #2196f3, #64b5f6);
  border-radius: 1px;
}

#mainMenu {
  padding: 10px 15px;
}

.menu-item {
  padding: 14px 16px;
  display: flex;
  align-items: center;
  color: #555;
  text-decoration: none;
  border-radius: 12px;
  margin-bottom: 6px;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  overflow: hidden;
  font-weight: 500;
}

.menu-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, 
    transparent, 
    rgba(33, 150, 243, 0.1), 
    transparent
  );
  transition: left 0.6s ease;
}

.menu-item:hover::before {
  left: 100%;
}

.menu-item:hover {
  background: linear-gradient(135deg, 
    rgba(33, 150, 243, 0.1), 
    rgba(33, 150, 243, 0.05)
  );
  color: #2196f3;
  transform: translateX(8px);
  box-shadow: 0 4px 20px rgba(33, 150, 243, 0.2);
}

.menu-item.active {
  background: linear-gradient(135deg, #2196f3, #1976d2);
  color: white;
  transform: translateX(8px);
  box-shadow: 
    0 8px 25px rgba(33, 150, 243, 0.4),
    0 0 0 1px rgba(255, 255, 255, 0.1);
}

.menu-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 14px;
  border-radius: 10px;
  background: linear-gradient(135deg, 
    rgba(33, 150, 243, 0.15), 
    rgba(33, 150, 243, 0.08)
  );
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  overflow: hidden;
}

.menu-icon::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: radial-gradient(circle, 
    rgba(255, 255, 255, 0.3), 
    transparent 70%
  );
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: all 0.3s ease;
}

.menu-item:hover .menu-icon {
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.9), 
    rgba(255, 255, 255, 0.7)
  );
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 6px 20px rgba(33, 150, 243, 0.3);
}

.menu-item:hover .menu-icon::after {
  width: 30px;
  height: 30px;
}

.menu-item.active .menu-icon {
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.3), 
    rgba(255, 255, 255, 0.1)
  );
  transform: scale(1.05);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.menu-icon i {
  font-size: 18px;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  z-index: 1;
  position: relative;
}

.menu-item:hover .menu-icon i {
  transform: scale(1.2);
  color: #2196f3;
}

.menu-item.active .menu-icon i {
  color: white;
  transform: scale(1.1);
}

.sidebar {
  width: 280px !important;
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.95), 
    rgba(255, 255, 255, 0.85)
  ) !important;
  backdrop-filter: blur(20px) !important;
  border-right: 1px solid rgba(255, 255, 255, 0.2) !important;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.5) !important;
}

.main-content {
  margin-left: 280px !important;
  width: calc(100% - 280px) !important;
}
`;

const STANDARD_MENU_HTML = (activePage) => `
<a href="index.html" class="menu-item ${activePage === 'index' ? 'active' : ''}">
  <span class="menu-icon"><i class="fas fa-home"></i></span>
  Trang chủ
</a>

<a href="booking.html" class="menu-item ${activePage === 'booking' ? 'active' : ''}">
  <span class="menu-icon"><i class="fas fa-calendar-plus"></i></span>
  Đặt lịch xét nghiệm
</a>

<a href="appoinments.html" class="menu-item ${activePage === 'appoinments' ? 'active' : ''}">
  <span class="menu-icon"><i class="fas fa-clipboard-list"></i></span>
  Danh sách lịch hẹn
</a>

<a href="list-kit.html" class="menu-item ${activePage === 'list-kit' ? 'active' : ''}">
  <span class="menu-icon"><i class="fas fa-vials"></i></span>
  Danh sách kit xét nghiệm
</a>

<a href="participants-management.html" class="menu-item ${activePage === 'participants-management' ? 'active' : ''}">
  <span class="menu-icon"><i class="fas fa-users"></i></span>
  Quản lý người tham gia
</a>

<a href="sample-management.html" class="menu-item ${activePage === 'sample-management' ? 'active' : ''}">
  <span class="menu-icon"><i class="fas fa-flask"></i></span>
  Quản lý mẫu xét nghiệm
</a>

<a href="results-management.html" class="menu-item ${activePage === 'results-management' ? 'active' : ''}">
  <span class="menu-icon"><i class="fas fa-chart-line"></i></span>
  Quản lý kết quả xét nghiệm
</a>
`;

function updateMainMenu() {
  // Get current page name
  const currentPage = window.location.pathname.split('/').pop().replace('.html', '');
  
  // Update styles
  const styleElement = document.createElement('style');
  styleElement.textContent = GLASSMORPHISM_MENU_STYLES;
  document.head.appendChild(styleElement);
  
  // Update menu HTML
  const mainMenuDiv = document.getElementById('mainMenu');
  if (mainMenuDiv) {
    mainMenuDiv.innerHTML = STANDARD_MENU_HTML(currentPage);
  }
}

// Auto-run when script is loaded
if (typeof window !== 'undefined') {
  updateMainMenu();
}

// Export for manual use
if (typeof module !== 'undefined') {
  module.exports = { updateMainMenu, GLASSMORPHISM_MENU_STYLES, STANDARD_MENU_HTML };
}
