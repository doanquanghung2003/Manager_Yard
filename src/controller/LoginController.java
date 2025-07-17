package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginController implements BaseController {

	 @FXML
	    private Button btn_facebookLogin;

	    @FXML
	    private Button btn_googleLogin;

	    @FXML
	    private Button btn_login;

	    @FXML
	    private Hyperlink forgotPasswordLink;

	    @FXML
	    private Hyperlink hl_registerLink;

	    @FXML
	    private Text txt_loginError;

	    @FXML
	    private Text txt_passwordError;

	    @FXML
	    private Text txt_usernameError;
	    @FXML
	    private CheckBox rememberMeCheckBox;

	    @FXML
	    private PasswordField tft_password;

	    @FXML
	    private TextField tft_username;

	public void initialize() {
		
	}
	    
	@Override
	public void constructorView() {
		
		
	}
	private void openRegisterView() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlclient/Register.fxml"));
			Parent root = loader.load();

			Scene scene = new Scene(root);
			Stage stage = (Stage) hl_registerLink.getScene().getWindow();
			stage.setScene(scene);
			stage.setFullScreenExitHint("");
			stage.setFullScreen(true);
			stage.setTitle("Đăng ký");
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
			
		}
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
