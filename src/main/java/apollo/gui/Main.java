package apollo.gui;

import java.io.IOException;

import apollo.Apollo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Launches and configures the Apollo graphical user interface.
 */
public class Main extends Application {

    private Apollo apollo = new Apollo();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            // inject the Apollo instance
            fxmlLoader.<MainWindow>getController().setApollo(apollo);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
