package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import bean.YardModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.JsonUtils;

public class ProductDetailController implements Initializable {
	@FXML
	private Text txt_nameYard;
	@FXML
	private Text txt_yardStatus;
	@FXML
	private Text txt_yardType;
	@FXML
	private Text txt_descriptionYard;
	@FXML
	private Text txt_addressYard;
	@FXML
	private Text txt_yardSurfaceType;
	@FXML
	private ImageView img_main;

	@FXML
	private Button btn_chooseTime, btn_ordeYard;

	private YardModel yard;
	private List<String> selectedTimeSlots;

	public void initialize(URL location, ResourceBundle resources) {

	}

	public void setYard(YardModel yd) {
		this.yard = yd;

		if (txt_nameYard != null)
			txt_nameYard.setText(yd.getYardName());
		if (txt_yardStatus != null)
			txt_yardStatus.setText(yd.getYardStatus());
		if (txt_yardType != null)
			txt_yardType.setText(yd.getYardType().toString());
		if (txt_descriptionYard != null)
			txt_descriptionYard.setText(yd.getYardDescription());
		if (txt_addressYard != null)
			txt_addressYard.setText(yd.getYardAddress());
		if (img_main != null && yd.getYardThumbnail() != null) {
			File file = new File(yd.getYardThumbnail());
			if (file.exists()) {
				img_main.setImage(new Image(file.toURI().toString()));
			}
		}
//		btn_ordeYard.setOnAction(e -> {
//			try {
//				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlclient/Checkout.fxml"));
//				Parent root = loader.load();
//
//				BookingController controller = loader.getController();
//
//				controller
//
//				Stage stage = new Stage();
//				stage.setTitle("Xác nhận đặt sân");
//				stage.setScene(new Scene(root));
//				stage.initModality(Modality.APPLICATION_MODAL);
//				stage.show();
//
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		});

	}

}
