// Kit management functionality

document.addEventListener('DOMContentLoaded', function() {
    // Load kits from local storage
    loadKits();
    
    // Initialize form
    const kitForm = document.getElementById('kit-form');
    const cancelKitBtn = document.getElementById('cancel-kit');
    
    // Initialize filters
    const statusFilter = document.getElementById('kit-status-filter');
    const dateFilter = document.getElementById('kit-date-filter');
    const resetFiltersBtn = document.getElementById('reset-kit-filters');
    
    // Handle form submission
    if (kitForm) {
        kitForm.addEventListener('submit', function(e) {
            e.preventDefault();
            saveKitForm();
        });
    }
    
    // Handle cancel button
    if (cancelKitBtn) {
        cancelKitBtn.addEventListener('click', function() {
            resetForm();
        });
    }
    
    // Handle filter changes
    if (statusFilter) {
        statusFilter.addEventListener('change', filterKits);
    }
    
    if (dateFilter) {
        dateFilter.addEventListener('change', filterKits);
    }
    
    // Handle reset filters button
    if (resetFiltersBtn) {
        resetFiltersBtn.addEventListener('click', function() {
            if (statusFilter) statusFilter.value = 'all';
            if (dateFilter) dateFilter.value = '';
            filterKits();
        });
    }
    
    // Check if there's an appointment parameter in the URL
    const urlParams = new URLSearchParams(window.location.search);
    const appointmentId = urlParams.get('appointment');
    if (appointmentId) {
        // Set the kit code based on the appointment ID
        const kitCodeInput = document.getElementById('kit_code');
        if (kitCodeInput) {
            // Generate a kit code based on the appointment ID
            // For example: APT-20240610-001 -> KIT-20240610-001
            const kitCode = appointmentId.replace('APT-', 'KIT-');
            kitCodeInput.value = kitCode;
        }
        
        // Set appointment ID in hidden field
        const appointmentIdField = document.getElementById('appointment_id');
        if (appointmentIdField) {
            appointmentIdField.value = appointmentId;
        }
    }
    
    // Initialize action buttons
    initActionButtons();
    
    // Initialize close button for kit details
    const closeKitDetailBtn = document.getElementById('close-kit-detail');
    if (closeKitDetailBtn) {
        closeKitDetailBtn.addEventListener('click', function() {
            document.getElementById('kit-detail').classList.remove('active');
        });
    }
    
    // Initialize upload result form
    const uploadResultForm = document.getElementById('upload-result-form');
    if (uploadResultForm) {
        uploadResultForm.addEventListener('submit', function(e) {
            e.preventDefault();
            uploadResultForm();
        });
    }
    
    // Initialize close button for upload result form
    const closeUploadResultBtn = document.getElementById('close-upload-result');
    if (closeUploadResultBtn) {
        closeUploadResultBtn.addEventListener('click', function() {
            document.getElementById('upload-result-modal').classList.remove('active');
        });
    }
    
    // Initialize function card buttons
    document.querySelectorAll('.function-card .btn').forEach(button => {
        if (button.textContent.includes('Đăng ký')) {
            button.addEventListener('click', function() {
                registerNewKit();
            });
        } else if (button.textContent.includes('Khai báo')) {
            button.addEventListener('click', function() {
                declareSampleShipment();
            });
        } else if (button.textContent.includes('Tra cứu')) {
            button.addEventListener('click', function() {
                openKitSearch();
            });
        } else if (button.textContent.includes('Hướng dẫn')) {
            button.addEventListener('click', function() {
                showKitGuide();
            });
        }
        // Other buttons are direct links and don't need event listeners
    });
    
    // Set up modal close functionality
    const modal = document.getElementById('kitDetailModal');
    if (modal) {
        const closeBtn = modal.querySelector('.close');
        
        if (closeBtn) {
            closeBtn.onclick = function() {
                modal.style.display = "none";
            }
        }
        
        window.onclick = function(event) {
            if (event.target == modal) {
                modal.style.display = "none";
            }
        }
    }
    
    // Add event listeners for the kit functions
    const selfCollectionBtn = document.getElementById('selfCollectionBtn');
    if (selfCollectionBtn) {
        selfCollectionBtn.addEventListener('click', function() {
            alert('Mở trang đặt kit để tự thu mẫu tại nhà');
            window.location.href = 'self-collection.html';
        });
    }
    
    const professionalCollectionBtn = document.getElementById('professionalCollectionBtn');
    if (professionalCollectionBtn) {
        professionalCollectionBtn.addEventListener('click', function() {
            alert('Mở trang đặt lịch cho chuyên viên thu mẫu');
            window.location.href = 'professional-collection.html';
        });
    }
    
    const trackKitBtn = document.getElementById('trackKitBtn');
    if (trackKitBtn) {
        trackKitBtn.addEventListener('click', function() {
            const kitCode = prompt("Nhập mã Kit cần theo dõi:");
            if (kitCode) {
                trackKit(kitCode);
            }
        });
    }
    
    const viewResultsBtn = document.getElementById('viewResultsBtn');
    if (viewResultsBtn) {
        viewResultsBtn.addEventListener('click', function() {
            const kitCode = prompt("Nhập mã Kit cần xem kết quả:");
            if (kitCode) {
                viewResults(kitCode);
            }
        });
    }

    // Xử lý các FAQ
    const faqQuestions = document.querySelectorAll('.faq-question');
    faqQuestions.forEach(question => {
        question.addEventListener('click', function() {
            this.classList.toggle('active');
            const answer = this.nextElementSibling;
            if (this.classList.contains('active')) {
                answer.style.display = 'block';
            } else {
                answer.style.display = 'none';
            }
        });
    });
});

// Load kits from local storage
function loadKits() {
    const kitsTable = document.querySelector('.kit-table tbody');
    if (!kitsTable) return;
    
    // Clear existing rows
    kitsTable.innerHTML = '';
    
    // Get kits from local storage
    const kits = getKits();
    
    // Add rows to table
    kits.forEach(kit => {
        const row = createKitRow(kit);
        kitsTable.appendChild(row);
    });
}

// Create kit row
function createKitRow(kit) {
    const row = document.createElement('tr');
    
    // Kit code column
    const codeCell = document.createElement('td');
    codeCell.textContent = kit.kit_code;
    row.appendChild(codeCell);
    
    // Staff column
    const staffCell = document.createElement('td');
    staffCell.textContent = kit.staff || '-';
    row.appendChild(staffCell);
    
    // Send date column
    const sendDateCell = document.createElement('td');
    sendDateCell.textContent = kit.send_date || '-';
    row.appendChild(sendDateCell);
    
    // Receive date column
    const receiveDateCell = document.createElement('td');
    receiveDateCell.textContent = kit.receive_date || '-';
    row.appendChild(receiveDateCell);
    
    // Status column
    const statusCell = document.createElement('td');
    const statusBadge = document.createElement('span');
    statusBadge.className = `status-badge ${kit.status}`;
    statusBadge.textContent = getKitStatusText(kit.status);
    statusCell.appendChild(statusBadge);
    row.appendChild(statusCell);
    
    // Actions column
    const actionsCell = document.createElement('td');
    actionsCell.className = 'actions';
    
    // View details button
    const viewBtn = document.createElement('button');
    viewBtn.className = 'btn btn-sm btn-info';
    viewBtn.title = 'Xem chi tiết';
    viewBtn.innerHTML = '<i class="fas fa-eye"></i>';
    viewBtn.addEventListener('click', function() {
        viewKitDetails(kit.kit_code);
    });
    actionsCell.appendChild(viewBtn);
    
    // Edit button
    const editBtn = document.createElement('button');
    editBtn.className = 'btn btn-sm btn-warning';
    editBtn.title = 'Chỉnh sửa';
    editBtn.innerHTML = '<i class="fas fa-edit"></i>';
    editBtn.addEventListener('click', function() {
        editKit(kit.kit_code);
    });
    actionsCell.appendChild(editBtn);
    
    // Upload result button (enabled only for certain statuses)
    const uploadBtn = document.createElement('button');
    uploadBtn.className = 'btn btn-sm btn-success';
    uploadBtn.title = 'Upload kết quả';
    uploadBtn.innerHTML = '<i class="fas fa-upload"></i>';
    
    // Disable upload button for kits that are not processed yet
    if (['cho-gui', 'da-gui', 'loi-mau'].includes(kit.status)) {
        uploadBtn.disabled = true;
        uploadBtn.classList.add('disabled');
    } else {
        uploadBtn.addEventListener('click', function() {
            uploadResult(kit.kit_code);
        });
    }
    actionsCell.appendChild(uploadBtn);
    
    row.appendChild(actionsCell);
    
    return row;
}

// Get kit status text from status code
function getKitStatusText(status) {
    const statusMap = {
        'cho-gui': 'Chờ gửi',
        'da-gui': 'Đã gửi',
        'da-nhan-mau': 'Đã nhận mẫu',
        'loi-mau': 'Lỗi mẫu',
        'da-xu-ly': 'Đã xử lý',
        'da-tra-ket-qua': 'Đã trả kết quả'
    };
    
    return statusMap[status] || status;
}

// Save kit information from form
function saveKitForm() {
    const kitCode = document.getElementById('kit_code').value;
    const appointmentId = document.getElementById('appointment_id')?.value;
    const isSelfCollected = document.querySelector('input[name="is_self_collected"]:checked').value === 'yes';
    const sendDate = document.getElementById('send_date').value;
    const sampleCount = document.getElementById('sample_count').value;
    const notes = document.getElementById('notes').value;
    
    // Format date from YYYY-MM-DD to DD/MM/YYYY
    let formattedSendDate = '';
    if (sendDate) {
        const dateParts = sendDate.split('-');
        formattedSendDate = `${dateParts[2]}/${dateParts[1]}/${dateParts[0]}`;
    }
    
    // Create kit object
    const kit = {
        kit_code: kitCode,
        appointment_id: appointmentId || '',
        staff: getCurrentUser()?.fullname || 'Nhân viên',
        is_self_collected: isSelfCollected,
        send_date: formattedSendDate,
        receive_date: '',
        sample_count: parseInt(sampleCount) || 0,
        status: 'cho-gui',
        notes: notes
    };
    
    // Save kit to local storage
    saveKit(kit);
    
    // If there's an appointment ID, update its status
    if (appointmentId) {
        updateAppointmentStatus(appointmentId, 'dang-cho-lay-mau');
    }
    
    // Show success notification
    showNotification(`Kit ${kitCode} đã được lưu thành công`, 'success');
    
    // Reset the form
    resetForm();
    
    // Reload kits
    loadKits();
    
    // Close modal if exists
    const kitFormModal = document.getElementById('kit-form-modal');
    if (kitFormModal) {
        kitFormModal.classList.remove('active');
    }
}

// Reset the form
function resetForm() {
    const kitForm = document.getElementById('kit-form');
    if (kitForm) {
        kitForm.reset();
    }
}

// Filter kits based on selected filters
function filterKits() {
    const statusFilter = document.getElementById('kit-status-filter').value;
    const dateFilter = document.getElementById('kit-date-filter').value;
    
    const rows = document.querySelectorAll('.kit-table tbody tr');
    
    rows.forEach(row => {
        let showRow = true;
        
        // Filter by status
        if (statusFilter !== 'all') {
            const statusCell = row.querySelector('td:nth-child(5) .status-badge');
            if (statusCell && !statusCell.classList.contains(statusFilter)) {
                showRow = false;
            }
        }
        
        // Filter by date
        if (dateFilter) {
            const dateCell = row.querySelector('td:nth-child(3)');
            if (dateCell) {
                // Convert display date format (DD/MM/YYYY) to Date object
                const dateParts = dateCell.textContent.split('/');
                if (dateParts.length === 3) {
                    const rowDate = new Date(dateParts[2], dateParts[1] - 1, dateParts[0]);
                    rowDate.setHours(0, 0, 0, 0);
                    
                    const filterDate = new Date(dateFilter);
                    filterDate.setHours(0, 0, 0, 0);
                    
                    if (rowDate.getTime() !== filterDate.getTime()) {
                        showRow = false;
                    }
                }
            }
        }
        
        // Show/hide row
        row.style.display = showRow ? '' : 'none';
    });
}

// Initialize action buttons
function initActionButtons() {
    // View kit details
    document.querySelectorAll('.btn[title="Xem chi tiết"]').forEach(btn => {
        btn.addEventListener('click', function() {
            const row = this.closest('tr');
            const kitCode = row.querySelector('td:first-child').textContent;
            viewKitDetails(kitCode);
        });
    });
    
    // Edit kit
    document.querySelectorAll('.btn[title="Chỉnh sửa"]').forEach(btn => {
        btn.addEventListener('click', function() {
            const row = this.closest('tr');
            const kitCode = row.querySelector('td:first-child').textContent;
            editKit(kitCode);
        });
    });
    
    // Upload result
    document.querySelectorAll('.btn[title="Upload kết quả"]:not([disabled])').forEach(btn => {
        btn.addEventListener('click', function() {
            const row = this.closest('tr');
            const kitCode = row.querySelector('td:first-child').textContent;
            uploadResult(kitCode);
        });
    });
}

// View kit details
function viewKitDetails(kitCode) {
    // Get kit from local storage
    const kit = getKitByCode(kitCode);
    
    if (kit) {
        // Show kit details in a modal or dedicated section
        const detailsContainer = document.getElementById('kit-detail');
        if (detailsContainer) {
            // Populate details
            document.getElementById('detail-kit-code').textContent = kit.kit_code;
            document.getElementById('detail-staff').textContent = kit.staff || '-';
            document.getElementById('detail-is-self-collected').textContent = kit.is_self_collected ? 'Có' : 'Không';
            document.getElementById('detail-send-date').textContent = kit.send_date || '-';
            document.getElementById('detail-receive-date').textContent = kit.receive_date || '-';
            document.getElementById('detail-sample-count').textContent = kit.sample_count;
            document.getElementById('detail-status').textContent = getKitStatusText(kit.status);
            document.getElementById('detail-status').className = `status-badge ${kit.status}`;
            document.getElementById('detail-notes').textContent = kit.notes || '-';
            
            // Show the details container
            detailsContainer.classList.add('active');
        } else {
            // Fallback if no details container exists
            showNotification(`Thông tin kit: ${kit.kit_code}`, 'info');
        }
    } else {
        showNotification(`Không tìm thấy thông tin kit: ${kitCode}`, 'error');
    }
}

// Edit kit
function editKit(kitCode) {
    // Get kit from local storage
    const kit = getKitByCode(kitCode);
    
    if (kit) {
        // Populate the form with kit data
        const kitForm = document.getElementById('kit-form');
        if (kitForm) {
            document.getElementById('kit_code').value = kit.kit_code;
            
            // Set appointment ID if field exists
            const appointmentIdField = document.getElementById('appointment_id');
            if (appointmentIdField) {
                appointmentIdField.value = kit.appointment_id;
            }
            
            // Set self-collected radio button
            if (kit.is_self_collected) {
                document.getElementById('self_collected_yes').checked = true;
            } else {
                document.getElementById('self_collected_no').checked = true;
            }
            
            // Convert date format from DD/MM/YYYY to YYYY-MM-DD for input
            let formattedSendDate = '';
            if (kit.send_date) {
                const dateParts = kit.send_date.split('/');
                formattedSendDate = `${dateParts[2]}-${dateParts[1]}-${dateParts[0]}`;
            }
            
            document.getElementById('send_date').value = formattedSendDate;
            document.getElementById('sample_count').value = kit.sample_count;
            document.getElementById('notes').value = kit.notes;
            
            // Show form
            const kitFormModal = document.getElementById('kit-form-modal');
            if (kitFormModal) {
                kitFormModal.classList.add('active');
            }
        } else {
            showNotification('Form chỉnh sửa kit không tồn tại', 'error');
        }
    } else {
        showNotification(`Không tìm thấy thông tin kit: ${kitCode}`, 'error');
    }
}

// Upload result
function uploadResult(kitCode) {
    // Get kit from local storage
    const kit = getKitByCode(kitCode);
    
    if (kit) {
        // Show upload form
        const uploadForm = document.getElementById('upload-result-modal');
        if (uploadForm) {
            // Set kit code in form
            document.getElementById('result_kit_code').value = kitCode;
            
            // Show upload form
            uploadForm.classList.add('active');
        } else {
            // Fallback if no upload form exists
            showNotification(`Chức năng upload kết quả cho kit: ${kitCode} sẽ được triển khai sau`, 'info');
        }
    } else {
        showNotification(`Không tìm thấy thông tin kit: ${kitCode}`, 'error');
    }
}

// Upload result form submission
function uploadResultForm() {
    const form = document.getElementById('upload-result-form');
    if (!form) return;
    
    const kitCode = document.getElementById('result_kit_code').value;
    const resultFile = document.getElementById('result_file').files[0];
    
    // Validate form
    if (!kitCode || !resultFile) {
        showNotification('Vui lòng chọn file kết quả', 'error');
        return;
    }
    
    // In a real application, this would upload the file to the server
    // For this client-side only version, we'll simulate a successful upload
    
    // Create a result object
    const result = {
        kit_code: kitCode,
        pdf_url: `data/results/${kitCode}.pdf`,
        created_at: new Date().toISOString().slice(0, 10)
    };
    
    // Save result to local storage
    saveResult(result);
    
    // Update kit status
    updateKitStatus(kitCode, 'da-tra-ket-qua');
    
    // Show success notification
    showNotification(`Kết quả cho kit ${kitCode} đã được tải lên thành công`, 'success');
    
    // Reset form
    form.reset();
    
    // Close modal
    document.getElementById('upload-result-modal').classList.remove('active');
    
    // Reload kits
    loadKits();
}

// Show notification
function showNotification(message, type = 'info') {
    // Check if the function exists in the parent scope
    if (typeof window.showNotification === 'function') {
        window.showNotification(message, type);
    } else {
        // Fallback implementation
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.textContent = message;
        document.body.appendChild(notification);
        
        setTimeout(() => {
            notification.style.opacity = '1';
            setTimeout(() => {
                notification.style.opacity = '0';
                setTimeout(() => {
                    notification.remove();
                }, 300);
            }, 3000);
        }, 10);
    }
}

// Register a new kit
function registerNewKit() {
    // In a real app, this would open a form or direct to a registration page
    alert('Mở form đăng ký Kit mới');
    
    // Scroll to the kit form if it exists
    const kitForm = document.getElementById('kit-form');
    if (kitForm) {
        kitForm.scrollIntoView({ behavior: 'smooth' });
    }
}

// Declare sample shipment
function declareSampleShipment() {
    alert('Mở form khai báo gửi mẫu');
}

// Open kit search
function openKitSearch() {
    // Focus on the search input if it exists
    const searchInput = document.querySelector('.search-input');
    if (searchInput) {
        searchInput.focus();
        searchInput.scrollIntoView({ behavior: 'smooth' });
    }
    
    alert('Nhập mã Kit để tra cứu thông tin');
}

// Show kit guide
function showKitGuide() {
    // In a real app, this would open a modal or redirect to a guide page
    alert('Hiển thị hướng dẫn sử dụng Kit');
}

// Sample data for demo purposes
const kits = [
    {
        code: "KIT-20240710-001",
        isSelfCollected: false,
        sendDate: "10/07/2024",
        sampleCount: 2,
        notes: "Lấy mẫu tại phòng khám",
        status: "Đang xử lý",
        appointmentId: "APT-20240710-001",
        serviceType: "Huyết thống Cha-Con",
        customer: {
            name: "Nguyễn Văn A",
            phone: "0901234567",
            email: "nguyenvana@example.com"
        },
        trackingInfo: {
            currentLocation: "Phòng xét nghiệm ADN",
            lastUpdate: "12/07/2024 14:30",
            estimatedCompletionDate: "17/07/2024"
        }
    },
    {
        code: "KIT-20240625-002",
        isSelfCollected: true,
        sendDate: "25/06/2024",
        sampleCount: 2,
        notes: "Khách hàng tự thu mẫu tại nhà",
        status: "Hoàn thành",
        appointmentId: "APT-20240620-002",
        serviceType: "Huyết thống Mẹ-Con",
        customer: {
            name: "Trần Thị B",
            phone: "0912345678",
            email: "tranthib@example.com"
        },
        trackingInfo: {
            currentLocation: "Hoàn thành",
            lastUpdate: "05/07/2024 10:15",
            estimatedCompletionDate: "05/07/2024"
        },
        resultDate: "05/07/2024",
        resultUrl: "results/kit-20240625-002.pdf"
    }
];

// Show kit details modal
function viewKitDetails(kitCode) {
    const kit = kits.find(k => k.code === kitCode);
    if (!kit) return;

    const modal = document.getElementById('kitDetailModal');
    const modalContent = modal.querySelector('.modal-content');

    let resultSection = '';
    if (kit.status === "Hoàn thành" && kit.resultUrl) {
        resultSection = `
            <div class="result-section">
                <h3>Kết quả xét nghiệm</h3>
                <div class="info-group">
                    <label>Ngày có kết quả:</label>
                    <span>${kit.resultDate}</span>
                </div>
                <div class="info-group">
                    <button class="btn btn-success" onclick="viewResults('${kit.code}')">
                        <i class="fas fa-file-medical"></i> Xem kết quả
                    </button>
                </div>
            </div>
        `;
    }

    let trackingSection = `
        <div class="tracking-section">
            <h3>Thông tin theo dõi</h3>
            <div class="info-group">
                <label>Vị trí hiện tại:</label>
                <span>${kit.trackingInfo.currentLocation}</span>
            </div>
            <div class="info-group">
                <label>Cập nhật lần cuối:</label>
                <span>${kit.trackingInfo.lastUpdate}</span>
            </div>
            <div class="info-group">
                <label>Ngày hoàn thành dự kiến:</label>
                <span>${kit.trackingInfo.estimatedCompletionDate}</span>
            </div>
            <div class="tracking-button">
                <button class="btn btn-primary" onclick="showTrackingHistory('${kit.code}')">
                    <i class="fas fa-map-marker-alt"></i> Xem lịch sử theo dõi
                </button>
            </div>
        </div>
    `;

    modalContent.innerHTML = `
        <div class="modal-header">
            <h2>Chi tiết Kit</h2>
            <span class="close">&times;</span>
        </div>
        <div class="modal-body">
            <div class="kit-info">
                <h3>Thông tin Kit</h3>
                <div class="info-group">
                    <label>Mã Kit:</label>
                    <span>${kit.code}</span>
                </div>
                <div class="info-group">
                    <label>Loại dịch vụ:</label>
                    <span>${kit.serviceType}</span>
                </div>
                <div class="info-group">
                    <label>Tự thu mẫu:</label>
                    <span>${kit.isSelfCollected ? 'Có' : 'Không'}</span>
                </div>
                <div class="info-group">
                    <label>Ngày gửi:</label>
                    <span>${kit.sendDate}</span>
                </div>
                <div class="info-group">
                    <label>Số lượng mẫu:</label>
                    <span>${kit.sampleCount}</span>
                </div>
                <div class="info-group">
                    <label>Ghi chú:</label>
                    <span>${kit.notes}</span>
                </div>
                <div class="info-group">
                    <label>Trạng thái:</label>
                    <span class="status-badge ${getStatusClass(kit.status)}">${kit.status}</span>
                </div>
            </div>
            
            <div class="customer-info">
                <h3>Thông tin khách hàng</h3>
                <div class="info-group">
                    <label>Họ tên:</label>
                    <span>${kit.customer.name}</span>
                </div>
                <div class="info-group">
                    <label>Số điện thoại:</label>
                    <span>${kit.customer.phone}</span>
                </div>
                <div class="info-group">
                    <label>Email:</label>
                    <span>${kit.customer.email}</span>
                </div>
            </div>
            
            ${trackingSection}
            ${resultSection}
        </div>
    `;
    
    modal.style.display = "block";
    
    // Add event listener to close button
    const closeBtn = modalContent.querySelector('.close');
    closeBtn.onclick = function() {
        modal.style.display = "none";
    };
    
    // Close modal when clicking outside
    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    };
}

// Helper function to get appropriate CSS class for status
function getStatusClass(status) {
    switch(status) {
        case "Đang xử lý": return "processing";
        case "Hoàn thành": return "completed";
        default: return "";
    }
}

// Track kit
function trackKit(kitCode) {
    const kit = kits.find(k => k.code === kitCode);
    if (!kit) {
        alert("Không tìm thấy thông tin Kit!");
        return;
    }
    
    // Here we would navigate to a tracking page, but for the demo we'll just show an alert
    alert(`Đang theo dõi Kit ${kitCode}\nVị trí hiện tại: ${kit.trackingInfo.currentLocation}\nCập nhật lần cuối: ${kit.trackingInfo.lastUpdate}`);
    
    // In a real app, you'd redirect to a tracking page:
    // window.location.href = `tracking.html?kitCode=${kitCode}`;
}

// View tracking history
function showTrackingHistory(kitCode) {
    alert("Đang mở lịch sử theo dõi cho Kit " + kitCode);
    // In a real app, this would either open a modal or navigate to a tracking history page
}

// View results
function viewResults(kitCode) {
    const kit = kits.find(k => k.code === kitCode);
    if (!kit || !kit.resultUrl) {
        alert("Kết quả chưa có sẵn cho Kit này!");
        return;
    }
    
    // In a real app, you'd open the results PDF or navigate to a results page
    alert(`Đang mở kết quả xét nghiệm cho Kit ${kitCode}`);
    // window.open(kit.resultUrl, '_blank');
} 