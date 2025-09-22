package lk.ijse.drivingschoolmanagement.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import lk.ijse.drivingschoolmanagement.bo.BOFactory;
import lk.ijse.drivingschoolmanagement.bo.custom.InstructorBO;
import lk.ijse.drivingschoolmanagement.bo.custom.LessonBO;
import lk.ijse.drivingschoolmanagement.bo.custom.PaymentBO;
import lk.ijse.drivingschoolmanagement.bo.custom.StudentBO;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DashboardContentController implements Initializable {
    public TextField txtSearch;
    public Label lblWelcomeMessage;
    public Label lblCurrentDateTime;
    public Label lblTotalStudents;
    public Label lblTotalInstructors;
    public Label lblTodayLessons;
    public Label lblMonthlyRevenue;

    private final StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOType.STUDENT);
    private final InstructorBO instructorBO = (InstructorBO) BOFactory.getInstance().getBO(BOFactory.BOType.INSTRUCTOR);
    private final LessonBO lessonBO = (LessonBO) BOFactory.getInstance().getBO(BOFactory.BOType.LESSON);
    private final PaymentBO paymentBO = (PaymentBO) BOFactory.getInstance().getBO(BOFactory.BOType.PAYMENT);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblWelcomeMessage.setText("Welcome to Driving School Management System!");

        startDateTimeUpdater();
        loadDashboardStats();
    }

    private void loadDashboardStats() {
        int totalStudents = studentBO.getTotalStudents();
        lblTotalStudents.setText(String.valueOf(totalStudents));

        int totalInstructors = instructorBO.getTotalInstructors();
        lblTotalInstructors.setText(String.valueOf(totalInstructors));

        int todayLessons = lessonBO.getTodayLessonsCount();
        lblTodayLessons.setText(String.valueOf(todayLessons));

        double monthlyRevenue = paymentBO.getMonthlyRevenue();
        lblMonthlyRevenue.setText("$" + String.format("%.2f", monthlyRevenue));

    }

    private void startDateTimeUpdater() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            lblCurrentDateTime.setText(now.format(formatter));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
