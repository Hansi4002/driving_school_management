package lk.ijse.drivingschoolmanagement.dao.custom;

import lk.ijse.drivingschoolmanagement.dao.CrudDAO;
import lk.ijse.drivingschoolmanagement.entity.Student;
import org.hibernate.Session;

import java.util.List;

public interface StudentDAO extends CrudDAO<Student, String> {
    List<Student> findStudentsEnrolledInAllCourses(Session session);
    List<Student> findStudentsWithEnrolledCourses(Session session);
}
