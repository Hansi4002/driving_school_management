
package lk.ijse.drivingschoolmanagement.tm;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentTM {
    private String studentId;
    private String name;
    private String nic;
    private String email;
    private String phoneNo;
    private String courseNames;
    private LocalDate enrollmentDate;
    private String progressStatus;
    private String paymentStatus;
}
