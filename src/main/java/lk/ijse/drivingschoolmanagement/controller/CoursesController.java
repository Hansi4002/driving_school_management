package lk.ijse.drivingschoolmanagement.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.drivingschoolmanagement.bo.BOFactory;
import lk.ijse.drivingschoolmanagement.bo.custom.CourseBO;
import lk.ijse.drivingschoolmanagement.dto.CourseDTO;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class CoursesController implements Initializable {

    public TextField txtSearch;
    public Button btnNewCourse;
    public TextField txtCourseId;
    public TextField txtCourseName;
    public TextField txtDuration;
    public TextField txtFee;
    public Button btnSave;
    public Button btnUpdate;
    public Button btnDelete;
    public Button btnViewAll;
    public TableView<CourseDTO> tblCourse;
    public TableColumn<CourseDTO, String> colId;
    public TableColumn<CourseDTO, String> colName;
    public TableColumn<CourseDTO, Integer> colDuration;
    public TableColumn<CourseDTO, Double> colFee;

    private final CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOType.COURSE);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableColumns();
        loadAllCourses();
        generateNextCourseId();
        setupTableSelection();
    }

    private void initializeTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colFee.setCellValueFactory(new PropertyValueFactory<>("fee"));
    }

    private void setupTableSelection() {
        tblCourse.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        setCourseData(newValue);
                    }
                }
        );
    }

    private void setCourseData(CourseDTO course) {
        txtCourseId.setText(course.getCourseId());
        txtCourseName.setText(course.getCourseName());
        txtDuration.setText(String.valueOf(course.getDuration()));
        txtFee.setText(String.valueOf(course.getFee()));
    }

    private void generateNextCourseId() {
        try {
            String nextId = courseBO.getNextCourseId();
            txtCourseId.setText(nextId);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to generate course ID: " + e.getMessage());
        }
    }

    private void loadAllCourses() {
        try {
            List<CourseDTO> courses = courseBO.findAllCourses();
            ObservableList<CourseDTO> obsList = FXCollections.observableArrayList(courses);
            tblCourse.setItems(obsList);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load courses: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        String courseName = txtCourseName.getText().trim();
        String durationText = txtDuration.getText().trim();
        String feeText = txtFee.getText().trim();

        if (courseName.isEmpty() || durationText.isEmpty() || feeText.isEmpty() ) {
            showAlert(Alert.AlertType.WARNING, "Please fill all fields!");
            return false;
        }

        if (!courseName.matches("[A-Za-z0-9\\s]+")) {
            showAlert(Alert.AlertType.WARNING, "Invalid Course Name! Only letters, numbers, and spaces allowed.");
            return false;
        }

        try {
            int duration = Integer.parseInt(durationText);
            if (duration <= 0) {
                showAlert(Alert.AlertType.WARNING, "Duration must be a positive number!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Duration must be a valid number!");
            return false;
        }

        try {
            double fee = Double.parseDouble(feeText);
            if (fee < 0) {
                showAlert(Alert.AlertType.WARNING, "Fee must be a positive number!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Fee must be a valid number!");
            return false;
        }

        return true;
    }

    public void btnNewCourseOnAction(ActionEvent actionEvent) {
        clearFields();
        generateNextCourseId();
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        if (!validateInput()) return;

        CourseDTO courseDTO = createCourseDTO();
        try {
            boolean saved = courseBO.saveCourse(courseDTO);
            if (saved) {
                showAlert(Alert.AlertType.INFORMATION, "Course saved successfully!");
                loadAllCourses();
                clearFields();
                generateNextCourseId();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to save course!");
            }
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error saving course: " + e.getMessage());
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        if (!validateInput() || txtCourseId.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please select a course to update!");
            return;
        }

        CourseDTO courseDTO = createCourseDTO();
        try {
            boolean updated = courseBO.updateCourse(courseDTO);
            if (updated) {
                showAlert(Alert.AlertType.INFORMATION, "Course updated successfully!");
                loadAllCourses();
                clearFields();
                generateNextCourseId();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to update course!");
            }
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error updating course: " + e.getMessage());
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        String courseId = txtCourseId.getText().trim();
        if (courseId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please select a course to delete!");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete Course");
        confirmation.setContentText("Are you sure you want to delete course " + courseId + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean deleted = courseBO.deleteCourse(courseId);
                    if (deleted) {
                        showAlert(Alert.AlertType.INFORMATION, "Course deleted successfully!");
                        loadAllCourses();
                        clearFields();
                        generateNextCourseId();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Failed to delete course!");
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    showAlert(Alert.AlertType.ERROR, "Error deleting course: " + e.getMessage());
                }
            }
        });
    }

    public void txtSearchOnAction(ActionEvent actionEvent) {
        searchCourses();
    }

    private void searchCourses() {
        String searchTerm = txtSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAllCourses();
            return;
        }

        try {
            List<CourseDTO> allCourses = courseBO.findAllCourses();
            ObservableList<CourseDTO> filteredCourses = FXCollections.observableArrayList();

            for (CourseDTO course : allCourses) {
                if (course.getCourseId().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        course.getCourseName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        String.valueOf(course.getDuration()).contains(searchTerm) ||
                        String.valueOf(course.getFee()).contains(searchTerm)) {
                    filteredCourses.add(course);
                }
            }

            tblCourse.setItems(filteredCourses);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error searching courses: " + e.getMessage());
        }
    }

    private CourseDTO createCourseDTO() {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourseId(txtCourseId.getText().trim());
        courseDTO.setCourseName(txtCourseName.getText().trim());
        courseDTO.setDuration(Integer.parseInt(txtDuration.getText().trim()));
        courseDTO.setFee(Double.parseDouble(txtFee.getText().trim()));
        return courseDTO;
    }

    private void clearFields() {
        txtCourseId.clear();
        txtCourseName.clear();
        txtDuration.clear();
        txtFee.clear();
        tblCourse.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.show();
    }

    public void btnViewCourseOnAction(ActionEvent actionEvent) {
        loadAllCourses();
    }
}