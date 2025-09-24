package lk.ijse.drivingschoolmanagement.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainDashboardController {
    public Button btnDashboard;
    public Button btnStudents;
    public Button btnCourses;
    public Button btnInstructors;
    public Button btnLessons;
    public Button btnPayments;
    public Button btnUsers;
    public Label lblCurrentUser;
    public Label lblUserRole;
    public Button btnLogout;
    public AnchorPane contentPane;

    public void loadDashboard(ActionEvent actionEvent) {
        loadUI("/view/DashboardContent.fxml");
    }

    public void loadStudents(ActionEvent actionEvent) {
        loadUI("/view/StudentForm.fxml");
    }

    public void loadCourses(ActionEvent actionEvent) {
        loadUI("/view/CoursesForm.fxml");
    }

    public void loadInstructors(ActionEvent actionEvent) {
        loadUI("/view/InstructorForm.fxml");
    }

    public void loadLessons(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PaymentCheck.fxml"));
            Parent root = loader.load();

            PaymentCheckController controller = loader.getController();
            controller.setDashboardController(this);

            contentPane.getChildren().clear();
            contentPane.getChildren().add(root);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load PaymentCheck.fxml");
        }
    }


    public void loadPayments(ActionEvent actionEvent) {
        loadUI("/view/PaymentForm.fxml");
    }

    public void loadUsers(ActionEvent actionEvent) {
        loadUI("/view/UserForm.fxml");
    }

    public void logout(ActionEvent actionEvent) {
        Stage stage = (Stage) btnLogout.getScene().getWindow();
        stage.close();
    }

    public void setRole(String username, String role) {
        lblCurrentUser.setText(username);
        lblUserRole.setText(role);

        switch (role) {
            case "Admin":
                btnDashboard.setDisable(false);
                btnStudents.setDisable(false);
                btnCourses.setDisable(false);
                btnInstructors.setDisable(false);
                btnLessons.setDisable(false);
                btnPayments.setDisable(false);
                btnUsers.setDisable(false);
                break;

            case "Receptionist":
                btnDashboard.setDisable(false);
                btnStudents.setDisable(false);
                btnLessons.setDisable(false);
                btnPayments.setDisable(false);
                btnUsers.setDisable(true);
                btnCourses.setDisable(true);
                btnInstructors.setDisable(true);
                break;

            default:
                break;
        }
        loadUI("/view/DashboardContent.fxml");
    }

    private void loadUI(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load FXML: " + fxmlFile + ". Check the file path and existence.");
        }
    }
}
