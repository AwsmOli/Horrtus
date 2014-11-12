package graphical.ErrorDialog;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;


/**
 * Error Dialog
 * Created by olfad on 24.01.14.
 */
public class ErrorDialogController {

    public TextArea errMsgTextArea;
    public TextArea supportInfoTextArea;

    @FXML
    public Button closeButton;

    public void initialize(){

    }

    @FXML
    private void close(){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public TextArea getErrMsgTextArea() {
        return errMsgTextArea;
    }

    public TextArea getSupportInfoTextArea() {
        return supportInfoTextArea;
    }

    public void processKeyboard(KeyEvent event){
        if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.ESCAPE) close();
    }

}
