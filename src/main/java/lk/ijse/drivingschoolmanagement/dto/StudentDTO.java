
package lk.ijse.drivingschoolmanagement.dto;

import lk.ijse.drivingschoolmanagement.entity.Course;
import lk.ijse.drivingschoolmanagement.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentDTO {
    private String studentId;

    private String name;

    private String nic;

    private String email;

    private String phoneNo;

    private List<String> courseIds;

    private LocalDate enrollmentDate;

    public StudentDTO(Student entity) {
        this.studentId = entity.getStudentId();
        this.name = entity.getName();
        this.nic = entity.getNic();
        this.email = entity.getEmail();
        this.phoneNo = entity.getPhoneNo();
        this.courseIds = entity.getCourses() != null ? entity.getCourses().stream()
                .map(Course::getCourseId)
                .toList() : null;
        this.enrollmentDate = entity.getEnrollmentDate();
    }
}
