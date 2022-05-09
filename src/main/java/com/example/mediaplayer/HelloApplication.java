package com.example.mediaplayer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;


public class HelloApplication extends Application {

    private Media media;
    private javafx.scene.media.MediaPlayer mediaPlayer;
    private MediaView mediaView;

    private InvalidationListener positionSliderListener;

    private double percentComplete = 0.0;


    @Override
    public void start(Stage stage) throws IOException {
        String style = getClass().getResource("style.css").toExternalForm();

        BorderPane borderPane = new BorderPane();
        HBox buttonBox = new HBox();

        TextField fileName = new TextField("");
        fileName.setPrefColumnCount(30);
        Button fileButton = new Button("Select a file");
        HBox fileChooseBox = new HBox(fileName,fileButton);
        fileChooseBox.setPadding(new Insets(17,15,17,15));


        Image pause = new Image("/pausepic.png");
        ImageView view1 = new ImageView(pause);
        view1.setFitHeight(50);
        view1.setFitWidth(60);
        view1.setPreserveRatio(true);

        Image play = new Image("/playpic.png");
        ImageView view2 = new ImageView(play);
        view2.setFitHeight(50);
        view2.setFitWidth(70);
        view2.setPreserveRatio(true);

        Image stop = new Image("/stoppic.png");
        ImageView view3 = new ImageView(stop);
        view3.setFitHeight(50);
        view3.setFitWidth(70);
        view3.setPreserveRatio(true);

        GridPane texts = new GridPane();
        texts.setAlignment(Pos.BOTTOM_RIGHT);
        Text vlm = new Text("Volume");
        texts.getChildren().add(vlm);

        GridPane text = new GridPane();
        text.setAlignment(Pos.BOTTOM_LEFT);
        Text time = new Text("seek time");
        text.getChildren().add(time);



        Slider volumeSlider = new Slider(0,1,0.3);
        volumeSlider.setPrefWidth(100);
        Slider positionSlider = new Slider(0,1,0);
        positionSlider.setPrefWidth(100);

        buttonBox.getChildren().addAll(text,positionSlider,view1,view2,view3,volumeSlider,vlm);
        buttonBox.setSpacing(20);
        buttonBox.setPadding(new Insets(15,12,20,12));
        buttonBox.setAlignment(Pos.BOTTOM_CENTER);

        borderPane.setBottom(buttonBox);
        borderPane.setTop(fileChooseBox);
        fileChooseBox.setAlignment(Pos.TOP_CENTER);
        borderPane.setBackground(new Background(new BackgroundFill(Color.BEIGE, new CornerRadii(90), Insets.EMPTY)));


        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(800, 900);
        stackPane.setMaxHeight(900);
        borderPane.setCenter(stackPane);



        stage.show();
        fileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File mediaFile = fileChooser.showOpenDialog(stage);
            if (mediaFile != null) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                }
            }

            fileName.setText(mediaFile.getAbsolutePath());
            media = new Media(mediaFile.toURI().toString());
            mediaPlayer = new javafx.scene.media.MediaPlayer(media);

            mediaView = new MediaView(mediaPlayer);
            mediaView.setPreserveRatio(true);
            mediaView.setFitHeight(500);
            mediaView.setFitWidth(800);
            stackPane.getChildren().addAll(mediaView);
            StackPane.setAlignment(mediaView, Pos.TOP_CENTER);
            volumeSlider.valueProperty().bindBidirectional(mediaPlayer.volumeProperty());
            positionSliderListener = observable -> {
                double percentCompleteNow = mediaPlayer.getCurrentTime().toMillis() /
                        mediaPlayer.getTotalDuration().toMillis();
                if (Math.abs(percentCompleteNow - percentComplete) > 0.005) {
                    percentComplete = percentCompleteNow;
                    Runnable r = () -> {
                        if (!positionSlider.isValueChanging()) {
                            positionSlider.setValue(percentComplete);
                        }
                    };
                    Platform.runLater(r);
                }
            };
            mediaPlayer.currentTimeProperty().addListener(positionSliderListener);

        });
        view1.setId("view1");
        view2.setId("view2");
        view3.setId("view3");
        view1.setOnMouseClicked(event ->{
            mediaPlayer.pause();
        });
        view2.setOnMouseClicked(event ->{
            mediaPlayer.play();
        });
        view3.setOnMouseClicked(event ->{
            mediaPlayer.stop();
        });

        positionSlider.valueProperty().addListener( (obsVal,oldValue,newValue) -> {
            if (mediaPlayer == null) {
                return;
            }

            Duration seekPosition = Duration.millis(positionSlider.getValue() *
                    mediaPlayer.getTotalDuration().toMillis());
            javafx.scene.media.MediaPlayer.Status status = mediaPlayer.getStatus();
            mediaPlayer.pause();
            mediaPlayer.seek(seekPosition);
            if (status == javafx.scene.media.MediaPlayer.Status.PLAYING) {
                mediaPlayer.play();
            }
        });


        Scene scene = new Scene(borderPane, 820, 640);
        scene.getStylesheets().add(style);
        stage.setTitle("Vlc Media Player");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}