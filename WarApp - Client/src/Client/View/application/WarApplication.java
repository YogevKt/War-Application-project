package Client.View.application;

import Client.Messaging.MessageController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;


public class WarApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/View/WarMainWindow.fxml"));
        Scene mainScene = new Scene(loader.load());

        primaryStage.setScene(mainScene);
        primaryStage.setFullScreen(true);
        primaryStage.setOnCloseRequest(v -> {
        	
            primaryStage.close();
            FXMLLoader statisticsLoader = new FXMLLoader(getClass().getResource("/Client/View/WarStatisticsWindow.fxml"));
            try {
                Scene statScene = new Scene(statisticsLoader.load());
                Stage statStage = new Stage();
                statStage.setScene(statScene);
                statStage.setResizable(false);
                statStage.setOnCloseRequest(v2 -> {
                	
                	MessageController.getInstance().exit();
                	statStage.close();
                	//System.exit(0); 
                	
                });
                statStage.showAndWait();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        primaryStage.show();
        askDBChoice();
        askIfToLoadFromJSON();
    }

    public static void askIfToLoadFromJSON(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Loading from JSON confirmation");
        alert.setHeaderText("Would you like to load war from JSON file?");
       // alert.setContentText("Choose your option.");

        ButtonType buttonTypeOne = new ButtonType("Yes");
        ButtonType buttonTypeTwo = new ButtonType("No");
       // ButtonType buttonTypeThree = new ButtonType("Three");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
                War.loadFromJson();

        } else if (result.get() == buttonTypeTwo) {


        } else {
            System.exit(0);
        }
        MessageController messageController = MessageController.getInstance();
        messageController.startTime();
    }

    public static void askDBChoice(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Select DB service");
        alert.setHeaderText("Which DB would you like to set?");
        // alert.setContentText("Choose your option.");

        ButtonType buttonTypeOne = new ButtonType("SQL");
        ButtonType buttonTypeTwo = new ButtonType("MongoDB");
        //ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);//buttonTypeCancel

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            MessageController.getInstance().setDBChoice("SQL");
        } else if (result.get() == buttonTypeTwo) {
            MessageController.getInstance().setDBChoice("MONGODB");
        } else {
            System.exit(0);
        }
    }
}
