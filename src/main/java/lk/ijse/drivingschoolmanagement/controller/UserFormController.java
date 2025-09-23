package lk.ijse.drivingschoolmanagement.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.drivingschoolmanagement.bo.BOFactory;
import lk.ijse.drivingschoolmanagement.bo.custom.UserBO;
import lk.ijse.drivingschoolmanagement.dto.UserDTO;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserFormController implements Initializable {
    public TextField txtSearch;
    public Button btnNewUser;
    public TextField txtUserId;
    public TextField txtName;
    public TextField txtEmail;
    public TextField txtPhoneNo;
    public TextField txtUserName;
    public PasswordField txtPassword;
    public ComboBox<String> cmbRole;
    public CheckBox chkActive;
    public Button btnSave;
    public Button btnUpdate;
    public Button btnDelete;
    public Button btnRefresh;
    public TableView<UserDTO> tblUser;
    public TableColumn<UserDTO, String> colUserId;
    public TableColumn<UserDTO, String> colName;
    public TableColumn<UserDTO, String> colEmail;
    public TableColumn<UserDTO, String> colPhoneNo;
    public TableColumn<UserDTO, String> colUserName;
    public TableColumn<UserDTO, String> colRole;
    public TableColumn<UserDTO, Boolean> colActive;

    private final UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOFactory.BOType.USER);
    private ObservableList<UserDTO> originalUserList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeRoleComboBox();
        initializeTableColumns();
        setupTableSelection();
        loadAllUsers();
        generateNextUserId();
        setupButtonStates();
    }

    private void initializeRoleComboBox() {
        cmbRole.getItems().addAll("ADMIN", "RECEPTIONIST");
        cmbRole.setValue("RECEPTIONIST");
    }

    private void initializeTableColumns() {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhoneNo.setCellValueFactory(new PropertyValueFactory<>("phoneNo"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colActive.setCellValueFactory(new PropertyValueFactory<>("active"));

        colActive.setCellFactory(column -> new TableCell<UserDTO, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Active" : "Inactive");
                }
            }
        });
    }

    private void setupTableSelection() {
        tblUser.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        setUserToForm(newValue);
                        setButtonStates(false, false, true, true);
                    }
                }
        );
    }

    private void setupButtonStates() {
        setButtonStates(true, true, false, false);
    }

    private void setButtonStates(boolean newEnabled, boolean saveEnabled, boolean updateEnabled, boolean deleteEnabled) {
        btnNewUser.setDisable(!newEnabled);
        btnSave.setDisable(!saveEnabled);
        btnUpdate.setDisable(!updateEnabled);
        btnDelete.setDisable(!deleteEnabled);
    }

    private void loadAllUsers() {
        try {
            List<UserDTO> users = userBO.findAllUsers();
            originalUserList.clear();
            originalUserList.addAll(users);
            tblUser.setItems(originalUserList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generateNextUserId() {
        try {
            String nextUserId = userBO.getNextUserId();
            txtUserId.setText(nextUserId);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to generate User ID: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setUserToForm(UserDTO user) {
        txtUserId.setText(user.getUserId());
        txtName.setText(user.getName());
        txtEmail.setText(user.getEmail());
        txtPhoneNo.setText(user.getPhoneNo());
        txtUserName.setText(user.getUserName());
        txtPassword.clear();
        cmbRole.setValue(user.getRole());
        chkActive.setSelected(user.isActive());
    }

    private boolean validateInput() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhoneNo.getText().trim();
        String username = txtUserName.getText().trim();
        String password = txtPassword.getText().trim();
        String role = cmbRole.getValue();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || username.isEmpty() || role == null) {
            showAlert(Alert.AlertType.WARNING, "Please fill all required fields!");
            return false;
        }

        if (isNewUser() && password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Password is required for new users!");
            txtPassword.requestFocus();
            return false;
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert(Alert.AlertType.WARNING, "Invalid email format!");
            txtEmail.requestFocus();
            return false;
        }

        if (!phone.matches("^0\\d{9}$")) {
            showAlert(Alert.AlertType.WARNING, "Invalid phone number! Must be 10 digits starting with 0.");
            txtPhoneNo.requestFocus();
            return false;
        }

        if (!username.matches("^[a-zA-Z0-9_]{3,20}$")) {
            showAlert(Alert.AlertType.WARNING,
                    "Username must be 3-20 characters long and contain only letters, numbers, and underscores!");
            txtUserName.requestFocus();
            return false;
        }

        if (isUsernameExists(username) && !isCurrentUser(username)) {
            showAlert(Alert.AlertType.WARNING, "Username already exists! Please choose a different username.");
            txtUserName.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isNewUser() {
        try {
            String currentUserId = txtUserId.getText().trim();
            String nextUserId = userBO.getNextUserId();
            return currentUserId.equals(nextUserId);
        } catch (SQLException e) {
            return true;
        }
    }

    private boolean isUsernameExists(String username) {
        try {
            UserDTO existingUser = userBO.findByUsername(username);
            return existingUser != null;
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean isCurrentUser(String username) {
        try {
            UserDTO currentFormUser = userBO.findUserById(txtUserId.getText().trim());
            return currentFormUser != null && currentFormUser.getUserName().equals(username);
        } catch (SQLException e) {
            return false;
        }
    }

    public void btnNewUserOnAction(ActionEvent actionEvent) {
        clearForm();
        generateNextUserId();
        setButtonStates(true, true, false, false);
        txtName.requestFocus();
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        if (!validateInput()) return;

        try {
            UserDTO userDTO = createUserDTO();
            boolean saved = userBO.saveUser(userDTO);

            if (saved) {
                showAlert(Alert.AlertType.INFORMATION, "User saved successfully!");
                loadAllUsers();
                clearForm();
                generateNextUserId();
                setButtonStates(true, true, false, false);
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to save user!");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error saving user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        if (!validateInput() || txtUserId.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please select a user to update!");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Update");
        confirmation.setHeaderText("Update User");
        confirmation.setContentText("Are you sure you want to update this user?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                UserDTO userDTO = createUserDTO();

                if (txtPassword.getText().isEmpty()) {
                    UserDTO existingUser = userBO.findUserById(txtUserId.getText().trim());
                    if (existingUser != null) {
                        userDTO.setPassword(existingUser.getPassword());
                    }
                } else {
                    userDTO.setPassword(txtPassword.getText());
                }

                boolean updated = userBO.updateUser(userDTO);

                if (updated) {
                    showAlert(Alert.AlertType.INFORMATION, "User updated successfully!");
                    loadAllUsers();
                    clearForm();
                    generateNextUserId();
                    setButtonStates(true, true, false, false);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to update user!");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error updating user: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private UserDTO createUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(txtUserId.getText().trim());
        userDTO.setName(txtName.getText().trim());
        userDTO.setEmail(txtEmail.getText().trim());
        userDTO.setPhoneNo(txtPhoneNo.getText().trim());
        userDTO.setUserName(txtUserName.getText().trim());
        userDTO.setRole(cmbRole.getValue());
        userDTO.setActive(chkActive.isSelected());

        userDTO.setPassword(txtPassword.getText());

        return userDTO;
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        String userId = txtUserId.getText().trim();
        if (userId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please select a user to delete!");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete User");
        confirmation.setContentText("Are you sure you want to delete user " + userId + "? This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean deleted = userBO.deleteUser(userId);
                if (deleted) {
                    showAlert(Alert.AlertType.INFORMATION, "User deleted successfully!");
                    loadAllUsers();
                    clearForm();
                    generateNextUserId();
                    setButtonStates(true, true, false, false);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to delete user!");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error deleting user: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void btnRefreshOnAction(ActionEvent actionEvent) {
        loadAllUsers();
        showAlert(Alert.AlertType.INFORMATION, "User list refreshed!");
    }

    public void txtSearchOnAction(ActionEvent actionEvent) {
        searchUsers();
    }

    private void searchUsers() {
        String searchTerm = txtSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            tblUser.setItems(originalUserList);
            return;
        }

        ObservableList<UserDTO> filteredUsers = FXCollections.observableArrayList();

        for (UserDTO user : originalUserList) {
            if (user.getUserId().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    user.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    user.getEmail().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    user.getUserName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    user.getPhoneNo().contains(searchTerm) ||
                    user.getRole().toLowerCase().contains(searchTerm.toLowerCase())) {
                filteredUsers.add(user);
            }
        }

        tblUser.setItems(filteredUsers);

        if (filteredUsers.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No users found matching: " + searchTerm);
        }
    }

    private void clearForm() {
        txtUserId.clear();
        txtName.clear();
        txtEmail.clear();
        txtPhoneNo.clear();
        txtUserName.clear();
        txtPassword.clear();
        cmbRole.setValue("RECEPTIONIST");
        chkActive.setSelected(true);
        txtSearch.clear();
        tblUser.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
