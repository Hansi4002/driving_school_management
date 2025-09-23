package lk.ijse.drivingschoolmanagement.bo.impl;

import lk.ijse.drivingschoolmanagement.bo.custom.EnrollmentBO;
import lk.ijse.drivingschoolmanagement.dao.DAOFactory;
import lk.ijse.drivingschoolmanagement.dao.custom.CourseDAO;
import lk.ijse.drivingschoolmanagement.dao.custom.EnrollmentDAO;
import lk.ijse.drivingschoolmanagement.dao.custom.StudentDAO;
import lk.ijse.drivingschoolmanagement.dto.EnrollmentDTO;
import lk.ijse.drivingschoolmanagement.entity.Course;
import lk.ijse.drivingschoolmanagement.entity.Enrollment;
import lk.ijse.drivingschoolmanagement.entity.Student;
import lk.ijse.drivingschoolmanagement.util.HibernateUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EnrollmentBOImpl implements EnrollmentBO {
    private final EnrollmentDAO enrollmentDAO = (EnrollmentDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.ENROLLMENT);
    private final StudentDAO studentDAO = (StudentDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.STUDENT);
    private final CourseDAO courseDAO = (CourseDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.COURSE);

    @Override
    public boolean saveEnrollment(EnrollmentDTO enrollmentDTO) throws SQLException {
        try {
            return HibernateUtil.execute(session -> {
                Optional<Student> studentOpt = studentDAO.findById(enrollmentDTO.getStudentId(), session);
                Optional<Course> courseOpt = courseDAO.findById(enrollmentDTO.getCourseId(), session);

                if (studentOpt.isEmpty()) {
                    throw new RuntimeException("Student not found: " + enrollmentDTO.getStudentId());
                }
                if (courseOpt.isEmpty()) {
                    throw new RuntimeException("Course not found: " + enrollmentDTO.getCourseId());
                }

                if (isStudentEnrolledInCourse(enrollmentDTO.getStudentId(), enrollmentDTO.getCourseId(), session)) {
                    throw new RuntimeException("Student is already enrolled in this course!");
                }

                Enrollment enrollment = convertToEntity(enrollmentDTO);
                enrollment.setStudent(studentOpt.get());
                enrollment.setCourse(courseOpt.get());

                return enrollmentDAO.save(enrollment, session);
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to save enrollment: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateEnrollment(EnrollmentDTO enrollmentDTO) throws SQLException {
        try {
            return HibernateUtil.execute(session -> {
                Optional<Enrollment> existingOpt = enrollmentDAO.findById(enrollmentDTO.getEnrollmentId(), session);
                if (existingOpt.isEmpty()) {
                    throw new RuntimeException("Enrollment not found for update: " + enrollmentDTO.getEnrollmentId());
                }

                Optional<Student> studentOpt = studentDAO.findById(enrollmentDTO.getStudentId(), session);
                Optional<Course> courseOpt = courseDAO.findById(enrollmentDTO.getCourseId(), session);

                if (studentOpt.isEmpty()) {
                    throw new RuntimeException("Student not found: " + enrollmentDTO.getStudentId());
                }
                if (courseOpt.isEmpty()) {
                    throw new RuntimeException("Course not found: " + enrollmentDTO.getCourseId());
                }

                Enrollment enrollment = existingOpt.get();
                updateEnrollmentEntity(enrollment, enrollmentDTO);
                enrollment.setStudent(studentOpt.get());
                enrollment.setCourse(courseOpt.get());

                return enrollmentDAO.update(enrollment, session);
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to update enrollment: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteEnrollment(String enrollmentId) throws SQLException {
        try {
            return HibernateUtil.execute(session ->
                    enrollmentDAO.deleteById(enrollmentId, session)
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to delete enrollment: " + e.getMessage(), e);
        }
    }

    @Override
    public EnrollmentDTO findEnrollmentById(String enrollmentId) throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    enrollmentDAO.findById(enrollmentId, session)
                            .map(EnrollmentDTO::new)
                            .orElse(null)
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find enrollment: " + e.getMessage(), e);
        }
    }

    @Override
    public List<EnrollmentDTO> findAllEnrollments() throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    enrollmentDAO.findAll(session).stream()
                            .map(EnrollmentDTO::new)
                            .collect(Collectors.toList())
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find all enrollments: " + e.getMessage(), e);
        }
    }

    @Override
    public List<EnrollmentDTO> findEnrollmentsByStudent(String studentId) throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    enrollmentDAO.findEnrollmentsByStudent(studentId, session).stream()
                            .map(EnrollmentDTO::new)
                            .collect(Collectors.toList())
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find enrollments by student: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNextEnrollmentId() throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session -> {
                Optional<String> lastIdOpt = enrollmentDAO.getLastId(session);
                if (lastIdOpt.isPresent()) {
                    String lastId = lastIdOpt.get();
                    int num = Integer.parseInt(lastId.replaceAll("\\D", "")) + 1;
                    return String.format("E%03d", num);
                }
                return "E001";
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to generate next enrollment ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<EnrollmentDTO> findEnrollmentsByCourse(String courseId) throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    enrollmentDAO.findEnrollmentsByCourse(courseId, session).stream()
                            .map(EnrollmentDTO::new)
                            .collect(Collectors.toList())
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find enrollments by course: " + e.getMessage(), e);
        }
    }

    private boolean isStudentEnrolledInCourse(String studentId, String courseId, org.hibernate.Session session) {
        String hql = "SELECT COUNT(e) FROM Enrollment e WHERE e.student.studentId = :studentId AND e.course.courseId = :courseId";
        Long count = (Long) session.createQuery(hql)
                .setParameter("studentId", studentId)
                .setParameter("courseId", courseId)
                .uniqueResult();
        return count != null && count > 0;
    }

    private Enrollment convertToEntity(EnrollmentDTO enrollmentDTO) {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(enrollmentDTO.getEnrollmentId());
        enrollment.setEnrollmentDate(enrollmentDTO.getEnrollmentDate());
        return enrollment;
    }

    private void updateEnrollmentEntity(Enrollment enrollment, EnrollmentDTO enrollmentDTO) {
        enrollment.setEnrollmentDate(enrollmentDTO.getEnrollmentDate());
    }
}