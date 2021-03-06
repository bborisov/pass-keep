package pass.keep.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.JavaFXFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class CameraController {

    protected static final String START_BUTTON_TRY_AGAIN = "Try again";
    protected static final String START_BUTTON_PROCEED = "Proceed";
    protected static final String NOTIFICATION_CAMERA_UNAVAILABLE = "Your camera is not available." +
            " Please check is it installed correctly, blocked by antivirus software or used by another program.";
    protected static final String NOTIFICATION_PROCESS_COMPLETED = "Process has completed successfully.";

    protected ScheduledExecutorService executor;
    protected VideoCapture capture;
    protected int frameCounter;
    protected boolean isClosingResources;

    protected OpenCVFrameConverter.ToMat matConverter;
    protected JavaFXFrameConverter fxConverter;

    @FXML
    protected ImageView cameraView;
    @FXML
    protected Text notification;
    @FXML
    protected Button startButton;

    protected Frame grabFrame() {
        if (!capture.isOpened()) {
            log.warn("Cannot grab frame - camera is not started");
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

    protected boolean handleCameraIssue(Frame frame) {
        if (frame == null) {
            // Workaround for receiving null frame few seconds after resource closing
            if (isClosingResources) {
                return true;
            }
            log.error("Camera issue encountered");
            notification.setText(NOTIFICATION_CAMERA_UNAVAILABLE);
            closeResources();
            startButton.setText(START_BUTTON_TRY_AGAIN);
            startButton.setDisable(false);
            return true;
        }

        return false;
    }

    protected void initResources(Runnable taskToBeScheduled) {
        log.info("Initializing resources");
        executor = Executors.newScheduledThreadPool(2);
        capture = new VideoCapture();
        frameCounter = 0;
        isClosingResources = false;

        matConverter = new OpenCVFrameConverter.ToMat();
        fxConverter = new JavaFXFrameConverter();

        capture.open(0);
        executor.scheduleAtFixedRate(taskToBeScheduled, 0, 33, TimeUnit.MILLISECONDS);

        startButton.setDisable(true);
    }

    protected void closeResources() {
        log.info("Closing resources");
        isClosingResources = true;
        cameraView.setImage(null);
        executor.shutdown();
        capture.release();
    }
}
