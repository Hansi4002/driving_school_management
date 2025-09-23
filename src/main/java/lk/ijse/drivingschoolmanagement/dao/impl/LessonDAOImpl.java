package lk.ijse.drivingschoolmanagement.dao.impl;

import lk.ijse.drivingschoolmanagement.dao.custom.LessonDAO;
import lk.ijse.drivingschoolmanagement.entity.Lesson;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class LessonDAOImpl implements LessonDAO {

    @Override
    public boolean save(Lesson entity, Session session) {
        try {
            session.persist(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Lesson entity, Session session) {
        try {
            session.merge(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteById(String lessonId, Session session) {
        try {
            Lesson lesson = session.get(Lesson.class, lessonId);
            if (lesson != null) {
                session.remove(lesson);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Lesson> findAll(Session session) {
        try {
            return session.createQuery("from Lesson", Lesson.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public Optional<Lesson> findById(String lessonId, Session session) {
        try {
            return Optional.ofNullable(session.get(Lesson.class, lessonId));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getLastId(Session session) {
        try {
            List<String> list = session.createQuery("SELECT l.lessonId FROM Lesson l ORDER BY l.lessonId DESC", String.class)
                    .setMaxResults(1)
                    .list();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}