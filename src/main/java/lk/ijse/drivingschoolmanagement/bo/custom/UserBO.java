package lk.ijse.drivingschoolmanagement.bo.custom;

import lk.ijse.drivingschoolmanagement.dto.UserDTO;

import java.sql.SQLException;
import java.util.List;

public interface UserBO {
    boolean saveUser(UserDTO userDTO) throws SQLException;
    boolean updateUser(UserDTO userDTO) throws SQLException;
    boolean deleteUser(String userId) throws SQLException;
    UserDTO findUserById(String userId) throws SQLException;
    List<UserDTO> findAllUsers() throws SQLException;
    String getNextUserId() throws SQLException;
    UserDTO findByUsername(String username) throws SQLException;

    UserDTO findByEmail(String email) throws SQLException;
}