// service.js - Quản lý đăng ký dịch vụ xét nghiệm ADN

document.addEventListener('DOMContentLoaded', function() {
    // ===== Khởi tạo các biến toàn cục =====
    const serviceForm = document.getElementById('serviceForm');
    const serviceCards = document.querySelectorAll('.service-card');
    const serviceTypeSelect = document.getElementById('serviceType');
    const serviceSelect = document.getElementById('service');
    const collectionMethodSelect = document.getElementById('collectionMethod');
    const addressField = document.getElementById('addressField');
    const participantsContainer = document.getElementById('participantsContainer');
    const addParticipantBtn = document.getElementById('addParticipantBtn');
    
    // Biến đếm người tham gia
    let participantCount = 0;
    
    // ===== Khởi tạo các sự kiện =====
    
    // Xử lý khi chọn service card
    serviceCards.forEach(card => {
        card.addEventListener('click', function() {
            const type = this.getAttribute('data-type');
            
            // Xóa active class từ tất cả cards
            serviceCards.forEach(c => c.classList.remove('active'));
            
            // Thêm active class vào card được chọn
            this.classList.add('active');
            
            // Cập nhật select
            serviceTypeSelect.value = type;
            
            // Cập nhật các tùy chọn dựa trên loại dịch vụ
            updateServiceOptions(type);
            
            // Cuộn đến form
            document.querySelector('.section-container').scrollIntoView({ behavior: 'smooth' });
        });
    });
    
    // Xử lý khi thay đổi loại dịch vụ
    serviceTypeSelect.addEventListener('change', function() {
        const type = this.value;
        
        // Cập nhật active class cho service cards
        serviceCards.forEach(card => {
            if (card.getAttribute('data-type') === type) {
                card.classList.add('active');
            } else {
                card.classList.remove('active');
            }
        });
        
        // Cập nhật các tùy chọn dựa trên loại dịch vụ
        updateServiceOptions(type);
    });
    
    // Xử lý khi thay đổi dịch vụ xét nghiệm
    serviceSelect.addEventListener('change', function() {
        const service = this.value;
        
        // Xóa người tham gia hiện tại
        participantsContainer.innerHTML = '';
        participantCount = 0;
        
        // Thêm người tham gia mặc định dựa trên loại dịch vụ
        addDefaultParticipants(service);
    });
    
    // Xử lý khi thay đổi phương thức thu mẫu
    collectionMethodSelect.addEventListener('change', function() {
        const method = this.value;
        const serviceType = serviceTypeSelect.value;
        
        // Nếu là xét nghiệm hành chính và không phải tại trung tâm, hiển thị thông báo
        if (serviceType === 'admin' && method !== 'center') {
            showNotification('Xét nghiệm ADN hành chính chỉ được thực hiện tại trung tâm.', 'warning');
            this.value = 'center';
            return;
        }
        
        // Hiển thị/ẩn trường địa chỉ
        if (method === 'home') {
            addressField.style.display = 'block';
            addressField.querySelector('textarea').setAttribute('required', 'required');
        } else {
            addressField.style.display = 'none';
            addressField.querySelector('textarea').removeAttribute('required');
        }
    });
    
    // Xử lý khi click nút thêm người tham gia
    addParticipantBtn.addEventListener('click', function() {
        addParticipant();
    });
    
    // Xử lý submit form
    serviceForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Kiểm tra số lượng người tham gia
        if (participantCount < 2) {
            showNotification('Vui lòng thêm ít nhất 2 người tham gia xét nghiệm.', 'error');
            return;
        }
        
        // Lấy dữ liệu form
        const formData = new FormData(serviceForm);
        const serviceData = {
            serviceType: formData.get('serviceType'),
            service: formData.get('service'),
            collectionMethod: formData.get('collectionMethod'),
            fullname: formData.get('fullname'),
            phone: formData.get('phone'),
            email: formData.get('email'),
            address: formData.get('address'),
            notes: formData.get('notes'),
            participants: []
        };
        
        // Lấy dữ liệu người tham gia
        const participantElements = document.querySelectorAll('.participant-item');
        participantElements.forEach(element => {
            const id = element.getAttribute('data-id');
            serviceData.participants.push({
                name: formData.get(`participant_name_${id}`),
                role: formData.get(`participant_role_${id}`),
                age: formData.get(`participant_age_${id}`),
                sampleType: formData.get(`participant_sample_${id}`)
            });
        });
        
        // Lưu dữ liệu vào localStorage
        saveServiceData(serviceData);
        
        // Hiển thị thông báo thành công
        showNotification('Đăng ký dịch vụ thành công! Chúng tôi sẽ liên hệ với bạn sớm nhất.', 'success');
        
        // Reset form
        setTimeout(() => {
            serviceForm.reset();
            participantsContainer.innerHTML = '';
            participantCount = 0;
            serviceCards.forEach(card => card.classList.remove('active'));
            addressField.style.display = 'none';
        }, 1000);
    });
    
    // ===== Các hàm xử lý =====
    
    // Cập nhật các tùy chọn dịch vụ dựa trên loại dịch vụ
    function updateServiceOptions(type) {
        // Xóa tất cả options hiện tại trừ option đầu tiên
        while (serviceSelect.options.length > 1) {
            serviceSelect.remove(1);
        }
        
        // Thêm các options mới dựa trên loại dịch vụ
        if (type === 'civil') {
            addOption(serviceSelect, 'father_child', 'Xét nghiệm cha con');
            addOption(serviceSelect, 'mother_child', 'Xét nghiệm mẹ con');
            addOption(serviceSelect, 'family', 'Xét nghiệm toàn diện');
            addOption(serviceSelect, 'siblings', 'Xét nghiệm anh chị em');
        } else if (type === 'admin') {
            addOption(serviceSelect, 'father_child', 'Xét nghiệm cha con');
            addOption(serviceSelect, 'mother_child', 'Xét nghiệm mẹ con');
            addOption(serviceSelect, 'family', 'Xét nghiệm toàn diện');
            addOption(serviceSelect, 'paternal_line', 'Xét nghiệm dòng họ cha');
            addOption(serviceSelect, 'maternal_line', 'Xét nghiệm dòng họ mẹ');
            
            // Đảm bảo phương thức thu mẫu là tại trung tâm
            collectionMethodSelect.value = 'center';
            
            // Kích hoạt sự kiện change để cập nhật UI
            const event = new Event('change');
            collectionMethodSelect.dispatchEvent(event);
        }
        
        // Reset service select
        serviceSelect.value = '';
        
        // Xóa người tham gia hiện tại
        participantsContainer.innerHTML = '';
        participantCount = 0;
    }
    
    // Thêm option vào select
    function addOption(selectElement, value, text) {
        const option = document.createElement('option');
        option.value = value;
        option.textContent = text;
        selectElement.appendChild(option);
    }
    
    // Thêm người tham gia mặc định dựa trên loại dịch vụ
    function addDefaultParticipants(service) {
        switch(service) {
            case 'father_child':
                addParticipant('Cha');
                addParticipant('Con');
                break;
            case 'mother_child':
                addParticipant('Mẹ');
                addParticipant('Con');
                break;
            case 'family':
                addParticipant('Cha');
                addParticipant('Mẹ');
                addParticipant('Con');
                break;
            case 'siblings':
                addParticipant('Anh/Chị');
                addParticipant('Em');
                break;
            case 'paternal_line':
                addParticipant('Người thứ nhất');
                addParticipant('Người thứ hai');
                break;
            case 'maternal_line':
                addParticipant('Người thứ nhất');
                addParticipant('Người thứ hai');
                break;
        }
    }
    
    // Thêm người tham gia
    function addParticipant(defaultRole = '') {
        participantCount++;
        const id = Date.now(); // Unique ID
        
        const participantItem = document.createElement('div');
        participantItem.className = 'participant-item';
        participantItem.setAttribute('data-id', id);
        
        participantItem.innerHTML = `
            <div class="participant-header">
                <h4>Người tham gia #${participantCount}</h4>
                <button type="button" class="btn-remove-participant" data-id="${id}">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <div class="participant-body">
                <div class="form-row">
                    <div class="form-group form-group-half">
                        <label for="participant_name_${id}">Họ tên <span class="required">*</span></label>
                        <input type="text" id="participant_name_${id}" name="participant_name_${id}" class="form-control" required>
                    </div>
                    <div class="form-group form-group-half">
                        <label for="participant_role_${id}">Vai trò <span class="required">*</span></label>
                        <input type="text" id="participant_role_${id}" name="participant_role_${id}" class="form-control" value="${defaultRole}" required>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group form-group-half">
                        <label for="participant_age_${id}">Tuổi</label>
                        <input type="number" id="participant_age_${id}" name="participant_age_${id}" class="form-control" min="0" max="120">
                    </div>
                    <div class="form-group form-group-half">
                        <label for="participant_sample_${id}">Loại mẫu <span class="required">*</span></label>
                        <select id="participant_sample_${id}" name="participant_sample_${id}" class="form-control" required>
                            <option value="cheek">Tế bào má</option>
                            <option value="blood">Máu</option>
                            <option value="hair">Tóc</option>
                            <option value="other">Khác</option>
                        </select>
                    </div>
                </div>
            </div>
        `;
        
        participantsContainer.appendChild(participantItem);
        
        // Thêm sự kiện xóa người tham gia
        participantItem.querySelector('.btn-remove-participant').addEventListener('click', function() {
            const id = this.getAttribute('data-id');
            removeParticipant(id);
        });
    }
    
    // Xóa người tham gia
    function removeParticipant(id) {
        const participantItem = document.querySelector(`.participant-item[data-id="${id}"]`);
        if (participantItem) {
            participantItem.remove();
            
            // Cập nhật lại số thứ tự
            const participantItems = document.querySelectorAll('.participant-item');
            participantItems.forEach((item, index) => {
                item.querySelector('h4').textContent = `Người tham gia #${index + 1}`;
            });
            
            participantCount = participantItems.length;
        }
    }
    
    // Lưu dữ liệu dịch vụ
    function saveServiceData(data) {
        // Tạo ID cho dịch vụ
        const id = `SRV-${Date.now()}`;
        data.id = id;
        data.createdAt = new Date().toISOString();
        data.status = 'pending';
        
        // Lấy dữ liệu hiện có
        let services = localStorage.getItem('services');
        services = services ? JSON.parse(services) : [];
        
        // Thêm dịch vụ mới
        services.unshift(data);
        
        // Lưu lại
        localStorage.setItem('services', JSON.stringify(services));
        
        // Tạo lịch hẹn tương ứng
        createAppointmentFromService(data);
        
        return id;
    }
    
    // Tạo lịch hẹn từ dịch vụ
    function createAppointmentFromService(serviceData) {
        // Lấy ngày hiện tại + 3 ngày
        const appointmentDate = new Date();
        appointmentDate.setDate(appointmentDate.getDate() + 3);
        
        // Format ngày thành DD/MM/YYYY
        const day = appointmentDate.getDate().toString().padStart(2, '0');
        const month = (appointmentDate.getMonth() + 1).toString().padStart(2, '0');
        const year = appointmentDate.getFullYear();
        const formattedDate = `${day}/${month}/${year}`;
        
        // Tạo ID cho lịch hẹn
        const id = `APT-${year}${month}${day}-${Math.floor(Math.random() * 1000).toString().padStart(3, '0')}`;
        
        // Tạo đối tượng lịch hẹn
        const appointment = {
            id: id,
            serviceType: serviceData.serviceType,
            service: getServiceText(serviceData.service),
            date: formattedDate,
            time: '09:00',
            method: serviceData.collectionMethod,
            methodText: getMethodText(serviceData.collectionMethod),
            status: 'da-dat',
            statusText: 'Đã đặt',
            location: serviceData.collectionMethod === 'home' ? serviceData.address : 'Trung tâm xét nghiệm ADN, 123 Nguyễn Huệ, Q1',
            notes: serviceData.notes,
            persons: serviceData.participants
        };
        
        // Lấy danh sách lịch hẹn hiện có
        let appointments = localStorage.getItem('appointments');
        appointments = appointments ? JSON.parse(appointments) : [];
        
        // Thêm lịch hẹn mới
        appointments.unshift(appointment);
        
        // Lưu lại
        localStorage.setItem('appointments', JSON.stringify(appointments));
    }
    
    // Lấy text của dịch vụ
    function getServiceText(service) {
        const serviceMap = {
            'father_child': 'Xét nghiệm cha con',
            'mother_child': 'Xét nghiệm mẹ con',
            'family': 'Xét nghiệm toàn diện',
            'siblings': 'Xét nghiệm anh chị em',
            'paternal_line': 'Xét nghiệm dòng họ cha',
            'maternal_line': 'Xét nghiệm dòng họ mẹ'
        };
        return serviceMap[service] || service;
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
    
    // Khởi tạo ban đầu
    function init() {
        // Ẩn trường địa chỉ ban đầu
        addressField.style.display = 'none';
    }
    
    // Gọi hàm khởi tạo
    init();
}); 