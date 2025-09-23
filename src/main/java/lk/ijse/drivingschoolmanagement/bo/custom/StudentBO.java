package lk.ijse.drivingschoolmanagement.bo.custom;

import lk.ijse.drivingschoolmanagement.dto.StudentDTO;

import java.sql.SQLException;
import java.util.List;

public interface StudentBO {
    boolean saveStudent(StudentDTO studentDTO) throws SQLException, ClassNotFoundException;
    boolean updateStudent(StudentDTO studentDTO) throws SQLException, ClassNotFoundException;
    boolean deleteStudent(String studentId) throws SQLException, ClassNotFoundException;
    StudentDTO findStudentById(String studentId) throws SQLException, ClassNotFoundException;
    List<StudentDTO> findAllStudents() throws SQLException, ClassNotFoundException;
    String getNextStudentId() throws SQLException, ClassNotFoundException;
    List<StudentDTO> findStudentsEnrolledInAllCourses() throws SQLException, ClassNotFoundException;
    List<StudentDTO> findStudentsWithEnrolledCourses() throws SQLException, ClassNotFoundException;

    int getTotalStudents();
}