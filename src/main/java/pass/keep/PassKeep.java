package pass.keep;

import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import pass.keep.utils.FxUtil;

@Slf4j
public class PassKeep extends Application {

    private static final String PASS_KEEP_FXML = "fxml/pass_keep.fxml";
    private static final String WINDOW_TITLE = "Password Keeper";

    @Override
    public void start(Stage primaryStage) throws Exception {
        FxUtil.loadScene(primaryStage, PASS_KEEP_FXML);
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.show();
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
