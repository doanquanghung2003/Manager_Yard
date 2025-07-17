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

public class LayoutClientController {
    @FXML
    private StackPane contentPane;

    @FXML private Button userInfoBtn;
    @FXML private Button logoutBtn;
    @FXML private Button productListBtn;
    @FXML private ComboBox<String> sportCombo;
    @FXML private ComboBox<String> cityCombo;
    @FXML private ComboBox<String> districtCombo;
    @FXML private Button searchBtn;
    @FXML private TextField searchBar;

    @FXML
    public void initialize() {
        // Khởi tạo dữ liệu cho các ComboBox
        if (sportCombo != null) sportCombo.getItems().addAll("Bóng đá", "Bóng chuyền", "Cầu lông", "Tennis");
        if (cityCombo != null) cityCombo.getItems().addAll("Hà Nội", "TP. Hồ Chí Minh", "Đà Nẵng");
        // districtCombo sẽ được cập nhật dựa trên cityCombo
        if (cityCombo != null && districtCombo != null) {
            cityCombo.setOnAction(event -> {
                String selectedCity = cityCombo.getValue();
                districtCombo.getItems().clear();
                if ("Hà Nội".equals(selectedCity)) {
                    districtCombo.getItems().addAll("Ba Đình", "Hoàn Kiếm", "Cầu Giấy");
                } else if ("TP. Hồ Chí Minh".equals(selectedCity)) {
                    districtCombo.getItems().addAll("Quận 1", "Quận 3", "Quận 7");
                } else if ("Đà Nẵng".equals(selectedCity)) {
                    districtCombo.getItems().addAll("Hải Châu", "Thanh Khê");
                }
            });
        }
        if (searchBtn != null) searchBtn.setOnAction(event -> handleSearch());
        if (userInfoBtn != null) userInfoBtn.setOnAction(event -> handleUserInfo());
        if (logoutBtn != null) logoutBtn.setOnAction(event -> handleLogout());
        if (productListBtn != null) productListBtn.setOnAction(event -> openProductPage());
    }

    public void setContent(Node node) {
        contentPane.getChildren().setAll(node);
    }

    private void handleSearch() {
        String sport = sportCombo != null ? sportCombo.getValue() : null;
        String city = cityCombo != null ? cityCombo.getValue() : null;
        String district = districtCombo != null ? districtCombo.getValue() : null;
        System.out.println("Tìm kiếm: " + sport + ", " + city + ", " + district);
    }

    private void handleUserInfo() {
        System.out.println("Quản lý thông tin người dùng");
    }

    private void handleLogout() {
        System.out.println("Đăng xuất");
    }

    private void openProductPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlClient/ProductsLayout.fxml"));
            Parent productRoot = loader.load();
            controller.YardController yardController = loader.getController();
            if (productRoot instanceof StackPane) {
                StackPane stackPane = (StackPane) productRoot;
                for (Node node : stackPane.getChildren()) {
                    if (node instanceof BorderPane) {
                        yardController.setMainBorderPane((BorderPane) node);
                        break;
                    }
                }
            }
            setContent(productRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
