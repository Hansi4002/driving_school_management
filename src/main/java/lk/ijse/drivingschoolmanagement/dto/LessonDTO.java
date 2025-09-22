
package lk.ijse.drivingschoolmanagement.dto;

import lk.ijse.drivingschoolmanagement.entity.Lesson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LessonDTO {
    private String lessonId;

    private LocalDate lessonDate;

    private LocalTime lessonStartTime;

    private LocalTime lessonEndTime;

    private String status;

    private String instructorId;

    private String courseId;

    public LessonDTO(Lesson entity) {
        this.lessonId = entity.getLessonId();
        this.lessonDate = entity.getLessonDate();
        this.lessonStartTime = entity.getLessonStartTime();
        this.lessonEndTime = entity.getLessonEndTime();
        this.status = entity.getStatus();
        this.instructorId = entity.getInstructor() != null ? entity.getInstructor().getInstructorId() : null;
        this.courseId = entity.getCourse() != null ? entity.getCourse().getCourseId() : null;
    }
}
