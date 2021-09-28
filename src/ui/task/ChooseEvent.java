package ui.task;

import businesslogic.event.EventInfo;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import ui.Main;

import java.io.IOException;

public class ChooseEvent {

    @FXML
    private AnchorPane taskManagementPane;

    @FXML
    private ListView<EventInfo> eventList;

    private Main mainPaneController;
    private ViewEvent viewEventController;
    private BorderPane viewEventPane;

    public void initialize() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view-event.fxml"));
        try {
            viewEventPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        viewEventController = fxmlLoader.getController();
        viewEventController.setParent(this);
        eventList.setCellFactory(param -> new ListCell<>(){

            @Override
            protected void updateItem(EventInfo eventInfo, boolean empty) {
                super.updateItem(eventInfo, empty);
                if (eventInfo == null || empty)
                    setText(null);
                else
                    setText(eventInfo.toString());
            }

        });
        eventList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void setChefEvents() {
        eventList.setItems(EventInfo.loadChefEvents());
    }

    public void setMainPaneController(Main main) {
        mainPaneController = main;
        viewEventController.setMainPaneController(mainPaneController);
    }

    public void endCookingTaskManagement() {
        eventList.getSelectionModel().clearSelection();
        mainPaneController.showStartPane(taskManagementPane);
    }

    public void selectEvent() {
        EventInfo event = eventList.getSelectionModel().getSelectedItem();
        if (event != null) {
            viewEventController.setCurrentEvent(event);
            mainPaneController.changeScene(taskManagementPane, viewEventPane);
        }
    }

    public Pane getRootPane() {
        return taskManagementPane;
    }

}
