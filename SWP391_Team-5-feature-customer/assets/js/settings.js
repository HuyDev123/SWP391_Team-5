// settings.js - Functionality for the settings page

document.addEventListener('DOMContentLoaded', function() {
    // Navigation between settings sections
    const navItems = document.querySelectorAll('.settings-nav-item');
    const sections = document.querySelectorAll('.settings-section');
    
    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            
            // Update active navigation item
            navItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');
            
            // Show the corresponding section
            const targetId = item.getAttribute('href').slice(1);
            sections.forEach(section => {
                section.style.display = section.id === targetId ? 'block' : 'none';
            });
        });
    });
    
    // Theme switching
    const themeOptions = document.querySelectorAll('input[name="theme"]');
    themeOptions.forEach(option => {
        option.addEventListener('change', (e) => {
            const theme = e.target.value;
            document.body.className = ''; // Reset theme classes
            document.body.classList.add(`theme-${theme}`);
            
            // Save preference to localStorage
            localStorage.setItem('preferred-theme', theme);
            
            showNotification(`Chủ đề ${getThemeName(theme)} đã được áp dụng`);
        });
    });
    
    // Load saved theme preference
    const savedTheme = localStorage.getItem('preferred-theme');
    if (savedTheme) {
        const themeInput = document.querySelector(`input[name="theme"][value="${savedTheme}"]`);
        if (themeInput) {
            themeInput.checked = true;
            document.body.className = '';
            document.body.classList.add(`theme-${savedTheme}`);
        }
    }
    
    // Initialize toggle switches
    initToggleSwitches();
    
    // Initialize save button
    const saveSettingsBtn = document.getElementById('save-settings');
    if (saveSettingsBtn) {
        saveSettingsBtn.addEventListener('click', function() {
            saveSettings();
        });
    }
    
    // Initialize reset button
    const resetSettingsBtn = document.getElementById('reset-settings');
    if (resetSettingsBtn) {
        resetSettingsBtn.addEventListener('click', function() {
            if(confirm('Bạn có chắc chắn muốn đặt lại tất cả cài đặt về mặc định?')) {
                resetSettings();
            }
        });
    }
    
    // Initialize function card buttons
    const securitySettingsBtn = document.getElementById('security-settings-btn');
    if (securitySettingsBtn) {
        securitySettingsBtn.addEventListener('click', function() {
            scrollToSection('security-section');
        });
    }
    
    const notificationSettingsBtn = document.getElementById('notification-settings-btn');
    if (notificationSettingsBtn) {
        notificationSettingsBtn.addEventListener('click', function() {
            scrollToSection('notification-section');
        });
    }
    
    const languageSettingsBtn = document.getElementById('language-settings-btn');
    if (languageSettingsBtn) {
        languageSettingsBtn.addEventListener('click', function() {
            scrollToSection('language-section');
        });
    }
    
    const socialSettingsBtn = document.getElementById('social-settings-btn');
    if (socialSettingsBtn) {
        socialSettingsBtn.addEventListener('click', function() {
            scrollToSection('account-section');
        });
    }
    
    // Handle responsive sidebar
    const sidebarToggle = document.getElementById('sidebar-toggle');
    const sidebar = document.getElementById('sidebar');
    
    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', function() {
            sidebar.classList.toggle('active');
        });
        
        // Close sidebar when clicking outside on mobile
        document.addEventListener('click', function(event) {
            const isClickInsideSidebar = sidebar.contains(event.target);
            const isClickOnToggle = sidebarToggle.contains(event.target);
            
            if (!isClickInsideSidebar && !isClickOnToggle && sidebar.classList.contains('active') && window.innerWidth <= 768) {
                sidebar.classList.remove('active');
            }
        });
    }
});

// Initialize toggle switches
function initToggleSwitches() {
    document.querySelectorAll('.toggle-switch input[type="checkbox"]').forEach(toggle => {
        toggle.addEventListener('change', function() {
            // In a real app, we would save this change to the user's settings
            const settingName = this.id;
            const isEnabled = this.checked;
            console.log(`Setting ${settingName} changed to ${isEnabled}`);
        });
    });
}

// Save settings
function saveSettings() {
    // Get all settings values
    const settings = {};
    
    // Get toggle switch values
    document.querySelectorAll('.toggle-switch input[type="checkbox"]').forEach(toggle => {
        settings[toggle.id] = toggle.checked;
    });
    
    // Get select values
    document.querySelectorAll('select').forEach(select => {
        settings[select.id] = select.value;
    });
    
    // In a real app, we would save these settings to the user's account
    console.log('Settings saved:', settings);
    
    // Show success message
    showNotification('Đã lưu cài đặt thành công!', 'success');
}

// Reset settings to default
function resetSettings() {
    // Reset toggle switches
    document.querySelectorAll('input[type="checkbox"]').forEach(checkbox => {
        checkbox.checked = checkbox.id === 'notify-appointment' || 
                           checkbox.id === 'notify-results' || 
                           checkbox.id === 'notify-kit' || 
                           checkbox.id === 'notify-email' || 
                           checkbox.id === 'anonymous-results';
    });
    
    // Reset selects
    document.getElementById('language').value = 'vi';
    document.getElementById('date-format').value = 'dd/mm/yyyy';
    
    // Show success message
    showNotification('Đã đặt lại cài đặt về mặc định!', 'success');
}

// Scroll to a specific settings section
function scrollToSection(sectionId) {
    // Try to find the section by ID first
    let section = document.getElementById(sectionId);
    
    // If not found by ID, try to find by heading content
    if (!section) {
        // Map of section IDs to heading text to search for
        const sectionMap = {
            'notification-section': 'Thông báo',
            'security-section': 'Bảo mật & Quyền riêng tư',
            'account-section': 'Tài khoản',
            'language-section': 'Ngôn ngữ & Vùng'
        };
        
        const headingText = sectionMap[sectionId];
        if (headingText) {
            // Find the heading that contains this text
            const heading = Array.from(document.querySelectorAll('.settings-section h2')).find(h => 
                h.textContent.includes(headingText)
            );
            
            if (heading) {
                section = heading.closest('.settings-section');
            }
        }
    }
    
    // If section is found, scroll to it
    if (section) {
        section.scrollIntoView({ behavior: 'smooth' });
        
        // Highlight the section briefly
        section.classList.add('highlight-section');
        setTimeout(() => {
            section.classList.remove('highlight-section');
        }, 2000);
    }
}

// Show notification
function showNotification(message, type = 'info') {
    // Check if notification container exists, if not, create it
    let notificationContainer = document.getElementById('notification-container');
    
    if (!notificationContainer) {
        notificationContainer = document.createElement('div');
        notificationContainer.id = 'notification-container';
        document.body.appendChild(notificationContainer);
    }
    
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    
    // Add close button
    const closeBtn = document.createElement('button');
    closeBtn.className = 'notification-close';
    closeBtn.innerHTML = '&times;';
    closeBtn.addEventListener('click', function() {
        notification.remove();
    });
    
    notification.appendChild(closeBtn);
    notificationContainer.appendChild(notification);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        notification.remove();
    }, 5000);
}

// Helper functions
function getThemeName(theme) {
    const names = {
        'light': 'Sáng',
        'dark': 'Tối',
        'system': 'Theo hệ thống'
    };
    return names[theme] || theme;
} 