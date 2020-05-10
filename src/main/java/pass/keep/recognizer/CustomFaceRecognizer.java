package pass.keep.recognizer;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import pass.keep.utils.FileUtil;

import java.io.File;
import java.nio.IntBuffer;

import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import static org.bytedeco.opencv.global.opencv_core.CV_8U;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

@Slf4j
public class CustomFaceRecognizer {

    private static final int EXPECTED_LABEL = Integer.parseInt(FileUtil.IDENTITY_LABEL);
    // TODO Needs adjustment
    private static final int EXPECTED_CONFIDENCE_THRESHOLD = 15;

    private FaceRecognizer recognizer = LBPHFaceRecognizer.create();
    private volatile boolean isTrained;

    public void train() {
        File[] imageFiles = FileUtil.getIdentity();
        if (imageFiles == null) {
            log.error("Cannot train face recognizer - no images provided");
            return;
        }

        log.info("Starting training process");
        MatVector images = new MatVector(imageFiles.length);
        Mat labels = new Mat(imageFiles.length, 1, CV_32SC1);
        IntBuffer labelsBuf = labels.createBuffer();

        int counter = 0;
        for (File imageFile : imageFiles) {
            // TODO Check to do the grayscale manually
            Mat image = imread(imageFile.getAbsolutePath(), IMREAD_GRAYSCALE);
            int label = Integer.parseInt(imageFile.getName().split(FileUtil.IDENTITY_LABEL_SEPARATOR)[0]);

            images.put(counter, image);
            labelsBuf.put(counter, label);
            counter++;
        }

        recognizer.train(images, labels);
        isTrained = true;
        log.info("Training process completed");
    }

    public boolean predict(Mat mat) {
        if (!isTrained) {
            log.warn("Cannot predict for image - face recognizer is not trained");
            return false;
        }

        Mat grayscaleMat = convertToGrayscale(mat);
        IntPointer label = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(1);
        recognizer.predict(grayscaleMat, label, confidence);

        log.info("Prediction result with label={} and confidence={}", label.get(), confidence.get());
        return label.get() == EXPECTED_LABEL && confidence.get() < EXPECTED_CONFIDENCE_THRESHOLD;
    }

    private Mat convertToGrayscale(Mat colorMat) {
        Mat grayscaleMat = new Mat(colorMat.rows(), colorMat.cols(), CV_8U);
        cvtColor(colorMat, grayscaleMat, opencv_imgproc.COLOR_BGR2GRAY);

        return grayscaleMat;
    }
}
