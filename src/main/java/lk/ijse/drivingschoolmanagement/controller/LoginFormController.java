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

public class LoginFormController {
    public TextField txtUsername;
    public PasswordField txtPassword;
    public CheckBox chkRememberMe;
    public Button btnForgotPassword;
    public Button btnLogin;

    private final UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOFactory.BOType.USER);

    public void btnForgotPasswordOnAction(ActionEvent actionEvent) {
        new Alert(Alert.AlertType.INFORMATION, "Please contact administrator to reset your password.").show();
    }

    public void btnLoginOnAction(ActionEvent actionEvent) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please enter username and password!").show();
            return;
        }

        try {
            UserDTO user = userBO.findByUsername(username);

            if (user != null && BCrypt.checkpw(password, user.getRawPassword())) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/view/DashboardForm.fxml"));
                Parent root = loader.load();

                MainDashboardController controller = loader.getController();
                controller.setRole(user.getRole(), user.getName());

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();

                btnLogin.getScene().getWindow().hide();
            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Invalid username or password!").show();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Login failed: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }
}
