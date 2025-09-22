
package lk.ijse.drivingschoolmanagement.tm;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LessonTM {
    private String lessonId;
    private LocalDate lessonDate;
    private LocalTime lessonStartTime;
    private LocalTime lessonEndTime;
    private String status;
    private String instructorId;
    private String instructorName;
    private String courseId;
    private String courseName;
    private List<String> studentIds;
    private List<String> studentNames;
}
