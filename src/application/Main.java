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
			System.out.println("FXML resource URL: " + getClass().getResource("/mainLayout/LayoutClient.fxml"));
			Parent mainRoot = mainLoader.load();
			controller.LayoutClientController mainController = mainLoader.getController();
			
			// Debug: Check if controller was loaded properly
			System.out.println("Main controller loaded: " + mainController);
			if (mainController != null) {
				System.out.println("Controller class: " + mainController.getClass().getName());
			}
			
			primaryStage.setScene(new Scene(mainRoot));
			primaryStage.setTitle("Manager Yard - Small View");
			primaryStage.setWidth(1000);
			primaryStage.setHeight(600);
			primaryStage.setMinWidth(800);
			primaryStage.setMinHeight(500);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		launch(args);
	}

}
