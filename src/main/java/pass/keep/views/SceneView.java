package pass.keep.views;

import javafx.scene.Scene;
import pass.keep.utils.FxUtil;

public enum SceneView {

    WELCOME("welcome.fxml"),
    REGISTRATION("registration.fxml");

    private static final String SCENE_FXML_FOLDER_PREFIX = "fxmls/";
    private final Scene scene;

    SceneView(String sceneFxmlName) {
        scene = FxUtil.loadScene(SCENE_FXML_FOLDER_PREFIX + sceneFxmlName);
    }

    public Scene getScene() {
        return scene;
    }
}
