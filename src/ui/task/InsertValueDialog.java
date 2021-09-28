package ui.task;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class InsertValueDialog {

    @FXML
    private TextField textField;

    @FXML
    private Text textFieldInfo;

    public void initialize() {}

    public void setTextFieldInfo(String text) {
        textFieldInfo.setText(text);
    }

    public String getText() {
        return textField.getText();
    }

}
