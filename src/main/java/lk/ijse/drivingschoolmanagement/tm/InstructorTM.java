
package lk.ijse.drivingschoolmanagement.tm;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InstructorTM {
    private String instructorId;
    private String name;
    private String email;
    private String phoneNo;
    private String lessonIds;
    private String lessonDates;
    private String courseId;
    private String courseName;
}
