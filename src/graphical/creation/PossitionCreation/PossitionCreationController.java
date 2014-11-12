package graphical.creation.PossitionCreation;

import graphical.MainWindow.MainController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import persistance.*;

import java.sql.SQLException;

/**
 * Created by olfad on 10.03.14.
 */
public class PossitionCreationController {

    public TreeView tProduct;
    public TextField tfPricePerUnit;
    public TextField tfAmount;
    public TextField tfPrice;

    public Label lError;
    public Button bSubmit;
    public Button bCancel;
    public Label lUnit;

    private Project project;

    private Position position;

    private SQLiteDBManager dbManager;

    public Label headline;


    public void initialize(){
        dbManager = new SQLiteDBManager();

        try {
            TreeItem<Category> treeItem = new TreeItem<>();
            fillProductTree(null, treeItem);

            tProduct.setRoot(treeItem);
            tProduct.setShowRoot(false);

            addTreeListener();
            addTextFieldListener();


        } catch (NoSuchDBEntryException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addTreeListener() {
        tProduct.getSelectionModel().selectedItemProperty().addListener( new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue,
                                Object newValue) {
                System.out.println("TreeValue Changed: " +oldValue +" to " + newValue);

                try {

                    TreeItem<Product> selectedItem = (TreeItem<Product>) newValue;
                    tfPricePerUnit.setText(selectedItem.getValue().getPricePerUnit() + "");
                    tfPrice.setText(selectedItem.getValue().getPricePerUnit() + "");
                    tfAmount.setText("1");
                    lUnit.setText(selectedItem.getValue().getUnit());

                } catch (ClassCastException ex) {
                    //User clicked on a Node that's not a Product
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }


            }

        });
    }

    private void addTextFieldListener() {

        ChangeListener<String> tfPricePerUnitListener;
        ChangeListener<String> tfAmountListener;
        ChangeListener<String> tfPriceListener;


        tfPriceListener = gettfPriceChangeListener();
        tfPricePerUnitListener = getPricePerUnitChangeListener(tfPriceListener);
        tfAmountListener = getAmountChangeListener(tfPriceListener);

        tfPrice.textProperty().addListener(tfPriceListener);
        tfPricePerUnit.textProperty().addListener(tfPricePerUnitListener);
        tfAmount.textProperty().addListener(tfAmountListener);
    }

    private ChangeListener<String> getAmountChangeListener(final ChangeListener<String> tfPriceListener) {
        ChangeListener<String> tfAmountListener;
        tfAmountListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                try {
                    int newAmount = Integer.parseInt(s2);
                    float price = Float.valueOf(tfPricePerUnit.getText());
                    tfPrice.textProperty().removeListener(tfPriceListener);
                    tfPrice.setText(Float.toString(newAmount*price));
                    tfPrice.textProperty().addListener(tfPriceListener);
                } catch (NullPointerException ex) {
                    //amount is empty
                } catch (NumberFormatException ex) {
                    lError.setText("Mänge muss eine Zahl sein!");
                }
            }
        };
        return tfAmountListener;
    }

    private ChangeListener<String> getPricePerUnitChangeListener(final ChangeListener<String> tfPriceListener) {
        ChangeListener<String> tfPricePerUnitListener;
        tfPricePerUnitListener =  new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                try {
                    float newPrice =   Float.valueOf(s2);
                    int amount = Integer.parseInt(tfAmount.getText());

                    tfPrice.textProperty().removeListener(tfPriceListener);
                    tfPrice.setText(Float.toString(newPrice*amount));
                    tfPrice.textProperty().addListener(tfPriceListener);
                } catch (NullPointerException ex) {
                    //amount is empty
                } catch (NumberFormatException ex) {
                    lError.setText("Mänge muss eine Zahl sein!");
                }
            }
        };
        return tfPricePerUnitListener;
    }

    private ChangeListener<String> gettfPriceChangeListener() {
        ChangeListener<String> tfPriceListener;
        tfPriceListener = new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                try {
                    float newPrice = Float.valueOf(s2);
                    int amount = Integer.parseInt(tfAmount.getText());
                    tfPricePerUnit.setText(Float.toString(newPrice / amount));
                } catch (NullPointerException ex) {
                    //amount is empty
                } catch (NumberFormatException ex) {
                    lError.setText("Mänge muss eine Zahl sein!");
                }
            }
        };
        return tfPriceListener;
    }

    private void fillProductTree(Category c, TreeItem rootItem) throws NoSuchDBEntryException, SQLException {

        rootItem.setExpanded(true);
        for(Category SubC : dbManager.getCategoriesbyCategory(c)){
            TreeItem categoryItem = new TreeItem(SubC);
            for(Product product :dbManager.getProductsByCategory(SubC)){
                categoryItem.getChildren().add(new TreeItem(product));
            }
            fillProductTree(SubC, categoryItem);
            rootItem.getChildren().add(categoryItem);
        }


    }

    @FXML
    private void close(){
        Stage stage = (Stage) bCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void submitButtonAction() throws SQLException {
        if(checkreqiredData()){
            save();
            close();
        } else {
            showError();
        }
    }

    private void save()  {
        if(position != null){
            try {
                position.setProduct(((TreeItem<Product>) tProduct.getSelectionModel().getSelectedItem()).getValue());
            } catch (ClassCastException e) {
                //Product not changed.
            }
            position.setPrice( Float.valueOf(tfPrice.getText().replaceAll(",", ".")));
            position.setAmount(Integer.parseInt(tfAmount.getText()));
            try {
                dbManager.store(position);
            } catch (SQLException e) {
                MainController.showErrorDialog(e);
                close();
            }
            return;
        }
        Product product = ((TreeItem<Product>) tProduct.getSelectionModel().getSelectedItem()).getValue();
        float price = Float.valueOf(tfPrice.getText().replaceAll(",", "."));
        int amount = Integer.parseInt(tfAmount.getText());

        try {
            dbManager.store(new Position(-1, price, amount, product, project));
        } catch (SQLException e) {
            MainController.showErrorDialog(e);
            close();
        }

    }

    public void setProject(Project project){
        this.project = project;
    }

    private void showError() {
        lError.setText("Bitte füllen Sie die rot markierten Felder aus.");
        tfAmount.setStyle("fx-border-color: red;");
    }

    private boolean checkreqiredData() {
        return true;
    }


    private void setTreeItem(TreeItem treeItem, Product p){
        for(Object o : treeItem.getChildren()){
            setTreeItem((TreeItem) o,p);
            if(((TreeItem) o).getValue().equals(p)){
                tProduct.getSelectionModel().select(o);
            }
        }
    }


    public void setPosition(Position position) {

        headline.setText("Position Bearbeiten:");
        this.position = position;

        setTreeItem(tProduct.getRoot(), position.getProduct());
        tfPricePerUnit.setText(position.getPricePerUnit() + "");
        tfAmount.setText(position.getAmount()+"");
        tfPrice.setText(position.getPrice()+"");
    }
    public void processKeyboard(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            save();
        } else if(event.getCode() == KeyCode.ESCAPE) {
            close();
        }
    }
}
