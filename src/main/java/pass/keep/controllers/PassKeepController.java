package pass.keep.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import pass.keep.utils.FxUtil;

public class PassKeepController {

    private static final String REGISTRATION_FXML = "fxml/registration.fxml";

    @FXML
    private Button actionBtn;

    @FXML
    protected void onAction(ActionEvent event) throws Exception {
        Stage stage = FxUtil.getStageFromActionEvent(event);
        FxUtil.loadScene(stage, REGISTRATION_FXML);
    }
}
