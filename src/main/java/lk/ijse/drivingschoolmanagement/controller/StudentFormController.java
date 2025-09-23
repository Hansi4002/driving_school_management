package lk.ijse.drivingschoolmanagement.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lk.ijse.drivingschoolmanagement.bo.BOFactory;
import lk.ijse.drivingschoolmanagement.bo.custom.StudentBO;
import lk.ijse.drivingschoolmanagement.dto.StudentDTO;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class StudentFormController implements Initializable {
    public TextField txtSearch;
    public Button btnNewStudent;
    public TextField txtStudentId;
    public TextField txtName;
    public TextField txtNic;
    public TextField txtEmail;
    public TextField txtPhoneNo;
    public DatePicker dateEnrollment;
    public Button btnSave;
    public Button btnUpdate;
    public Button btnDelete;
    public Button btnEnrollCourse;
    public Button btnViewStudents;
    public TableView<StudentDTO> tblStudent;
    public TableColumn<StudentDTO, String> colId;
    public TableColumn<StudentDTO, String> colName;
    public TableColumn<StudentDTO, String> colNIC;
    public TableColumn<StudentDTO, String> colEmail;
    public TableColumn<StudentDTO, String> colPhoneNumber;
    public TableColumn<StudentDTO, LocalDate> colEnrollmentDate;
    public TableColumn<StudentDTO, String> colCourseCount;

    private final StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOType.STUDENT);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableColumns();
        initializeTableSelection();
        clearFields();
        loadAllStudents();
    }

    private void initializeTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colNIC.setCellValueFactory(new PropertyValueFactory<>("nic"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNo"));
        colEnrollmentDate.setCellValueFactory(new PropertyValueFactory<>("enrollmentDate"));
    }

    private void initializeTableSelection() {
        tblStudent.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        setStudentData(newValue);
                    }
                }
        );
    }

    public void btnNewStudentOnAction(ActionEvent actionEvent) {
        clearFields();
        try {
            txtStudentId.setText(studentBO.getNextStudentId());
            txtName.requestFocus();
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error generating Student ID: " + e.getMessage());
        }
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        if (!validateInput()) return;

        StudentDTO student = createStudentDTO();
        try {
            boolean saved = studentBO.saveStudent(student);
            if (saved) {
                showAlert(Alert.AlertType.INFORMATION, "Student saved successfully!");
                clearFields();
                loadAllStudents();
            } else {
                showAlert(Alert.AlertType.WARNING, "Failed to save student!");
            }
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error saving student: " + e.getMessage());
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        if (!validateInput() || txtStudentId.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please select a student to update!");
            return;
        }

        StudentDTO student = createStudentDTO();
        try {
            boolean updated = studentBO.updateStudent(student);
            if (updated) {
                showAlert(Alert.AlertType.INFORMATION, "Student updated successfully!");
                clearFields();
                loadAllStudents();
            } else {
                showAlert(Alert.AlertType.WARNING, "Failed to update student!");
            }
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error updating student: " + e.getMessage());
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        String studentId = txtStudentId.getText().trim();
        if (studentId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Select a student to delete!");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete student " + studentId + "?",
                ButtonType.YES, ButtonType.NO
        );
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    boolean deleted = studentBO.deleteStudent(studentId);
                    if (deleted) {
                        showAlert(Alert.AlertType.INFORMATION, "Student deleted successfully!");
                        clearFields();
                        loadAllStudents();
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Failed to delete student!");
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    showAlert(Alert.AlertType.ERROR, "Error deleting student: " + e.getMessage());
                }
            }
        });
    }

    private StudentDTO createStudentDTO() {
        StudentDTO student = new StudentDTO();
        student.setStudentId(txtStudentId.getText().trim());
        student.setName(txtName.getText().trim());
        student.setNic(txtNic.getText().trim());
        student.setEmail(txtEmail.getText().trim());
        student.setPhoneNo(txtPhoneNo.getText().trim());
        student.setEnrollmentDate(dateEnrollment.getValue());
        return student;
    }

    private void setStudentData(StudentDTO student) {
        txtStudentId.setText(student.getStudentId());
        txtName.setText(student.getName());
        txtNic.setText(student.getNic());
        txtEmail.setText(student.getEmail());
        txtPhoneNo.setText(student.getPhoneNo());
        dateEnrollment.setValue(student.getEnrollmentDate());
    }

    private void loadAllStudents() {
        try {
            List<StudentDTO> students = studentBO.findAllStudents();
            tblStudent.setItems(FXCollections.observableList(students));
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading students: " + e.getMessage());
        }
    }

    private void clearFields() {
        txtStudentId.clear();
        txtName.clear();
        txtNic.clear();
        txtEmail.clear();
        txtPhoneNo.clear();
        dateEnrollment.setValue(LocalDate.now());
        tblStudent.getSelectionModel().clearSelection();
    }

    private boolean validateInput() {
        String name = txtName.getText().trim();
        String nic = txtNic.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhoneNo.getText().trim();
        LocalDate enrollment = dateEnrollment.getValue();

        if (name.isEmpty() || nic.isEmpty() || email.isEmpty() || phone.isEmpty() || enrollment == null) {
            showAlert(Alert.AlertType.WARNING, "Please fill all fields!");
            return false;
        }

        if (!nic.matches("^\\d{9}[VvXx]|\\d{12}$")) {
            showAlert(Alert.AlertType.WARNING, "Invalid NIC!");
            return false;
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert(Alert.AlertType.WARNING, "Invalid Email!");
            return false;
        }

        if (!phone.matches("^0\\d{9}$")) {
            showAlert(Alert.AlertType.WARNING, "Invalid Phone Number!");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String message) {
        new Alert(type, message).show();
    }

    public void btnEnrollCourseOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EnrollmentForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Enroll Student in Course");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open Enrollment Form!").show();
        }
    }

    public void btnViewStudentsOnAction(ActionEvent actionEvent) {
        loadAllStudents();
    }
}