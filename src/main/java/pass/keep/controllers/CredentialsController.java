package pass.keep.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import pass.keep.dto.CredentialsDto;
import pass.keep.exceptions.DbUnavailableException;
import pass.keep.utils.DbUtil;
import pass.keep.views.CredentialsDataListCell;

import java.util.Collection;

public class CredentialsController {

    private static final String NOTIFICATION_DB_UNAVAILABLE = "Database is unavailable. Please contact our support desk!";
    private static final String NOTIFICATION_ADD_MORE_CREDENTIALS = "Click below in order to add more credentials.";

    ObservableList<CredentialsDto> observableCredentialsList = FXCollections.observableArrayList();

    @FXML
    private ListView<CredentialsDto> credentialsList;
    @FXML
    private Text notification;
    @FXML
    private Button addButton;

    @FXML
    protected void addCredentials() {
        observableCredentialsList.add(new CredentialsDto());
        credentialsList.scrollTo(observableCredentialsList.size() - 1);
        notification.setText(NOTIFICATION_ADD_MORE_CREDENTIALS);
    }

    public void loadCredentials() {
        Collection<CredentialsDto> credentials;
        try {
            credentials = DbUtil.loadCredentials();
        } catch (DbUnavailableException e) {
            notification.setText(NOTIFICATION_DB_UNAVAILABLE);
            addButton.setDisable(true);
            return;
        }

        if (!credentials.isEmpty()) {
            notification.setText(NOTIFICATION_ADD_MORE_CREDENTIALS);
        }

        credentialsList.setCellFactory(call -> new CredentialsDataListCell());
        observableCredentialsList.setAll(credentials);
        credentialsList.setItems(observableCredentialsList);
    }
}
