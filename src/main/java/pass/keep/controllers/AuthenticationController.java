package pass.keep.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.Mat;
import pass.keep.recognizer.CustomFaceRecognizer;
import pass.keep.utils.FxUtil;
import pass.keep.views.SceneView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AuthenticationController extends CameraController {

    // 90th frame -> 3rd second, 180th frame -> 6th second, 270th frame -> 9th second (30 frames per second)
    private static final List<Integer> RECOGNIZABLE_FRAME_INDEXES = Arrays.asList(90, 180, 270);

    private CustomFaceRecognizer recognizer;
    private List<Mat> recognizableFrames;
    private volatile boolean isRecognizing;
    private volatile boolean isAuthenticated;

    @FXML
    protected void verifyIdentity(ActionEvent event) {
        if (isAuthenticated) {
            // TODO Change after new scene is ready
            FxUtil.openScene(event, SceneView.WELCOME, true);
            return;
        }

        initResources();
    }

    protected void processFrame(Frame frame) {
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
            Platform.runLater(() -> {
                notification.setText(NOTIFICATION_PROCESS_COMPLETED);
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
        recognizer = new CustomFaceRecognizer();
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
