// Participants API functions
const PARTICIPANTS_API = {
    // Get all participants with pagination
    getAllParticipants: async function(page = 0, size = 10) {
        try {
            const response = await fetch(`/participants?page=${page}&size=${size}`);
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

    // Search participants with pagination
    searchParticipants: async function(searchTerm, page = 0, size = 10) {
        try {
            const response = await fetch(`/participants/search?searchTerm=${encodeURIComponent(searchTerm)}&page=${page}&size=${size}`);
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

    // Show loading state for buttons
    showLoading: function(button) {
        if (button) {
            button.disabled = true;
            button.innerHTML = '<div class="loading"></div>';
        }
    },

    // Hide loading state for buttons
    hideLoading: function(button, originalText) {
        if (button) {
            button.disabled = false;
            button.innerHTML = originalText;
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
