package lk.ijse.drivingschoolmanagement.dao;

import lk.ijse.drivingschoolmanagement.dao.impl.*;
import lk.ijse.drivingschoolmanagement.util.FactoryConfiguration;
import org.hibernate.Session;

public class DAOFactory {
    private static DAOFactory instance;

    private DAOFactory() {}

    public static DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    public enum DAOType {
        COURSE, ENROLLMENT, INSTRUCTOR, LESSON, PAYMENT, QUERY, STUDENT, USER
    }

    public <T> T getDAO(DAOType type) {
        switch (type) {
            case COURSE: return (T) new CourseDAOImpl();
            case ENROLLMENT: return (T) new EnrollmentDAOImpl();
            case INSTRUCTOR: return (T) new InstructorDAOImpl();
            case LESSON: return (T) new LessonDAOImpl();
            case PAYMENT: return (T) new PaymentDAOImpl();
            case QUERY: return (T) new QueryDAOImpl();
            case STUDENT: return (T) new StudentDAOImpl();
            case USER: return (T) new UserDAOImpl();
            default: return null;
        }
    }

    public Session getSession() {
        Session session = FactoryConfiguration.getInstance().getSession();
        if (!session.getTransaction().isActive()) {
            session.beginTransaction();
        }
        return session;
    }
}
