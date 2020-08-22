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

import java.io.InputStream;

@Slf4j
public class CredentialsDataController {

    private static final String CREDENTIALS_DATA_FXML = "credentials_data";

    private int index;
    private TextField unmaskedPassword;
    private boolean isFirstDataUpdate = true;

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
    }

    @FXML
    protected void saveCredentials() throws DbUnavailableException {
        CredentialsDto credentialsDto = new CredentialsDto();
        credentialsDto.setDescription(description.getText());
        credentialsDto.setUsername(username.getText());
        credentialsDto.setPassword(password.getText());

        DbUtil.saveCredentials(index, credentialsDto);
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

    public void setCredentialsData(int index, CredentialsDto data) {
        if (isFirstDataUpdate) {
            initUnmaskedPassword();
            initEyeIcon();
            isFirstDataUpdate = false;
        }

        this.index = index;
        description.setText(data.getDescription());
        username.setText(data.getUsername());
        password.setText(data.getPassword());
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

    private void initEyeIcon() {
        InputStream eyeIconInputStream = FileUtil.getEyeIconInputStream();
        if (eyeIconInputStream != null) {
            passwordToggle.setText(null);
            passwordToggle.setGraphic(new ImageView(new Image(eyeIconInputStream)));
        }
    }
}
