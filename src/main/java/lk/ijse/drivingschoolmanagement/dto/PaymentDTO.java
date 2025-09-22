package lk.ijse.drivingschoolmanagement.dto;

import lk.ijse.drivingschoolmanagement.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentDTO {
    private String paymentId;

    private BigDecimal amount;

    private LocalDateTime date;

    private String status;

    private String studentId;

    public PaymentDTO(Payment entity) {
        this.paymentId = entity.getPaymentId();
        this.amount = entity.getAmount();
        this.date = entity.getDate();
        this.status = entity.getStatus();
        this.studentId = entity.getStudent() != null ? entity.getStudent().getStudentId() : null;
    }
}