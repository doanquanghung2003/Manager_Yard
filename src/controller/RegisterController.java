package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import utils.UserService;
import bean.UserModel;
import java.time.LocalDateTime;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegisterController implements BaseController {

    @FXML
    private Button btn_register;

    @FXML
    private Label confirmPasswordError;

    @FXML
    private Label emailError;

    @FXML
    private Hyperlink hl_login;

    @FXML
    private Label nameError;

    @FXML
    private Label passwordError;

    @FXML
    private Label phoneError;

    @FXML
    private PasswordField pl_confirmPassword;

    @FXML
    private PasswordField pl_password;

    @FXML
    private TextField tft_email;

    @FXML
    private TextField tft_name;

    @FXML
    private TextField tft_phone;

    @FXML
    private TextField tft_username;

    @FXML
    private Label usernameError;

	@Override
	public void constructorView() {
		
		
	}

	@Override
	public void setOnAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

    public void initialize() {
        btn_register.setOnAction(e -> handleRegister());
        hl_login.setOnAction(e -> openLoginView());
    }

    private void handleRegister() {
        String username = tft_username.getText().trim();
        String password = pl_password.getText();
        String confirmPassword = pl_confirmPassword.getText();
        String email = tft_email.getText().trim();
        String fullName = tft_name.getText().trim();

        // Reset lỗi
        usernameError.setText("");
        passwordError.setText("");
        confirmPasswordError.setText("");
        emailError.setText("");
        nameError.setText("");

        boolean valid = true;
        if (username.isEmpty()) { usernameError.setText("Không được để trống"); valid = false; }
        if (password.isEmpty()) { passwordError.setText("Không được để trống"); valid = false; }
        if (!password.equals(confirmPassword)) { confirmPasswordError.setText("Mật khẩu không khớp"); valid = false; }
        if (email.isEmpty()) { emailError.setText("Không được để trống"); valid = false; }
        if (fullName.isEmpty()) { nameError.setText("Không được để trống"); valid = false; }
        if (UserService.isUsernameExists(username)) { usernameError.setText("Tên tài khoản đã tồn tại"); valid = false; }

        if (!valid) return;

        UserModel user = new UserModel(username, password, email, fullName, "customer-role", LocalDateTime.now());
        UserService.addUser(user);


        openLoginView();
    }

    private void openLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlClient/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) hl_login.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
