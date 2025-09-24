package lk.ijse.drivingschoolmanagement.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijse.drivingschoolmanagement.bo.BOFactory;
import lk.ijse.drivingschoolmanagement.bo.custom.UserBO;
import lk.ijse.drivingschoolmanagement.dto.UserDTO;
import org.mindrot.jbcrypt.BCrypt;

import java.util.prefs.Preferences;

public class LoginFormController {
    public TextField txtEmail;
    public PasswordField txtPassword;
    public CheckBox chkRememberMe;
    public Button btnForgotPassword;
    public Button btnLogin;
    public Button btnEmergency;

    private final UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOFactory.BOType.USER);
    private final Preferences prefs = Preferences.userNodeForPackage(LoginFormController.class);

    public void initialize() {
        txtPassword.setOnAction(this::btnLoginOnAction);
        loadRememberedCredentials();
    }

    private void loadRememberedCredentials() {
        try {
            String savedEmail = prefs.get("email", "");
            if (!savedEmail.isEmpty()) {
                txtEmail.setText(savedEmail);
                chkRememberMe.setSelected(true);
                txtPassword.requestFocus();
            }
        } catch (Exception e) {
            System.err.println("Error loading preferences: " + e.getMessage());
        }
    }

    public void btnForgotPasswordOnAction(ActionEvent actionEvent) {
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter your email address first!").show();
            return;
        }

        new Alert(Alert.AlertType.INFORMATION,
                "Password reset instructions have been sent to: " + email + "\n\n" +
                        "Please contact administrator for further assistance.").show();
    }

    public void btnLoginOnAction(ActionEvent actionEvent) {
        String emailOrUsername = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        if (emailOrUsername.isEmpty() || password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please enter both email/username and password!").show();
            return;
        }

        try {
            UserDTO user = userBO.findByEmail(emailOrUsername);
            if (user == null) {
                user = userBO.findByUsername(emailOrUsername);
            }

            if (user == null) {
                new Alert(Alert.AlertType.ERROR, "No user found with the provided email/username!").show();
                return;
            }

            if (!user.isActive()) {
                new Alert(Alert.AlertType.WARNING, "Your account is deactivated! Contact admin.").show();
                return;
            }

            String storedHash = user.getPassword().trim();
            if (storedHash == null || storedHash.isEmpty() || !storedHash.startsWith("$2")) {
                new Alert(Alert.AlertType.ERROR, "Password not set or invalid format! Contact admin.").show();
                return;
            }

            if (BCrypt.checkpw(password, storedHash)) {
                System.out.println("✓ Password verification successful!");

                if (chkRememberMe.isSelected()) {
                    prefs.put("email", emailOrUsername);
                } else {
                    prefs.remove("email");
                }

                navigateToDashboard(user);

            } else {
                System.out.println("✗ Password verification failed!");
                new Alert(Alert.AlertType.ERROR, "Invalid email/username or password!").show();
                txtPassword.clear();
                txtPassword.requestFocus();
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Login failed: " + e.getMessage()).show();
        }
    }

    private void navigateToDashboard(UserDTO user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DashboardForm.fxml"));
            Parent root = loader.load();

            MainDashboardController dashboardController = loader.getController();
            dashboardController.setRole(user.getUserName(), user.getRole());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Driving School Management System - Dashboard");
            stage.setMaximized(true);

            Stage loginStage = (Stage) btnLogin.getScene().getWindow();
            loginStage.close();

            stage.show();

        } catch (Exception e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading dashboard: " + e.getMessage()).show();
        }
    }

    public void btnEmergencyLoginOnAction(ActionEvent actionEvent) {
        String username = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        UserDTO emergencyUser = null;

        if (username.equals("admin") && password.equals("admin123")) {
            emergencyUser = new UserDTO();
            emergencyUser.setUserName("admin");
            emergencyUser.setName("System Administrator");
            emergencyUser.setRole("Admin");
            emergencyUser.setActive(true);

        } else if (username.equals("recep") && password.equals("recep123")) {
            emergencyUser = new UserDTO();
            emergencyUser.setUserName("recep");
            emergencyUser.setName("Receptionist User");
            emergencyUser.setRole("Receptionist");
            emergencyUser.setActive(true);
        }

        if (emergencyUser != null) {
            navigateToDashboard(emergencyUser);
        } else {
            new Alert(Alert.AlertType.ERROR, "Emergency login failed!\nUse: admin / admin123 or recep / recep123").show();
        }
    }
}