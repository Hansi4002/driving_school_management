package lk.ijse.drivingschoolmanagement.dao.custom;

import lk.ijse.drivingschoolmanagement.dao.CrudDAO;
import lk.ijse.drivingschoolmanagement.entity.User;
import org.hibernate.Session;

import java.util.Optional;

public interface UserDAO extends CrudDAO<User, String> {
    Optional<User> findByUsername(String username, Session session);

    Optional<User> findByEmail(String email, Session session);
}
