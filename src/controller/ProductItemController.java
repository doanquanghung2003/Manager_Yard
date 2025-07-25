package controller;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;

import bean.YardModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ProductItemController {
    @FXML private Text txt_nameProduct, txt_priceProduct, txt_descriptionProduct;
    @FXML private Button btn_detailProduct, btn_bookYard;
    @FXML private ImageView img_thumbnailProduct;
    @FXML private VBox rootCard;

    private YardModel currentYard;
    private Consumer<YardModel> onSelect;
    private boolean isSelected = false;

    NumberFormat vnFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public void setData(YardModel yd) {
        this.currentYard = yd;

        txt_nameProduct.setText(yd.getYardName());
        txt_priceProduct.setText(vnFormat.format(yd.getYardPrice()) + "/1h");
        txt_descriptionProduct.setText(yd.getYardAddress());

        File file = new File(yd.getYardThumbnail());
        if (file.exists()) {
            img_thumbnailProduct.setImage(new Image(file.toURI().toString()));
        }

        // Nút chi tiết
        btn_detailProduct.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlclient/DetailProduct.fxml"));
                Parent detailRoot = loader.load();

                ProductDetailController detailController = loader.getController();
                detailController.setYard(currentYard);

                Stage detailStage = new Stage();
                detailStage.setTitle("Chi tiết sân bóng");
                detailStage.setScene(new Scene(detailRoot));
                detailStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Nút "Chọn sân"
        btn_bookYard.setOnAction(e -> {
            isSelected = !isSelected;
            updateSelectionStyle();

            if (onSelect != null) {
                onSelect.accept(currentYard);
            }
        });

        updateSelectionStyle(); 
    }

    private void updateSelectionStyle() {
        if (isSelected) {
            btn_bookYard.setText("✓ Đã chọn");
            rootCard.setStyle("-fx-background-color: #d0ffd0; -fx-border-color: green;");
        } else {
            btn_bookYard.setText("Chọn sân");
            rootCard.setStyle("");
        }
    }

    public void setOnSelect(Consumer<YardModel> onSelect) {
        this.onSelect = onSelect;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        updateSelectionStyle();
    }

    public YardModel getCurrentYard() {
        return currentYard;
    }
}