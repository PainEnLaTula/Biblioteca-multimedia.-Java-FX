package hellofx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Controller {

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

    private Media media;
    private MediaPlayer mediaPlayer;
    private Stage mainWindow;

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
                    loadVideo(newValue);
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
    /*@FXML
    private Label label;

    public void initialize() {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        label.setText("Hello, JavaFX " + javafxVersion + "\nRunning on Java " + javaVersion + ".");*/
    }
}
