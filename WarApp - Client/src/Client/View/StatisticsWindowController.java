package Client.View;

import Client.Messaging.MessageController;
import Entities.Statistics;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;


import java.net.URL;
import java.util.ResourceBundle;

public class StatisticsWindowController implements Initializable{
    @FXML
    private Label launchedMissilesLabel;

    @FXML
    private Label missileHitsLabel;

    @FXML
    private Label destructedMissilesLabel;

    @FXML
    private Label destructedLaunchersLabel;

    @FXML Label totalDamageLabel;

    @FXML
    private Button exitButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Statistics statistics = MessageController.getInstance().getStatistics();
        launchedMissilesLabel.setText(statistics.getNumOfLaunchedMissiles() +"");
        missileHitsLabel.setText(statistics.getNumOfHits() +"");
        destructedMissilesLabel.setText(statistics.getNumOfDestructedMissiles() +"");
        destructedLaunchersLabel.setText(statistics.getNumOfDestructedLaunchers() +"");
        totalDamageLabel.setText(statistics.getTotalDamage() +"");
        exitButton.setOnAction(e -> {
            MessageController.getInstance().exit();
            close();
        });
    }

    private void close(){
        Stage stage = (Stage)exitButton.getScene().getWindow();
        stage.close();
    }
}
