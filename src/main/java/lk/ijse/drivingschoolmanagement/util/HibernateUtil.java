package lk.ijse.drivingschoolmanagement.util;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.function.Function;

public class HibernateUtil {
    public static <T> T execute(Function<Session, T> operation) {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            T result = operation.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Hibernate operation failed: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    public static <T> T executeWithoutTransaction(Function<Session, T> operation) {
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            return operation.apply(session);
        } catch (Exception e) {
            throw new RuntimeException("Hibernate operation failed: " + e.getMessage(), e);
        }
    }

    @FunctionalInterface
    public interface SessionConsumer {
        void accept(Session session);
    }
}