package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/*
 * In GUI, black pin represents a real-time location
 *         red - estimated location.
 */

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("layout.fxml"));
		
		Scene scene = new Scene(root, Configuration.GUI_WIDTH, Configuration.GUI_HEIGHT);
		
		primaryStage.setTitle("Wi-Fi Fingerprinting using WKNN");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
