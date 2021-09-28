package ui;

import businesslogic.CatERing;
import businesslogic.user.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.*;
import ui.menu.MenuManagement;
import ui.task.ChooseEvent;
import ui.task.ShiftBoardsDialog;
import ui.task.ViewEvent;
import ui.task.ViewTaskSummary;

import java.io.IOException;

public class Main {

    @FXML
    AnchorPane paneContainer;

    @FXML
    FlowPane startPane;

    @FXML
    Start startPaneController;

    BorderPane menuManagementPane;
    MenuManagement menuManagementPaneController;

    AnchorPane chooseEventPane;
    ChooseEvent chooseEventPaneController;

    public void initialize() {
        startPaneController.setParent(this);

        FXMLLoader loaderMenu = new FXMLLoader(getClass().getResource("menu/menu-management.fxml"));
        FXMLLoader loaderTask = new FXMLLoader(getClass().getResource("task/choose-event.fxml"));
        try {
            menuManagementPane = loaderMenu.load();
            menuManagementPaneController = loaderMenu.getController();
            menuManagementPaneController.setMainPaneController(this);
            chooseEventPane = loaderTask.load();
            chooseEventPaneController = loaderTask.getController();
            chooseEventPaneController.setMainPaneController(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void startMenuManagement() {
        LoginDialog loginDialog = login();
        if (loginDialog != null) {
            String username = loginDialog.getUsername();
            String password = loginDialog.getPassword();
            CatERing.getInstance().getUserManager().trueLogin(username, password);
            User user = CatERing.getInstance().getUserManager().getCurrentUser();
            if (user != null && user.isOrganizer()) {
                paneSettings(menuManagementPane);
                menuManagementPaneController.initialize();
                paneContainer.getChildren().remove(startPane);
                paneContainer.getChildren().add(menuManagementPane);
            }
        }
    }

    public void showStartPane(Pane pane) {
        startPaneController.initialize();
        paneContainer.getChildren().remove(pane);
        paneContainer.getChildren().add(startPane);
    }

    public void startCookingTaskManagement() {
        LoginDialog loginDialog = login();
        if (loginDialog != null) {
            String username = loginDialog.getUsername();
            String password = loginDialog.getPassword();
            CatERing.getInstance().getUserManager().trueLogin(username, password);
            User user = CatERing.getInstance().getUserManager().getCurrentUser();
            if (user != null && user.isChef()) {
                paneSettings(chooseEventPane);
                //chooseEventPaneController.initialize();
                chooseEventPaneController.setChefEvents();
                paneContainer.getChildren().remove(startPane);
                paneContainer.getChildren().add(chooseEventPane);
            }
        }
    }

    private void paneSettings(Pane pane) {
        AnchorPane.setTopAnchor(pane, 0.0);
        AnchorPane.setBottomAnchor(pane, 0.0);
        AnchorPane.setLeftAnchor(pane, 0.0);
        AnchorPane.setRightAnchor(pane, 0.0);
    }

    /*public void viewEventForTaskManagement(Pane pane) {
        BorderPane viewEventPane = chooseEventPaneController.getViewEventPane();
        paneSettings(viewEventPane);
        ViewEvent viewEventController = chooseEventPaneController.getViewEventController();
        viewEventController.initialize();
        paneContainer.getChildren().remove(pane);
        paneContainer.getChildren().add(viewEventPane);
    }

    public void viewEventsForTaskManagement() {
        BorderPane viewEventPane = chooseEventPaneController.getViewEventPane();
        chooseEventPaneController.initialize();
        paneContainer.getChildren().remove(viewEventPane);
        paneContainer.getChildren().add(chooseEventPane);
    }

    public void viewTaskSummary() {
        BorderPane viewEventPane = chooseEventPaneController.getViewEventPane();
        BorderPane viewTaskSummaryPane = chooseEventPaneController.getViewEventController().getViewTaskSummaryPane();
        ViewTaskSummary viewTaskSummaryController = chooseEventPaneController.getViewEventController().getViewTaskSummaryController();
        viewTaskSummaryController.initialize();
        paneContainer.getChildren().remove(viewEventPane);
        paneContainer.getChildren().add(viewTaskSummaryPane);
    }*/

    public void changeScene(Pane oldScene, Pane newScene) {
        paneContainer.getChildren().remove(oldScene);
        paneSettings(newScene);
        paneContainer.getChildren().add(newScene);
    }

    private LoginDialog login() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loginDialog.fxml"));
        try {
            DialogPane dialogPane = loader.load();
            LoginDialog loginDialog = loader.getController();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Login");
            dialog.setDialogPane(dialogPane);
            dialog.showAndWait();
            return loginDialog;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
