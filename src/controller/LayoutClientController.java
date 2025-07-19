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
    private StackPane contentPane;

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
        System.out.println("LayoutClientController.initialize() called");
        System.out.println("contentPane: " + contentPane);
        System.out.println("userInfoBtn: " + userInfoBtn);
        System.out.println("productListBtn: " + productListBtn);
        System.out.println("avatarMenuBtn: " + avatarMenuBtn);
        
        // If FXML injection failed, try to find the elements manually
        if (contentPane == null) {
            System.err.println("FXML injection failed for contentPane. Trying manual lookup...");
            // Try to find contentPane in the current scene
            try {
                javafx.scene.Node root = javafx.scene.control.Button.class.cast(userInfoBtn).getScene().getRoot();
                if (root != null) {
                    javafx.scene.Node foundPane = root.lookup("#contentPane");
                    if (foundPane instanceof StackPane) {
                        contentPane = (StackPane) foundPane;
                        System.out.println("Found contentPane in initialize method");
                    }
                }
            } catch (Exception e) {
                System.err.println("Could not find contentPane in initialize: " + e.getMessage());
            }
        }
        
        if (userInfoBtn != null) userInfoBtn.setOnAction(event -> handleUserInfo());
        if (productListBtn != null) productListBtn.setOnAction(event -> openProductPage());
        updateAvatarMenu();
    }

    public void setContent(Node node) {
        System.out.println("setContent called with node: " + node);
        System.out.println("contentPane: " + contentPane);
        
        // If contentPane is null, try to find it in the scene
        if (contentPane == null) {
            System.err.println("Error: contentPane is null. FXML injection may have failed.");
            System.err.println("This means the FXML file is not loading the controller properly.");
            
            // Try to find the contentPane manually as a fallback
            try {
                // This is a fallback - try to find the StackPane in the scene
                if (node != null && node.getScene() != null) {
                    javafx.scene.Node foundPane = node.getScene().lookup("#contentPane");
                    if (foundPane instanceof StackPane) {
                        contentPane = (StackPane) foundPane;
                        System.out.println("Found contentPane manually via scene lookup");
                    }
                }
                
                // If still null, try to find it in the parent scene
                if (contentPane == null && node != null && node.getScene() != null) {
                    javafx.scene.Node root = node.getScene().getRoot();
                    if (root != null) {
                        javafx.scene.Node foundPane = root.lookup("#contentPane");
                        if (foundPane instanceof StackPane) {
                            contentPane = (StackPane) foundPane;
                            System.out.println("Found contentPane manually via root lookup");
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Fallback lookup failed: " + e.getMessage());
            }
        }
        
        if (contentPane != null) {
            contentPane.getChildren().setAll(node);
            System.out.println("Content set successfully");
        } else {
            System.err.println("Could not find contentPane. Cannot set content.");
            // As a last resort, try to add the node to the scene directly
            if (node != null && node.getScene() != null) {
                try {
                    javafx.scene.Parent root = (javafx.scene.Parent) node.getScene().getRoot();
                    if (root instanceof javafx.scene.layout.BorderPane) {
                        javafx.scene.layout.BorderPane borderPane = (javafx.scene.layout.BorderPane) root;
                        borderPane.setCenter(node);
                        System.out.println("Set content directly to BorderPane center");
                    }
                } catch (Exception e) {
                    System.err.println("Direct content setting also failed: " + e.getMessage());
                }
            }
        }
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
            controller.setUser(currentUser);
            if (contentPane != null) {
                contentPane.getChildren().setAll(userInfo);
            }
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
}
