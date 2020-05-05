package pass.keep.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.JavaFXFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import pass.keep.utils.FileUtil;
import pass.keep.utils.FxUtil;
import pass.keep.views.SceneView;

import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RegistrationController {

    private static final String START_BUTTON_TRY_AGAIN = "Try again";
    private static final String START_BUTTON_PROCEED = "Proceed";
    private static final String NOTIFICATION_CAMERA_UNAVAILABLE = "Your camera is not available." +
            " Please check is it installed correctly, blocked by antivirus software or used by another program.";
    private static final String NOTIFICATION_REGISTRATION_COMPLETED = "Your registration has completed successfully." +
            " Please proceed to credentials section.";
    private static final String FACE_DIRECTION_RIGHT = "Look to the right";
    private static final String FACE_DIRECTION_LEFT = "Look to the left";
    private static final String FACE_DIRECTION_UP = "Look up";
    private static final String FACE_DIRECTION_DOWN = "Look down";
    private static final String FACE_DIRECTION_CENTER = "Look straight ahead";
    private static final int IMAGE_COUNT_THRESHOLD = 60;

    private ScheduledExecutorService executor;
    private VideoCapture capture;
    private int frameCounter;

    private OpenCVFrameConverter.ToMat matConverter;
    private JavaFXFrameConverter fxConverter;
    private Java2DFrameConverter cartesianConverter;

    @FXML
    private Text faceDirection;
    @FXML
    private ImageView cameraView;
    @FXML
    private Text notification;
    @FXML
    private Button startButton;

    @FXML
    protected void collectIdentity(ActionEvent event) {
        if (isIdentityCollected()) {
            // TODO Change after new scene is ready
            FxUtil.openScene(event, SceneView.WELCOME, true);
            return;
        }

        initResources();
        startButton.setDisable(true);
    }

    private Frame grabFrame() {
        if (!capture.isOpened()) {
            log.warn("Cannot grab a frame because camera was not started");
            return null;
        }

        Frame frame = null;
        Mat mat = new Mat();
        try {
            this.capture.read(mat);
            if (!mat.empty()) {
                frame = matConverter.convert(mat);
                frameCounter++;
            }
        } catch (Exception e) {
            log.error("Error while capturing the current frame", e);
        }

        return frame;
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
            notification.setText(NOTIFICATION_REGISTRATION_COMPLETED);
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

    private boolean handleCameraIssue(Frame frame) {
        if (frame == null) {
            log.error("Camera issue encountered");
            notification.setText(NOTIFICATION_CAMERA_UNAVAILABLE);
            closeResources();
            startButton.setText(START_BUTTON_TRY_AGAIN);
            startButton.setDisable(false);
            return true;
        }

        return false;
    }

    private void saveFrame(Frame frame) {
        int imageIndex = frameCounter / 10;
        executor.schedule(() -> {
            BufferedImage bufferedImage = cartesianConverter.convert(frame);
            FileUtil.saveIdentity(bufferedImage, imageIndex);
        }, 0, TimeUnit.MILLISECONDS);
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

    private void initResources() {
        log.info("Initializing resources");
        capture = new VideoCapture();
        executor = Executors.newScheduledThreadPool(2);

        matConverter = new OpenCVFrameConverter.ToMat();
        fxConverter = new JavaFXFrameConverter();
        cartesianConverter = new Java2DFrameConverter();

        // Start the video capture from camera
        capture.open(0);

        // Grab a frame every 33 ms (30 frames/sec)
        executor.scheduleAtFixedRate(() -> {
            Frame frame = grabFrame();
            Platform.runLater(() -> processFrame(frame));
        }, 0, 33, TimeUnit.MILLISECONDS);
    }

    private void closeResources() {
        log.info("Closing resources");
        cameraView.setImage(null);
        executor.shutdown();
        capture.release();
    }
}
