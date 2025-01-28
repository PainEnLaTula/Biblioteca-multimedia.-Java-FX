package hellofx;
/*import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("hellofx.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}*/

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.Stage;
import java.io.File;

public class Main extends Application {
    private MediaPlayer mediaPlayer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        
        // Menú superior
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Archivo");
        Menu libraryMenu = new Menu("Biblioteca");
        Menu viewMenu = new Menu("Ver");
        Menu aboutMenu = new Menu("Acerca");
        menuBar.getMenus().addAll(fileMenu, libraryMenu, viewMenu, aboutMenu);
        root.setTop(menuBar);

        // Panel de biblioteca
        ListView<String> libraryView = new ListView<>();
        File mediaDir = new File("media"); // Carpeta de archivos multimedia
        if (mediaDir.exists() && mediaDir.isDirectory()) {
            for (File file : mediaDir.listFiles()) {
                libraryView.getItems().add(file.getName());
            }
        }
        root.setRight(libraryView);

        // Reproductor central
        MediaView mediaView = new MediaView();
        StackPane mediaPane = new StackPane(mediaView);
        root.setCenter(mediaPane);

        // Controles inferiores
        HBox controls = new HBox(10);
        Button playBtn = new Button("Play");
        Button pauseBtn = new Button("Pause");
        Button stopBtn = new Button("Stop");
        controls.getChildren().addAll(playBtn, pauseBtn, stopBtn);
        root.setBottom(controls);

        // Configuración del reproductor
        libraryView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                File file = new File(mediaDir, newVal);
                Media media = new Media(file.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
            }
        });
        
        playBtn.setOnAction(e -> { if (mediaPlayer != null) mediaPlayer.play(); });
        pauseBtn.setOnAction(e -> { if (mediaPlayer != null) mediaPlayer.pause(); });
        stopBtn.setOnAction(e -> { if (mediaPlayer != null) mediaPlayer.stop(); });

        // Configurar escena
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Biblioteca Multimedia");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
} 