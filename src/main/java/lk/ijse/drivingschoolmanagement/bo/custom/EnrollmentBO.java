package lk.ijse.drivingschoolmanagement.bo.custom;

import lk.ijse.drivingschoolmanagement.dto.EnrollmentDTO;

import java.sql.SQLException;
import java.util.List;

public interface EnrollmentBO {
    boolean saveEnrollment(EnrollmentDTO enrollmentDTO) throws SQLException, ClassNotFoundException;
    boolean updateEnrollment(EnrollmentDTO enrollmentDTO) throws SQLException, ClassNotFoundException;
    boolean deleteEnrollment(String enrollmentId) throws SQLException, ClassNotFoundException;
    EnrollmentDTO findEnrollmentById(String enrollmentId) throws SQLException, ClassNotFoundException;
    List<EnrollmentDTO> findAllEnrollments() throws SQLException, ClassNotFoundException;
    List<EnrollmentDTO> findEnrollmentsByStudent(String studentId) throws SQLException, ClassNotFoundException;
    String getNextEnrollmentId() throws SQLException, ClassNotFoundException;

    List<EnrollmentDTO> findEnrollmentsByCourse(String searchText) throws SQLException;
}