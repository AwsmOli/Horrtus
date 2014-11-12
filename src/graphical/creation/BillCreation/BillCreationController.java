package graphical.creation.BillCreation;

import Util.Properties;
import graphical.MainWindow.MainController;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import persistance.Bill;
import persistance.Customer;
import persistance.SQLiteDBManager;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by olfad on 24.01.14.
 */
public class BillCreationController {

    public ListView<Customer> list;
    public ComboBox<String> type;
    public TextField date;
    public TextField text;

    public Label lblErr;

    public Button cancel;

    public Button save;

    private SQLiteDBManager dbManager;

    private  SimpleDateFormat snf = new SimpleDateFormat("d.M.y");

    private int billID = -1;

    public Label headline;


    public void initialize(){
        dbManager = new SQLiteDBManager();
        ObservableList<Customer> observableList = null;
        try {
            observableList = FXCollections.observableArrayList(dbManager.getAllCustomers());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        SimpleListProperty<Customer> items = new SimpleListProperty<>(observableList);
        list.setItems(items);


        type.setItems(new SimpleListProperty<> (FXCollections.observableArrayList (Properties.get("Titles").split(";"))));

        text.setText(Properties.get("defaultText"));
        date.setText(snf.format(new Date()));



    }
    @FXML
    public void save() {
        if(list.getSelectionModel().getSelectedItem() == null) {
            showError("Wähle einen Kunden aus.");
            return;
        }

        if(type.getSelectionModel().getSelectedItem() == null) {
            showError("Bitte wähle einen Typ aus.");
            return;
        }
        try {
            dbManager.store(new Bill(billID, list.getSelectionModel().getSelectedItem(),type.getSelectionModel().getSelectedItem(), snf.parse(date.getText()),text.getText()));
        } catch (SQLException e) {
            MainController.showErrorDialog(e);
        } catch (ParseException e) {
            lblErr.setText("Bitte gib ein gültiges Datum ein. z.B. 31.12.2000");
        }

        close();
    }

    private void showError(String msg) {

        lblErr.setText(msg);
    }

    @FXML
    public void close(){
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();

    }
    @FXML
    public void newCustomer(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphical/creation/CustomerCreation/CustomerCreationDialog.fxml"));
        Parent root = null;
        try {
            root =  loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage stage = new Stage();
        stage.setTitle("Neuer Kunde");
        stage.setResizable(false);
        Scene scene = new Scene(root);

        stage.setScene(scene);

        stage.centerOnScreen();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.showAndWait();
        initialize();
    }
    public void setBill(Bill bill){


        headline.setText("Rechnung Bearbeiten:");
        list.getSelectionModel().select(bill.getCustomer());
        type.getSelectionModel().select(bill.getType());
        date.setText(snf.format(bill.getDate()));
        text.setText(bill.getText());


        billID = bill.getNumberReel();
    }

    public void processKeyboard(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            save();
        } else if(event.getCode() == KeyCode.ESCAPE) {
            close();
        }
    }
}
