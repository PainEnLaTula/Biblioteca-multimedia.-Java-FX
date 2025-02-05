package hellofx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller implements Initializable {

    @FXML
    private MediaView mediaView;
    @FXML
    private Text videoTitle;
    @FXML
    private Button playButton, pauseButton, resetButton, defaultV, fbutton, sbutton, tbutton;
    @FXML
    private Slider slider, volumeSlider, speedSlider;
    @FXML
    private ListView<String> videoList;
    @FXML 
    private MenuItem aboutMenuItem;
    
    private Media media;
    private MediaPlayer mediaPlayer;
    private Stage mainWindow;

    private boolean isMaximized = false;

    private Map<String, Duration> playbackPositions = new HashMap<>();
    private String currentVideo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            File directory = new File("src/hellofx");
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".mp4") || name.endsWith(".avi"));

            if (files != null) {
                for (File file : files) {
                    videoList.getItems().add(file.toURI().toString());
                }
            }

            videoList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    savePlaybackPosition();
                    loadVideo(newValue);
                    showFileDialog(new File(newValue).getName());
                }
            });

            volumeSlider.setValue(50);
            volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(newValue.doubleValue() / 100);
                }
            });

            speedSlider.setValue(1.0);
            speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (mediaPlayer != null) {
                    mediaPlayer.setRate(newValue.doubleValue());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMainWindow(Stage mainWindow) {
        this.mainWindow = mainWindow;
    }

    @FXML
    public void playMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    @FXML
    public void pauseMedia() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        }
    }

    @FXML
    public void sliderPressed(MouseEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.seconds(slider.getValue()));
        }
    }

    @FXML
    public void resetMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.play();
        }
    }

    @FXML
    public void changeVolume(MouseEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volumeSlider.getValue() / 100);
        }
    }

    @FXML
    public void changeSpeed(MouseEvent event) {
        if (mediaPlayer != null) {
            double speed = speedSlider.getValue();
            mediaPlayer.setRate(speed);
        }
    }

    @FXML
    public void defaultV(MouseEvent e) {
        if (mediaPlayer != null) {
            speedSlider.setValue(1.0);
            mediaPlayer.setRate(1.0);
        }
    }

    @FXML
private void setAspectRatio4_3() {
    mediaView.setPreserveRatio(true);
    mediaView.fitWidthProperty().unbind();
    mediaView.fitHeightProperty().unbind();

    if (mediaView.getParent() instanceof Pane) {
        Pane parentPane = (Pane) mediaView.getParent();
        double parentWidth = parentPane.getWidth();
        double parentHeight = parentPane.getHeight() - 18; // Evitar que cubra botones

        // Calcular tamaño manteniendo relación 4:3
        double newWidth = parentWidth;
        double newHeight = newWidth * (3.0 / 4.0);

        if (newHeight > parentHeight) {
            newHeight = parentHeight;
            newWidth = newHeight * (4.0 / 3.0);
        }

        mediaView.setFitWidth(newWidth);
        mediaView.setFitHeight(newHeight);
    }
}

@FXML
private void setAspectRatioFill() {
    mediaView.setPreserveRatio(false);
    mediaView.fitWidthProperty().unbind();
    mediaView.fitHeightProperty().unbind();

    if (mediaView.getParent() instanceof Pane) {
        Pane parentPane = (Pane) mediaView.getParent();
        mediaView.fitWidthProperty().bind(parentPane.widthProperty());
        mediaView.fitHeightProperty().bind(parentPane.heightProperty().subtract(18));
    }
}


    @FXML
    private void setAspectRatio16_9() {
    mediaView.setPreserveRatio(true);
    mediaView.setFitWidth(mediaView.getScene().getWidth() * 0.9);
    mediaView.setFitHeight(mediaView.getScene().getHeight() * 0.7);
}

    private void loadVideo(String filePath) {
        if (mediaPlayer != null) {
            savePlaybackPosition();
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        media = new Media(filePath);
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        currentVideo = filePath;

        mediaPlayer.currentTimeProperty().addListener((observableValue, oldValue, newValue) -> {
            slider.setValue(newValue.toSeconds());
        });

        mediaPlayer.setOnReady(() -> {
            Duration totalDuration = media.getDuration();
            slider.setMax(totalDuration.toSeconds());
            videoTitle.setText(new File(filePath).getName().toString().replace(".mp4", ""));

            if (playbackPositions.containsKey(filePath)) {
                mediaPlayer.seek(playbackPositions.get(filePath));
            }
        });

        mediaPlayer.play();
    }

    @FXML
    public void showAboutDialog(ActionEvent event) {
        
            Stage aboutStage = new Stage();
            aboutStage.initModality(Modality.APPLICATION_MODAL);
            aboutStage.setTitle("Acerca de");

            VBox vbox = new VBox(10);
            vbox.setStyle("-fx-padding: 20;");
            Label label1 = new Label("Biblioteca Multimedia");
            Label label2 = new Label("Autor: Manuel\nVersión: 1.0");
            Button closeButton = new Button("Cerrar");
            closeButton.setOnAction(e -> aboutStage.close());
            vbox.getChildren().addAll(label1, label2, closeButton);

            Scene scene = new Scene(vbox);
            aboutStage.setScene(scene);
            aboutStage.showAndWait();
       
    }

    private void showFileDialog(String fileName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Archivo abierto");
        alert.setHeaderText(null);
        alert.setContentText("Se ha abierto: " + fileName);
        alert.show();
    }

    private void savePlaybackPosition() {
        if (mediaPlayer != null && currentVideo != null) {
            playbackPositions.put(currentVideo, mediaPlayer.getCurrentTime());
        }
    }

@FXML
private void toggleMaximize() {
    if (mediaView.isPreserveRatio()) {
        mediaView.setPreserveRatio(false);
        mediaView.fitWidthProperty().bind(mediaView.getScene().widthProperty());
        mediaView.fitHeightProperty().bind(mediaView.getScene().heightProperty().subtract(140));
    } else {
        mediaView.setPreserveRatio(true);
        mediaView.fitWidthProperty().unbind();
        mediaView.fitHeightProperty().unbind();
        mediaView.setFitWidth(600);
        mediaView.setFitHeight(300);
    }
}
}
