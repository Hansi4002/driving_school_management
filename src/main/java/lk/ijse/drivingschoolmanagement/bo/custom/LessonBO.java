package lk.ijse.drivingschoolmanagement.bo.custom;

import lk.ijse.drivingschoolmanagement.dto.LessonDTO;

import java.sql.SQLException;
import java.util.List;

public interface LessonBO {
    boolean saveLesson(LessonDTO lessonDTO) throws SQLException, ClassNotFoundException;
    boolean updateLesson(LessonDTO lessonDTO) throws SQLException, ClassNotFoundException;
    boolean deleteLesson(String lessonId) throws SQLException, ClassNotFoundException;
    LessonDTO findLessonById(String lessonId) throws SQLException, ClassNotFoundException;
    List<LessonDTO> findAllLessons() throws SQLException, ClassNotFoundException;
    String getNextLessonId() throws SQLException, ClassNotFoundException;

    int getTodayLessonsCount();
}