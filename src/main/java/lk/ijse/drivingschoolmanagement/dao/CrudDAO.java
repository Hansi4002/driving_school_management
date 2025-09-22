package lk.ijse.drivingschoolmanagement.dao;

import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public interface CrudDAO<T, ID> extends SuperDAO {
    boolean save(T entity, Session session);
    boolean update(T entity, Session session);
    boolean deleteById(ID id, Session session);
    List<T> findAll(Session session);
    Optional<T> findById(ID id, Session session);
    Optional<String> getLastId(Session session);
}