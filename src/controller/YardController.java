package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import bean.TimeSlot;
import bean.YardModel;
import bean.YardType;
import singleton.DuLieu;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class YardController implements BaseController {
    @FXML
    private FlowPane flowPane;

    @FXML
    private Button btn_proceedBooking;

    @FXML
    private Button homeBtn;

    private List<YardModel> selectedYards = new ArrayList<>();

    // Fields for adding new yard
    private String yardId;
    private String yardName;
    private YardType yardType;
    private double yardPrice;
    private String yardDescription;
    private String yardThumbnail;
    private ArrayList<String> yardImages;
    private String yardAddress;
    private boolean isAvailable;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    	public void initialize() {
		DuLieu.getInstance().loadYardsFromFile("yards.json");
		constructorView();
		setOnAction();
		refresh();

		if (homeBtn != null) {
			homeBtn.setOnAction(event -> openHomePage());
		}
	}

    @Override
    public void constructorView() {
        handleShowAllYards();
    }

    	@Override
	public void setOnAction() {
		if (btn_proceedBooking != null) {
			btn_proceedBooking.setOnAction(e -> handleProceedBooking());
		}
	}

    public void handleAddYard() {
        Scanner sc = new Scanner(System.in);
        System.out.println("🔹 Nhập thông tin sân mới:");

        this.yardId = UUID.randomUUID().toString();

        System.out.print("Tên sân: ");
        this.yardName = sc.nextLine();

        System.out.print("Loại sân (5-a-side, 7-a-side, etc): ");
        String type = sc.nextLine();
        this.yardType = YardType.fromString(type);

        System.out.print("Giá thuê sân (VD:1500): ");
        this.yardPrice = Double.parseDouble(sc.nextLine());

        System.out.print("Mô tả sân: ");
        this.yardDescription = sc.nextLine();

        System.out.print("Ảnh đại diện sân (URL/filename): ");
        this.yardThumbnail = sc.nextLine();

        System.out.print("Địa chỉ sân: ");
        this.yardAddress = sc.nextLine();

        System.out.print("Các ảnh góc sân (ngăn cách bằng dấu phẩy): ");
        String imagesInput = sc.nextLine();
        this.yardImages = new ArrayList<>();
        if (!imagesInput.trim().isEmpty()) {
            String[] imagesArray = imagesInput.split(",");
            for (String img : imagesArray) {
                this.yardImages.add(img.trim());
            }
        }

        System.out.print("Trạng thái sân (true: sẵn sàng / false: không): ");
        this.isAvailable = Boolean.parseBoolean(sc.nextLine());

        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();

        YardModel yard = new YardModel(
            yardId,
            yardName,
            yardType,
            yardPrice,
            yardDescription,
            yardThumbnail,
            yardImages,
            yardAddress,
            "active",
            isAvailable,
            createAt,
            updateAt
        );

        DuLieu.getInstance().addYard(yard);
        DuLieu.getInstance().saveYardToFile("yards.json");
        System.out.println("✅ Thêm sân thành công!");
    }

    public void handleShowAllYards() {
        flowPane.getChildren().clear();
        List<YardModel> yardList = DuLieu.getInstance().getYards();
        if (yardList == null || yardList.isEmpty()) {
            System.out.println("Không có sân nào trong danh sách.");
            return;
        }

        for (YardModel yd : yardList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlclient/ProductItemLayout.fxml"));
                VBox itemBox = loader.load();

                ProductItemController controller = loader.getController();
                controller.setData(yd);
                controller.setOnSelect(yard -> {
                    if (selectedYards.contains(yard)) {
                        selectedYards.remove(yard);
                    } else {
                        selectedYards.add(yard);
                    }
                });

                FlowPane.setMargin(itemBox, new Insets(10));
                flowPane.getChildren().add(itemBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleProceedBooking() {
        if (selectedYards.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng chọn ít nhất một sân để đặt.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlclient/Checkout.fxml"));
            Parent root = loader.load();

            BookingController controller = loader.getController();
            controller.setSelectedYards(new ArrayList<>(selectedYards));

            Stage stage = new Stage();
            stage.setTitle("Đặt sân");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadData() {
        DuLieu.getInstance().loadYardsFromFile("yards.json");
    }

    @Override
    public void refresh() {
        loadData();
    }

    private void openHomePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlclient/Home.fxml"));
            Parent homeRoot = loader.load();

            Stage stage = (Stage) homeBtn.getScene().getWindow();
            stage.getScene().setRoot(homeRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
