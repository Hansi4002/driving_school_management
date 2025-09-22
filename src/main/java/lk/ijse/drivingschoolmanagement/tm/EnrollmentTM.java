
package lk.ijse.drivingschoolmanagement.tm;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EnrollmentTM {
    private String enrollmentId;
    private String studentId;
    private String studentName;
    private String courseId;
    private String courseName;
    private LocalDate enrollmentDate;
    private String paymentStatus;
    private Double paymentAmount;
}
