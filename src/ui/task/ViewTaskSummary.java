package ui.task;

import businesslogic.CatERing;
import businesslogic.recipe.CookingProcedure;
import businesslogic.task.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import ui.Main;

import java.io.IOException;
import java.util.Optional;

public class ViewTaskSummary {
    private Main mainPaneController;
    private ViewEvent viewEventController;

    @FXML
    private BorderPane root;

    @FXML
    private AnchorPane viewTaskPane;

    @FXML
    ViewTask viewTaskPaneController;

    @FXML
    ListView<Task> taskList;

    @FXML
    Button removeTaskBtn;

    @FXML
    Button upBtn;

    @FXML
    Button downBtn;

    public void initialize() {
        taskList.setCellFactory(param -> new ListCell<>() {

            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (task == null || empty)
                    setText(null);
                else
                    setText(task.toString());
            }

        });
        taskList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        removeTaskBtn.setDisable(true);
        upBtn.setDisable(true);
        downBtn.setDisable(true);
        viewTaskPaneController.initialize();
    }

    public void setMainPaneController(Main mainPaneController) {
        this.mainPaneController = mainPaneController;
    }

    public void setTaskSummary() {
        taskList.setItems(CatERing.getInstance().getTaskManager().getTaskSummary().getTasks());
        taskList.getSelectionModel().clearSelection();
        removeTaskBtn.setDisable(true);
        upBtn.setDisable(true);
        downBtn.setDisable(true);
        viewTaskPaneController.initialize();
    }

    public void viewSelectedTask() throws Exception {
        Task task = taskList.getSelectionModel().getSelectedItem();
        if (task != null) {
            viewTaskPaneController.viewTaskInfo(task);
            int position = taskList.getSelectionModel().getSelectedIndex();
            removeTaskBtn.setDisable(false);
            checkUpDownBtn(position);
        }
        else {
            removeTaskBtn.setDisable(true);
            upBtn.setDisable(true);
            downBtn.setDisable(true);
            viewTaskPaneController.initialize();
        }
    }

    private void checkUpDownBtn(int position) throws Exception {
        if (position > 0)
            upBtn.setDisable(false);
        else
            upBtn.setDisable(true);
        if (position < CatERing.getInstance().getTaskManager().getTaskListSize() - 1)
            downBtn.setDisable(false);
        else
            downBtn.setDisable(true);
    }

    public void createTask() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("cooking-procedures-dialog.fxml"));
        DialogPane dialogPane;
        try {
            dialogPane = loader.load();
            CookingProceduresDialog cookingProceduresDialog = loader.getController();
            cookingProceduresDialog.initialize();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Seleziona La Procedura Di Cucina");
            dialog.setDialogPane(dialogPane);
            Optional<ButtonType> buttonType = dialog.showAndWait();
            if (buttonType.isPresent() && buttonType.get() == ButtonType.FINISH) {
                CookingProcedure cookingProcedure = cookingProceduresDialog.getSelectedCookingProcedure();
                if (cookingProcedure != null)
                    CatERing.getInstance().getTaskManager().addTask(cookingProcedure);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeTask() throws Exception {
        Task task = taskList.getSelectionModel().getSelectedItem();
        CatERing.getInstance().getTaskManager().removeTask(task);
    }

    public void goBack() {
        mainPaneController.changeScene(root, viewEventController.getRootPane());
    }

    public void moveUpTask() throws Exception {
        changeItemPosition(-1);
    }

    public void moveDownTask() throws Exception {
        changeItemPosition(+1);
    }

    private void changeItemPosition(int change) throws Exception {
        Task task = taskList.getSelectionModel().getSelectedItem();
        int newPosition = taskList.getSelectionModel().getSelectedIndex() + change;
        CatERing.getInstance().getTaskManager().orderList(task, newPosition);
        taskList.getSelectionModel().select(newPosition);
        checkUpDownBtn(newPosition);
    }

    public void viewShiftBoards() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("shift-boards-dialog.fxml"));
        try {
            DialogPane dialogPane = loader.load();
            ShiftBoardsDialog shiftBoardsDialog = loader.getController();
            shiftBoardsDialog.initialize();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Visualizzazione Tabelloni Dei Turni");
            dialog.setDialogPane(dialogPane);
            dialog.showAndWait();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setParent(ViewEvent viewEventController) {
        this.viewEventController = viewEventController;
    }

}
