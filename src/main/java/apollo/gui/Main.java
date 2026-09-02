package apollo.gui;

import java.io.IOException;

import apollo.Apollo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Starts Apollo's JavaFX interface. */
public class Main extends Application {

    private final Apollo apollo = new Apollo();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            fxmlLoader.<MainWindow>getController().setApollo(this.apollo);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
