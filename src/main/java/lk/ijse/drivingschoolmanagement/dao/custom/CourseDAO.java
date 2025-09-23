package lk.ijse.drivingschoolmanagement.dao.custom;

import lk.ijse.drivingschoolmanagement.dao.CrudDAO;
import lk.ijse.drivingschoolmanagement.entity.Course;
import org.hibernate.Session;

import java.util.List;

public interface CourseDAO extends CrudDAO<Course, String> {
    List<Course> findCoursesByDuration(int duration, Session session);
}
