package ui.task;

import businesslogic.CatERing;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.task.TaskSummary;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import ui.Main;

import java.io.IOException;

public class ViewEvent {
    private Main mainPaneController;
    private ChooseEvent chooseEventController;

    private BorderPane viewTaskSummaryPane;
    private ViewTaskSummary viewTaskSummaryController;

    @FXML
    private BorderPane root;

    @FXML
    private AnchorPane viewServicePane;

    @FXML
    ViewService viewServicePaneController;

    @FXML
    private Text eventName;

    @FXML
    private Text eventDateStart;

    @FXML
    private Text eventDateEnd;

    @FXML
    private Text eventParticipants;

    @FXML
    private Text eventOrganizer;

    @FXML
    private ListView<ServiceInfo> serviceList;

    @FXML
    private Button createTaskSummaryBtn;

    @FXML
    private Button openTaskSummaryBtn;

    @FXML
    private Button resetTaskSummaryBtn;

    public void initialize() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view-task-summary.fxml"));
        try {
            viewTaskSummaryPane = loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        viewTaskSummaryController = loader.getController();
        viewTaskSummaryController.setParent(this);
        serviceList.setCellFactory(param -> new ListCell<>(){

            @Override
            protected void updateItem(ServiceInfo serviceInfo, boolean empty) {
                super.updateItem(serviceInfo, empty);
                if (serviceInfo == null || empty)
                    setText(null);
                else
                    setText(serviceInfo.toString());
            }

        });
        serviceList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        createTaskSummaryBtn.setDisable(true);
        openTaskSummaryBtn.setDisable(true);
        resetTaskSummaryBtn.setDisable(true);
        viewServicePaneController.initialize();
    }

    public void setMainPaneController(Main mainPaneController) {
        this.mainPaneController = mainPaneController;
        viewTaskSummaryController.setMainPaneController(mainPaneController);
    }

    public void setCurrentEvent(EventInfo currentEvent) {
        eventName.setText(currentEvent.getName());
        eventDateStart.setText(currentEvent.getDateStart().toString());
        eventDateEnd.setText(currentEvent.getDateEnd().toString());
        eventParticipants.setText(String.valueOf(currentEvent.getParticipants()));
        eventOrganizer.setText(currentEvent.getOrganizer().getUserName());
        serviceList.setItems(currentEvent.getServices());
        createTaskSummaryBtn.setDisable(true);
        openTaskSummaryBtn.setDisable(true);
        resetTaskSummaryBtn.setDisable(true);
        viewServicePaneController.initialize();
    }

    //if the FocusModel gives problems use getSelectionModel().getSelectedItem();
    public void viewSelectedService() {
        ServiceInfo service = serviceList.getSelectionModel().getSelectedItem();
        if (service != null) {
            viewServicePaneController.viewServiceInfo(service);
            if (service.getMenu() != null) {
                if (TaskSummary.loadTaskSummaryByService(service) != null) {
                    createTaskSummaryBtn.setDisable(true);
                    openTaskSummaryBtn.setDisable(false);
                    resetTaskSummaryBtn.setDisable(false);
                } else {
                    createTaskSummaryBtn.setDisable(false);
                    openTaskSummaryBtn.setDisable(true);
                    resetTaskSummaryBtn.setDisable(true);
                }
            }
            else {
                createTaskSummaryBtn.setDisable(true);
                openTaskSummaryBtn.setDisable(true);
                resetTaskSummaryBtn.setDisable(true);
            }
        }
        else {
            createTaskSummaryBtn.setDisable(true);
            openTaskSummaryBtn.setDisable(true);
            resetTaskSummaryBtn.setDisable(true);
            viewServicePaneController.initialize();
        }
    }

    public void backToChooseEvent() {
        serviceList.getSelectionModel().clearSelection();
        chooseEventController.setChefEvents();
        mainPaneController.changeScene(root, chooseEventController.getRootPane());
    }

    public void createTaskSummary() throws Exception {
        ServiceInfo service = serviceList.getSelectionModel().getSelectedItem();
        CatERing.getInstance().getTaskManager().createTaskSummary(service);
        viewTaskSummaryController.setTaskSummary();
        createTaskSummaryBtn.setDisable(true);
        openTaskSummaryBtn.setDisable(false);
        resetTaskSummaryBtn.setDisable(false);
        mainPaneController.changeScene(root, viewTaskSummaryPane);
    }

    public void openTaskSummary() throws Exception{
        ServiceInfo service = serviceList.getSelectionModel().getSelectedItem();
        CatERing.getInstance().getTaskManager().setTaskSummary(service);
        viewTaskSummaryController.setTaskSummary();
        mainPaneController.changeScene(root, viewTaskSummaryPane);
    }

    //TODO: to fix
    public void resetTaskSummary() throws Exception {
        ServiceInfo service = serviceList.getSelectionModel().getSelectedItem();
        CatERing.getInstance().getTaskManager().resetTaskSummary(service);
        viewTaskSummaryController.setTaskSummary();
        mainPaneController.changeScene(root, viewTaskSummaryPane);
    }

    public void setParent(ChooseEvent chooseEventController) {
        this.chooseEventController = chooseEventController;
    }

    public Pane getRootPane() {
        return root;
    }

}
