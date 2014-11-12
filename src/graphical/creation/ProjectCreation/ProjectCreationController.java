package graphical.creation.ProjectCreation;

import graphical.MainWindow.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import persistance.*;


import java.sql.SQLException;

/**
 *
 * Created by olfad on 24.01.14.
 */
public class ProjectCreationController {
    public Button bCancel;
    public Button bSave;

    public TextField tfName;
    public TextArea taDescription;

    public Label errLbl;

    private SQLiteDBManager dbManager;

    private Bill bill;

    private int projectID = -1;

    public Label headline;

    public void initialize(){
        dbManager = new SQLiteDBManager();

    }

    @FXML
    private void close(){
        Stage stage = (Stage) bCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void save() {
        try {
            if(checkreqiredData()){
                saveProject();
                close();
            }
        } catch (Throwable e) {
            Stage stage = (Stage) bCancel.getScene().getWindow();
            stage.hide();
            MainController.showErrorDialog(e);
            close();
        }
    }




    private void showError(String msg) {
        errLbl.setText(msg);

    }

    private boolean checkreqiredData() {
        if(tfName.getText().trim().equals("")){
            showError("Dein Projekt braucht einen Namen.");
            return false;
        }
        return true;
    }

    private void saveProject() throws SQLException {
        Project p = new Project(projectID,tfName.getText(),bill,taDescription.getText());
        dbManager.store(p);
    }

    public void setBill(Bill b){
        bill = b;
    }

    public void setProject(Project project){
        headline.setText("Projekt Bearbeiten:");
        tfName.setText(project.getName());
        taDescription.setText(project.getDescription());
        projectID = project.getId();
    }

    public void processKeyboard(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            save();
        } else if(event.getCode() == KeyCode.ESCAPE) {
            close();
        }
    }
}
