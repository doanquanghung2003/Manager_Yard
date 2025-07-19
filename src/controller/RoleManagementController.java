package controller;

import bean.RoleModel;
import bean.UserModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import singleton.DuLieu;
import java.util.*;
import java.util.stream.Collectors;

public class RoleManagementController {
    
    @FXML private TableView<RoleModel> rolesTable;
    @FXML private TableColumn<RoleModel, String> colRoleName;
    @FXML private TableColumn<RoleModel, String> colRoleDescription;
    @FXML private TableColumn<RoleModel, Void> colRoleActions;
    
    @FXML private VBox permissionsContainer;
    @FXML private Label lblSelectedRole;
    @FXML private Button btnSavePermissions;
    @FXML private Button btnResetPermissions;
    
    @FXML private ComboBox<UserModel> cbUsers;
    @FXML private ComboBox<RoleModel> cbRoles;
    @FXML private Button btnAssignRole;
    
    @FXML private TableView<UserModel> userRolesTable;
    @FXML private TableColumn<UserModel, String> colUserName;
    @FXML private TableColumn<UserModel, String> colUserRole;
    @FXML private TableColumn<UserModel, Void> colUserActions;
    
    @FXML private Button btnAddRole;
    @FXML private Button btnBack;
    
    private RoleModel selectedRole;
    private Map<String, CheckBox> permissionCheckBoxes;
    private List<String> allPermissions;
    
    @FXML
    public void initialize() {
        loadData();
        setupTableColumns();
        setupEventHandlers();
        setupPermissionCategories();
    }
    
    private void loadData() {
        // Load roles and users from DuLieu
        DuLieu.getInstance().loadRolesFromFile("data/roles.json");
        DuLieu.getInstance().loadUsersFromFile("data/users.json");
        
        // Setup roles table
        ObservableList<RoleModel> roles = FXCollections.observableArrayList(DuLieu.getInstance().getRoles());
        rolesTable.setItems(roles);
        
        // Setup user assignment comboboxes
        ObservableList<UserModel> users = FXCollections.observableArrayList(DuLieu.getInstance().getUsers());
        cbUsers.setItems(users);
        cbUsers.setCellFactory(param -> new ListCell<UserModel>() {
            @Override
            protected void updateItem(UserModel user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getFullName() + " (" + user.getUserName() + ")");
                }
            }
        });
        cbUsers.setButtonCell(cbUsers.getCellFactory().call(null));
        
        ObservableList<RoleModel> rolesForCombo = FXCollections.observableArrayList(DuLieu.getInstance().getRoles());
        cbRoles.setItems(rolesForCombo);
        cbRoles.setCellFactory(param -> new ListCell<RoleModel>() {
            @Override
            protected void updateItem(RoleModel role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) {
                    setText(null);
                } else {
                    setText(role.getRoleName());
                }
            }
        });
        cbRoles.setButtonCell(cbRoles.getCellFactory().call(null));
        
        // Setup user roles table
        userRolesTable.setItems(users);
    }
    
    private void setupTableColumns() {
        // Roles table columns
        colRoleName.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getRoleName()));
        colRoleDescription.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDescription()));
        colRoleActions.setCellFactory(param -> createRoleActionCell());
        
        // User roles table columns
        colUserName.setCellValueFactory(cellData -> {
            UserModel user = cellData.getValue();
            return new SimpleStringProperty(user.getFullName() + " (" + user.getUserName() + ")");
        });
        colUserRole.setCellValueFactory(cellData -> {
            UserModel user = cellData.getValue();
            RoleModel role = DuLieu.getInstance().findRoleById(user.getRoleId());
            return new SimpleStringProperty(role != null ? role.getRoleName() : "Chưa phân quyền");
        });
        colUserActions.setCellFactory(param -> createUserActionCell());
    }
    
    private void setupEventHandlers() {
        // Role selection
        rolesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedRole = newSelection;
                lblSelectedRole.setText("Vai trò: " + newSelection.getRoleName());
                loadPermissionsForRole(newSelection);
                btnSavePermissions.setDisable(false);
                btnResetPermissions.setDisable(false);
            }
        });
        
        // Add role button
        btnAddRole.setOnAction(e -> showAddRoleDialog());
        
        // Save permissions button
        btnSavePermissions.setOnAction(e -> savePermissions());
        
        // Reset permissions button
        btnResetPermissions.setOnAction(e -> loadPermissionsForRole(selectedRole));
        
        // Assign role button
        btnAssignRole.setOnAction(e -> assignRoleToUser());
        
        // Back button
        btnBack.setOnAction(e -> goBack());
    }
    
    private void setupPermissionCategories() {
        allPermissions = Arrays.asList(
            // Yard Management
            "yard.view", "yard.create", "yard.edit", "yard.delete",
            // Booking Management
            "booking.view", "booking.create", "booking.edit", "booking.delete", "booking.approve",
            // Service Management
            "service.view", "service.create", "service.edit", "service.delete",
            // User Management
            "user.view", "user.create", "user.edit", "user.delete",
            // Role Management
            "role.view", "role.create", "role.edit", "role.delete",
            // Statistics
            "stats.view", "stats.export",
            // System
            "system.settings", "system.backup", "system.restore"
        );
        
        permissionCheckBoxes = new HashMap<>();
    }
    
    private TableCell<RoleModel, Void> createRoleActionCell() {
        return new TableCell<RoleModel, Void>() {
            private final Button btnEdit = new Button("✏️");
            private final Button btnDelete = new Button("🗑️");
            private final HBox pane = new HBox(5, btnEdit, btnDelete);
            
            {
                btnEdit.setStyle("-fx-background-color: #ffc107; -fx-text-fill: #212529; -fx-font-size: 12px;");
                btnDelete.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 12px;");
                
                btnEdit.setOnAction(e -> {
                    RoleModel role = getTableView().getItems().get(getIndex());
                    showEditRoleDialog(role);
                });
                
                btnDelete.setOnAction(e -> {
                    RoleModel role = getTableView().getItems().get(getIndex());
                    deleteRole(role);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
    }
    
    private TableCell<UserModel, Void> createUserActionCell() {
        return new TableCell<UserModel, Void>() {
            private final Button btnChangeRole = new Button("🔄");
            private final HBox pane = new HBox(5, btnChangeRole);
            
            {
                btnChangeRole.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-size: 12px;");
                btnChangeRole.setOnAction(e -> {
                    UserModel user = getTableView().getItems().get(getIndex());
                    showChangeUserRoleDialog(user);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
    }
    
    private void loadPermissionsForRole(RoleModel role) {
        permissionsContainer.getChildren().clear();
        permissionCheckBoxes.clear();
        
        if (role == null) return;
        
        // Group permissions by category
        Map<String, List<String>> permissionCategories = new HashMap<>();
        permissionCategories.put("Quản lý sân", allPermissions.stream()
            .filter(p -> p.startsWith("yard.")).collect(Collectors.toList()));
        permissionCategories.put("Quản lý đặt sân", allPermissions.stream()
            .filter(p -> p.startsWith("booking.")).collect(Collectors.toList()));
        permissionCategories.put("Quản lý dịch vụ", allPermissions.stream()
            .filter(p -> p.startsWith("service.")).collect(Collectors.toList()));
        permissionCategories.put("Quản lý người dùng", allPermissions.stream()
            .filter(p -> p.startsWith("user.")).collect(Collectors.toList()));
        permissionCategories.put("Quản lý vai trò", allPermissions.stream()
            .filter(p -> p.startsWith("role.")).collect(Collectors.toList()));
        permissionCategories.put("Thống kê", allPermissions.stream()
            .filter(p -> p.startsWith("stats.")).collect(Collectors.toList()));
        permissionCategories.put("Hệ thống", allPermissions.stream()
            .filter(p -> p.startsWith("system.")).collect(Collectors.toList()));
        
        for (Map.Entry<String, List<String>> entry : permissionCategories.entrySet()) {
            VBox categoryBox = new VBox(5);
            categoryBox.setStyle("-fx-padding: 10px; -fx-border-color: #dee2e6; -fx-border-radius: 5px;");
            
            Label categoryLabel = new Label(entry.getKey());
            categoryLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            categoryBox.getChildren().add(categoryLabel);
            
            for (String permission : entry.getValue()) {
                CheckBox checkBox = new CheckBox(getPermissionDisplayName(permission));
                checkBox.setSelected(role.getPermissions().contains(permission));
                permissionCheckBoxes.put(permission, checkBox);
                categoryBox.getChildren().add(checkBox);
            }
            
            permissionsContainer.getChildren().add(categoryBox);
        }
    }
    
    private String getPermissionDisplayName(String permission) {
        Map<String, String> displayNames = new HashMap<>();
        displayNames.put("yard.view", "Xem sân");
        displayNames.put("yard.create", "Thêm sân");
        displayNames.put("yard.edit", "Sửa sân");
        displayNames.put("yard.delete", "Xóa sân");
        displayNames.put("booking.view", "Xem đặt sân");
        displayNames.put("booking.create", "Tạo đặt sân");
        displayNames.put("booking.edit", "Sửa đặt sân");
        displayNames.put("booking.delete", "Xóa đặt sân");
        displayNames.put("booking.approve", "Duyệt đặt sân");
        displayNames.put("service.view", "Xem dịch vụ");
        displayNames.put("service.create", "Thêm dịch vụ");
        displayNames.put("service.edit", "Sửa dịch vụ");
        displayNames.put("service.delete", "Xóa dịch vụ");
        displayNames.put("user.view", "Xem người dùng");
        displayNames.put("user.create", "Thêm người dùng");
        displayNames.put("user.edit", "Sửa người dùng");
        displayNames.put("user.delete", "Xóa người dùng");
        displayNames.put("role.view", "Xem vai trò");
        displayNames.put("role.create", "Thêm vai trò");
        displayNames.put("role.edit", "Sửa vai trò");
        displayNames.put("role.delete", "Xóa vai trò");
        displayNames.put("stats.view", "Xem thống kê");
        displayNames.put("stats.export", "Xuất thống kê");
        displayNames.put("system.settings", "Cài đặt hệ thống");
        displayNames.put("system.backup", "Sao lưu");
        displayNames.put("system.restore", "Khôi phục");
        
        return displayNames.getOrDefault(permission, permission);
    }
    
    private void savePermissions() {
        if (selectedRole == null) return;
        
        List<String> selectedPermissions = new ArrayList<>();
        for (Map.Entry<String, CheckBox> entry : permissionCheckBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                selectedPermissions.add(entry.getKey());
            }
        }
        
        selectedRole.setPermissions(selectedPermissions);
        selectedRole.setUpdateAt(java.time.LocalDateTime.now());
        
        DuLieu.getInstance().saveRoleToFile("data/roles.json");
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText("Đã lưu quyền hạn cho vai trò: " + selectedRole.getRoleName());
        alert.showAndWait();
    }
    
    private void showAddRoleDialog() {
        Dialog<RoleModel> dialog = new Dialog<>();
        dialog.setTitle("Thêm vai trò mới");
        
        TextField nameField = new TextField();
        nameField.setPromptText("Tên vai trò");
        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Mô tả");
        descriptionField.setPrefRowCount(3);
        
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Tên vai trò:"), nameField,
            new Label("Mô tả:"), descriptionField
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                RoleModel newRole = new RoleModel();
                newRole.setRoleId(java.util.UUID.randomUUID().toString());
                newRole.setRoleName(nameField.getText());
                newRole.setDescription(descriptionField.getText());
                newRole.setPermissions(new ArrayList<>());
                return newRole;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(newRole -> {
            DuLieu.getInstance().getRoles().add(newRole);
            DuLieu.getInstance().saveRoleToFile("data/roles.json");
            rolesTable.getItems().add(newRole);
        });
    }
    
    private void showEditRoleDialog(RoleModel role) {
        Dialog<RoleModel> dialog = new Dialog<>();
        dialog.setTitle("Sửa vai trò");
        
        TextField nameField = new TextField(role.getRoleName());
        TextArea descriptionField = new TextArea(role.getDescription());
        descriptionField.setPrefRowCount(3);
        
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Tên vai trò:"), nameField,
            new Label("Mô tả:"), descriptionField
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                role.setRoleName(nameField.getText());
                role.setDescription(descriptionField.getText());
                role.setUpdateAt(java.time.LocalDateTime.now());
                return role;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(updatedRole -> {
            DuLieu.getInstance().saveRoleToFile("data/roles.json");
            rolesTable.refresh();
        });
    }
    
    private void deleteRole(RoleModel role) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc muốn xóa vai trò: " + role.getRoleName() + "?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                DuLieu.getInstance().getRoles().remove(role);
                DuLieu.getInstance().saveRoleToFile("data/roles.json");
                rolesTable.getItems().remove(role);
                
                if (selectedRole != null && selectedRole.equals(role)) {
                    selectedRole = null;
                    permissionsContainer.getChildren().clear();
                    lblSelectedRole.setText("Chọn vai trò để xem quyền hạn");
                    btnSavePermissions.setDisable(true);
                    btnResetPermissions.setDisable(true);
                }
            }
        });
    }
    
    private void assignRoleToUser() {
        UserModel selectedUser = cbUsers.getValue();
        RoleModel selectedRole = cbRoles.getValue();
        
        if (selectedUser == null || selectedRole == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng chọn cả người dùng và vai trò!");
            alert.showAndWait();
            return;
        }
        
        selectedUser.setRoleId(selectedRole.getRoleId());
        DuLieu.getInstance().saveUserToFile("data/users.json");
        userRolesTable.refresh();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText("Đã gán vai trò " + selectedRole.getRoleName() + " cho " + selectedUser.getFullName());
        alert.showAndWait();
    }
    
    private void showChangeUserRoleDialog(UserModel user) {
        Dialog<RoleModel> dialog = new Dialog<>();
        dialog.setTitle("Thay đổi vai trò cho " + user.getFullName());
        
        ComboBox<RoleModel> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList(DuLieu.getInstance().getRoles()));
        roleCombo.setCellFactory(param -> new ListCell<RoleModel>() {
            @Override
            protected void updateItem(RoleModel role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) {
                    setText(null);
                } else {
                    setText(role.getRoleName());
                }
            }
        });
        roleCombo.setButtonCell(roleCombo.getCellFactory().call(null));
        
        // Set current role
        RoleModel currentRole = DuLieu.getInstance().findRoleById(user.getRoleId());
        if (currentRole != null) {
            roleCombo.setValue(currentRole);
        }
        
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Chọn vai trò mới:"), roleCombo
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return roleCombo.getValue();
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(newRole -> {
            if (newRole != null) {
                user.setRoleId(newRole.getRoleId());
                DuLieu.getInstance().saveUserToFile("data/users.json");
                userRolesTable.refresh();
            }
        });
    }
    
    private void goBack() {
        // Return to dashboard or previous screen
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxmlManager/Dashboard.fxml"));
            javafx.scene.Parent dashboardRoot = loader.load();
            
            javafx.scene.Scene scene = btnBack.getScene();
            scene.setRoot(dashboardRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 