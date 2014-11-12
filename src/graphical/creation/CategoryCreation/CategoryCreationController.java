package graphical.creation.CategoryCreation;

import graphical.MainWindow.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import persistance.*;

import java.sql.SQLException;
import java.text.ParseException;

/**
 * Created by olfad on 24.01.14.
 */


public class CategoryCreationController {

    private SQLiteDBManager dbManager;
    public TreeView categoryList;
    public TextField categoryNameField;

    public Label headline;


    public void initialize(){
        dbManager = new SQLiteDBManager();
        TreeItem<Category> treeItem = new TreeItem<>();
        fillCategoryTree(null, treeItem);
        categoryList.setRoot(treeItem);
        categoryList.setShowRoot(false);
}

    private void fillCategoryTree(Category c, TreeItem<Category> rootItem) {

        rootItem.setExpanded(true);

        try {
            for(Category SubC : dbManager.getCategoriesbyCategory(c)){
                TreeItem<Category> categoryItem = new TreeItem<>(SubC);
                fillCategoryTree(SubC, categoryItem);
                rootItem.getChildren().add(categoryItem);
            }
        } catch (SQLException | NoSuchDBEntryException e) {
            e.printStackTrace();
        }

    }


    @FXML
    public void save()  {
        Category c = null;
        if(categoryList.getSelectionModel().getSelectedItem()!=null){
            c = ((TreeItem<Category>)categoryList.getSelectionModel().getSelectedItem()).getValue();
        }
        try {
            dbManager.store(new Category(categoryNameField.getText(),c));
        } catch (SQLException e) {
            MainController.showErrorDialog(e);
            close();
        }
        close();
    }
    @FXML
    public void close(){
        Stage stage = (Stage) categoryList.getScene().getWindow();
        stage.close();

    }

    public void processKeyboard(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            save();
        } else if(event.getCode() == KeyCode.ESCAPE) {
            close();
        }
    }
}
