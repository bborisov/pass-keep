package pass.keep.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import pass.keep.utils.FileUtil;
import pass.keep.utils.FxUtil;
import pass.keep.views.SceneView;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RegistrationController extends CameraController {

    private static final String NOTIFICATION_PROCESS_COMPLETED_REGISTRATION = NOTIFICATION_PROCESS_COMPLETED +
            " Please proceed to authentication section.";
    private static final String FACE_DIRECTION_RIGHT = "Look to the right";
    private static final String FACE_DIRECTION_LEFT = "Look to the left";
    private static final String FACE_DIRECTION_UP = "Look up";
    private static final String FACE_DIRECTION_DOWN = "Look down";
    private static final String FACE_DIRECTION_CENTER = "Look straight ahead";
    private static final int IMAGE_COUNT_THRESHOLD = 60;

    private Java2DFrameConverter cartesianConverter;

    @FXML
    private Text faceDirection;

    @FXML
    protected void collectIdentity(ActionEvent event) {
        if (isIdentityCollected()) {
            FxUtil.openScene(event, SceneView.AUTHENTICATION, true);
            return;
        }

        initResources();
    }

    private void processFrame(Frame frame) {
        if (handleIdentityCollected()) {
            return;
        }

        if (handleCameraIssue(frame)) {
            return;
        }

        notification.setVisible(false);
        cameraView.setImage(fxConverter.convert(frame));
        adjustFaceDirectionText();
        if (frameCounter % 10 != 0) {
            return;
        }

        saveFrame(frame);
    }

    private boolean handleIdentityCollected() {
        if (isIdentityCollected()) {
            log.info("Identity frames collected successfully");
            faceDirection.setVisible(false);
            closeResources();
            notification.setText(NOTIFICATION_PROCESS_COMPLETED_REGISTRATION);
            notification.setVisible(true);
            startButton.setText(START_BUTTON_PROCEED);
            startButton.setDisable(false);
            return true;
        }

        return false;
    }

    private boolean isIdentityCollected() {
        return frameCounter / 10 > IMAGE_COUNT_THRESHOLD;
    }

    private void adjustFaceDirectionText() {
        int index = frameCounter / 10 / 12;

        switch (index) {
            case 0: {
                faceDirection.setText(FACE_DIRECTION_RIGHT);
                break;
            }
            case 1: {
                faceDirection.setText(FACE_DIRECTION_LEFT);
                break;
            }
            case 2: {
                faceDirection.setText(FACE_DIRECTION_UP);
                break;
            }
            case 3: {
                faceDirection.setText(FACE_DIRECTION_DOWN);
                break;
            }
            case 4: {
                faceDirection.setText(FACE_DIRECTION_CENTER);
                break;
            }
        }
    }

    private void saveFrame(Frame frame) {
        int imageIndex = frameCounter / 10;
        executor.schedule(() -> {
            BufferedImage bufferedImage = cartesianConverter.convert(frame);
            FileUtil.saveIdentity(bufferedImage, imageIndex);
        }, 0, TimeUnit.MILLISECONDS);
    }

    private void initResources() {
        cartesianConverter = new Java2DFrameConverter();

        initResources(() -> {
            Frame frame = grabFrame();
            Platform.runLater(() -> processFrame(frame));
        });
    }
}
