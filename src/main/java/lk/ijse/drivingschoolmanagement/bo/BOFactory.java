package lk.ijse.drivingschoolmanagement.bo;

import lk.ijse.drivingschoolmanagement.bo.impl.*;

public class BOFactory {
    private static BOFactory instance;
    private BOFactory() {}

    public static BOFactory getInstance() {
        return (instance == null) ? new BOFactory() : instance;
    }

    public enum BOType {
        COURSE,
        ENROLLMENT,
        INSTRUCTOR,
        LESSON,
        PAYMENT,
        STUDENT,
        USER
    }

    public Object getBO(BOType type) {
        switch (type) {
            case COURSE:
                return new CourseBOImpl();
            case ENROLLMENT:
                return new EnrollmentBOImpl();
            case INSTRUCTOR:
                return new InstructorBOImpl();
            case LESSON:
                return new LessonBOImpl();
            case PAYMENT:
                return new PaymentBOImpl();
            case STUDENT:
                return new StudentBOImpl();
            case USER:
                return new UserBOImpl();
            default:
                return null;
        }
    }
}
