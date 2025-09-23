package lk.ijse.drivingschoolmanagement.bo.custom;

import lk.ijse.drivingschoolmanagement.dto.InstructorDTO;

import java.sql.SQLException;
import java.util.List;

public interface InstructorBO {
    boolean saveInstructor(InstructorDTO instructorDTO) throws SQLException, ClassNotFoundException;
    boolean updateInstructor(InstructorDTO instructorDTO) throws SQLException, ClassNotFoundException;
    boolean deleteInstructor(String instructorId) throws SQLException, ClassNotFoundException;
    InstructorDTO findInstructorById(String instructorId) throws SQLException, ClassNotFoundException;
    List<InstructorDTO> findAllInstructors() throws SQLException, ClassNotFoundException;
    String getNextInstructorId() throws SQLException, ClassNotFoundException;

    int getTotalInstructors();
}