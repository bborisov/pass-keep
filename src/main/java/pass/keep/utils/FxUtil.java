package pass.keep.utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class FxUtil {

    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();

    public static void loadScene(Stage stage, String sceneFxmlPath) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(CLASS_LOADER.getResource(sceneFxmlPath));
        BorderPane root = fxmlLoader.load();
        stage.setScene(new Scene(root));
    }

    public static Stage getStageFromActionEvent(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }
}
