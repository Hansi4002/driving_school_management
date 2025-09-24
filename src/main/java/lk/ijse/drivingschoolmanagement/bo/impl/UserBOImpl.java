package lk.ijse.drivingschoolmanagement.bo.impl;

import lk.ijse.drivingschoolmanagement.bo.custom.UserBO;
import lk.ijse.drivingschoolmanagement.dao.DAOFactory;
import lk.ijse.drivingschoolmanagement.dao.custom.UserDAO;
import lk.ijse.drivingschoolmanagement.dto.UserDTO;
import lk.ijse.drivingschoolmanagement.entity.User;
import lk.ijse.drivingschoolmanagement.util.HibernateUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserBOImpl implements UserBO {

    private final UserDAO userDAO = (UserDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.USER);

    @Override
    public boolean saveUser(UserDTO userDTO) throws SQLException {
        try {
            return HibernateUtil.execute(session -> {
                User user = convertToEntity(userDTO);
                user.setPassword(hashPasswordIfNeeded(userDTO.getPassword()));
                return userDAO.save(user, session);
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to save user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateUser(UserDTO userDTO) throws SQLException {
        try {
            return HibernateUtil.execute(session -> {
                Optional<User> existingOpt = userDAO.findById(userDTO.getUserId(), session);
                if (existingOpt.isEmpty()) {
                    throw new RuntimeException("User not found for update: " + userDTO.getUserId());
                }

                User user = existingOpt.get();
//                updateUserEntity(user, userDTO);

                if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                    user.setPassword(hashPasswordIfNeeded(userDTO.getPassword()));
                }

                return userDAO.update(user, session);
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to update user: " + e.getMessage(), e);
        }
    }

    private String hashPasswordIfNeeded(String password) {
        if (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$")) {
            return password;
        }
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private void updateUserEntity(User user, UserDTO userDTO) {
        user.setUserId(userDTO.getUserId());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNo(userDTO.getPhoneNo());
        user.setUserName(userDTO.getUserName());
        user.setRole(userDTO.getRole());
        user.setActive(userDTO.isActive());
    }

    @Override
    public boolean deleteUser(String userId) throws SQLException {
        try {
            return HibernateUtil.execute(session ->
                    userDAO.deleteById(userId, session)
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    @Override
    public UserDTO findUserById(String userId) throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    userDAO.findById(userId, session)
                            .map(UserDTO::new)
                            .orElse(null)
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find user: " + e.getMessage(), e);
        }
    }

    @Override
    public List<UserDTO> findAllUsers() throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    userDAO.findAll(session).stream()
                            .map(UserDTO::new)
                            .collect(Collectors.toList())
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find all users: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNextUserId() throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session -> {
                Optional<String> lastIdOpt = userDAO.getLastId(session);
                if (lastIdOpt.isPresent()) {
                    String lastId = lastIdOpt.get();
                    int num = Integer.parseInt(lastId.substring(1)) + 1;
                    return String.format("U%03d", num);
                }
                return "U001";
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to generate next user ID: " + e.getMessage(), e);
        }
    }

    @Override
    public UserDTO findByUsername(String username) throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    userDAO.findByUsername(username, session)
                            .map(UserDTO::new)
                            .orElse(null)
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find user by username: " + e.getMessage(), e);
        }
    }

    @Override
    public UserDTO findByEmail(String email) throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    userDAO.findByEmail(email, session)
                            .map(UserDTO::new)
                            .orElse(null)
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find user by email: " + e.getMessage(), e);
        }
    }

    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        updateUserEntity(user, userDTO);
        return user;
    }
}