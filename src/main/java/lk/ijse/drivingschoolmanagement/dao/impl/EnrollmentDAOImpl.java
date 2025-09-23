package lk.ijse.drivingschoolmanagement.dao.impl;

import lk.ijse.drivingschoolmanagement.dao.custom.EnrollmentDAO;
import lk.ijse.drivingschoolmanagement.entity.Enrollment;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class EnrollmentDAOImpl implements EnrollmentDAO {

    @Override
    public boolean save(Enrollment entity, Session session) {
        try {
            session.persist(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Enrollment entity, Session session) {
        try {
            session.merge(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteById(String enrollmentId, Session session) {
        try {
            Enrollment enrollment = session.get(Enrollment.class, enrollmentId);
            if (enrollment != null) {
                session.remove(enrollment);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Enrollment> findAll(Session session) {
        try {
            return session.createQuery("FROM Enrollment", Enrollment.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public Optional<Enrollment> findById(String enrollmentId, Session session) {
        try {
            return Optional.ofNullable(session.get(Enrollment.class, enrollmentId));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getLastId(Session session) {
        try {
            List<String> list = session.createQuery("SELECT e.enrollmentId FROM Enrollment e ORDER BY e.enrollmentId DESC", String.class)
                    .setMaxResults(1)
                    .list();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Enrollment> findEnrollmentsByStudent(String studentId, Session session) {
        try {
            return session.createQuery("FROM Enrollment e WHERE e.student.studentId = :studentId", Enrollment.class)
                    .setParameter("studentId", studentId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public List<Enrollment> findEnrollmentsByCourse(String courseId, Session session) {
        try {
            return session.createQuery("FROM Enrollment e WHERE e.course.courseId = :courseId", Enrollment.class)
                    .setParameter("courseId", courseId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}