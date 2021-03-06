package pass.keep.utils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileUtil {

    public static final String IDENTITY_LABEL = "1";
    public static final String IDENTITY_LABEL_SEPARATOR = "-";

    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    private static final String FXML_FOLDER = "fxmls";
    private static final String FXML_FILE_EXTENSION = "fxml";
    private static final String IDENTITY_FOLDER = "identity";
    private static final String IDENTITY_FILE_EXTENSION = "png";
    private static final String IDENTITY_FILE_PREFIX = IDENTITY_LABEL + IDENTITY_LABEL_SEPARATOR + "face_";
    private static final String DB_FOLDER = "db";
    private static final String DB_FILE_NAME = "storage.db";
    private static final String EYE_ICON_FILE_NAME = "icons8-eye-16.png";

    static {
        try {
            Path identityDirPath = Paths.get(FileUtil.getWorkingDirectory().toURI()).resolve(IDENTITY_FOLDER);
            Files.createDirectories(identityDirPath);
            Path dbDirPath = Paths.get(FileUtil.getWorkingDirectory().toURI()).resolve(DB_FOLDER);
            Files.createDirectories(dbDirPath);
        } catch (URISyntaxException | IOException e) {
            log.error("Cannot create application required folders on start up", e);
        }
    }

    public static boolean isIdentityProvided() {
        URL identityDirUrl = getIdentityDirectory();
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

    public static void saveIdentity(BufferedImage image, int index) {
        String fileName = IDENTITY_FILE_PREFIX + index + "." + IDENTITY_FILE_EXTENSION;
        try {
            File file = Paths.get(getIdentityDirectory().toURI()).resolve(fileName).toFile();
            ImageIO.write(image, IDENTITY_FILE_EXTENSION, file);
        } catch (URISyntaxException | IOException e) {
            log.error("Cannot save identity image", e);
        }
    }

    public static File[] getIdentity() {
        try {
            FilenameFilter imgFilter = (dir, name) -> name.toLowerCase().endsWith("." + IDENTITY_FILE_EXTENSION);
            return new File(getIdentityDirectory().toURI()).listFiles(imgFilter);
        } catch (URISyntaxException e) {
            log.error("Cannot get identity images", e);
        }

        return null;
    }

    public static InputStream getEyeIconInputStream() {
        try {
            File file = Paths.get(getWorkingDirectory().toURI()).resolve(EYE_ICON_FILE_NAME).toFile();
            return new FileInputStream(file);
        } catch (URISyntaxException | FileNotFoundException e) {
            log.warn("Cannot get eye icon", e);
        }

        return null;
    }

    public static File getDbFile() {
        try {
            return Paths.get(getDbDirectory().toURI()).resolve(DB_FILE_NAME).toFile();
        } catch (URISyntaxException e) {
            log.error("Cannot get database file", e);
        }

        return null;
    }

    public static URL getFxmlResource(String sceneName) {
        return CLASS_LOADER.getResource(FXML_FOLDER + "/" + sceneName + "." + FXML_FILE_EXTENSION);
    }

    public static URL getDbDirectory() {
        return CLASS_LOADER.getResource(DB_FOLDER);
    }

    public static URL getIdentityDirectory() {
        return CLASS_LOADER.getResource(IDENTITY_FOLDER);
    }

    public static URL getWorkingDirectory() {
        return CLASS_LOADER.getResource("./");
    }
}
