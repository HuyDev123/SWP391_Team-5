// Test Samples API functions
const TEST_SAMPLES_API = {
    // Get all test samples with pagination
    getAllTestSamples: async function(page = 0, size = 10) {
        try {
            const params = new URLSearchParams();
            params.append('page', page);
            params.append('size', size);
            
            const response = await fetch(`/test-samples?${params.toString()}`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return await response.json();
        } catch (error) {
            console.error('Error fetching test samples:', error);
            throw error;
        }
    },

    // Get test sample by ID
    getTestSampleById: async function(id) {
        try {
            const response = await fetch(`/test-samples/${id}`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return await response.json();
        } catch (error) {
            console.error('Error fetching test sample:', error);
            throw error;
        }
    },

    // Add new test sample
    addTestSample: async function(testSampleData) {
        try {
            const response = await fetch('/test-samples', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(testSampleData)
            });
            
            if (!response.ok) {
                const errorMessage = await response.text();
                throw new Error(errorMessage);
            }
            
            return await response.text();
        } catch (error) {
            console.error('Error adding test sample:', error);
            throw error;
        }
    },

    // Update test sample
    updateTestSample: async function(id, testSampleData) {
        try {
            const response = await fetch(`/test-samples/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(testSampleData)
            });
            
            if (!response.ok) {
                const errorMessage = await response.text();
                throw new Error(errorMessage);
            }
            
            return await response.text();
        } catch (error) {
            console.error('Error updating test sample:', error);
            throw error;
        }
    },

    // Delete test sample
    deleteTestSample: async function(id) {
        try {
            const response = await fetch(`/test-samples/${id}`, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                const errorMessage = await response.text();
                throw new Error(errorMessage);
            }
            
            return await response.text();
        } catch (error) {
            console.error('Error deleting test sample:', error);
            throw error;
        }
    },

    // Search test samples with pagination
    searchTestSamples: async function(searchTerm, page = 0, size = 10) {
        try {
            const params = new URLSearchParams();
            if (searchTerm) {
                params.append('searchTerm', searchTerm);
            }
            params.append('page', page);
            params.append('size', size);
            
            const response = await fetch(`/test-samples/search?${params.toString()}`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return await response.json();
        } catch (error) {
            console.error('Error searching test samples:', error);
            throw error;
        }
    },

    // Get services by participant
    getServicesByParticipant: async function(participantId) {
        try {
            const response = await fetch(`/test-samples/participant/${participantId}/services`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return await response.json();
        } catch (error) {
            console.error('Error fetching services by participant:', error);
            throw error;
        }
    }
};

// Utility functions for test samples management
const TEST_SAMPLES_UTILS = {
    // Format date for display
    formatDate: function(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN');
    },

    // Format datetime for display
    formatDateTime: function(dateTimeString) {
        if (!dateTimeString) return '';
        const date = new Date(dateTimeString);
        return date.toLocaleString('vi-VN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
        });
    },

    // Show notification
    showNotification: function(message, type = 'info') {
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            border-radius: 5px;
            color: white;
            font-weight: 500;
            z-index: 10000;
            max-width: 300px;
            word-wrap: break-word;
            animation: slideInRight 0.3s ease;
        `;

        // Set background color based on type
        switch (type) {
            case 'success':
                notification.style.backgroundColor = '#4caf50';
                break;
            case 'error':
                notification.style.backgroundColor = '#f44336';
                break;
            case 'warning':
                notification.style.backgroundColor = '#ff9800';
                break;
            default:
                notification.style.backgroundColor = '#2196f3';
        }

        notification.textContent = message;
        document.body.appendChild(notification);

        // Remove notification after 3 seconds
        setTimeout(() => {
            notification.style.animation = 'slideOutRight 0.3s ease';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, 3000);
    },

    // Show loading spinner
    showLoading: function(element) {
        if (element) {
            element.innerHTML = '<div class="loading"></div>';
            element.disabled = true;
        }
    },

    // Hide loading spinner
    hideLoading: function(element, originalText) {
        if (element) {
            element.innerHTML = originalText;
            element.disabled = false;
        }
    }
};

// Add CSS animations for notifications
const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }

    @keyframes slideOutRight {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style); 