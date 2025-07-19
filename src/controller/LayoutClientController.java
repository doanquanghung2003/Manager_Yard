package controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import controller.YardController;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import bean.UserModel;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class LayoutClientController {
    @FXML
    private StackPane stp_contentPane;

    @FXML private Button userInfoBtn;
    @FXML private Button productListBtn;
    @FXML private MenuButton avatarMenuBtn;
    private UserModel currentUser = null; 

    private LayoutClientController mainLayoutController;
    public void setMainLayoutController(LayoutClientController controller) {
        this.mainLayoutController = controller;
    }

    @FXML
    public void initialize() {
        if (userInfoBtn != null) userInfoBtn.setOnAction(event -> handleUserInfo());
        if (productListBtn != null) productListBtn.setOnAction(event -> openProductPage());
        updateAvatarMenu();
    }

    public void setContent(Node node) {
        stp_contentPane.getChildren().setAll(node);
    }

    private void handleSearch() {
        System.out.println("Tìm kiếm");
    }

    private void handleUserInfo() {
        System.out.println("Quản lý thông tin người dùng");
    }

    private void handleLogout() {
        currentUser = null;
        updateAvatarMenu();
        // Có thể chuyển về trang chủ hoặc login
    }

    private void openProductPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlClient/ProductsLayout.fxml"));
            Parent productRoot = loader.load();
            YardController yardController = loader.getController();
            yardController.setMainLayoutController(this); // 'this' là LayoutClientController hiện tại
            setContent(productRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showCheckoutPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlClient/Checkout.fxml"));
            Parent checkoutPage = loader.load();
            // Truyền controller cha (LayoutClientController) cho BookingController nếu có setter
            Object controller = loader.getController();
            // Nếu BookingController có setMainLayoutController hoặc setMainBorderPane thì gọi ở đây
            // Ví dụ:
            // if (controller instanceof BookingController) {
            //     ((BookingController) controller).setMainLayoutController(this);
            // }
            setContent(checkoutPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateAvatarMenu() {
        avatarMenuBtn.getItems().clear();
        if (currentUser == null) {
            MenuItem loginItem = new MenuItem("Đăng nhập");
            loginItem.setOnAction(e -> openLoginView());
            avatarMenuBtn.getItems().add(loginItem);
        } else {
            MenuItem infoItem = new MenuItem("Quản lý thông tin");
            infoItem.setOnAction(e -> openUserInfo());
            MenuItem historyItem = new MenuItem("Lịch sử đặt sân");
            historyItem.setOnAction(e -> openBookingHistory());
            MenuItem logoutItem = new MenuItem("Đăng xuất");
            logoutItem.setOnAction(e -> handleLogout());
            avatarMenuBtn.getItems().addAll(infoItem, historyItem, logoutItem);
        }
    }

    private void openLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlClient/Login.fxml"));
            Parent loginRoot = loader.load();
            setContent(loginRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openRegisterView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlClient/Register.fxml"));
            Parent registerRoot = loader.load();
            setContent(registerRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openUserInfo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlClient/UserInfo.fxml"));
            Parent userInfoRoot = loader.load();
            setContent(userInfoRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openBookingHistory() {
        // Load BookingHistory.fxml hoặc show dialog
    }
}
