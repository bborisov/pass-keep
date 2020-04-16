package pass.keep.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import pass.keep.utils.FxUtil;
import pass.keep.views.SceneView;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WelcomeController {

    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    private static final String IDENTITY_FOLDER = "identity";
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
        if (isIdentityProvided()) {
            // TODO Change after new scene is ready
            nextScene = SceneView.REGISTRATION;
        }

        FxUtil.openScene(event, nextScene);
    }

    public void adjustTexts() {
        String lowerTitleText = LOWER_TITLE_REGISTER;
        String nextSceneButtonText = NEXT_SCENE_BUTTON_REGISTER;
        if (isIdentityProvided()) {
            lowerTitleText = LOWER_TITLE_CHECK_IDENTITY;
            nextSceneButtonText = NEXT_SCENE_BUTTON_CHECK_IDENTITY;
        }

        lowerTitle.setText(lowerTitleText);
        nextSceneButton.setText(nextSceneButtonText);
    }

    private boolean isIdentityProvided() {
        URL identityDirUrl = CLASS_LOADER.getResource(IDENTITY_FOLDER);
        if (identityDirUrl == null) {
            return false;
        }

        try {
            URI identityDirUri = identityDirUrl.toURI();
            if (!Files.list(Paths.get(identityDirUri)).findAny().isPresent()) {
                return false;
            }
        } catch (URISyntaxException | IOException e) {
            return false;
        }

        return true;
    }
}
