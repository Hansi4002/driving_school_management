module lk.ijse.drivingschoolmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    requires java.sql;
    requires java.naming;
    requires jbcrypt;
    requires java.mail;

    opens lk.ijse.drivingschoolmanagement.controller to javafx.fxml, org.hibernate.orm.core;
    opens lk.ijse.drivingschoolmanagement.entity to org.hibernate.orm.core, jakarta.persistence;
    opens lk.ijse.drivingschoolmanagement.dto to javafx.base, org.hibernate.orm.core;
    opens lk.ijse.drivingschoolmanagement to javafx.fxml;

    exports lk.ijse.drivingschoolmanagement;
}