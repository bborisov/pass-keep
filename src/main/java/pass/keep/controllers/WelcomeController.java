package pass.keep.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import pass.keep.utils.FileUtil;
import pass.keep.utils.FxUtil;
import pass.keep.views.SceneView;

public class WelcomeController {

    private static final String NEXT_SCENE_BUTTON_REGISTER = "Register";
    private static final String NEXT_SCENE_BUTTON_CHECK_IDENTITY = "Check identity";
    private static final String LOWER_TITLE_REGISTER = "In order to use this application you need to create an account." +
            " Please clink the \"" + NEXT_SCENE_BUTTON_REGISTER + "\" button below.";
    private static final String LOWER_TITLE_CHECK_IDENTITY = "You already have created an account." +
            " Please clink the \"" + NEXT_SCENE_BUTTON_CHECK_IDENTITY + "\" button below in order to access your data.";

    @FXML
    private Text lowerTitle;
    @FXML
    private Button nextSceneButton;

    @FXML
    protected void openNextScene(ActionEvent event) {
        SceneView nextScene = SceneView.REGISTRATION;
        if (FileUtil.isIdentityProvided()) {
            // TODO Change after new scene is ready
            nextScene = SceneView.REGISTRATION;
        }

        FxUtil.openScene(event, nextScene, true);
    }

    public void adjustTexts() {
        String lowerTitleText = LOWER_TITLE_REGISTER;
        String nextSceneButtonText = NEXT_SCENE_BUTTON_REGISTER;
        if (FileUtil.isIdentityProvided()) {
            lowerTitleText = LOWER_TITLE_CHECK_IDENTITY;
            nextSceneButtonText = NEXT_SCENE_BUTTON_CHECK_IDENTITY;
        }

        lowerTitle.setText(lowerTitleText);
        nextSceneButton.setText(nextSceneButtonText);
    }
}
