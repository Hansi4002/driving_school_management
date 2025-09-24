package lk.ijse.drivingschoolmanagement.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.drivingschoolmanagement.bo.BOFactory;
import lk.ijse.drivingschoolmanagement.bo.custom.CourseBO;
import lk.ijse.drivingschoolmanagement.bo.custom.EnrollmentBO;
import lk.ijse.drivingschoolmanagement.bo.custom.StudentBO;
import lk.ijse.drivingschoolmanagement.dto.CourseDTO;
import lk.ijse.drivingschoolmanagement.dto.EnrollmentDTO;
import lk.ijse.drivingschoolmanagement.dto.StudentDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EnrollmentFormController {

    public TextField txtSearch;
    public Button btnNewEnrollment;
    public TextField txtEnrollmentId;
    public DatePicker dpEnrollmentDate;
    public Button btnSave;
    public Button btnUpdate;
    public Button btnDelete;
    public Button btnViewAll;
    public TableView<EnrollmentDTO> tblEnrollment;
    public TableColumn<EnrollmentDTO, String> colEnrollmentId;
    public TableColumn<EnrollmentDTO, String> colStudentId;
    public TableColumn<EnrollmentDTO, String> colCourseId;
    public TableColumn<EnrollmentDTO, LocalDate> colEnrollmentDate;
    public ComboBox<String> comboStudentId;
    public ComboBox<String> comboCourseId;

    private EnrollmentBO enrollmentBO = (EnrollmentBO) BOFactory.getInstance().getBO(BOFactory.BOType.ENROLLMENT);
    private StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOType.STUDENT);
    private CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOType.COURSE);

    private ObservableList<EnrollmentDTO> enrollmentList = FXCollections.observableArrayList();
    private ObservableList<String> studentIdList = FXCollections.observableArrayList();
    private ObservableList<String> courseIdList = FXCollections.observableArrayList();

    public void initialize() {
        setupTableColumns();
        loadStudentIds();
        loadCourseIds();
        loadAllEnrollments();
        setButtonStates(true, false, false, false);
        clearFields();

        // Add table selection listener
        tblEnrollment.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        setEnrollmentDataToFields(newValue);
                        setButtonStates(false, false, true, true);
                    }
                }
        );
    }

    private void setupTableColumns() {
        colEnrollmentId.setCellValueFactory(new PropertyValueFactory<>("enrollmentId"));
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colCourseId.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        colEnrollmentDate.setCellValueFactory(new PropertyValueFactory<>("enrollmentDate"));
        tblEnrollment.setItems(enrollmentList);
    }

    private void loadStudentIds() {
        try {
            List<StudentDTO> allStudents = studentBO.findAllStudents();
            studentIdList.clear();
            for (StudentDTO student : allStudents) {
                studentIdList.add(student.getStudentId());
            }
            comboStudentId.setItems(studentIdList);
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading student IDs: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    private void loadCourseIds() {
        try {
            List<CourseDTO> allCourses = courseBO.findAllCourses();
            courseIdList.clear();
            for (CourseDTO course : allCourses) {
                courseIdList.add(course.getCourseId());
            }
            comboCourseId.setItems(courseIdList);
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading course IDs: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void txtSearchOnAction(ActionEvent actionEvent) {
        String searchText = txtSearch.getText().trim();
        if (!searchText.isEmpty()) {
            searchEnrollments(searchText);
        } else {
            loadAllEnrollments();
        }
    }

    public void btnNewEnrollmentOnAction(ActionEvent actionEvent) {
        clearFields();
        generateNewEnrollmentId();
        setButtonStates(false, true, false, false);
        comboStudentId.requestFocus();
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        if (validateFields()) {
            try {
                EnrollmentDTO enrollmentDTO = createEnrollmentDTOFromFields();
                boolean isSaved = enrollmentBO.saveEnrollment(enrollmentDTO);

                if (isSaved) {
                    new Alert(Alert.AlertType.INFORMATION, "Enrollment saved successfully!").show();
                    clearFields();
                    loadAllEnrollments();
                    setButtonStates(true, false, false, false);
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save enrollment!").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Error saving enrollment: " + e.getMessage()).show();
                e.printStackTrace();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        if (validateFields() && txtEnrollmentId.getText().trim().length() > 0) {
            try {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to update this enrollment?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> result = confirmation.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.YES) {
                    EnrollmentDTO enrollmentDTO = createEnrollmentDTOFromFields();
                    boolean isUpdated = enrollmentBO.updateEnrollment(enrollmentDTO);

                    if (isUpdated) {
                        new Alert(Alert.AlertType.INFORMATION, "Enrollment updated successfully!").show();
                        clearFields();
                        loadAllEnrollments();
                        setButtonStates(true, false, false, false);
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update enrollment!").show();
                    }
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Error updating enrollment: " + e.getMessage()).show();
                e.printStackTrace();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        String enrollmentId = txtEnrollmentId.getText().trim();
        if (enrollmentId.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select an enrollment to delete!").show();
            return;
        }

        try {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete enrollment " + enrollmentId + "?",
                    ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = confirmation.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.YES) {
                boolean isDeleted = enrollmentBO.deleteEnrollment(enrollmentId);

                if (isDeleted) {
                    new Alert(Alert.AlertType.INFORMATION, "Enrollment deleted successfully!").show();
                    clearFields();
                    loadAllEnrollments();
                    setButtonStates(true, false, false, false);
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete enrollment!").show();
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "Error deleting enrollment: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void btnViewEnrollmentOnAction(ActionEvent actionEvent) {
        loadAllEnrollments();
    }

    private void loadAllEnrollments() {
        try {
            List<EnrollmentDTO> allEnrollments = enrollmentBO.findAllEnrollments();
            enrollmentList.clear();
            enrollmentList.addAll(allEnrollments);
            tblEnrollment.refresh();
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading enrollments: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    private void searchEnrollments(String searchText) {
        try {
            EnrollmentDTO enrollment = enrollmentBO.findEnrollmentById(searchText);
            if (enrollment != null) {
                enrollmentList.clear();
                enrollmentList.add(enrollment);
            } else {
                List<EnrollmentDTO> studentEnrollments = enrollmentBO.findEnrollmentsByStudent(searchText);
                if (!studentEnrollments.isEmpty()) {
                    enrollmentList.clear();
                    enrollmentList.addAll(studentEnrollments);
                } else {
                    List<EnrollmentDTO> courseEnrollments = enrollmentBO.findEnrollmentsByCourse(searchText);
                    if (!courseEnrollments.isEmpty()) {
                        enrollmentList.clear();
                        enrollmentList.addAll(courseEnrollments);
                    } else {
                        new Alert(Alert.AlertType.INFORMATION, "No enrollments found for: " + searchText).show();
                        loadAllEnrollments();
                    }
                }
            }
            tblEnrollment.refresh();
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "Error searching enrollments: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    private void generateNewEnrollmentId() {
        try {
            String nextId = enrollmentBO.getNextEnrollmentId();
            txtEnrollmentId.setText(nextId);
            txtEnrollmentId.setDisable(false);
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "Error generating enrollment ID: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    private EnrollmentDTO createEnrollmentDTOFromFields() {
        return new EnrollmentDTO(
                txtEnrollmentId.getText().trim(),
                comboStudentId.getValue(),
                comboCourseId.getValue(),
                dpEnrollmentDate.getValue()
        );
    }

    private boolean validateFields() {
        String enrollmentId = txtEnrollmentId.getText().trim();
        String studentId = comboStudentId.getValue();
        String courseId = comboCourseId.getValue();
        LocalDate enrollmentDate = dpEnrollmentDate.getValue();

        if (enrollmentId.isEmpty()) {
            showValidationError("Enrollment ID cannot be empty!");
            txtEnrollmentId.requestFocus();
            return false;
        }
        if (studentId == null || studentId.isEmpty()) {
            showValidationError("Please select a student!");
            comboStudentId.requestFocus();
            return false;
        }
        if (courseId == null || courseId.isEmpty()) {
            showValidationError("Please select a course!");
            comboCourseId.requestFocus();
            return false;
        }
        if (enrollmentDate == null) {
            showValidationError("Enrollment date cannot be empty!");
            dpEnrollmentDate.requestFocus();
            return false;
        }
        if (enrollmentDate.isAfter(LocalDate.now())) {
            showValidationError("Enrollment date cannot be in the future!");
            dpEnrollmentDate.requestFocus();
            return false;
        }

        return true;
    }

    private void showValidationError(String message) {
        new Alert(Alert.AlertType.WARNING, message).show();
    }

    private void clearFields() {
        txtEnrollmentId.clear();
        comboStudentId.getSelectionModel().clearSelection();
        comboCourseId.getSelectionModel().clearSelection();
        dpEnrollmentDate.setValue(null);
        txtEnrollmentId.setDisable(true);
    }

    private void setButtonStates(boolean newEnabled, boolean saveEnabled, boolean updateEnabled, boolean deleteEnabled) {
        btnNewEnrollment.setDisable(!newEnabled);
        btnSave.setDisable(!saveEnabled);
        btnUpdate.setDisable(!updateEnabled);
        btnDelete.setDisable(!deleteEnabled);
    }

    private void setEnrollmentDataToFields(EnrollmentDTO enrollment) {
        txtEnrollmentId.setText(enrollment.getEnrollmentId());
        comboStudentId.setValue(enrollment.getStudentId());
        comboCourseId.setValue(enrollment.getCourseId());
        dpEnrollmentDate.setValue(enrollment.getEnrollmentDate());
        txtEnrollmentId.setDisable(false);
    }
}