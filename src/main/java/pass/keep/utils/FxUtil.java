package pass.keep.utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import pass.keep.views.SceneView;

import java.io.IOException;

@Slf4j
public class FxUtil {

    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();

    public static void openScene(ActionEvent event, SceneView sceneView, boolean keepStageResolution) {
        Stage stage = FxUtil.getStageFromActionEvent(event);
        FxUtil.openScene(stage, sceneView, keepStageResolution);
    }

    public static void openScene(Stage stage, SceneView sceneView, boolean keepStageResolution) {
        double xPos = stage.getX();
        double yPos = stage.getY();
        double stageWidth = stage.getWidth();
        double stageHeight = stage.getHeight();

        stage.hide();
        stage.setScene(sceneView.getScene());
        if (keepStageResolution) {
            stage.setX(xPos);
            stage.setY(yPos);
            stage.setWidth(stageWidth);
            stage.setHeight(stageHeight);
        }
        stage.show();
    }

    public static Stage getStageFromActionEvent(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    public static <T> T getController(Stage stage) {
        return ((FXMLLoader) stage.getScene().getUserData()).getController();
    }

    public static Scene loadScene(String sceneFxmlPath) {
        FXMLLoader fxmlLoader = new FXMLLoader(CLASS_LOADER.getResource(sceneFxmlPath));

        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            scene.setUserData(fxmlLoader);
            log.info("Scene's FXML with path '{}' loaded successfully", sceneFxmlPath);
        } catch (IOException e) {
            log.error("Cannot load scene's FXML with path " + sceneFxmlPath, e);
        }

        return scene;
    }
}
