package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;

public class HomeController {

    @FXML
    private Button userInfoBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private ComboBox<String> sportCombo;

    @FXML
    private ComboBox<String> cityCombo;

    @FXML
    private ComboBox<String> districtCombo;

    @FXML
    private Button searchBtn;

    @FXML
    private TextField searchBar;

    @FXML
    private Button productListBtn;

    @FXML
    public void initialize() {
        // Khởi tạo dữ liệu cho các ComboBox
        sportCombo.getItems().addAll("Bóng đá", "Bóng chuyền", "Cầu lông", "Tennis");
        cityCombo.getItems().addAll("Hà Nội", "TP. Hồ Chí Minh", "Đà Nẵng");
        // districtCombo sẽ được cập nhật dựa trên cityCombo

        // Xử lý sự kiện khi chọn tỉnh/thành phố
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

        // Xử lý nút tìm kiếm
        searchBtn.setOnAction(event -> handleSearch());

        // Xử lý nút thông tin người dùng
        userInfoBtn.setOnAction(event -> handleUserInfo());

        // Xử lý nút đăng xuất
        logoutBtn.setOnAction(event -> handleLogout());

        productListBtn.setOnAction(event -> openProductPage());
    }

    private void handleSearch() {
        String sport = sportCombo.getValue();
        String city = cityCombo.getValue();
        String district = districtCombo.getValue();
        // Thực hiện logic tìm kiếm ở đây
        System.out.println("Tìm kiếm: " + sport + ", " + city + ", " + district);
    }

    private void handleUserInfo() {
        // Hiển thị thông tin người dùng
        System.out.println("Quản lý thông tin người dùng");
    }

    private void handleLogout() {
        // Xử lý đăng xuất
        System.out.println("Đăng xuất");
    }

    private void openProductPage() {
        try {
            // Load Product.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlclient/ProductsLayout.fxml"));
            Parent productRoot = loader.load();

            // Lấy Stage hiện tại
            Stage stage = (Stage) productListBtn.getScene().getWindow();

            // Hiển thị trang Product
            stage.getScene().setRoot(productRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
