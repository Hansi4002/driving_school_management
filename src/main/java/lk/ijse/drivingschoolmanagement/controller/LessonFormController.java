package lk.ijse.drivingschoolmanagement.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.drivingschoolmanagement.bo.BOFactory;
import lk.ijse.drivingschoolmanagement.bo.custom.CourseBO;
import lk.ijse.drivingschoolmanagement.bo.custom.InstructorBO;
import lk.ijse.drivingschoolmanagement.bo.custom.LessonBO;
import lk.ijse.drivingschoolmanagement.dto.CourseDTO;
import lk.ijse.drivingschoolmanagement.dto.InstructorDTO;
import lk.ijse.drivingschoolmanagement.dto.LessonDTO;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class LessonFormController implements Initializable {
    public TextField txtSearch;
    public Button btnNewLesson;
    public TextField txtLessonId;
    public DatePicker dpLessonDate;
    public TextField txtLessonStartTime;
    public TextField txtLessonEndTime;
    public ComboBox<String> cmbStatus;
    public ComboBox<String> cmbInstructor;
    public ComboBox<String> cmbCourse;
    public Button btnSave;
    public Button btnUpdate;
    public Button btnDelete;
    public Button btnViewLessons;
    public TableView<LessonDTO> tblLesson;
    public TableColumn<LessonDTO, String> colId;
    public TableColumn<LessonDTO, LocalDate> colDate;
    public TableColumn<LessonDTO, LocalTime> colStartTime;
    public TableColumn<LessonDTO, LocalTime> colEndTime;
    public TableColumn<LessonDTO, String> colStatus;
    public TableColumn<LessonDTO, String> colInstructor;
    public TableColumn<LessonDTO, String> colCourse;

    private final LessonBO lessonBO = (LessonBO) BOFactory.getInstance().getBO(BOFactory.BOType.LESSON);
    private final InstructorBO instructorBO = (InstructorBO) BOFactory.getInstance().getBO(BOFactory.BOType.INSTRUCTOR);
    private final CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOType.COURSE);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComboBoxes();
        initializeTableColumns();
        setupTableSelection();
        loadAllLessons();
        generateNextLessonId();
        loadInstructorsAndCourses();
    }

    private void initializeComboBoxes() {
        cmbStatus.getItems().addAll("SCHEDULED", "ONGOING", "COMPLETED", "CANCELLED");
        cmbStatus.setValue("SCHEDULED");
    }

    private void initializeTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("lessonId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("lessonDate"));
        colStartTime.setCellValueFactory(new PropertyValueFactory<>("lessonStartTime"));
        colEndTime.setCellValueFactory(new PropertyValueFactory<>("lessonEndTime"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colInstructor.setCellValueFactory(new PropertyValueFactory<>("instructorId"));
        colCourse.setCellValueFactory(new PropertyValueFactory<>("courseId"));
    }

    private void setupTableSelection() {
        tblLesson.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        setLessonToForm(newValue);
                    }
                }
        );
    }

    private void loadInstructorsAndCourses() {
        try {
            List<InstructorDTO> instructors = instructorBO.findAllInstructors();
            ObservableList<String> instructorIds = FXCollections.observableArrayList();
            for (InstructorDTO instructor : instructors) {
                instructorIds.add(instructor.getInstructorId() + " - " + instructor.getName());
            }
            cmbInstructor.setItems(instructorIds);

            List<CourseDTO> courses = courseBO.findAllCourses();
            ObservableList<String> courseIds = FXCollections.observableArrayList();
            for (CourseDTO course : courses) {
                courseIds.add(course.getCourseId() + " - " + course.getCourseName());
            }
            cmbCourse.setItems(courseIds);

        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load instructors/courses: " + e.getMessage());
        }
    }

    private void loadAllLessons() {
        try {
            List<LessonDTO> lessons = lessonBO.findAllLessons();
            ObservableList<LessonDTO> lessonList = FXCollections.observableArrayList(lessons);
            tblLesson.setItems(lessonList);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load lessons: " + e.getMessage());
        }
    }

    private void generateNextLessonId() {
        try {
            String nextLessonId = lessonBO.getNextLessonId();
            txtLessonId.setText(nextLessonId);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to generate Lesson ID: " + e.getMessage());
        }
    }

    private void setLessonToForm(LessonDTO lesson) {
        txtLessonId.setText(lesson.getLessonId());
        dpLessonDate.setValue(lesson.getLessonDate());
        txtLessonStartTime.setText(lesson.getLessonStartTime().toString());
        txtLessonEndTime.setText(lesson.getLessonEndTime().toString());
        cmbStatus.setValue(lesson.getStatus());

        if (lesson.getInstructorId() != null) {
            cmbInstructor.setValue(lesson.getInstructorId());
        }
        if (lesson.getCourseId() != null) {
            cmbCourse.setValue(lesson.getCourseId());
        }
    }

    private boolean validateInput() {
        LocalDate lessonDate = dpLessonDate.getValue();
        String startTimeText = txtLessonStartTime.getText().trim();
        String endTimeText = txtLessonEndTime.getText().trim();
        String status = cmbStatus.getValue();
        String instructor = cmbInstructor.getValue();
        String course = cmbCourse.getValue();

        if (lessonDate == null || startTimeText.isEmpty() || endTimeText.isEmpty() ||
                status == null || instructor == null || course == null) {
            showAlert(Alert.AlertType.WARNING, "Please fill all required fields!");
            return false;
        }

        if (lessonDate.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Lesson date cannot be in the past!");
            return false;
        }

        try {
            LocalTime startTime = LocalTime.parse(startTimeText);
            LocalTime endTime = LocalTime.parse(endTimeText);

            if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
                showAlert(Alert.AlertType.WARNING, "End time must be after start time!");
                return false;
            }

            if (java.time.Duration.between(startTime, endTime).toMinutes() < 30) {
                showAlert(Alert.AlertType.WARNING, "Lesson duration must be at least 30 minutes!");
                return false;
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.WARNING, "Invalid time format! Use HH:mm format (e.g., 09:00)");
            return false;
        }

        return true;
    }

    public void txtSearchOnAction(ActionEvent actionEvent) {
        searchLessons();
    }

    public void btnNewLessonOnAction(ActionEvent actionEvent) {
        clearForm();
        generateNextLessonId();
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        if (!validateInput()) return;

        try {
            LessonDTO lessonDTO = createLessonDTO();
            boolean saved = lessonBO.saveLesson(lessonDTO);

            if (saved) {
                showAlert(Alert.AlertType.INFORMATION, "Lesson saved successfully!");
                loadAllLessons();
                clearForm();
                generateNextLessonId();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to save lesson!");
            }
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error saving lesson: " + e.getMessage());
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        if (!validateInput() || txtLessonId.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please select a lesson to update!");
            return;
        }

        try {
            LessonDTO lessonDTO = createLessonDTO();
            boolean updated = lessonBO.updateLesson(lessonDTO);

            if (updated) {
                showAlert(Alert.AlertType.INFORMATION, "Lesson updated successfully!");
                loadAllLessons();
                clearForm();
                generateNextLessonId();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to update lesson!");
            }
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error updating lesson: " + e.getMessage());
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        String lessonId = txtLessonId.getText().trim();
        if (lessonId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please select a lesson to delete!");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete Lesson");
        confirmation.setContentText("Are you sure you want to delete lesson " + lessonId + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean deleted = lessonBO.deleteLesson(lessonId);
                    if (deleted) {
                        showAlert(Alert.AlertType.INFORMATION, "Lesson deleted successfully!");
                        loadAllLessons();
                        clearForm();
                        generateNextLessonId();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Failed to delete lesson!");
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    showAlert(Alert.AlertType.ERROR, "Error deleting lesson: " + e.getMessage());
                }
            }
        });
    }

    public void btnViewLessonsOnAction(ActionEvent actionEvent) {
        loadAllLessons();
    }

    private void searchLessons() {
        String searchTerm = txtSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAllLessons();
            return;
        }

        try {
            List<LessonDTO> allLessons = lessonBO.findAllLessons();
            ObservableList<LessonDTO> filteredLessons = FXCollections.observableArrayList();

            for (LessonDTO lesson : allLessons) {
                if (lesson.getLessonId().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        lesson.getStatus().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        lesson.getInstructorId().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        lesson.getCourseId().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        lesson.getLessonDate().toString().contains(searchTerm)) {
                    filteredLessons.add(lesson);
                }
            }

            tblLesson.setItems(filteredLessons);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error searching lessons: " + e.getMessage());
        }
    }

    private LessonDTO createLessonDTO() {
        LessonDTO lessonDTO = new LessonDTO();
        lessonDTO.setLessonId(txtLessonId.getText().trim());
        lessonDTO.setLessonDate(dpLessonDate.getValue());
        lessonDTO.setLessonStartTime(LocalTime.parse(txtLessonStartTime.getText().trim()));
        lessonDTO.setLessonEndTime(LocalTime.parse(txtLessonEndTime.getText().trim()));
        lessonDTO.setStatus(cmbStatus.getValue());

        if (cmbInstructor.getValue() != null) {
            String instructorValue = cmbInstructor.getValue();
            lessonDTO.setInstructorId(instructorValue.split(" - ")[0]);
        }

        if (cmbCourse.getValue() != null) {
            String courseValue = cmbCourse.getValue();
            lessonDTO.setCourseId(courseValue.split(" - ")[0]);
        }

        return lessonDTO;
    }

    private void clearForm() {
        txtLessonId.clear();
        dpLessonDate.setValue(LocalDate.now());
        txtLessonStartTime.clear();
        txtLessonEndTime.clear();
        cmbStatus.setValue("SCHEDULED");
        cmbInstructor.getSelectionModel().clearSelection();
        cmbCourse.getSelectionModel().clearSelection();
        tblLesson.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.show();
    }
}