package pass.keep.views;

import javafx.scene.control.ListCell;
import pass.keep.controllers.CredentialsDataController;

public class CredentialsListCell extends ListCell<CredentialsDataController> {

    @Override
    protected void updateItem(CredentialsDataController data, boolean empty) {
        super.updateItem(data, empty);

        if (empty || data == null) {
            setGraphic(null);
            return;
        }

        data.setIndex(getIndex());
        setGraphic(data.getContainer());
    }
}
