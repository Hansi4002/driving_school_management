package lk.ijse.drivingschoolmanagement.dao.impl;

import lk.ijse.drivingschoolmanagement.dao.custom.UserDAO;
import lk.ijse.drivingschoolmanagement.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    @Override
    public boolean save(User entity, Session session) {
        try {
            session.persist(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(User entity, Session session) {
        try {
            session.merge(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteById(String userId, Session session) {
        try {
            User user = session.get(User.class, userId);
            if (user != null) {
                session.remove(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<User> findAll(Session session) {
        try {
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public Optional<User> findById(String userId, Session session) {
        try {
            return Optional.ofNullable(session.get(User.class, userId));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getLastId(Session session) {
        try {
            List<String> list = session.createQuery("SELECT u.userId FROM User u ORDER BY u.userId DESC", String.class)
                    .setMaxResults(1)
                    .list();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByUsername(String username, Session session) {
        try {
            Query<User> query = session.createQuery("FROM User u WHERE u.userName = :username", User.class);
            query.setParameter("username", username);
            return Optional.ofNullable(query.uniqueResult());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email, Session session) {
        try {
            Query<User> query = session.createQuery("FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            return Optional.ofNullable(query.uniqueResult());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}