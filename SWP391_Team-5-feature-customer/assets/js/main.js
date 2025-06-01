// Main JavaScript file for Genx DNA Testing Customer Portal

document.addEventListener('DOMContentLoaded', function() {
    // Toggle sidebar on mobile
    const toggleSidebarBtn = document.querySelector('.toggle-sidebar');
    const sidebar = document.querySelector('.sidebar');
    const mainContent = document.querySelector('.main-content');
    
    if (toggleSidebarBtn) {
        toggleSidebarBtn.addEventListener('click', function() {
            sidebar.classList.toggle('active');
            document.body.classList.toggle('sidebar-open');
        });
    }
    
    // Close sidebar when clicking outside on mobile
    document.addEventListener('click', function(event) {
        const isMobile = window.innerWidth < 992;
        const clickedOutsideSidebar = !event.target.closest('.sidebar') && !event.target.closest('.toggle-sidebar');
        
        if (isMobile && clickedOutsideSidebar && sidebar.classList.contains('active')) {
            sidebar.classList.remove('active');
            document.body.classList.remove('sidebar-open');
        }
    });
    
    // Notification dropdown
    const notificationBtn = document.querySelector('.notification-btn');
    if (notificationBtn) {
        notificationBtn.addEventListener('click', function() {
            // Implement notification dropdown functionality here
            console.log('Notification button clicked');
        });
    }
    
    // Mark notification as read
    const markReadBtns = document.querySelectorAll('.btn-mark-read');
    markReadBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const notificationItem = this.closest('.notification-item');
            notificationItem.classList.remove('unread');
            this.setAttribute('disabled', true);
            
            // Here you would typically send an AJAX request to mark the notification as read in the backend
            console.log('Marked notification as read');
        });
    });
    
    // Mark all notifications as read
    const markAllReadBtn = document.querySelector('.btn-mark-all-read');
    if (markAllReadBtn) {
        markAllReadBtn.addEventListener('click', function() {
            const unreadNotifications = document.querySelectorAll('.notification-item.unread');
            unreadNotifications.forEach(item => {
                item.classList.remove('unread');
                const markReadBtn = item.querySelector('.btn-mark-read');
                if (markReadBtn) {
                    markReadBtn.setAttribute('disabled', true);
                }
            });
            
            // Here you would typically send an AJAX request to mark all notifications as read in the backend
            console.log('Marked all notifications as read');
        });
    }
    
    // Delete notification
    const deleteBtns = document.querySelectorAll('.btn-delete');
    deleteBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const notificationItem = this.closest('.notification-item');
            
            // Simple fade out animation
            notificationItem.style.opacity = '0';
            setTimeout(() => {
                notificationItem.style.height = '0';
                notificationItem.style.padding = '0';
                notificationItem.style.margin = '0';
                notificationItem.style.overflow = 'hidden';
                
                setTimeout(() => {
                    notificationItem.remove();
                    
                    // Update notification count if needed
                    const unreadCount = document.querySelectorAll('.notification-item.unread').length;
                    const badge = document.querySelector('.notification-btn .badge');
                    if (badge) {
                        badge.textContent = unreadCount;
                        if (unreadCount === 0) {
                            badge.style.display = 'none';
                        }
                    }
                }, 300);
            }, 200);
            
            // Here you would typically send an AJAX request to delete the notification in the backend
            console.log('Deleted notification');
        });
    });
    
    // Filter appointments
    const statusFilter = document.getElementById('status-filter');
    const dateFilter = document.getElementById('date-filter');
    const serviceFilter = document.getElementById('service-filter');
    
    function filterAppointments() {
        if (!statusFilter || !dateFilter || !serviceFilter) return;
        
        const statusValue = statusFilter.value;
        const dateValue = dateFilter.value;
        const serviceValue = serviceFilter.value;
        
        const appointments = document.querySelectorAll('.appointment-item');
        let visibleCount = 0;
        
        appointments.forEach(appointment => {
            const status = appointment.getAttribute('data-status');
            const date = appointment.getAttribute('data-date');
            const service = appointment.getAttribute('data-service');
            
            let statusMatch = statusValue === 'all' || status === statusValue;
            let dateMatch = dateValue === 'all' || (date && date === dateValue);
            let serviceMatch = serviceValue === 'all' || (service && service === serviceValue);
            
            if (statusMatch && dateMatch && serviceMatch) {
                appointment.style.display = 'block';
                visibleCount++;
            } else {
                appointment.style.display = 'none';
            }
        });
        
        // Show or hide empty state
        const emptyState = document.querySelector('.appointments-empty');
        if (emptyState) {
            emptyState.style.display = visibleCount === 0 ? 'block' : 'none';
        }
    }
    
    // Add event listeners to filters
    if (statusFilter) statusFilter.addEventListener('change', filterAppointments);
    if (dateFilter) dateFilter.addEventListener('change', filterAppointments);
    if (serviceFilter) serviceFilter.addEventListener('change', filterAppointments);
    
    // Initialize filters if they exist
    if (statusFilter && dateFilter && serviceFilter) {
        filterAppointments();
    }
    
    // Handle form submission
    const appointmentForm = document.getElementById('appointment-form');
    if (appointmentForm) {
        appointmentForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Form validation
            const serviceType = document.getElementById('service-type').value;
            const location = document.getElementById('location').value;
            const date = document.getElementById('date').value;
            const time = document.getElementById('time').value;
            const collectionMethod = document.querySelector('input[name="collection-method"]:checked');
            
            if (!serviceType || !location || !date || !time || !collectionMethod) {
                alert('Vui lòng điền đầy đủ thông tin.');
                return;
            }
            
            // Here you would typically send an AJAX request to submit the form data
            console.log('Form submitted:', {
                serviceType,
                location,
                date,
                time,
                collectionMethod: collectionMethod.value
            });
            
            // Show success message or redirect
            alert('Đặt lịch hẹn thành công!');
            // window.location.href = 'appointments.html';
        });
    }
    
    // Responsive adjustments
    function handleResize() {
        const isMobile = window.innerWidth < 992;
        
        if (isMobile) {
            sidebar.classList.remove('active');
            mainContent.style.marginLeft = '0';
        } else {
            mainContent.style.marginLeft = `${sidebar.offsetWidth}px`;
        }
    }
    
    window.addEventListener('resize', handleResize);
    handleResize();
}); 