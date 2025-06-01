// Result management functionality

document.addEventListener('DOMContentLoaded', function() {
    // Load results from local storage
    loadResults();
    
    // Initialize filters
    const statusFilter = document.getElementById('status-filter');
    const dateFilter = document.getElementById('date-filter');
    const resetFiltersBtn = document.getElementById('reset-filters');
    
    // Handle filter changes
    if (statusFilter) {
        statusFilter.addEventListener('change', filterResults);
    }
    
    if (dateFilter) {
        dateFilter.addEventListener('change', filterResults);
    }
    
    // Handle reset filters button
    if (resetFiltersBtn) {
        resetFiltersBtn.addEventListener('click', function() {
            if (statusFilter) statusFilter.value = 'all';
            if (dateFilter) dateFilter.value = '';
            filterResults();
        });
    }
    
    // Check if there's an appointment parameter in the URL
    const urlParams = new URLSearchParams(window.location.search);
    const appointmentId = urlParams.get('appointment');
    if (appointmentId) {
        // Filter results for this appointment
        filterResultsByAppointment(appointmentId);
    }
    
    // Initialize close button for PDF viewer
    const closeResultDetailBtn = document.getElementById('close-result-detail');
    if (closeResultDetailBtn) {
        closeResultDetailBtn.addEventListener('click', function() {
            document.getElementById('result-detail').classList.remove('active');
        });
    }
});

// Load results from local storage
function loadResults() {
    const resultsTable = document.querySelector('.results-table tbody');
    if (!resultsTable) return;
    
    // Clear existing rows
    resultsTable.innerHTML = '';
    
    // Get results from local storage
    const results = getResults();
    
    // Add rows to table
    results.forEach(result => {
        const row = createResultRow(result);
        resultsTable.appendChild(row);
    });
}

// Create result row
function createResultRow(result) {
    // Get kit information
    const kit = getKitByCode(result.kit_code);
    if (!kit) return null;
    
    const row = document.createElement('tr');
    
    // Kit code column
    const codeCell = document.createElement('td');
    codeCell.textContent = result.kit_code;
    row.appendChild(codeCell);
    
    // Date column
    const dateCell = document.createElement('td');
    dateCell.textContent = result.created_at || '-';
    row.appendChild(dateCell);
    
    // Actions column
    const actionsCell = document.createElement('td');
    actionsCell.className = 'actions';
    
    // View button
    const viewBtn = document.createElement('button');
    viewBtn.className = 'btn btn-sm btn-primary';
    viewBtn.title = 'Xem kết quả';
    viewBtn.innerHTML = '<i class="fas fa-eye"></i>';
    viewBtn.addEventListener('click', function() {
        viewResultPDF(result.kit_code);
    });
    actionsCell.appendChild(viewBtn);
    
    // Download button
    const downloadBtn = document.createElement('button');
    downloadBtn.className = 'btn btn-sm btn-success';
    downloadBtn.title = 'Tải xuống';
    downloadBtn.innerHTML = '<i class="fas fa-download"></i>';
    downloadBtn.addEventListener('click', function() {
        downloadPDF(result.kit_code);
    });
    actionsCell.appendChild(downloadBtn);
    
    row.appendChild(actionsCell);
    
    return row;
}

// Filter results based on selected filters
function filterResults() {
    const statusFilter = document.getElementById('status-filter').value;
    const dateFilter = document.getElementById('date-filter').value;
    
    const rows = document.querySelectorAll('.results-table tbody tr');
    
    rows.forEach(row => {
        let showRow = true;
        
        // Filter by date
        if (dateFilter) {
            const dateCell = row.querySelector('td:nth-child(2)');
            if (dateCell) {
                // Convert display date format (YYYY-MM-DD) to Date object
                const rowDate = new Date(dateCell.textContent);
                rowDate.setHours(0, 0, 0, 0);
                
                const filterDate = new Date(dateFilter);
                filterDate.setHours(0, 0, 0, 0);
                
                if (rowDate.getTime() !== filterDate.getTime()) {
                    showRow = false;
                }
            }
        }
        
        // Show/hide row
        row.style.display = showRow ? '' : 'none';
    });
}

// Filter results by appointment ID
function filterResultsByAppointment(appointmentId) {
    // Get kits for this appointment
    const kits = getKitsByAppointmentId(appointmentId);
    
    // Get kit codes
    const kitCodes = kits.map(kit => kit.kit_code);
    
    // Filter results table
    const rows = document.querySelectorAll('.results-table tbody tr');
    
    rows.forEach(row => {
        const kitCode = row.querySelector('td:first-child').textContent;
        row.style.display = kitCodes.includes(kitCode) ? '' : 'none';
    });
    
    // Show notification
    if (kitCodes.length > 0) {
        showNotification(`Đang hiển thị kết quả cho lịch hẹn: ${appointmentId}`, 'info');
    } else {
        showNotification(`Không tìm thấy kết quả cho lịch hẹn: ${appointmentId}`, 'info');
    }
}

// View PDF result
function viewResultPDF(kitCode) {
    // Get result from local storage
    const result = getResultByKitCode(kitCode);
    
    if (result) {
        // Set the PDF source in the iframe
        const pdfViewer = document.getElementById('pdf-viewer');
        if (pdfViewer) {
            pdfViewer.src = result.pdf_url;
            
            // Show the PDF viewer
            document.getElementById('result-detail').classList.add('active');
        } else {
            // Fallback if no PDF viewer exists
            window.open(result.pdf_url, '_blank');
        }
    } else {
        showNotification(`Không tìm thấy kết quả cho kit: ${kitCode}`, 'error');
    }
}

// Download PDF result
function downloadPDF(kitCode) {
    // Get result from local storage
    const result = getResultByKitCode(kitCode);
    
    if (result) {
        // Create a temporary link to trigger the download
        const link = document.createElement('a');
        link.href = result.pdf_url;
        link.download = `KetQua_${kitCode}.pdf`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        // Show notification
        showNotification(`Đang tải xuống kết quả cho Kit: ${kitCode}`, 'success');
    } else {
        showNotification(`Không tìm thấy kết quả cho kit: ${kitCode}`, 'error');
    }
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