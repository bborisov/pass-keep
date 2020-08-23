package pass.keep.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import pass.keep.dto.CredentialsDto;
import pass.keep.exceptions.DbUnavailableException;
import pass.keep.utils.DbUtil;
import pass.keep.utils.FileUtil;
import pass.keep.utils.FxUtil;

import javax.crypto.SecretKey;
import java.io.InputStream;

@Slf4j
public class CredentialsDataController {

    private static final String CREDENTIALS_DATA_FXML = "credentials_data";

    private byte[] initVector;
    private SecretKey secretKey;

    private TextField unmaskedPassword;

    @FXML
    private VBox credentialsContainer;
    @FXML
    private TextField description;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button passwordToggle;
    @FXML
    private Button saveButton;

    public CredentialsDataController() {
        FxUtil.loadScene(CREDENTIALS_DATA_FXML, this);
        initUnmaskedPassword();
        initSaveButtonValidation();
        initEyeIcon();
    }

    public CredentialsDataController(String description, String username, String password, byte[] initVector,
                                     SecretKey secretKey) {
        this();
        this.description.setText(description);
        this.username.setText(username);
        this.password.setText(password);
        this.initVector = initVector;
        this.secretKey = secretKey;
    }

    @FXML
    protected void saveCredentials() throws DbUnavailableException {
        CredentialsDto credentialsDto = new CredentialsDto();
        credentialsDto.setDescription(description.getText());
        credentialsDto.setUsername(username.getText());
        credentialsDto.setPassword(password.getText());
        credentialsDto.setInitVector(initVector);
        credentialsDto.setSecretKey(secretKey);

        DbUtil.saveCredentials(credentialsDto);
    }

    @FXML
    protected void togglePasswordVisibility() {
        TextField elementToHide = password.isVisible() ? password : unmaskedPassword;
        TextField elementToShow = !password.isVisible() ? password : unmaskedPassword;

        elementToHide.setVisible(false);
        elementToShow.setVisible(true);

        ObservableList<Node> passwordElements = ((HBox) elementToHide.getParent()).getChildren();
        passwordElements.remove(elementToHide);
        passwordElements.add(0, elementToShow);
    }

    public Pane getContainer() {
        return credentialsContainer;
    }

    private void initUnmaskedPassword() {
        unmaskedPassword = new TextField();
        unmaskedPassword.setVisible(false);
        unmaskedPassword.setPromptText(password.getPromptText());
        unmaskedPassword.setPrefWidth(password.getPrefWidth());
        unmaskedPassword.textProperty().bindBidirectional(password.textProperty());
    }

    private void initSaveButtonValidation() {
        saveButton.disableProperty().bind(username.textProperty().isEmpty().or(password.textProperty().isEmpty()));
    }

    private void initEyeIcon() {
        InputStream eyeIconInputStream = FileUtil.getEyeIconInputStream();
        if (eyeIconInputStream != null) {
            passwordToggle.setText(null);
            passwordToggle.setGraphic(new ImageView(new Image(eyeIconInputStream)));
        }
    }
}
