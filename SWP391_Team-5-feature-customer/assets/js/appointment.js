// appointment.js - Quản lý lịch hẹn xét nghiệm ADN

document.addEventListener('DOMContentLoaded', function() {
    // ===== Khởi tạo các biến toàn cục =====
    const appointmentModal = document.getElementById('appointmentModal');
    const detailModal = document.getElementById('appointmentDetailModal');
    const appointmentForm = document.getElementById('appointmentForm');
    const collectionMethod = document.getElementById('collectionMethod');
    const addressField = document.getElementById('addressField');
    const appointmentsTable = document.querySelector('.appointments-table tbody');
    
    // ===== Khởi tạo dữ liệu mẫu =====
    const sampleAppointments = [
        {
            id: 'APT-20240715-001',
            serviceType: 'civil',
            service: 'Xét nghiệm cha con',
            date: '15/07/2024',
            time: '09:30',
            method: 'center',
            methodText: 'Tại trung tâm',
            status: 'da-dat',
            statusText: 'Đã đặt',
            location: 'Trung tâm xét nghiệm ADN, 123 Nguyễn Huệ, Q1',
            notes: 'Khách hàng yêu cầu kết quả gấp.',
            persons: [
                { name: 'Nguyễn Văn A', role: 'Cha', age: 42, sampleType: 'Tế bào má' },
                { name: 'Nguyễn Văn B', role: 'Con', age: 12, sampleType: 'Tế bào má' }
            ]
        },
        {
            id: 'APT-20240708-002',
            serviceType: 'civil',
            service: 'Xét nghiệm mẹ con',
            date: '08/07/2024',
            time: '14:00',
            method: 'home',
            methodText: 'Tại nhà',
            status: 'dang-cho-lay-mau',
            statusText: 'Đang chờ lấy mẫu',
            location: '456 Đường Lê Lợi, Q3, TP.HCM',
            notes: 'Liên hệ trước khi đến.',
            persons: [
                { name: 'Trần Thị C', role: 'Mẹ', age: 35, sampleType: 'Tế bào má' },
                { name: 'Trần Văn D', role: 'Con', age: 8, sampleType: 'Tế bào má' }
            ]
        },
        {
            id: 'APT-20240701-003',
            serviceType: 'admin',
            service: 'Xét nghiệm toàn diện',
            date: '01/07/2024',
            time: '10:15',
            method: 'center',
            methodText: 'Tại trung tâm',
            status: 'da-nhan-mau',
            statusText: 'Đã nhận mẫu',
            location: 'Trung tâm xét nghiệm ADN, 123 Nguyễn Huệ, Q1',
            notes: '',
            persons: [
                { name: 'Lê Văn E', role: 'Cha', age: 45, sampleType: 'Tế bào má' },
                { name: 'Lê Thị F', role: 'Mẹ', age: 40, sampleType: 'Tế bào má' },
                { name: 'Lê Văn G', role: 'Con', age: 15, sampleType: 'Tế bào má' }
            ]
        },
        {
            id: 'APT-20240624-004',
            serviceType: 'admin',
            service: 'Xét nghiệm cha con',
            date: '24/06/2024',
            time: '08:00',
            method: 'center',
            methodText: 'Tại trung tâm',
            status: 'da-hoan',
            statusText: 'Đã hoàn thành',
            location: 'Trung tâm xét nghiệm ADN, 123 Nguyễn Huệ, Q1',
            notes: '',
            persons: [
                { name: 'Phạm Văn H', role: 'Cha', age: 50, sampleType: 'Tế bào má' },
                { name: 'Phạm Thị I', role: 'Con', age: 22, sampleType: 'Tế bào má' }
            ]
        }
    ];
    
    // Lưu vào localStorage nếu chưa có
    if (!localStorage.getItem('appointments')) {
        localStorage.setItem('appointments', JSON.stringify(sampleAppointments));
    }
    
    // ===== Khởi tạo các sự kiện =====
    
    // Hiển thị modal đặt lịch
    document.getElementById('createAppointmentBtn')?.addEventListener('click', openAppointmentModal);
    document.getElementById('newAppointmentBtn')?.addEventListener('click', openAppointmentModal);
    document.getElementById('facilityCollectionBtn')?.addEventListener('click', () => {
        openAppointmentModal('center');
    });
    document.getElementById('selfCollectionBtn')?.addEventListener('click', () => {
        openAppointmentModal('self');
    });
    
    // Đóng modal
    document.getElementById('closeAppointmentModal')?.addEventListener('click', closeAppointmentModal);
    document.getElementById('cancelAppointmentBtn')?.addEventListener('click', closeAppointmentModal);
    document.getElementById('closeDetailModal')?.addEventListener('click', closeDetailModal);
    
    // Xử lý thay đổi phương thức thu mẫu
    collectionMethod?.addEventListener('change', handleCollectionMethodChange);
    
    // Xử lý submit form đặt lịch
    appointmentForm?.addEventListener('submit', handleAppointmentSubmit);
    
    // Xử lý hủy lịch hẹn
    document.getElementById('cancelDetailBtn')?.addEventListener('click', handleCancelAppointment);
    
    // Xử lý chỉnh sửa lịch hẹn
    document.getElementById('editDetailBtn')?.addEventListener('click', handleEditAppointment);
    
    // Xử lý lọc lịch hẹn
    document.getElementById('statusFilter')?.addEventListener('change', filterAppointments);
    document.getElementById('dateFilter')?.addEventListener('change', filterAppointments);
    document.getElementById('searchBtn')?.addEventListener('click', searchAppointments);
    document.getElementById('resetFilters')?.addEventListener('click', resetFilters);
    
    // Khởi tạo dữ liệu ban đầu
    loadAppointments();
    
    // ===== Các hàm xử lý =====
    
    // Mở modal đặt lịch
    function openAppointmentModal(methodType = null) {
        if (!appointmentModal) return;
        
        appointmentForm.reset();
        
        if (methodType) {
            collectionMethod.value = methodType;
            handleCollectionMethodChange();
        }
        
        appointmentModal.style.display = 'block';
    }
    
    // Đóng modal đặt lịch
    function closeAppointmentModal() {
        if (!appointmentModal) return;
        appointmentModal.style.display = 'none';
    }
    
    // Đóng modal chi tiết
    function closeDetailModal() {
        if (!detailModal) return;
        detailModal.style.display = 'none';
    }
    
    // Xử lý thay đổi phương thức thu mẫu
    function handleCollectionMethodChange() {
        if (!collectionMethod || !addressField) return;
        
        if (collectionMethod.value === 'home') {
            addressField.style.display = 'block';
            addressField.querySelector('textarea').setAttribute('required', 'required');
        } else {
            addressField.style.display = 'none';
            addressField.querySelector('textarea').removeAttribute('required');
        }
    }
    
    // Xử lý submit form đặt lịch
    function handleAppointmentSubmit(e) {
        e.preventDefault();
        
        // Lấy dữ liệu từ form
        const serviceType = document.getElementById('serviceType').value;
        const service = document.getElementById('service').value;
        const method = collectionMethod.value;
        const date = document.getElementById('appointmentDate').value;
        const time = document.getElementById('appointmentTime').value;
        const notes = document.getElementById('notes').value;
        const address = method === 'home' ? document.getElementById('address').value : '';
        
        // Tạo ID mới
        const today = new Date();
        const idDate = today.toISOString().slice(0, 10).replace(/-/g, '');
        const appointments = getAppointments();
        const count = appointments.length + 1;
        const id = `APT-${idDate}-${count.toString().padStart(3, '0')}`;
        
        // Tạo đối tượng lịch hẹn mới
        const newAppointment = {
            id: id,
            serviceType: serviceType,
            service: getServiceText(service),
            date: formatDate(date),
            time: time,
            method: method,
            methodText: getMethodText(method),
            status: 'da-dat',
            statusText: 'Đã đặt',
            location: method === 'home' ? address : 'Trung tâm xét nghiệm ADN, 123 Nguyễn Huệ, Q1',
            notes: notes,
            persons: []
        };
        
        // Thêm vào danh sách
        appointments.unshift(newAppointment);
        localStorage.setItem('appointments', JSON.stringify(appointments));
        
        // Hiển thị thông báo và đóng modal
        showNotification('Đặt lịch thành công!', 'success');
        closeAppointmentModal();
        
        // Cập nhật danh sách
        loadAppointments();
    }
    
    // Xử lý hủy lịch hẹn
    function handleCancelAppointment() {
        const id = document.getElementById('detailId').textContent;
        
        if (confirm('Bạn có chắc muốn hủy lịch hẹn này?')) {
            let appointments = getAppointments();
            appointments = appointments.filter(appointment => appointment.id !== id);
            localStorage.setItem('appointments', JSON.stringify(appointments));
            
            showNotification('Đã hủy lịch hẹn thành công!', 'success');
            closeDetailModal();
            loadAppointments();
        }
    }
    
    // Xử lý chỉnh sửa lịch hẹn
    function handleEditAppointment() {
        const id = document.getElementById('detailId').textContent;
        const appointments = getAppointments();
        const appointment = appointments.find(item => item.id === id);
        
        if (appointment) {
            // Đóng modal chi tiết
            closeDetailModal();
            
            // Mở modal đặt lịch và điền thông tin
            openAppointmentModal();
            
            // Điền thông tin vào form
            document.getElementById('serviceType').value = appointment.serviceType;
            
            // Chuyển đổi tên dịch vụ thành giá trị
            const serviceMap = {
                'Xét nghiệm cha con': 'father_child',
                'Xét nghiệm mẹ con': 'mother_child',
                'Xét nghiệm toàn diện': 'family'
            };
            document.getElementById('service').value = serviceMap[appointment.service] || '';
            
            document.getElementById('collectionMethod').value = appointment.method;
            
            // Chuyển đổi định dạng ngày
            const dateParts = appointment.date.split('/');
            const formattedDate = `${dateParts[2]}-${dateParts[1].padStart(2, '0')}-${dateParts[0].padStart(2, '0')}`;
            document.getElementById('appointmentDate').value = formattedDate;
            
            document.getElementById('appointmentTime').value = appointment.time;
            document.getElementById('notes').value = appointment.notes;
            
            if (appointment.method === 'home') {
                document.getElementById('address').value = appointment.location;
                handleCollectionMethodChange();
            }
        }
    }
    
    // Tải danh sách lịch hẹn
function loadAppointments() {
    if (!appointmentsTable) return;
    
        // Xóa dữ liệu cũ
    appointmentsTable.innerHTML = '';
    
        // Lấy danh sách lịch hẹn
    const appointments = getAppointments();
    
        // Hiển thị dữ liệu
        if (appointments.length === 0) {
            const emptyRow = document.createElement('tr');
            emptyRow.innerHTML = `<td colspan="6" class="empty-message">Không có lịch hẹn nào.</td>`;
            appointmentsTable.appendChild(emptyRow);
        } else {
    appointments.forEach(appointment => {
        const row = createAppointmentRow(appointment);
        appointmentsTable.appendChild(row);
    });
        }
}

    // Tạo hàng cho bảng lịch hẹn
function createAppointmentRow(appointment) {
    const row = document.createElement('tr');
    
        // ID
    const idCell = document.createElement('td');
    idCell.textContent = appointment.id;
    row.appendChild(idCell);
    
        // Dịch vụ
    const serviceCell = document.createElement('td');
    serviceCell.textContent = appointment.service;
    row.appendChild(serviceCell);
    
        // Ngày
    const dateCell = document.createElement('td');
        dateCell.textContent = `${appointment.date} ${appointment.time}`;
    row.appendChild(dateCell);
    
        // Phương thức
    const methodCell = document.createElement('td');
        methodCell.textContent = appointment.methodText;
    row.appendChild(methodCell);
    
        // Trạng thái
    const statusCell = document.createElement('td');
    const statusBadge = document.createElement('span');
    statusBadge.className = `status-badge ${appointment.status}`;
        statusBadge.textContent = appointment.statusText;
    statusCell.appendChild(statusBadge);
    row.appendChild(statusCell);
    
        // Thao tác
    const actionsCell = document.createElement('td');
    actionsCell.className = 'actions';
    
        // Nút xem chi tiết
    const viewBtn = document.createElement('button');
    viewBtn.className = 'btn btn-sm btn-info';
    viewBtn.innerHTML = '<i class="fas fa-eye"></i>';
        viewBtn.addEventListener('click', () => viewAppointmentDetails(appointment.id));
    actionsCell.appendChild(viewBtn);
        
        // Nút hủy lịch hẹn (chỉ hiển thị khi chưa hoàn thành)
        if (appointment.status !== 'da-hoan') {
            const cancelBtn = document.createElement('button');
            cancelBtn.className = 'btn btn-sm btn-danger';
            cancelBtn.innerHTML = '<i class="fas fa-times"></i>';
            cancelBtn.addEventListener('click', () => {
                if (confirm('Bạn có chắc muốn hủy lịch hẹn này?')) {
                    cancelAppointment(appointment.id);
                }
            });
            actionsCell.appendChild(cancelBtn);
        }
        
        row.appendChild(actionsCell);
        return row;
    }
    
    // Xem chi tiết lịch hẹn
    function viewAppointmentDetails(id) {
        if (!detailModal) return;
        
        const appointments = getAppointments();
        const appointment = appointments.find(item => item.id === id);
        
        if (appointment) {
            // Điền thông tin vào modal
            document.getElementById('detailId').textContent = appointment.id;
            document.getElementById('detailService').textContent = appointment.service;
            document.getElementById('detailDateTime').textContent = `${appointment.date} ${appointment.time}`;
            document.getElementById('detailMethod').textContent = appointment.methodText;
            document.getElementById('detailLocation').textContent = appointment.location;
            
            const statusElement = document.getElementById('detailStatus');
            statusElement.innerHTML = `<span class="status-badge ${appointment.status}">${appointment.statusText}</span>`;
            
            document.getElementById('detailNotes').textContent = appointment.notes || 'Không có ghi chú';
            
            // Hiển thị thông tin người tham gia
            const personCards = document.getElementById('personCards');
            personCards.innerHTML = '';
            
            if (appointment.persons && appointment.persons.length > 0) {
                appointment.persons.forEach(person => {
        const personCard = document.createElement('div');
        personCard.className = 'person-card';
        personCard.innerHTML = `
            <div class="person-card-header">
                <i class="fas fa-user-circle"></i>
                <div class="person-name">${person.name}</div>
            </div>
            <div class="person-card-body">
                <div class="person-info-item">
                    <span class="info-label">Vai trò:</span>
                    <span class="info-value">${person.role}</span>
                </div>
                <div class="person-info-item">
                    <span class="info-label">Tuổi:</span>
                    <span class="info-value">${person.age}</span>
                </div>
                <div class="person-info-item">
                    <span class="info-label">Loại mẫu:</span>
                    <span class="info-value">${person.sampleType}</span>
                </div>
            </div>
        `;
                    personCards.appendChild(personCard);
                });
    } else {
                personCards.innerHTML = '<p>Không có thông tin người tham gia.</p>';
            }
            
            // Ẩn/hiện nút chỉnh sửa và hủy dựa vào trạng thái
            document.getElementById('editDetailBtn').style.display = appointment.status === 'da-hoan' ? 'none' : 'inline-block';
            document.getElementById('cancelDetailBtn').style.display = appointment.status === 'da-hoan' ? 'none' : 'inline-block';
            
            // Hiển thị modal
            detailModal.style.display = 'block';
        }
    }
    
    // Hủy lịch hẹn
    function cancelAppointment(id) {
        let appointments = getAppointments();
        appointments = appointments.filter(appointment => appointment.id !== id);
        localStorage.setItem('appointments', JSON.stringify(appointments));
        
        showNotification('Đã hủy lịch hẹn thành công!', 'success');
        loadAppointments();
    }
    
    // Lọc lịch hẹn
    function filterAppointments() {
        if (!appointmentsTable) return;
        
        const statusFilter = document.getElementById('statusFilter').value;
        const dateFilter = document.getElementById('dateFilter').value;
        
        const rows = document.querySelectorAll('.appointments-table tbody tr');
        
        rows.forEach(row => {
            let showRow = true;
            
            // Lọc theo trạng thái
            if (statusFilter !== 'all') {
                const statusCell = row.querySelector('td:nth-child(5)');
                const statusBadge = statusCell.querySelector('.status-badge');
                if (!statusBadge.classList.contains(statusFilter)) {
                    showRow = false;
                }
            }
            
            // Lọc theo thời gian
            if (dateFilter !== 'all' && showRow) {
                const dateCell = row.querySelector('td:nth-child(3)').textContent;
                const appointmentDate = parseDate(dateCell);
                const today = new Date();
                
                if (dateFilter === 'upcoming' && appointmentDate < today) {
                    showRow = false;
                } else if (dateFilter === 'past' && appointmentDate > today) {
                    showRow = false;
                }
            }
            
            row.style.display = showRow ? '' : 'none';
        });
    }
    
    // Tìm kiếm lịch hẹn
    function searchAppointments() {
        if (!appointmentsTable) return;
        
        const searchTerm = document.getElementById('appointmentSearch').value.toLowerCase();
        const rows = document.querySelectorAll('.appointments-table tbody tr');
        
        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            row.style.display = text.includes(searchTerm) ? '' : 'none';
        });
    }
    
    // Đặt lại bộ lọc
    function resetFilters() {
        if (!appointmentsTable) return;
        
        document.getElementById('statusFilter').value = 'all';
        document.getElementById('dateFilter').value = 'all';
        document.getElementById('appointmentSearch').value = '';
        
        const rows = document.querySelectorAll('.appointments-table tbody tr');
        rows.forEach(row => {
            row.style.display = '';
        });
    }
    
    // ===== Các hàm tiện ích =====
    
    // Lấy danh sách lịch hẹn từ localStorage
    function getAppointments() {
        const appointmentsData = localStorage.getItem('appointments');
        return appointmentsData ? JSON.parse(appointmentsData) : [];
    }
    
    // Hiển thị thông báo
    function showNotification(message, type = 'info') {
        const notification = document.getElementById('notification');
        if (!notification) return;
        
        const icon = notification.querySelector('.notification-icon');
        const msg = notification.querySelector('.notification-message');
        
        icon.className = 'notification-icon';
        if (type === 'success') icon.classList.add('fas', 'fa-check-circle');
        else if (type === 'error') icon.classList.add('fas', 'fa-times-circle');
        else icon.classList.add('fas', 'fa-info-circle');
        
        msg.textContent = message;
        notification.classList.add('show');
        
        setTimeout(() => notification.classList.remove('show'), 3000);
    }
    
    // Chuyển đổi định dạng ngày
    function formatDate(dateString) {
        const date = new Date(dateString);
        return `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getFullYear()}`;
    }
    
    // Chuyển đổi chuỗi ngày thành đối tượng Date
    function parseDate(dateString) {
        const parts = dateString.split(' ')[0].split('/');
        return new Date(parts[2], parts[1] - 1, parts[0]);
    }
    
    // Lấy text của phương thức thu mẫu
    function getMethodText(method) {
        const methodMap = {
            'center': 'Tại trung tâm',
            'home': 'Tại nhà',
            'self': 'Tự thu mẫu'
        };
        return methodMap[method] || method;
    }
    
    // Lấy text của dịch vụ
    function getServiceText(service) {
        const serviceMap = {
            'father_child': 'Xét nghiệm cha con',
            'mother_child': 'Xét nghiệm mẹ con',
            'family': 'Xét nghiệm toàn diện'
        };
        return serviceMap[service] || service;
    }
});
