package pass.keep.views;

import javafx.scene.layout.Pane;
import pass.keep.utils.FxUtil;

public enum SceneView {

    WELCOME("welcome.fxml"),
    REGISTRATION("registration.fxml");

    private static final String SCENE_FXML_FOLDER_PREFIX = "fxmls/";
    private final Pane root;

    SceneView(String sceneFxmlName) {
        root = FxUtil.loadRootPane(SCENE_FXML_FOLDER_PREFIX + sceneFxmlName);
    }

    public Pane getRoot() {
        return root;
    }
}
