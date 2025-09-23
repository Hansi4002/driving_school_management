package lk.ijse.drivingschoolmanagement.bo.impl;

import lk.ijse.drivingschoolmanagement.bo.custom.LessonBO;
import lk.ijse.drivingschoolmanagement.dao.DAOFactory;
import lk.ijse.drivingschoolmanagement.dao.custom.CourseDAO;
import lk.ijse.drivingschoolmanagement.dao.custom.InstructorDAO;
import lk.ijse.drivingschoolmanagement.dao.custom.LessonDAO;
import lk.ijse.drivingschoolmanagement.dto.LessonDTO;
import lk.ijse.drivingschoolmanagement.entity.Course;
import lk.ijse.drivingschoolmanagement.entity.Instructor;
import lk.ijse.drivingschoolmanagement.entity.Lesson;
import lk.ijse.drivingschoolmanagement.util.HibernateUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LessonBOImpl implements LessonBO {

    private final LessonDAO lessonDAO = (LessonDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.LESSON);
    private final InstructorDAO instructorDAO = (InstructorDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.INSTRUCTOR);
    private final CourseDAO courseDAO = (CourseDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.COURSE);

    @Override
    public boolean saveLesson(LessonDTO lessonDTO) throws SQLException {
        try {
            return HibernateUtil.execute(session -> {
                Optional<Instructor> instructorOpt = instructorDAO.findById(lessonDTO.getInstructorId(), session);
                Optional<Course> courseOpt = courseDAO.findById(lessonDTO.getCourseId(), session);

                if (instructorOpt.isEmpty()) {
                    throw new RuntimeException("Instructor not found: " + lessonDTO.getInstructorId());
                }
                if (courseOpt.isEmpty()) {
                    throw new RuntimeException("Course not found: " + lessonDTO.getCourseId());
                }

                Lesson lesson = convertToEntity(lessonDTO);
                lesson.setInstructor(instructorOpt.get());
                lesson.setCourse(courseOpt.get());

                return lessonDAO.save(lesson, session);
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to save lesson: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateLesson(LessonDTO lessonDTO) throws SQLException {
        try {
            return HibernateUtil.execute(session -> {
                Optional<Lesson> existingOpt = lessonDAO.findById(lessonDTO.getLessonId(), session);
                if (existingOpt.isEmpty()) {
                    throw new RuntimeException("Lesson not found for update: " + lessonDTO.getLessonId());
                }

                Optional<Instructor> instructorOpt = instructorDAO.findById(lessonDTO.getInstructorId(), session);
                Optional<Course> courseOpt = courseDAO.findById(lessonDTO.getCourseId(), session);

                if (instructorOpt.isEmpty()) {
                    throw new RuntimeException("Instructor not found: " + lessonDTO.getInstructorId());
                }
                if (courseOpt.isEmpty()) {
                    throw new RuntimeException("Course not found: " + lessonDTO.getCourseId());
                }

                Lesson lesson = existingOpt.get();
                updateLessonEntity(lesson, lessonDTO);
                lesson.setInstructor(instructorOpt.get());
                lesson.setCourse(courseOpt.get());

                return lessonDAO.update(lesson, session);
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to update lesson: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteLesson(String lessonId) throws SQLException {
        try {
            return HibernateUtil.execute(session ->
                    lessonDAO.deleteById(lessonId, session)
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to delete lesson: " + e.getMessage(), e);
        }
    }

    @Override
    public LessonDTO findLessonById(String lessonId) throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    lessonDAO.findById(lessonId, session)
                            .map(LessonDTO::new)
                            .orElse(null)
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find lesson: " + e.getMessage(), e);
        }
    }

    @Override
    public List<LessonDTO> findAllLessons() throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    lessonDAO.findAll(session).stream()
                            .map(LessonDTO::new)
                            .collect(Collectors.toList())
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find all lessons: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNextLessonId() throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session -> {
                Optional<String> lastIdOpt = lessonDAO.getLastId(session);
                if (lastIdOpt.isPresent()) {
                    String lastId = lastIdOpt.get();
                    int num = Integer.parseInt(lastId.replaceAll("\\D", "")) + 1;
                    return String.format("L%03d", num);
                }
                return "L001";
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to generate next lesson ID: " + e.getMessage(), e);
        }
    }

    @Override
    public int getTodayLessonsCount() {
        try {
            return HibernateUtil.executeWithoutTransaction(session -> {
                LocalDate today = LocalDate.now();
                String hql = "SELECT COUNT(l.lessonId) FROM Lesson l WHERE l.lessonDate = :today";
                Long count = (Long) session.createQuery(hql)
                        .setParameter("today", today)
                        .uniqueResult();
                return count != null ? count.intValue() : 0;
            });
        } catch (RuntimeException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private Lesson convertToEntity(LessonDTO lessonDTO) {
        Lesson lesson = new Lesson();
        lesson.setLessonId(lessonDTO.getLessonId());
        lesson.setLessonDate(lessonDTO.getLessonDate());
        lesson.setLessonStartTime(lessonDTO.getLessonStartTime());
        lesson.setLessonEndTime(lessonDTO.getLessonEndTime());
        lesson.setStatus(lessonDTO.getStatus());
        return lesson;
    }

    private void updateLessonEntity(Lesson lesson, LessonDTO lessonDTO) {
        lesson.setLessonDate(lessonDTO.getLessonDate());
        lesson.setLessonStartTime(lessonDTO.getLessonStartTime());
        lesson.setLessonEndTime(lessonDTO.getLessonEndTime());
        lesson.setStatus(lessonDTO.getStatus());
    }
}