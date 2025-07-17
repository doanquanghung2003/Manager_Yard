package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
    
public void initialize() {
		
	}
    

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

}
