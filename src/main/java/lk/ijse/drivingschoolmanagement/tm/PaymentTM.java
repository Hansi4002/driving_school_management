
package lk.ijse.drivingschoolmanagement.tm;

import lk.ijse.drivingschoolmanagement.dto.PaymentDTO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentTM {
    private String paymentId;
    private BigDecimal amount;
    private LocalDateTime date;
    private String status;
    private String studentId;
    private String studentName;
    private String courseId;
    private String courseName;
}
