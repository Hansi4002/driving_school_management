package lk.ijse.drivingschoolmanagement.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.drivingschoolmanagement.bo.BOFactory;
import lk.ijse.drivingschoolmanagement.bo.custom.PaymentBO;
import lk.ijse.drivingschoolmanagement.bo.custom.StudentBO;
import lk.ijse.drivingschoolmanagement.dto.PaymentDTO;
import lk.ijse.drivingschoolmanagement.dto.StudentDTO;
import lk.ijse.drivingschoolmanagement.util.EmailUtil;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class PaymentFormController implements Initializable {
    public TextField txtSearch;
    public Button btnNewPayment;
    public TextField txtPaymentId;
    public TextField txtAmount;
    public DatePicker dpPaymentDate;
    public ComboBox<String> cmbStatus;
    public ComboBox<String> cmbStudent;
    public Button btnPayment;
    public Button btnUpdate;
    public Button btnDelete;
    public Button btnEmail;
    public Button btnViewPayments;
    public TableView<PaymentDTO> tblPayment;
    public TableColumn<PaymentDTO, String> colId;
    public TableColumn<PaymentDTO, Double> colAmount;
    public TableColumn<PaymentDTO, LocalDateTime> colDate;
    public TableColumn<PaymentDTO, String> colStatus;
    public TableColumn<PaymentDTO, String> colStudent;

    private final PaymentBO paymentBO = (PaymentBO) BOFactory.getInstance().getBO(BOFactory.BOType.PAYMENT);
    private final StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOType.STUDENT);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComboBoxes();
        initializeTableColumns();
        setupTableSelection();
        loadAllPayments();
        generateNextPaymentId();
        loadStudents();
    }

    private void initializeComboBoxes() {
        cmbStatus.getItems().addAll("PENDING", "PAID", "CANCELLED", "REFUNDED");
        cmbStatus.setValue("PENDING");
    }

    private void initializeTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentId"));
    }

    private void setupTableSelection() {
        tblPayment.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        setPaymentToForm(newValue);
                    }
                }
        );
    }

    private void loadStudents() {
        try {
            List<StudentDTO> students = studentBO.findAllStudents();
            ObservableList<String> studentOptions = FXCollections.observableArrayList();
            for (StudentDTO student : students) {
                studentOptions.add(student.getStudentId() + " - " + student.getName());
            }
            cmbStudent.setItems(studentOptions);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load students: " + e.getMessage());
        }
    }

    private void loadAllPayments() {
        try {
            List<PaymentDTO> payments = paymentBO.findAllPayments();
            ObservableList<PaymentDTO> paymentList = FXCollections.observableArrayList(payments);
            tblPayment.setItems(paymentList);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load payments: " + e.getMessage());
        }
    }

    private void generateNextPaymentId() {
        try {
            String nextPaymentId = paymentBO.getNextPaymentId();
            txtPaymentId.setText(nextPaymentId);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to generate Payment ID: " + e.getMessage());
        }
    }

    private void setPaymentToForm(PaymentDTO payment) {
        txtPaymentId.setText(payment.getPaymentId());
        txtAmount.setText(String.valueOf(payment.getAmount()));
        dpPaymentDate.setValue(payment.getDate().toLocalDate());
        cmbStatus.setValue(payment.getStatus());

        if (payment.getStudentId() != null) {
            cmbStudent.setValue(payment.getStudentId());
        }
    }

    private boolean validateInput() {
        String amountText = txtAmount.getText().trim();
        LocalDate paymentDate = dpPaymentDate.getValue();
        String status = cmbStatus.getValue();
        String student = cmbStudent.getValue();

        if (amountText.isEmpty() || paymentDate == null || status == null || student == null) {
            showAlert(Alert.AlertType.WARNING, "Please fill all required fields!");
            return false;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Amount must be greater than 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid amount! Please enter a valid number.");
            return false;
        }

        if (paymentDate.isAfter(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Payment date cannot be in the future!");
            return false;
        }

        return true;
    }

    public void txtSearchOnAction(ActionEvent actionEvent) {
        searchPayments();
    }

    public void btnNewPaymentOnAction(ActionEvent actionEvent) {
        clearForm();
        generateNextPaymentId();
    }

    public void btnPaymentOnAction(ActionEvent actionEvent) {
        if (!validateInput()) return;

        try {
            PaymentDTO paymentDTO = createPaymentDTO();
            boolean saved = paymentBO.savePayment(paymentDTO);

            if (saved) {
                showAlert(Alert.AlertType.INFORMATION, "Payment saved successfully!");
                loadAllPayments();
                clearForm();
                generateNextPaymentId();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to save payment!");
            }
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error saving payment: " + e.getMessage());
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        if (!validateInput() || txtPaymentId.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please select a payment to update!");
            return;
        }

        try {
            PaymentDTO paymentDTO = createPaymentDTO();
            boolean updated = paymentBO.updatePayment(paymentDTO);

            if (updated) {
                showAlert(Alert.AlertType.INFORMATION, "Payment updated successfully!");
                loadAllPayments();
                clearForm();
                generateNextPaymentId();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to update payment!");
            }
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error updating payment: " + e.getMessage());
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        String paymentId = txtPaymentId.getText().trim();
        if (paymentId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please select a payment to delete!");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete Payment");
        confirmation.setContentText("Are you sure you want to delete payment " + paymentId + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean deleted = paymentBO.deletePayment(paymentId);
                    if (deleted) {
                        showAlert(Alert.AlertType.INFORMATION, "Payment deleted successfully!");
                        loadAllPayments();
                        clearForm();
                        generateNextPaymentId();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Failed to delete payment!");
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    showAlert(Alert.AlertType.ERROR, "Error deleting payment: " + e.getMessage());
                }
            }
        });
    }

    public void btnEmailOnAction(ActionEvent actionEvent) {
        try {
            String paymentId = txtPaymentId.getText().trim();
            if (paymentId.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please select a payment to send email!");
                return;
            }

            PaymentDTO payment = paymentBO.findPaymentById(paymentId);
            if (payment == null) {
                showAlert(Alert.AlertType.WARNING, "Payment not found!");
                return;
            }

            StudentDTO student = studentBO.findStudentById(payment.getStudentId());
            if (student == null) {
                showAlert(Alert.AlertType.WARNING, "Student not found!");
                return;
            }

            String studentEmail = student.getEmail();
            if (studentEmail == null || studentEmail.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Student email not found!");
                return;
            }

            String subject = "Payment Receipt - Driving School Management";
            String body = createEmailBody(payment, student);

            boolean emailSent = EmailUtil.sendEmail(studentEmail, subject, body);

            if (emailSent) {
                showAlert(Alert.AlertType.INFORMATION,
                        "Payment receipt sent successfully to: " + studentEmail);
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to send email!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error sending email: " + e.getMessage());
        }
    }


    private String createEmailBody(PaymentDTO payment, StudentDTO student) {
        return String.format(
                "Dear %s,\n\n" +
                        "Thank you for your payment. Here are your payment details:\n\n" +
                        "PAYMENT RECEIPT\n" +
                        "===============\n" +
                        "Payment ID: %s\n" +
                        "Student ID: %s\n" +
                        "Student Name: %s\n" +
                        "Amount Paid: Rs. %.2f\n" +
                        "Payment Date: %s\n" +
                        "Payment Status: %s\n\n" +
                        "If you have any questions, please contact our administration.\n\n" +
                        "Best regards,\n" +
                        "Driving School Management Team\n" +
                        "Contact: 011-1234567 | info@drivingschool.lk",
                student.getName(),
                payment.getPaymentId(),
                student.getStudentId(),
                student.getName(),
                payment.getAmount(),
                payment.getDate().toLocalDate().toString(),
                payment.getStatus()
        );
    }



    public void btnViewPaymentsOnAction(ActionEvent actionEvent) {
        loadAllPayments();
    }

    private void searchPayments() {
        String searchTerm = txtSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAllPayments();
            return;
        }

        try {
            List<PaymentDTO> allPayments = paymentBO.findAllPayments();
            ObservableList<PaymentDTO> filteredPayments = FXCollections.observableArrayList();

            for (PaymentDTO payment : allPayments) {
                if (payment.getPaymentId().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        payment.getStatus().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        payment.getStudentId().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        String.valueOf(payment.getAmount()).contains(searchTerm) ||
                        payment.getDate().toString().contains(searchTerm)) {
                    filteredPayments.add(payment);
                }
            }

            tblPayment.setItems(filteredPayments);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error searching payments: " + e.getMessage());
        }
    }

    private PaymentDTO createPaymentDTO() {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentId(txtPaymentId.getText().trim());
        paymentDTO.setAmount(BigDecimal.valueOf(Double.parseDouble(txtAmount.getText().trim())));

        LocalDate paymentDate = dpPaymentDate.getValue();
        LocalDateTime paymentDateTime = LocalDateTime.of(paymentDate, LocalTime.now());
        paymentDTO.setDate(paymentDateTime);

        paymentDTO.setStatus(cmbStatus.getValue());

        if (cmbStudent.getValue() != null) {
            String studentValue = cmbStudent.getValue();
            paymentDTO.setStudentId(studentValue.split(" - ")[0]);
        }

        return paymentDTO;
    }

    private void clearForm() {
        txtPaymentId.clear();
        txtAmount.clear();
        dpPaymentDate.setValue(LocalDate.now());
        cmbStatus.setValue("PENDING");
        cmbStudent.getSelectionModel().clearSelection();
        tblPayment.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.show();
    }
}