package pass.keep.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.JavaFXFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class RegistrationController {

    @FXML
    private Button startBtn;
    @FXML
    private ImageView currentFrame;

    private Timer timer;
    private VideoCapture capture = new VideoCapture();
    private OpenCVFrameConverter.ToMat matConverter = new OpenCVFrameConverter.ToMat();
    private JavaFXFrameConverter fxConverter = new JavaFXFrameConverter();

    @FXML
    protected void startCamera() {
        final ImageView frameView = currentFrame;
        if (!capture.isOpened()) {
            // start the video capture
            capture.open(0);
            // grab a frame every 33 ms (30 frames/sec)
            TimerTask frameGrabber = new TimerTask() {
                @Override
                public void run() {
                    Image frame = grabFrame();
                    Platform.runLater(() -> frameView.setImage(frame));
                }
            };
            timer = new Timer();
            timer.schedule(frameGrabber, 0, 33);
            startBtn.setText("Stop Camera");
        } else {
            startBtn.setText("Start Camera");
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            capture.release();
            frameView.setImage(null);
        }
    }

    private Image grabFrame() {
        if (!capture.isOpened()) {
            log.warn("Cannot grab a frame because camera was not started");
            return null;
        }

        Image imageToShow = null;
        Mat frame = new Mat();
        try {
            this.capture.read(frame);
            if (!frame.empty()) {
                imageToShow = mat2Image(frame);
            }
        } catch (Exception e) {
            log.error("Error while processing the current frame", e);
        }

        return imageToShow;
    }

    /**
     * Converts the JavaCV's matrix object to a JavaFX's image object.
     *
     * @param mat matrix to be converted
     * @return converted image
     */
    private Image mat2Image(Mat mat) {
        Frame frame = matConverter.convert(mat);
        return fxConverter.convert(frame);
    }
}
