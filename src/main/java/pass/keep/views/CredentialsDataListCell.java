package pass.keep.views;

import javafx.scene.control.ListCell;
import pass.keep.controllers.CredentialsDataController;
import pass.keep.dto.CredentialsDto;

public class CredentialsDataListCell extends ListCell<CredentialsDto> {

    private CredentialsDataController credentialsDataController = new CredentialsDataController();

    @Override
    protected void updateItem(CredentialsDto data, boolean empty) {
        super.updateItem(data, empty);

        if (empty) {
            setGraphic(null);
            return;
        }

        credentialsDataController.setCredentialsData(getIndex(), data);
        setGraphic(credentialsDataController.getContainer());
    }
}
