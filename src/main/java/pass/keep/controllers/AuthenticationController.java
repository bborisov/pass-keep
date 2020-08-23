package pass.keep.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.Mat;
import pass.keep.recognizers.FaceRecognizerWrapper;
import pass.keep.utils.FxUtil;
import pass.keep.views.SceneView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AuthenticationController extends CameraController {

    private static final String NOTIFICATION_PROCESS_COMPLETED_AUTHENTICATION = NOTIFICATION_PROCESS_COMPLETED +
            " Please proceed to credentials section.";

    // 90th frame -> 3rd second, 180th frame -> 6th second, 270th frame -> 9th second (30 frames per second)
    private static final List<Integer> RECOGNIZABLE_FRAME_INDEXES = Arrays.asList(90, 180, 270);

    private FaceRecognizerWrapper recognizer;
    private List<Mat> recognizableFrames;
    private volatile boolean isRecognizing;
    private volatile boolean isAuthenticated;

    @FXML
    protected void verifyIdentity(ActionEvent event) {
        if (isAuthenticated) {
            Stage stage = FxUtil.openScene(event, SceneView.CREDENTIALS, true);
            CredentialsController credentialsController = FxUtil.getController(stage);
            credentialsController.loadCredentials();
            return;
        }

        initResources();
    }

    private void processFrame(Frame frame) {
        if (handleCameraIssue(frame)) {
            return;
        }

        notification.setVisible(false);
        cameraView.setImage(fxConverter.convert(frame));

        if (RECOGNIZABLE_FRAME_INDEXES.contains(frameCounter)) {
            recognizableFrames.add(matConverter.convert(frame));
        }

        attemptRecognition();
    }

    private void attemptRecognition() {
        if (!areRecognizableFramesCollected() || isRecognizing) {
            return;
        }

        log.info("Starting recognition");
        isRecognizing = true;
        executor.schedule(this::recognize, 0, TimeUnit.MILLISECONDS);
    }

    private void recognize() {
        for (Mat mat : recognizableFrames) {
            if (recognizer.predict(mat)) {
                isAuthenticated = true;
                break;
            }
        }

        if (isAuthenticated) {
            log.info("Successful recognition");
            // TODO Adjust for negative case
            Platform.runLater(() -> {
                notification.setText(NOTIFICATION_PROCESS_COMPLETED_AUTHENTICATION);
                notification.setVisible(true);
                startButton.setText(START_BUTTON_PROCEED);
                startButton.setDisable(false);
            });
        } else {
            log.info("No successful recognition after {} attempts", recognizableFrames.size());
        }
        closeResources();
    }

    private boolean areRecognizableFramesCollected() {
        return recognizableFrames.size() == RECOGNIZABLE_FRAME_INDEXES.size();
    }

    private void initResources() {
        recognizer = new FaceRecognizerWrapper();
        recognizableFrames = new ArrayList<>(RECOGNIZABLE_FRAME_INDEXES.size());
        isRecognizing = false;
        isAuthenticated = false;

        initResources(() -> {
            Frame frame = grabFrame();
            Platform.runLater(() -> processFrame(frame));
        });
        executor.schedule(recognizer::train, 0, TimeUnit.MILLISECONDS);
    }
}
