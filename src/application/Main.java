package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/mainLayout/LayoutClient.fxml"));
			Parent mainRoot = mainLoader.load();
			controller.LayoutClientController mainController = mainLoader.getController();

			FXMLLoader pageLoader = new FXMLLoader(getClass().getResource("/fxmlClient/Home.fxml"));
			Parent homePage = pageLoader.load();

			mainController.setContent(homePage);

			primaryStage.setScene(new Scene(mainRoot));
			primaryStage.setTitle("Manager Yard");
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		launch(args);
	}

}
