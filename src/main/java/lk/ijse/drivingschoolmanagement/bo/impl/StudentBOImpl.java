package lk.ijse.drivingschoolmanagement.bo.impl;

import lk.ijse.drivingschoolmanagement.bo.custom.StudentBO;
import lk.ijse.drivingschoolmanagement.dao.DAOFactory;
import lk.ijse.drivingschoolmanagement.dao.custom.StudentDAO;
import lk.ijse.drivingschoolmanagement.dto.StudentDTO;
import lk.ijse.drivingschoolmanagement.entity.Student;
import lk.ijse.drivingschoolmanagement.util.HibernateUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StudentBOImpl implements StudentBO {
    private final StudentDAO studentDAO = (StudentDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.STUDENT);

    @Override
    public boolean saveStudent(StudentDTO studentDTO) throws SQLException {
        try {
            return HibernateUtil.execute(session -> {
                Student student = convertToEntity(studentDTO);
                return studentDAO.save(student, session);
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to save student: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStudent(StudentDTO studentDTO) throws SQLException, ClassNotFoundException {
        try {
            return HibernateUtil.execute(session -> {
                Student student = convertToEntity(studentDTO);
                return studentDAO.update(student, session);
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to update student: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteStudent(String studentId) throws SQLException, ClassNotFoundException {
        try {
            return HibernateUtil.execute(session ->
                    studentDAO.deleteById(studentId, session)
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to delete student: " + e.getMessage(), e);
        }
    }

    @Override
    public StudentDTO findStudentById(String studentId) throws SQLException, ClassNotFoundException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    studentDAO.findById(studentId, session)
                            .map(StudentDTO::new)
                            .orElse(null)
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find student: " + e.getMessage(), e);
        }
    }

    @Override
    public List<StudentDTO> findAllStudents() throws SQLException, ClassNotFoundException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    studentDAO.findAll(session).stream()
                            .map(StudentDTO::new)
                            .collect(Collectors.toList())
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find all students: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized String getNextStudentId() throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session -> {
                Optional<String> lastId = studentDAO.getLastId(session);
                if (lastId.isPresent()) {
                    String id = lastId.get();
                    int num = Integer.parseInt(id.replaceAll("\\D", "")) + 1;
                    return String.format("S%03d", num);
                }
                return "S001";
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to generate next student ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<StudentDTO> findStudentsEnrolledInAllCourses() throws SQLException, ClassNotFoundException {
        try {
            return HibernateUtil.execute(session ->
                    studentDAO.findStudentsEnrolledInAllCourses(session).stream()
                            .map(StudentDTO::new)
                            .collect(Collectors.toList())
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find students enrolled in all courses: " + e.getMessage(), e);
        }
    }

    @Override
    public List<StudentDTO> findStudentsWithEnrolledCourses() throws SQLException, ClassNotFoundException {
        try {
            return HibernateUtil.execute(session ->
                    studentDAO.findStudentsWithEnrolledCourses(session).stream()
                            .map(StudentDTO::new)
                            .collect(Collectors.toList())
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find students with enrolled courses: " + e.getMessage(), e);
        }
    }

    @Override
    public int getTotalStudents() {
        try {
            return HibernateUtil.execute(session -> {
                String hql = "SELECT COUNT(s.studentId) FROM Student s";
                Long count = (Long) session.createQuery(hql).uniqueResult();
                return count != null ? count.intValue() : 0;
            });
        } catch (RuntimeException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private Student convertToEntity(StudentDTO studentDTO) {
        Student student = new Student();
        student.setStudentId(studentDTO.getStudentId());
        student.setName(studentDTO.getName());
        student.setNic(studentDTO.getNic());
        student.setEmail(studentDTO.getEmail());
        student.setPhoneNo(studentDTO.getPhoneNo());
        student.setEnrollmentDate(studentDTO.getEnrollmentDate());
        return student;
    }
}