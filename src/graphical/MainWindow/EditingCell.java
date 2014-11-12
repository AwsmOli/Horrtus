package graphical.MainWindow;

import com.sun.prism.impl.Disposer;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.awt.event.KeyEvent;

/**
 * Created by olfad on 01.07.2014.
 */
public class EditingCell extends TableCell<Disposer.Record, Double> {
    private TextField textField;
    
    @Override
    public void commitEdit(Double aDouble) {
        super.commitEdit(aDouble);

        this.getTableRow().getText();
    }

    @Override
    protected void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                    setGraphic(textField);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                } else {
                    setText(getString());
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setText(String.valueOf(getItem()));
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    public void startEdit() {
        super.startEdit();
        
        if(textField == null){
            createTextField();
        }

        setGraphic(textField);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        textField.selectAll();
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()*2);
        textField.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent keyEvent) {
                if(keyEvent.getCode() == KeyCode.ENTER){
                    commitEdit(Double.parseDouble(textField.getText()));

                } else if (keyEvent.getCode() == KeyCode.ESCAPE){
                    cancelEdit();
                }
            }
        });
    }

    private String getString() {
        return getItem() == null  ? "" : getItem().toString();
    }
}
