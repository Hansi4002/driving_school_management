package lk.ijse.drivingschoolmanagement.dao.custom;

import lk.ijse.drivingschoolmanagement.dao.CrudDAO;
import lk.ijse.drivingschoolmanagement.entity.Enrollment;
import org.hibernate.Session;

import java.util.List;

public interface EnrollmentDAO extends CrudDAO<Enrollment, String> {
    List<Enrollment> findEnrollmentsByStudent(String studentId, Session session);
    List<Enrollment> findEnrollmentsByCourse(String courseId, Session session);
}