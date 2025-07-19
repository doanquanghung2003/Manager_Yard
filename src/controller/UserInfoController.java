package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import bean.UserModel;

public class UserInfoController {
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private TextField nameField;
    @FXML private Label joinDateLabel;
    @FXML private PasswordField passwordField;

    private UserModel user;

    public void setUser(UserModel user) {
        this.user = user;
        if (user != null) {
            emailField.setText(user.getEmail());
            usernameField.setText(user.getUserName());
            nameField.setText(user.getFullName());
            if (user.getCreateAt() != null) {
                // Nếu getCreateAt() là LocalDateTime
                joinDateLabel.setText(String.format("%02d/%02d/%04d",
                        user.getCreateAt().getDayOfMonth(),
                        user.getCreateAt().getMonthValue(),
                        user.getCreateAt().getYear()
                ));
            }
        }
    }
}
