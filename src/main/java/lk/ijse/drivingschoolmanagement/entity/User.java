package lk.ijse.drivingschoolmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class User {
    @Id
    private String userId;

    private String name;

    private String email;

    private String phoneNo;

    private String userName;

    private String password;

    private String role;

    private boolean active = true;

//    public void setPassword(String rawPassword) {
//        this.password = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
//    }

    public boolean verifyPassword(String rawPassword) {
        return BCrypt.checkpw(rawPassword, this.password);
    }
}