
package lk.ijse.drivingschoolmanagement.dto;

import lk.ijse.drivingschoolmanagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    private String userId;

    private String name;

    private String email;

    private String phoneNo;

    private String userName;

    private String role;

    private boolean active;

    private String rawPassword;

    public UserDTO(User entity) {
        this.userId = entity.getUserId();
        this.name = entity.getName();
        this.email = entity.getEmail();
        this.phoneNo = entity.getPhoneNo();
        this.userName = entity.getUserName();
        this.role = entity.getRole();
        this.active = entity.isActive();
    }
}
