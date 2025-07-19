package com.genx.adnmanagement.service;

import com.genx.adnmanagement.entity.Booking;
import com.genx.adnmanagement.entity.Surcharge;
import com.genx.adnmanagement.repository.SurchargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SurchargeService {
    
    @Autowired
    private SurchargeRepository surchargeRepository;
    
    /**
     * Tính tổng phụ phí cho một booking
     * @param booking Booking cần tính phụ phí
     * @return Tổng số tiền phụ phí
     */
    public BigDecimal calculateSurchargeForBooking(Booking booking) {
        BigDecimal totalSurcharge = BigDecimal.ZERO;
        
        // Lấy tất cả surcharge đang active
        List<Surcharge> activeSurcharges = surchargeRepository.findByIsActiveTrue();
        
        for (Surcharge surcharge : activeSurcharges) {
            // Kiểm tra xem phụ phí này có áp dụng cho loại booking này không
            if (isSurchargeApplicable(surcharge, booking)) {
                BigDecimal surchargeAmount = calculateSurchargeAmount(surcharge, booking);
                totalSurcharge = totalSurcharge.add(surchargeAmount);
            }
        }
        
        return totalSurcharge;
    }
    
    /**
     * Tính phụ phí cho một loại surcharge cụ thể
     * @param surcharge Loại phụ phí
     * @param booking Booking cần tính
     * @return Số tiền phụ phí
     */
    private BigDecimal calculateSurchargeAmount(Surcharge surcharge, Booking booking) {
        switch (surcharge.getUnit()) {
            case "participant":
                // Tính theo số người tham gia
                return surcharge.getFeePerUnit().multiply(BigDecimal.valueOf(booking.getNumParticipants()));

            case "booking":
                // Tính theo booking (1 lần)
                return surcharge.getFeePerUnit();
                
            case "sample":
                // Tính theo tổng số mẫu (cần tính từ BookingService)
                // Sẽ được tính riêng trong BookingServiceRepository
                return BigDecimal.ZERO;
                
            default:
                return BigDecimal.ZERO;
        }
    }
    
    /**
     * Lấy danh sách phụ phí đang active
     * @return Danh sách surcharge
     */
    public List<Surcharge> getActiveSurcharges() {
        return surchargeRepository.findByIsActiveTrue();
    }
    
    /**
     * Kiểm tra xem phụ phí có áp dụng cho booking này không
     * @param surcharge Loại phụ phí
     * @param booking Booking cần kiểm tra
     * @return true nếu áp dụng, false nếu không
     */
    private boolean isSurchargeApplicable(Surcharge surcharge, Booking booking) {
        // Nếu tên phụ phí chứa từ khóa "hành chính", chỉ áp dụng cho booking hành chính
        if (surcharge.getName().toLowerCase().contains("hành chính") || 
            surcharge.getName().toLowerCase().contains("administrative") ||
            surcharge.getName().toLowerCase().contains("legal")) {
            return booking.getIsAdministrative() != null && booking.getIsAdministrative();
        }
        
        // Các phụ phí khác áp dụng cho tất cả booking
        return true;
    }
    
    /**
     * Lấy phụ phí theo tên
     * @param name Tên phụ phí
     * @return Surcharge hoặc null
     */
    public Surcharge getSurchargeByName(String name) {
        return surchargeRepository.findByNameAndIsActiveTrue(name);
    }
}
