package pass.keep.views;

import javafx.scene.Scene;
import lombok.Getter;
import pass.keep.utils.FxUtil;

@Getter
public enum SceneView {

    WELCOME, REGISTRATION, AUTHENTICATION, CREDENTIALS;

    private final Scene scene;

    SceneView() {
        scene = FxUtil.loadScene(name().toLowerCase(), null);
    }
}
