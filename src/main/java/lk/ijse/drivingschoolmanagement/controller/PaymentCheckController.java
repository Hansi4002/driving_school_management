package lk.ijse.drivingschoolmanagement.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import lk.ijse.drivingschoolmanagement.bo.BOFactory;
import lk.ijse.drivingschoolmanagement.bo.custom.PaymentBO;
import lk.ijse.drivingschoolmanagement.dto.PaymentDTO;
import lombok.Setter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PaymentCheckController {
    public ComboBox<String> cmbPayments;
    public Button btnCheck;

    private PaymentBO paymentBO = (PaymentBO) BOFactory.getInstance().getBO(BOFactory.BOType.PAYMENT);

    @Setter
    private MainDashboardController dashboardController;

    public void initialize() {
        loadPaymentIds();
    }

    private void loadPaymentIds() {
        try {
            List<PaymentDTO> allPayments = paymentBO.findAllPayments();
            cmbPayments.getItems().clear();
            for (PaymentDTO payment : allPayments) {
                cmbPayments.getItems().add(payment.getPaymentId());
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading payments: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void checkPayment(ActionEvent actionEvent) {
        String selectedPaymentId = cmbPayments.getValue();

        if (selectedPaymentId == null || selectedPaymentId.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select a payment ID!").show();
            return;
        }

        try {
            PaymentDTO payment = paymentBO.findPaymentById(selectedPaymentId);
            if (payment != null && "Paid".equalsIgnoreCase(payment.getStatus())) {
                loadLessonForm();
            } else {
                new Alert(Alert.AlertType.WARNING,
                        "Payment is not completed or pending!\n" +
                                "Payment ID: " + selectedPaymentId + "\n" +
                                "Status: " + (payment != null ? payment.getStatus() : "Not Found")).show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "Error checking payment: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    private void loadLessonForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LessonForm.fxml"));
            Parent lessonForm = loader.load();

            dashboardController.contentPane.getChildren().clear();
            dashboardController.contentPane.getChildren().add(lessonForm);

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading lesson form: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }
}
