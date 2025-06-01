// dashboard.js
// Dashboard, hiển thị lịch hẹn, kết quả 

document.addEventListener('DOMContentLoaded', function() {
    // Dashboard statistics update
    function updateDashboardStats() {
        // In a real app, this would fetch data from an API
        const stats = {
            appointments: 2,
            kits: 1,
            results: 3
        };
        
        // Update stats on dashboard
        document.querySelectorAll('.stat-card').forEach(card => {
            // Just for demonstration, actual implementation would be more specific
            console.log('Updating dashboard statistics...');
        });
    }

    // Call this function to update dashboard stats
    updateDashboardStats();
    
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