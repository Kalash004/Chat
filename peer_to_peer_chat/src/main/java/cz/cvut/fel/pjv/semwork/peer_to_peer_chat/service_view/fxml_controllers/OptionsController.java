package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.fxml_controllers;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.tictactoe_service.TictactoeGameHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.UiController;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.sort_utils.PeerSorter;

import java.io.IOException;

public class OptionsController {
    @FXML private Button resetInvitationsButton;
    @FXML private AnchorPane rootPane;
    @FXML private ChoiceBox sortChoiceBox;
    @FXML private CheckBox darkThemeSwitch;
    private PeerSorter peerSorter;

    public void initialize() {
        peerSorter = new PeerSorter();
        ObservableList<String> options = FXCollections.observableArrayList(peerSorter.SORT_OPTIONS);

        sortChoiceBox.setItems(options);
    }

    public Stage getStage() {
        return (Stage) darkThemeSwitch.getScene().getWindow();
    }

    public void setDarkMode(boolean darkMode) {
        darkThemeSwitch.setSelected(darkMode);
        rootPane.getStyleClass().removeAll("light-mode", "dark-mode");
        if(darkMode) {
            rootPane.getStyleClass().add("dark-mode");
        } else {
            rootPane.getStyleClass().add("light-mode");
        }
    }

    public void setSortOption(String peerSortOption) {
        sortChoiceBox.setValue(peerSortOption);
    }

    @FXML
    public void onDarkThemeSwitch(ActionEvent event) {
        UiController.getInstance().setDarkMode(); // true or false
    }


    @FXML
    private void onSortChoiceBox(ActionEvent actionEvent) {
        String selected = (String) sortChoiceBox.getValue();
        UiController.getInstance().setPeerSortOption(selected);
    }

    /**
     * Retrieves the current window (stage) and instructs the UI controller to navigate back to last opened window.
     *
     * @param actionEvent the event triggered by clicking the "Return" button
     * @throws IOException if loading the new page fails
     */
    @FXML
    private void onReturnClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) darkThemeSwitch.getScene().getWindow();
        UiController.getInstance().openPage(null, stage);
    }

}
