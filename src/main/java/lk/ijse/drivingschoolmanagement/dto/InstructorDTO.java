package lk.ijse.drivingschoolmanagement.dto;

import lk.ijse.drivingschoolmanagement.entity.Instructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InstructorDTO {
    private String instructorId;

    private String name;

    private String email;

    private String phoneNo;

    private boolean available;

    public InstructorDTO(Instructor entity) {
        this.instructorId = entity.getInstructorId();
        this.name = entity.getName();
        this.email = entity.getEmail();
        this.phoneNo = entity.getPhoneNo();
        this.available = entity.isAvailable();
    }
}