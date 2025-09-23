package lk.ijse.drivingschoolmanagement.dao.impl;

import lk.ijse.drivingschoolmanagement.dao.custom.CourseDAO;
import lk.ijse.drivingschoolmanagement.entity.Course;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class CourseDAOImpl implements CourseDAO {

    @Override
    public boolean save(Course entity, Session session) {
        try {
            session.persist(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Course entity, Session session) {
        try {
            session.merge(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteById(String courseId, Session session) {
        try {
            Course course = session.get(Course.class, courseId);
            if (course != null) {
                session.remove(course);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Course> findAll(Session session) {
        try {
            return session.createQuery("from Course", Course.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public Optional<Course> findById(String courseId, Session session) {
        try {
            return Optional.ofNullable(session.get(Course.class, courseId));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getLastId(Session session) {
        try {
            List<String> list = session.createQuery("SELECT c.courseId FROM Course c ORDER BY c.courseId DESC", String.class)
                    .setMaxResults(1)
                    .list();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Course> findCoursesByDuration(int duration, Session session) {
        try {
            return session.createQuery("FROM Course c WHERE c.duration = :duration", Course.class)
                    .setParameter("duration", duration)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}