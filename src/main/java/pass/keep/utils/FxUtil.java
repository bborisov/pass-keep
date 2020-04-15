package pass.keep.utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import pass.keep.views.SceneView;

import java.io.IOException;

@Slf4j
public class FxUtil {

    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();

    public static void openScene(ActionEvent event, SceneView sceneView) {
        Stage stage = FxUtil.getStageFromActionEvent(event);
        FxUtil.openScene(stage, sceneView);
    }

    public static void openScene(Stage stage, SceneView sceneView) {
        stage.setScene(new Scene(sceneView.getRoot()));
    }

    public static Pane loadRootPane(String sceneFxmlPath) {
        FXMLLoader fxmlLoader = new FXMLLoader(CLASS_LOADER.getResource(sceneFxmlPath));
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            log.error("Cannot load scene's FXML with path " + sceneFxmlPath, e);
        }

        return null;
    }

    public static Stage getStageFromActionEvent(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }
}
