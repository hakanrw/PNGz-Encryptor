package tk.candarlabs.pngzapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/main.fxml"));
        primaryStage.setTitle("PNGz Encryptor");
        Scene scene = UIFunctions.scene = new Scene(root, 750, 500);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(750);
        primaryStage.setMinHeight(500);
        primaryStage.show();

        UIFunctions.hostServices = getHostServices();
        UIFunctions.init();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
