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
import utils.UserService;
import bean.UserModel;
import java.time.LocalDateTime;
import controller.LayoutClientController;

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

	    private LayoutClientController mainLayoutController;
	    public void setMainLayoutController(LayoutClientController controller) {
	        this.mainLayoutController = controller;
	    }

	public void initialize() {
		btn_login.setOnAction(e -> handleLogin());
		hl_registerLink.setOnAction(e -> openRegisterView());
	}
	    
	@Override
	public void constructorView() {
		
		
	}
	private void openRegisterView() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlClient/Register.fxml"));
			Parent registerRoot = loader.load();
			RegisterController registerController = loader.getController();
			if (mainLayoutController != null) {
				registerController.setMainLayoutController(mainLayoutController);
				mainLayoutController.setContent(registerRoot);
			} else {
				// fallback: đổi scene nếu không có controller cha
				Stage stage = (Stage) hl_registerLink.getScene().getWindow();
				stage.setScene(new Scene(registerRoot));
				stage.setTitle("Đăng ký");
				stage.show();
			}
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

	private void handleLogin() {
		String username = tft_username.getText().trim();
		String password = tft_password.getText();

		txt_usernameError.setText("");
		txt_passwordError.setText("");
		txt_loginError.setText("");

		if (username.isEmpty()) {
			txt_usernameError.setText("Không được để trống");
			return;
		}
		if (password.isEmpty()) {
			txt_passwordError.setText("Không được để trống");
			return;
		}
		// Giả sử UserService có hàm getUserByLogin trả về UserModel nếu đúng, null nếu sai
		UserModel userDangNhapThanhCong = UserService.getUserByLogin(username, password);
		if (userDangNhapThanhCong != null) {
			if (mainLayoutController != null) {
				mainLayoutController.setCurrentUser(userDangNhapThanhCong);
			}
			// Đăng nhập thành công, chuyển sang giao diện chính
			openMainView();
		} else {
			// Sai tài khoản hoặc mật khẩu
			txt_loginError.setText("Sai tài khoản hoặc mật khẩu");
		}
	}

	private void openMainView() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainLayout/LayoutClient.fxml"));
        Parent root = loader.load();
        LayoutClientController mainController = loader.getController();

        // Lấy user vừa đăng nhập
        String username = tft_username.getText().trim();
        UserModel loggedInUser = UserService.getUserByUsername(username);
        mainController.setCurrentUser(loggedInUser);

        // Load trang home
        FXMLLoader homeLoader = new FXMLLoader(getClass().getResource("/fxmlClient/Home.fxml"));
        Parent homeRoot = homeLoader.load();
        mainController.setContent(homeRoot);

        // Chuyển scene
        Stage stage = (Stage) btn_login.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Trang chủ");
        stage.show();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
 
}
