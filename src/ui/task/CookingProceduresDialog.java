package ui.task;

import businesslogic.recipe.CookingProcedure;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class CookingProceduresDialog {

    @FXML
    private ListView<CookingProcedure> cookingProcedureList;

    public void initialize() {
        cookingProcedureList.setCellFactory(param -> new ListCell<>(){

            @Override
            protected void updateItem(CookingProcedure cookingProcedure, boolean empty) {
                super.updateItem(cookingProcedure, empty);
                if (cookingProcedure == null || empty)
                    setText(null);
                else {
                    String text = cookingProcedure.getName() + " - TACc: " + cookingProcedure.getTacc() + " TACv: " + cookingProcedure.getTacv() +
                            " TUc: " + cookingProcedure.getTuc() + " TUv: " + cookingProcedure.getTuv() + " TT: " + cookingProcedure.getTt();
                    setText(text);
                }
            }
        });

        cookingProcedureList.setItems(businesslogic.recipe.CookingProcedure.loadAllCookingProcedures());
    }

    public businesslogic.recipe.CookingProcedure getSelectedCookingProcedure() {
        return cookingProcedureList.getFocusModel().getFocusedItem();
    }

}
