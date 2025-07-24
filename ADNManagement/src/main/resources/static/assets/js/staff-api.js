// Staff API functions
const STAFF_API = {
    // Existing functions...
    
    // Payment Management API functions
    PAYMENT: {
        // Get pending payments with pagination
        getPendingPayments: async function(page = 0, size = 10, search = '') {
            try {
                const params = new URLSearchParams();
                params.append('page', page);
                params.append('size', size);
                if (search) {
                    params.append('search', search);
                }
                
                const response = await fetch(`/payment/staff/pending?${params.toString()}`, {
                    credentials: 'include'
                });
                
                if (!response.ok) {
                    if (response.status === 401) {
                        window.location.href = "/internal-login";
                        return;
                    }
                    throw new Error('Network response was not ok');
                }
                
                return await response.json();
            } catch (error) {
                console.error('Error fetching pending payments:', error);
                throw error;
            }
        },

        // Get payment history with pagination and filters
        getPaymentHistory: async function(page = 0, size = 10, search = '', date = '', method = '') {
            try {
                const params = new URLSearchParams();
                params.append('page', page);
                params.append('size', size);
                if (search) {
                    params.append('search', search);
                }
                if (date) {
                    params.append('date', date);
                }
                if (method) {
                    params.append('method', method);
                }
                
                const response = await fetch(`/payment/staff/history?${params.toString()}`, {
                    credentials: 'include'
                });
                
                if (!response.ok) {
                    if (response.status === 401) {
                        window.location.href = "/internal-login";
                        return;
                    }
                    throw new Error('Network response was not ok');
                }
                
                return await response.json();
            } catch (error) {
                console.error('Error fetching payment history:', error);
                throw error;
            }
        },

        // Confirm payment
        confirmPayment: async function(paymentId, actualAmount, notes) {
            try {
                const response = await fetch('/payment/staff/confirm', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    credentials: 'include',
                    body: JSON.stringify({
                        paymentId: paymentId,
                        actualAmount: actualAmount,
                        notes: notes
                    })
                });
                
                if (!response.ok) {
                    if (response.status === 401) {
                        window.location.href = "/internal-login";
                        return;
                    }
                    const errorMessage = await response.text();
                    throw new Error(errorMessage);
                }
                
                return await response.json();
            } catch (error) {
                console.error('Error confirming payment:', error);
                throw error;
            }
        },

        // Create new payment
        createPayment: async function(bookingId, amount, paymentMethod, notes) {
            try {
                const response = await fetch('/payment/staff/create', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    credentials: 'include',
                    body: JSON.stringify({
                        bookingId: bookingId,
                        amount: amount,
                        paymentMethod: paymentMethod,
                        notes: notes
                    })
                });
                
                if (!response.ok) {
                    if (response.status === 401) {
                        window.location.href = "/internal-login";
                        return;
                    }
                    const errorMessage = await response.text();
                    throw new Error(errorMessage);
                }
                
                return await response.json();
            } catch (error) {
                console.error('Error creating payment:', error);
                throw error;
            }
        },

        // Create new payment with receipt image
        createPaymentWithReceipt: async function(formData) {
            try {
                const response = await fetch('/payment/staff/create-with-receipt', {
                    method: 'POST',
                    credentials: 'include',
                    body: formData // FormData will automatically set the correct Content-Type
                });
                
                if (!response.ok) {
                    if (response.status === 401) {
                        window.location.href = "/internal-login";
                        return;
                    }
                    const errorMessage = await response.text();
                    throw new Error(errorMessage);
                }
                
                return await response.json();
            } catch (error) {
                console.error('Error creating payment with receipt:', error);
                throw error;
            }
        },

        // Get unpaid bookings
        getUnpaidBookings: async function() {
            try {
                const response = await fetch('/payment/staff/unpaid-bookings', {
                    credentials: 'include'
                });
                
                if (!response.ok) {
                    if (response.status === 401) {
                        window.location.href = "/internal-login";
                        return;
                    }
                    throw new Error('Network response was not ok');
                }
                
                return await response.json();
            } catch (error) {
                console.error('Error fetching unpaid bookings:', error);
                throw error;
            }
        },

        // Nhắc khách hàng thanh toán đủ 10% để gửi kit
        remind10PercentPayment: async function(appointmentId) {
            try {
                const response = await fetch(`/api/kits/appointment/${appointmentId}/remind-10-percent-payment`, {
                    method: 'POST',
                    credentials: 'include'
                });
                if (!response.ok) {
                    const errorMessage = await response.text();
                    throw new Error(errorMessage);
                }
                return await response.text();
            } catch (error) {
                console.error('Error sending 10% payment reminder:', error);
                throw error;
            }
        },

        // Nhắc khách hàng thanh toán đủ 100% để nhận mẫu
        remindFullPayment: async function(appointmentId) {
            try {
                const response = await fetch(`/api/kits/appointment/${appointmentId}/remind-full-payment`, {
                    method: 'POST',
                    credentials: 'include'
                });
                if (!response.ok) {
                    const errorMessage = await response.text();
                    throw new Error(errorMessage);
                }
                return await response.text();
            } catch (error) {
                console.error('Error sending full payment reminder:', error);
                throw error;
            }
        }
    }
}; 