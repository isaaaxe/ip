package apollo.gui;

import apollo.Apollo;
import apollo.gui.components.DialogBox;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Apollo apollo;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/Default_pfp.jpg"));
    private Image apolloImage = new Image(this.getClass().getResourceAsStream("/images/Apollo.jpg"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Apollo instance used to process commands. */
    public void setApollo(Apollo apollo) {
        this.apollo = apollo;
        if (apollo.getLoadingError() != null) {
            dialogContainer.getChildren().add(
                    DialogBox.getApolloDialog(apollo.getLoadingError(), apolloImage, "ErrorCommand"));
        }
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Apollo's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = apollo.getResponse(input);
        String commandType = apollo.getCommandType();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getApolloDialog(response, apolloImage, commandType)
        );
        userInput.clear();
    }
}
