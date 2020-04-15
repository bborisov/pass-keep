package pass.keep.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import pass.keep.utils.FxUtil;
import pass.keep.views.SceneView;

public class WelcomeController {

    @FXML
    private Button actionBtn;

    @FXML
    protected void onAction(ActionEvent event) {
        FxUtil.openScene(event, SceneView.REGISTRATION);
    }
}
