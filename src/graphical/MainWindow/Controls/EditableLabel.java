package graphical.MainWindow.Controls;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;


/**
 * Created by olfad on 08.10.2014.
 */
public class EditableLabel extends Label {


    TextField textField;
    Popup popup;

    public EditableLabel() {
        init();
    }

    public EditableLabel(String s) {
        super(s);
        init();
    }

    public EditableLabel(String s, Node node) {
        super(s, node);
        init();
    }

    private void init(){
        textField = new TextField(this.getText());
        popup = new Popup();
        popup.getContent().add(textField);
        textField.setPrefWidth(this.getPrefWidth());
        popup.setWidth(this.getWidth());
        popup.setHeight(this.getHeight());


        addLabelListener();
        addTextFieldListener();
    }

    private void addLabelListener(){

         this.setOnMouseClicked(new EventHandler<MouseEvent>() {
             @Override
             public void handle(MouseEvent mouseEvent) {
                 System.out.println(mouseEvent.getClickCount());
                 if (mouseEvent.getClickCount() == 2) {
                     becomeTextBox();
                 }
             }
         });

    }

    private void addTextFieldListener(){
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    becomeLabel();
                }
            }
        });
    }

    private void becomeLabel() {
       popup.hide();
    }

    private void becomeTextBox() {
        Bounds bounds = this.localToScene(this.getBoundsInLocal());
        popup.show(this,bounds.getMinX(), bounds.getMinY());
        textField.requestFocus();
    }
}
