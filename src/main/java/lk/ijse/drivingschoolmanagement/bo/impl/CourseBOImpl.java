package lk.ijse.drivingschoolmanagement.bo.impl;

import lk.ijse.drivingschoolmanagement.bo.custom.CourseBO;
import lk.ijse.drivingschoolmanagement.dao.DAOFactory;
import lk.ijse.drivingschoolmanagement.dao.custom.CourseDAO;
import lk.ijse.drivingschoolmanagement.dto.CourseDTO;
import lk.ijse.drivingschoolmanagement.entity.Course;
import lk.ijse.drivingschoolmanagement.util.HibernateUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CourseBOImpl implements CourseBO {

    private final CourseDAO courseDAO = (CourseDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.COURSE);

    @Override
    public boolean saveCourse(CourseDTO courseDTO) throws SQLException {
        try {
            return HibernateUtil.execute(session -> {
                Course course = convertToEntity(courseDTO);
                return courseDAO.save(course, session);
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to save course: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateCourse(CourseDTO courseDTO) throws SQLException, ClassNotFoundException {
        try {
            return HibernateUtil.execute(session -> {
                Course course = convertToEntity(courseDTO);
                return courseDAO.update(course, session);
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to update course: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteCourse(String courseId) throws SQLException, ClassNotFoundException {
        try {
            return HibernateUtil.execute(session -> courseDAO.deleteById(courseId, session));
        } catch (RuntimeException e) {
            throw new SQLException("Failed to delete course: " + e.getMessage(), e);
        }
    }

    @Override
    public CourseDTO findCourseById(String courseId) throws SQLException, ClassNotFoundException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    courseDAO.findById(courseId, session)
                            .map(CourseDTO::new)
                            .orElse(null)
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find course: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CourseDTO> findAllCourses() throws SQLException, ClassNotFoundException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    courseDAO.findAll(session).stream()
                            .map(CourseDTO::new)
                            .collect(Collectors.toList())
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find all courses: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNextCourseId() throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session -> {
                Optional<String> lastId = courseDAO.getLastId(session);
                if (lastId.isPresent()) {
                    String id = lastId.get();
                    int num = Integer.parseInt(id.replaceAll("\\D", "")) + 1;
                    return String.format("C%04d", num);
                }
                return "C1001";
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to generate next course ID: " + e.getMessage(), e);
        }
    }

    private Course convertToEntity(CourseDTO courseDTO) {
        Course course = new Course();
        course.setCourseId(courseDTO.getCourseId());
        course.setCourseName(courseDTO.getCourseName());
        course.setDuration(courseDTO.getDuration());
        course.setFee(courseDTO.getFee());
        return course;
    }
}
