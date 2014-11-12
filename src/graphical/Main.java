package graphical;

import graphical.MainWindow.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow/MainWindow.fxml"));
        primaryStage.setTitle("Horrtus");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }


    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            MainController.showErrorDialog(e);
        }
    }


}
