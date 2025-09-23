package lk.ijse.drivingschoolmanagement.dao.impl;

import lk.ijse.drivingschoolmanagement.dao.custom.InstructorDAO;
import lk.ijse.drivingschoolmanagement.entity.Instructor;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class InstructorDAOImpl implements InstructorDAO {

    @Override
    public boolean save(Instructor entity, Session session) {
        try {
            session.persist(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Instructor entity, Session session) {
        try {
            session.merge(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteById(String instructorId, Session session) {
        try {
            Instructor instructor = session.get(Instructor.class, instructorId);
            if (instructor != null) {
                session.remove(instructor);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Instructor> findAll(Session session) {
        try {
            return session.createQuery("from Instructor", Instructor.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public Optional<Instructor> findById(String instructorId, Session session) {
        try {
            return Optional.ofNullable(session.get(Instructor.class, instructorId));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getLastId(Session session) {
        try {
            List<String> list = session.createQuery("SELECT i.instructorId FROM Instructor i ORDER BY i.instructorId DESC", String.class)
                    .setMaxResults(1)
                    .list();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}