package ui.task;

import businesslogic.CatERing;
import businesslogic.task.Cook;
import businesslogic.task.turn.CookingTurn;
import businesslogic.task.turn.ShiftBoard;
import businesslogic.task.turn.ShiftBoardInfo;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ChooseAssignmentsDialog {

    @FXML
    private TreeView<ShiftBoardInfo> treeView;

    public void initialize() {}

    //Warning: getFocusedItem method could return
    public Cook getSelectedCook() {
        if (treeView.getFocusModel().getFocusedItem() == null)
            return null;
        return (Cook) treeView.getFocusModel().getFocusedItem().getValue();
    }

    public void initializeCookDialog() {
        TreeItem<ShiftBoardInfo> root = new TreeItem<>(null);
        treeView.setShowRoot(false);
        ObservableList<Cook> cooks = Cook.loadCooks();
        for (Cook cook : cooks)
            root.getChildren().add(new TreeItem<>(cook));
        treeView.setRoot(root);
    }

    public void initializeCookingTurnDialog() {
        TreeItem<ShiftBoardInfo> root = new TreeItem<>(null);
        treeView.setShowRoot(false);
        ObservableList<ShiftBoard> shiftBoards = CatERing.getInstance().getTaskManager().getShiftBoards();
        for (ShiftBoard shiftBoard : shiftBoards) {
            TreeItem<ShiftBoardInfo> shiftBoardItem = new TreeItem<>(shiftBoard);
            root.getChildren().add(shiftBoardItem);
            ObservableList<CookingTurn> cookingTurns = shiftBoard.getCookingTurns();
            for (CookingTurn cookingTurn : cookingTurns) {
                TreeItem<ShiftBoardInfo> cookingTurnItem = new TreeItem<>(cookingTurn);
                shiftBoardItem.getChildren().add(cookingTurnItem);
            }
        }
        treeView.setRoot(root);
    }

    public ShiftBoardInfo getSelectedCookingTurn() {
        if (treeView.getFocusModel().getFocusedItem() == null)
            return null;
        return treeView.getFocusModel().getFocusedItem().getValue();
    }

}
