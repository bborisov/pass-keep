package pass.keep.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileUtil {

    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    private static final String FXML_FOLDER = "fxmls";
    private static final String FXML_FILE_EXTENSION = ".fxml";
    private static final String IDENTITY_FOLDER = "identity";
    private static final String IDENTITY_FILE_PREFIX = "1-face_";
    private static final String IDENTITY_FILE_EXTENSION = ".png";

    static {
        try {
            Path identityDirPath = Paths.get(FileUtil.getWorkingDirectory().toURI()).resolve(IDENTITY_FOLDER);
            Files.createDirectories(identityDirPath);
        } catch (URISyntaxException | IOException e) {
            log.error("Cannot create '" + IDENTITY_FOLDER + "' folder on start up", e);
        }
    }

    public static boolean isIdentityProvided() {
        URL identityDirUrl = CLASS_LOADER.getResource(IDENTITY_FOLDER);
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

    public static URL getFxmlResource(String sceneName) {
        return CLASS_LOADER.getResource(FXML_FOLDER + "/" + sceneName + FXML_FILE_EXTENSION);
    }

    public static URL getWorkingDirectory() {
        return CLASS_LOADER.getResource("./");
    }
}
