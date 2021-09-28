package ui.task;

import businesslogic.CatERing;
import businesslogic.task.Task;
import businesslogic.task.turn.CookingTurn;
import businesslogic.task.turn.ShiftBoard;
import businesslogic.task.turn.ShiftBoardInfo;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Button;


public class ShiftBoardsDialog {
    private CookingTurn currentCookingTurn;

    @FXML
    TreeView<ShiftBoardInfo> shiftBoardsTreeView;

    @FXML
    private Button signAsFullBtn;

    @FXML
    private Button signAsNotFullBtn;

    public void initialize() {
        ObservableList<ShiftBoard> shiftBoards = CatERing.getInstance().getTaskManager().getShiftBoards();
        shiftBoardsTreeView.setShowRoot(false);
        TreeItem<ShiftBoardInfo> root = new TreeItem<>(null);
        for (ShiftBoard shiftBoard : shiftBoards) {
            TreeItem<ShiftBoardInfo> nodeShiftBoard = new TreeItem<>(shiftBoard);
            root.getChildren().add(nodeShiftBoard);
            ObservableList<CookingTurn> cookingTurns = shiftBoard.getCookingTurns();
            for (CookingTurn cookingTurn : cookingTurns) {
                TreeItem<ShiftBoardInfo> nodeCookingTurn = new TreeItem<>(cookingTurn);
                nodeShiftBoard.getChildren().add(nodeCookingTurn);
                ObservableList<Task> tasks = cookingTurn.getTasks();
                for (Task task : tasks) {
                    TreeItem<ShiftBoardInfo> nodeTask = new TreeItem<>(task);
                    nodeCookingTurn.getChildren().add(nodeTask);
                }
            }
        }
        shiftBoardsTreeView.setRoot(root);
        signAsFullBtn.setDisable(true);
        signAsNotFullBtn.setDisable(true);
    }

    public void itemSelected() {
        ShiftBoardInfo shiftBoardInfo = shiftBoardsTreeView.getFocusModel().getFocusedItem().getValue();
        if (shiftBoardInfo != null && shiftBoardInfo.getClass() == CookingTurn.class) {
            currentCookingTurn = (CookingTurn) shiftBoardInfo;
            if (!CatERing.getInstance().getTaskManager().checkCookingTurnDateTime(currentCookingTurn)) {
                signAsFullBtn.setDisable(true);
                signAsNotFullBtn.setDisable(true);
            }
            else {
                if (currentCookingTurn.isFull()) {
                    signAsFullBtn.setDisable(true);
                    signAsNotFullBtn.setDisable(false);
                }
                else {
                    signAsFullBtn.setDisable(false);
                    signAsNotFullBtn.setDisable(true);
                }
            }
        }
        else {
            signAsFullBtn.setDisable(true);
            signAsNotFullBtn.setDisable(true);
        }
    }

    public void signFull() {
        CatERing.getInstance().getTaskManager().setCookingTurnFull(currentCookingTurn);
        signAsFullBtn.setDisable(true);
        signAsNotFullBtn.setDisable(false);
        shiftBoardsTreeView.getFocusModel().getFocusedItem().valueProperty().setValue(currentCookingTurn);
    }

    public void signNotFull() {
        CatERing.getInstance().getTaskManager().setCookingTurnAvailable(currentCookingTurn);
        signAsFullBtn.setDisable(false);
        signAsNotFullBtn.setDisable(true);
        shiftBoardsTreeView.getFocusModel().getFocusedItem().valueProperty().setValue(currentCookingTurn);
    }

}
