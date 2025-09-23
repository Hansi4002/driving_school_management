package lk.ijse.drivingschoolmanagement.bo.custom;

import lk.ijse.drivingschoolmanagement.dto.CourseDTO;

import java.sql.SQLException;
import java.util.List;

public interface CourseBO {
    boolean saveCourse(CourseDTO courseDTO) throws SQLException, ClassNotFoundException;
    boolean updateCourse(CourseDTO courseDTO) throws SQLException, ClassNotFoundException;
    boolean deleteCourse(String courseId) throws SQLException, ClassNotFoundException;
    CourseDTO findCourseById(String courseId) throws SQLException, ClassNotFoundException;
    List<CourseDTO> findAllCourses() throws SQLException, ClassNotFoundException;
    String getNextCourseId() throws SQLException, ClassNotFoundException;
}