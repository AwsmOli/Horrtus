package graphical.creation.CustomerCreation;

import graphical.MainWindow.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import persistance.Customer;
import persistance.SQLiteDBManager;

import java.sql.SQLException;

/**
 * Created by olfad on 27.01.14.
 */
public class CustomerCreationController {


    public TextField tfTitle;
    public TextField tfFirstName;
    public TextField tfLastName;
    public TextField tfCompany;
    public TextField tfAddress1;
    public TextField tfAddress2;
    public TextField tfZipCode;
    public TextField tfCity;
    public TextField tfEmail;
    public TextField tfFax;
    public TextField tfPhone;

    public Button bSubmit;
    public Button bCancel;

    public Label lError;
    private int customerID = -1;

    public Label headline;

    public void initialize(){

    }

    @FXML
    private void close(){
        Stage stage = (Stage) bCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void submitButtonAction() {
        if(checkreqiredData()){
            save();
            close();
        } else {

        }
    }

    private void save(){
        Customer c = new Customer(customerID,tfFirstName.getText().trim(),
                tfLastName.getText().trim(),
                tfCompany.getText().trim(),
                tfTitle.getText().trim(),
                tfAddress1.getText().trim(),
                tfAddress2.getText().trim(),
                tfZipCode.getText().trim(),
                tfCity.getText().trim(),
                tfEmail.getText().trim(),
                tfFax.getText().trim(),
                tfPhone.getText().trim());

        SQLiteDBManager sqLiteDBManager = new SQLiteDBManager();
        try {
            sqLiteDBManager.store(c);
        } catch (SQLException e) {
            this.close();
            MainController.showErrorDialog(e);

        }

    }

    /**
     * Checks that reqired Data is put into the Textfieds.
     * Reqired Data:
     * (Firstname OR Title AND Lastname)OR Company
     * Address 1 OR Address ´2
     * ZIP Code
     * City
     */
    private boolean checkreqiredData() {

        if(tfTitle.getText().trim().isEmpty() && tfFirstName.getText().trim().isEmpty()){
            if(tfCompany.getText().trim().isEmpty()){
                showError("Bitte geben Sie einen Titel oder einen Vornamen an.");
                return false;
            }
        }
        if(tfLastName.getText().trim().isEmpty()){
            if(tfCompany.getText().trim().isEmpty()){
                showError("Bitte geben Sie einen Nachnamen oder eine Firma an.");
                return false;
            }
        }
        if(tfAddress1.getText().trim().isEmpty() && tfAddress2.getText().trim().isEmpty()){
            showError("Eines der Adressfelder muss ausgefüllt sein.");
            return false;
        }
        if(tfZipCode.getText().trim().isEmpty()){
            showError("Geben Sie bitte einen Postleitzahl an.");
            return false;
        }
        if(tfCity.getText().trim().isEmpty()){
            showError("Geben Sie bitte einen Ort an.");
            return false;
        }

        return true;
    }

    private void showError(String msg) {
        lError.setText(msg);
        lError.setVisible(true);
    }

    public void setCustomer(Customer customer){

        headline.setText("Kunde Bearbeiten:");
          tfTitle.setText(customer.getTitle());
          tfFirstName.setText(customer.getFirstname());
          tfLastName.setText(customer.getLastname());
          tfCompany.setText(customer.getCompany());
          tfAddress1.setText(customer.getAddressLine());
          tfAddress2.setText(customer.getAddressLine2());
          tfZipCode.setText(customer.getZip());
          tfCity.setText(customer.getCity());
          tfEmail.setText(customer.getEmail());
          tfFax.setText(customer.getFax());
          tfPhone.setText(customer.getPhone());

        customerID = customer.getNumber();

    }
    public void processKeyboard(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            submitButtonAction();
        } else if(event.getCode() == KeyCode.ESCAPE) {
            close();
        }
    }
}
