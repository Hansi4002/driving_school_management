package lk.ijse.drivingschoolmanagement.dao.impl;

import lk.ijse.drivingschoolmanagement.dao.custom.StudentDAO;
import lk.ijse.drivingschoolmanagement.entity.Student;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class StudentDAOImpl implements StudentDAO {

    @Override
    public boolean save(Student entity, Session session) {
        try {
            session.persist(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Student entity, Session session) {
        try {
            session.merge(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteById(String studentId, Session session) {
        try {
            Student student = session.get(Student.class, studentId);
            if (student != null) {
                session.remove(student);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Student> findAll(Session session) {
        try {
            return session.createQuery("from Student", Student.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public Optional<Student> findById(String studentId, Session session) {
        try {
            return Optional.ofNullable(session.get(Student.class, studentId));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getLastId(Session session) {
        try {
            List<String> list = session.createQuery("SELECT s.studentId FROM Student s ORDER BY s.studentId DESC", String.class)
                    .setMaxResults(1)
                    .list();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Student> findStudentsEnrolledInAllCourses(Session session) {
        try {
            Query<Student> query = session.createQuery(
                    "SELECT s FROM Student s JOIN s.courses c GROUP BY s HAVING COUNT(c) = (SELECT COUNT(c2) FROM Course c2)",
                    Student.class
            );
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public List<Student> findStudentsWithEnrolledCourses(Session session) {
        try {
            Query<Student> query = session.createQuery(
                    "SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses",
                    Student.class
            );
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}