// Participants API functions
const PARTICIPANTS_API = {
    // Get all participants
    getAllParticipants: async function() {
        try {
            const response = await fetch('/participants');
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return await response.json();
        } catch (error) {
            console.error('Error fetching participants:', error);
            throw error;
        }
    },

    // Get participant by ID
    getParticipantById: async function(id) {
        try {
            const response = await fetch(`/participants/${id}`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return await response.json();
        } catch (error) {
            console.error('Error fetching participant:', error);
            throw error;
        }
    },

    // Add new participant
    addParticipant: async function(participantData) {
        try {
            const response = await fetch('/participants', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(participantData)
            });
            
            if (!response.ok) {
                const errorMessage = await response.text();
                throw new Error(errorMessage);
            }
            
            return await response.text();
        } catch (error) {
            console.error('Error adding participant:', error);
            throw error;
        }
    },

    // Update participant
    updateParticipant: async function(id, participantData) {
        try {
            const response = await fetch(`/participants/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(participantData)
            });
            
            if (!response.ok) {
                const errorMessage = await response.text();
                throw new Error(errorMessage);
            }
            
            return await response.text();
        } catch (error) {
            console.error('Error updating participant:', error);
            throw error;
        }
    },

    // Delete participant
    deleteParticipant: async function(id) {
        try {
            const response = await fetch(`/participants/${id}`, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                const errorMessage = await response.text();
                throw new Error(errorMessage);
            }
            
            return await response.text();
        } catch (error) {
            console.error('Error deleting participant:', error);
            throw error;
        }
    },

    // Get booking details by booking ID
    getBookingDetails: async function(bookingId) {
        try {
            const response = await fetch(`/participants/booking/${bookingId}`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return await response.json();
        } catch (error) {
            console.error('Error fetching booking details:', error);
            throw error;
        }
    },

    // Search participants
    searchParticipants: async function(searchTerm) {
        try {
            const response = await fetch(`/participants/search?searchTerm=${encodeURIComponent(searchTerm)}`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return await response.json();
        } catch (error) {
            console.error('Error searching participants:', error);
            throw error;
        }
    }
};

// Utility functions for participants management
const PARTICIPANTS_UTILS = {
    // Format date for display
    formatDate: function(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN');
    },

    // Validate email format
    validateEmail: function(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    },

    // Validate phone number format (Vietnamese)
    validatePhone: function(phone) {
        const phoneRegex = /^(\+84|84|0)[0-9]{9}$/;
        return phoneRegex.test(phone);
    },

    // Validate CCCD format (Vietnamese)
    validateCCCD: function(cccd) {
        const cccdRegex = /^[0-9]{12}$/;
        return cccdRegex.test(cccd);
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
