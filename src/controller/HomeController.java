package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;

public class HomeController {
    // Không cần khai báo các biến FXML cho header/menu nữa
    // Chỉ giữ lại các biến liên quan đến nội dung động nếu có

    @FXML
    public void initialize() {
        // Nếu không còn node nào trong FXML cần khởi tạo, có thể để trống hoặc xóa hàm này
    }
    // Xóa toàn bộ các hàm handleSearch, handleUserInfo, handleLogout, openProductPage
}
