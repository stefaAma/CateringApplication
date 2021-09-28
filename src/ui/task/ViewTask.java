package ui.task;

import businesslogic.CatERing;
import businesslogic.task.Cook;
import businesslogic.task.Task;
import businesslogic.task.turn.CookingTurn;
import businesslogic.task.turn.ShiftBoardInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.text.Text;
import ui.util.Toast;

import java.io.IOException;

//TODO: evaluate the possibility to introduce the Toast.makeText method in order to manage the results of some TaskManager functions

public class ViewTask {
    private Task currentTask;

    @FXML
    private Text tacc;

    @FXML
    private Text tacv;

    @FXML
    private Text tuc;

    @FXML
    private Text tuv;

    @FXML
    private Text tt;

    @FXML
    private Text cookingProcedureName;

    @FXML
    private Text timeRequested;

    @FXML
    private Text cook;

    @FXML
    private Text cookingTurn;

    @FXML
    private Text dosesNeeded;

    @FXML
    private Text dosesPrepared;

    @FXML
    private Button timeBtn;

    @FXML
    private Button cookBtn;

    @FXML
    private Button turnBtn;

    @FXML
    private Button dosesNeededBtn;

    @FXML
    private Button dosesPreparedBtn;

    @FXML
    private Button deleteAssignmentsBtn;

    public void initialize() {
        tacc.setText("0");
        tacv.setText("0");
        tuc.setText("0");
        tuv.setText("0");
        tt.setText("0");
        cookingProcedureName.setText("Nessun Compito Selezionato!");
        timeRequested.setText("0");
        cook.setText("N/A");
        cookingTurn.setText("N/A");
        dosesNeeded.setText("N/A");
        dosesPrepared.setText("N/A");
        timeBtn.setDisable(true);
        cookBtn.setDisable(true);
        turnBtn.setDisable(true);
        dosesNeededBtn.setDisable(true);
        dosesPreparedBtn.setDisable(true);
        deleteAssignmentsBtn.setDisable(true);
    }

    public void viewTaskInfo(Task task) {
        currentTask = task;
        cookingProcedureName.setText(currentTask.getCookingProcedure().getName());
        tacc.setText(currentTask.getCookingProcedure().getTacc());
        tacv.setText(currentTask.getCookingProcedure().getTacv());
        tuc.setText(currentTask.getCookingProcedure().getTuc());
        tuv.setText(currentTask.getCookingProcedure().getTuv());
        tt.setText(currentTask.getCookingProcedure().getTt());
        timeRequested.setText(String.valueOf(currentTask.getTimeRequested()));
        if (currentTask.getCook() != null)
            cook.setText(currentTask.getCook().getUserName());
        else
            cook.setText("N/A");
        if (currentTask.getCookingTurn() != null)
            cookingTurn.setText(currentTask.getCookingTurn().getTurnInfo());
        else
            cookingTurn.setText("N/A");
        dosesNeeded.setText(currentTask.getDosesNeeded());
        dosesPrepared.setText(currentTask.getDosesPrepared());
        /*if (currentTask.getTimeRequested() > 0)
            cookBtn.setDisable(false);
        else
            cookBtn.setDisable(true);*/
        if (currentTask.getCook() != null || currentTask.getCookingTurn() != null)
            deleteAssignmentsBtn.setDisable(false);
        else
            deleteAssignmentsBtn.setDisable(true);
        timeBtn.setDisable(false);
        cookBtn.setDisable(false);
        turnBtn.setDisable(false);
        dosesPreparedBtn.setDisable(false);
        dosesNeededBtn.setDisable(false);
    }

    public void assignTimeRequested() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("insert-value-dialog.fxml"));
        try {
            DialogPane dialogPane = loader.load();
            InsertValueDialog insertValueDialog = loader.getController();
            insertValueDialog.setTextFieldInfo("Time:");
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Assegna Tempo Rischiesto");
            dialog.setDialogPane(dialogPane);
            dialog.showAndWait();
            String timeStr = insertValueDialog.getText();
            if (timeStr != null && !timeStr.isEmpty()) {
                try {
                    int time = Integer.parseInt(timeStr);
                    if (time > 0 && CatERing.getInstance().getTaskManager().assignTimeRequested(currentTask, time)) {
                        timeRequested.setText(timeStr);
                        //cookBtn.setDisable(false);
                    }
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void assignCook() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("choose-assignments-dialog.fxml"));
        try {
            DialogPane dialogPane = loader.load();
            ChooseAssignmentsDialog chooseAssignmentsDialog = loader.getController();
            chooseAssignmentsDialog.initializeCookDialog();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Scegli Cuoco");
            dialog.setDialogPane(dialogPane);
            dialog.showAndWait();
            Cook cookObj = chooseAssignmentsDialog.getSelectedCook();
            if (cookObj != null) {
                if (CatERing.getInstance().getTaskManager().assignCook(currentTask, cookObj)) {
                    cook.setText(cookObj.toString());
                    deleteAssignmentsBtn.setDisable(false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void assignCookingTurn() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("choose-assignments-dialog.fxml"));
        try {
            DialogPane dialogPane = loader.load();
            ChooseAssignmentsDialog chooseAssignmentsDialog = loader.getController();
            chooseAssignmentsDialog.initializeCookingTurnDialog();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Scegli Turno Di Cucina");
            dialog.setDialogPane(dialogPane);
            dialog.showAndWait();
            ShiftBoardInfo shiftBoardInfo = chooseAssignmentsDialog.getSelectedCookingTurn();
            if (shiftBoardInfo != null) {
                if (shiftBoardInfo.getClass() == CookingTurn.class) {
                    CookingTurn cookingTurnObj = (CookingTurn) shiftBoardInfo;
                    if (CatERing.getInstance().getTaskManager().assignCookingTurn(currentTask, cookingTurnObj)) {
                        cookingTurn.setText(cookingTurnObj.getTurnInfo());
                        deleteAssignmentsBtn.setDisable(false);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void assignDosesNeeded() throws Exception {
        String text = dialogInsertDosesValue("Assegna Dosi Da Preparare");
        if (text != null && !text.isEmpty()) {
            CatERing.getInstance().getTaskManager().assignDosesNeeded(currentTask, text);
            dosesNeeded.setText(text);
        }
    }

    public void assignDosesPrepared() throws Exception {
        String text = dialogInsertDosesValue("Assegna Dosi Già Preparate");
        if (text != null && !text.isEmpty()) {
            CatERing.getInstance().getTaskManager().assignDosesPrepared(currentTask, text);
            dosesPrepared.setText(text);
        }
    }

    private String dialogInsertDosesValue(String title) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("insert-value-dialog.fxml"));
        try {
            DialogPane dialogPane = loader.load();
            InsertValueDialog insertValueDialog = loader.getController();
            insertValueDialog.setTextFieldInfo("Doses:");
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle(title);
            dialog.setDialogPane(dialogPane);
            dialog.showAndWait();
            return insertValueDialog.getText();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteAllAssignments() throws Exception {
        CatERing.getInstance().getTaskManager().deleteAssignments(currentTask);
        cook.setText("N/A");
        cookingTurn.setText("N/A");
        deleteAssignmentsBtn.setDisable(true);
    }

}
