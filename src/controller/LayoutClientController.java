package controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import bean.UserModel;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.event.ActionEvent;


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

    public void setCurrentUser(UserModel user) {
        this.currentUser = user;
        updateAvatarMenu();
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
        handleUserInfo(null);
    }

    @FXML
    private void handleUserInfo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlClient/UserInfo.fxml"));
            Parent userInfo = loader.load();
            UserInfoController controller = loader.getController();
            controller.setUser(currentUser); // <-- Dòng này rất quan trọng!
            stp_contentPane.getChildren().setAll(userInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            infoItem.setOnAction(e -> handleUserInfo(null)); // Changed to call handleUserInfo directly
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
            LoginController loginController = loader.getController();
            loginController.setMainLayoutController(this); // Truyền controller cha
            setContent(loginRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openRegisterView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlClient/Register.fxml"));
            Parent registerRoot = loader.load();
            RegisterController registerController = loader.getController();
            registerController.setMainLayoutController(this); // this là LayoutClientController
            setContent(registerRoot);
        } catch (Exception e) {
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

    public void handleHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlClient/Home.fxml"));
            Parent homeRoot = loader.load();
            setContent(homeRoot); // setContent là của LayoutClientController
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
