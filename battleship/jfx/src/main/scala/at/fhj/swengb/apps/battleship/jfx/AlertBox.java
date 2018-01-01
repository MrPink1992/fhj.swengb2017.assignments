package at.fhj.swengb.apps.battleship.jfx;

import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;



public class AlertBox {

    public static void shoot() {
        Media cannonshoot = new Media("file:///C:/workspace/fhj.swengb2017.assignments/battleship/jfx/src/main/resources/at/fhj/swengb/apps/battleship/jfx/Cannon+2.mp3");
        MediaPlayer cannonplay = new MediaPlayer(cannonshoot);
        cannonplay.play();

    }

    public static void display(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        window.setFullScreen(true);
        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button("Ja");
        closeButton.setPrefSize(400,400);
        closeButton.setStyle("-fx-background-color: #add8e6");

        closeButton.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label,closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        shoot();
    }





}
