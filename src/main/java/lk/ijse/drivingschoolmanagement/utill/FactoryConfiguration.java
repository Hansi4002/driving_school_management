package lk.ijse.drivingschoolmanagement.utill;

import lk.ijse.drivingschoolmanagement.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.InputStream;
import java.util.Properties;

public class FactoryConfiguration {

    private static FactoryConfiguration factoryConfiguration;
    private final SessionFactory sessionFactory;

    private FactoryConfiguration() {
        try {
            Properties properties = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("hibernate.properties")) {
                if (input == null) {
                    throw new RuntimeException("hibernate.properties not found in resources folder!");
                }
                properties.load(input);
            }

            Configuration configuration = new Configuration();
            configuration.setProperties(properties)
                    .addAnnotatedClass(Student.class)
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Course.class)
                    .addAnnotatedClass(Instructor.class)
                    .addAnnotatedClass(Lesson.class)
                    .addAnnotatedClass(Enrollment.class)
                    .addAnnotatedClass(Payment.class);

            sessionFactory = configuration.buildSessionFactory();
            System.out.println("✅ Hibernate SessionFactory created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Hibernate configuration failed", e);
        }
    }

    public static synchronized FactoryConfiguration getInstance() {
        if (factoryConfiguration == null) {
            factoryConfiguration = new FactoryConfiguration();
        }
        return factoryConfiguration;
    }

    public Session getSession() {
        return sessionFactory.openSession();
    }

    public void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            System.out.println("✅ Hibernate SessionFactory closed successfully!");
        }
    }
}
