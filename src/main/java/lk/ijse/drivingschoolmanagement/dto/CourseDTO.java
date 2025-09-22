
package lk.ijse.drivingschoolmanagement.dto;

import lk.ijse.drivingschoolmanagement.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseDTO {
    private String courseId;

    private String courseName;

    private Integer duration;

    private Double fee;

    public CourseDTO(Course entity) {
        this.courseId = entity.getCourseId();
        this.courseName = entity.getCourseName();
        this.duration = entity.getDuration();
        this.fee = entity.getFee();
    }
}
