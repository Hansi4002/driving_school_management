package lk.ijse.drivingschoolmanagement.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.drivingschoolmanagement.bo.BOFactory;
import lk.ijse.drivingschoolmanagement.bo.custom.InstructorBO;
import lk.ijse.drivingschoolmanagement.dto.InstructorDTO;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class InstructorController implements Initializable {
    public TextField txtSearch;
    public Button btnNewInstructor;
    public TextField txtInstructorId;
    public TextField txtName;
    public TextField txtEmail;
    public TextField txtPhoneNo;
    public CheckBox chkAvailable;
    public Button btnSave;
    public Button btnUpdate;
    public Button btnDelete;
    public Button btnViewInstructor;
    public TableView<InstructorDTO> tblInstructor;
    public TableColumn<InstructorDTO, String> colId;
    public TableColumn<InstructorDTO, String> colName;
    public TableColumn<InstructorDTO, String> colEmail;
    public TableColumn<InstructorDTO, String> colPhoneNumber;
    public TableColumn<InstructorDTO, Boolean> colAvailable;

    private final InstructorBO instructorBO = (InstructorBO) BOFactory.getInstance().getBO(BOFactory.BOType.INSTRUCTOR);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableColumns();
        setupTableSelection();
        loadAllInstructors();
        generateNextInstructorId();
    }

    private void initializeTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("instructorId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNo"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));
    }

    private void setupTableSelection() {
        tblInstructor.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        setInstructorToForm(newValue);
                    }
                }
        );
    }

    private void loadAllInstructors() {
        try {
            List<InstructorDTO> instructors = instructorBO.findAllInstructors();
            ObservableList<InstructorDTO> instructorList = FXCollections.observableArrayList(instructors);
            tblInstructor.setItems(instructorList);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load instructors: " + e.getMessage());
        }
    }

    private void generateNextInstructorId() {
        try {
            String nextInstructorId = instructorBO.getNextInstructorId();
            txtInstructorId.setText(nextInstructorId);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to generate Instructor ID: " + e.getMessage());
        }
    }

    private void setInstructorToForm(InstructorDTO instructor) {
        txtInstructorId.setText(instructor.getInstructorId());
        txtName.setText(instructor.getName());
        txtEmail.setText(instructor.getEmail());
        txtPhoneNo.setText(instructor.getPhoneNo());
        chkAvailable.setSelected(instructor.isAvailable());
    }

    private boolean validateInput() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhoneNo.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please fill all required fields!");
            return false;
        }

        if (!name.matches("[A-Za-z\\s]+")) {
            showAlert(Alert.AlertType.WARNING, "Invalid name! Only letters and spaces allowed.");
            return false;
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert(Alert.AlertType.WARNING, "Invalid email format!");
            return false;
        }

        if (!phone.matches("^0\\d{9}$")) {
            showAlert(Alert.AlertType.WARNING, "Invalid phone number! Must be 10 digits starting with 0.");
            return false;
        }

        return true;
    }

    public void txtSearchOnAction(ActionEvent actionEvent) {
        searchInstructors();
    }

    public void btnNewInstructorOnAction(ActionEvent actionEvent) {
        clearForm();
        generateNextInstructorId();
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        if (!validateInput()) return;

        try {
            InstructorDTO instructorDTO = createInstructorDTO();
            boolean saved = instructorBO.saveInstructor(instructorDTO);

            if (saved) {
                showAlert(Alert.AlertType.INFORMATION, "Instructor saved successfully!");
                loadAllInstructors();
                clearForm();
                generateNextInstructorId();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to save instructor!");
            }
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error saving instructor: " + e.getMessage());
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        if (!validateInput() || txtInstructorId.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please select an instructor to update!");
            return;
        }

        try {
            InstructorDTO instructorDTO = createInstructorDTO();
            boolean updated = instructorBO.updateInstructor(instructorDTO);

            if (updated) {
                showAlert(Alert.AlertType.INFORMATION, "Instructor updated successfully!");
                loadAllInstructors();
                clearForm();
                generateNextInstructorId();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to update instructor!");
            }
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error updating instructor: " + e.getMessage());
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        String instructorId = txtInstructorId.getText().trim();
        if (instructorId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please select an instructor to delete!");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete Instructor");
        confirmation.setContentText("Are you sure you want to delete instructor " + instructorId + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean deleted = instructorBO.deleteInstructor(instructorId);
                    if (deleted) {
                        showAlert(Alert.AlertType.INFORMATION, "Instructor deleted successfully!");
                        loadAllInstructors();
                        clearForm();
                        generateNextInstructorId();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Failed to delete instructor!");
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    showAlert(Alert.AlertType.ERROR, "Error deleting instructor: " + e.getMessage());
                }
            }
        });
    }

    public void btnViewInstructorOnAction(ActionEvent actionEvent) {
        String instructorId = txtInstructorId.getText().trim();
        if (instructorId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter an Instructor ID!");
            return;
        }

        try {
            InstructorDTO instructor = instructorBO.findInstructorById(instructorId);
            if (instructor != null) {
                setInstructorToForm(instructor);
            } else {
                showAlert(Alert.AlertType.WARNING, "Instructor not found!");
            }
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error finding instructor: " + e.getMessage());
        }
    }

    private void searchInstructors() {
        String searchTerm = txtSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAllInstructors();
            return;
        }

        try {
            List<InstructorDTO> allInstructors = instructorBO.findAllInstructors();
            ObservableList<InstructorDTO> filteredInstructors = FXCollections.observableArrayList();

            for (InstructorDTO instructor : allInstructors) {
                if (instructor.getInstructorId().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        instructor.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        instructor.getEmail().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        instructor.getPhoneNo().contains(searchTerm)) {
                    filteredInstructors.add(instructor);
                }
            }

            tblInstructor.setItems(filteredInstructors);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error searching instructors: " + e.getMessage());
        }
    }

    private InstructorDTO createInstructorDTO() {
        InstructorDTO instructorDTO = new InstructorDTO();
        instructorDTO.setInstructorId(txtInstructorId.getText().trim());
        instructorDTO.setName(txtName.getText().trim());
        instructorDTO.setEmail(txtEmail.getText().trim());
        instructorDTO.setPhoneNo(txtPhoneNo.getText().trim());
        instructorDTO.setAvailable(chkAvailable.isSelected());
        return instructorDTO;
    }

    private void clearForm() {
        txtInstructorId.clear();
        txtName.clear();
        txtEmail.clear();
        txtPhoneNo.clear();
        chkAvailable.setSelected(true);
        tblInstructor.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.show();
    }
}