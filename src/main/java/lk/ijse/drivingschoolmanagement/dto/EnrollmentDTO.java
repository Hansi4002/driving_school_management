
package lk.ijse.drivingschoolmanagement.dto;

import lk.ijse.drivingschoolmanagement.entity.Enrollment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EnrollmentDTO {
    private String enrollmentId;

    private String studentId;

    private String courseId;

    private LocalDate enrollmentDate;

    public EnrollmentDTO(Enrollment entity) {
        this.enrollmentId = entity.getEnrollmentId();
        this.studentId = entity.getStudent() != null ? entity.getStudent().getStudentId() : null;
        this.courseId = entity.getCourse() != null ? entity.getCourse().getCourseId() : null;
        this.enrollmentDate = entity.getEnrollmentDate();
    }
}
