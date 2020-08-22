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

    public static Stage openScene(ActionEvent event, SceneView sceneView, boolean keepStageResolution) {
        Stage stage = FxUtil.getStage(event);
        FxUtil.openScene(stage, sceneView, keepStageResolution);

        return stage;
    }

    public static void openScene(Stage stage, SceneView sceneView, boolean keepStageResolution) {
        log.info("Move action to '{}' scene", sceneView.name().toLowerCase());
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

    public static Stage getStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    public static <T> T getController(Stage stage) {
        return ((FXMLLoader) stage.getScene().getUserData()).getController();
    }

    public static Scene loadScene(String sceneName, Object controller) {
        FXMLLoader fxmlLoader = new FXMLLoader(FileUtil.getFxmlResource(sceneName));
        if (controller != null) {
            fxmlLoader.setController(controller);
        }

        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            scene.setUserData(fxmlLoader);
            log.info("FXML of '{}' scene loaded successfully", sceneName);
        } catch (IOException e) {
            log.error("Cannot load FXML of '" + sceneName + "' scene", e);
        }

        return scene;
    }
}
