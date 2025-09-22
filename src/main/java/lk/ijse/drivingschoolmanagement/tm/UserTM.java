
package lk.ijse.drivingschoolmanagement.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserTM {
    private String userId;
    private String name;
    private String phoneNo;
    private String userName;
    private String role;
    private boolean active;
}
