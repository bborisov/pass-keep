package pass.keep;

import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import pass.keep.controllers.WelcomeController;
import pass.keep.utils.FxUtil;
import pass.keep.views.SceneView;

@Slf4j
public class PassKeep extends Application {

    private static final String APP_WINDOW_TITLE = "Password Keeper";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(APP_WINDOW_TITLE);

        FxUtil.openScene(primaryStage, SceneView.WELCOME, false);
        WelcomeController welcomeController = FxUtil.getController(primaryStage);
        welcomeController.adjustTexts();

        log.info("Application started successfully");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        log.info("Application stopped successfully");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
