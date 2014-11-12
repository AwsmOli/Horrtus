package graphical.creation.ProductCreation;

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
import java.sql.Time;
import java.text.ParseException;


/**
 * Created by olfad on 24.01.14.
 */
public class ProductCreationController {
    public TextField tfName;
    public TextField tfPrice;
    public TextField tfUnit;
    public TreeView lCategory;
    public TextField tfTime;

    public Label lError;

    private int productID = -1;

    public Label headline;

    SQLiteDBManager dbManager;
    private Project project;

    public void initialize(){
        dbManager = new SQLiteDBManager();
        TreeItem<Category> treeItem = new TreeItem<>();
        fillCategoryTree(null, treeItem);
        lCategory.setRoot(treeItem);
        lCategory.setShowRoot(false);
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
    public void save() {
        if(lCategory.getSelectionModel().getSelectedItem() == null){
            showError("Bitte wählen Sie eine Kategorie.");
            return;
        }
        try {
            dbManager.store(new Product(productID, tfName.getText(), tfUnit.getText(), Float.valueOf(tfPrice.getText().replaceAll(",", ".")), Integer.parseInt(tfTime.getText()), ((TreeItem<Category>) lCategory.getSelectionModel().getSelectedItem()).getValue()));
            close();
        } catch (IllegalArgumentException e){
            showError("Die Arbeitszeit muss in folgendem Format sein: STUNDEN:MINTUTEN:SEKUNDEN");
        } catch (SQLException e) {
            MainController.showErrorDialog(e);
            close();
        }
    }

    private void showError(String ErrMsg) {
        lError.setText(ErrMsg);

    }

    @FXML
    public void close(){
        Stage stage = (Stage) lCategory.getScene().getWindow();
        stage.close();



    }

    private void setTreeItem(TreeItem treeItem, Category c){
        for(Object o : treeItem.getChildren()){
            setTreeItem((TreeItem) o,c);
            if(((TreeItem) o).getValue().equals(c)){
                lCategory.getSelectionModel().select(o);
            }
        }
    }

    public void setProduct(Product product){
        headline.setText("Produkt Bearbeiten:");
        tfName.setText(product.getName());
        tfPrice.setText(product.getPricePerUnit()+"");
        tfUnit.setText(product.getUnit());
        tfTime.setText(product.getMinutes()+"");

        if(product.getCategory() != null)setTreeItem(lCategory.getRoot(), product.getCategory());
        System.out.println(product.getCategory().toString());
        productID = product.getId();
    }


    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public void processKeyboard(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            save();
        } else if(event.getCode() == KeyCode.ESCAPE) {
            close();
        }
    }
}
