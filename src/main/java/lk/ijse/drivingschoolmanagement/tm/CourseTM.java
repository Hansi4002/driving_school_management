package lk.ijse.drivingschoolmanagement.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseTM {
    private String courseId;
    private String courseName;
    private Integer duration;
    private Double fee;
    private List<String> lessonIds;
    private List<String> lessonNames;
    private List<String> studentIds;
    private List<String> studentNames;
}