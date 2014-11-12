package graphical.MainWindow;


import Util.Writer;
import graphical.ErrorDialog.ErrorDialogController;
import graphical.creation.BillCreation.BillCreationController;
import graphical.creation.CustomerCreation.CustomerCreationController;
import graphical.creation.PossitionCreation.PossitionCreationController;
import graphical.creation.ProductCreation.ProductCreationController;
import graphical.creation.ProjectCreation.ProjectCreationController;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import persistance.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MainController {

    //Menu Bar
    public MenuBar menuBar;


    //Bill Controls
    public TextField billDate;

    public TextField costumerName;
    public TextField customerLastName;
    public TextField customerTitle;
    public TextField costumerAddress;
    public TextField customerZipCode;
    public TextField costumerCity;

    public TextField title;

    public Label ErrLabel;

    public TextArea billText;

    public TreeTableView<Position> positionTreeTableView;
    public TreeTableView<Product> productTreeTableView;

    //public Button addProductButton;
    //public Button addCustomerButton;

    //Navigation
    public TreeView<?> navigation;

    public Label lblTotal;
    public Label lblPrice;
    public Label lblUst;
    public Label lblTime;

    //Class Variables
    private SQLiteDBManager dbManager;
    private Bill selectedBill;
    private SimpleDateFormat snf = new SimpleDateFormat("d.M.y");


    public void initialize(){
        dbManager = new SQLiteDBManager();

        dbManager.createDB();
        fillCustomerTree();
        addTreeListener();
        fillproductTreeTableView();
    }



    private void fillCategoryTree(Category c, TreeItem<Product> rootItem) {

        rootItem.setExpanded(true);

        try {
            for(Category SubC : dbManager.getCategoriesbyCategory(c)) {
                Product tmp = new Product(-1, SubC.getName(), "", -1, 0, SubC);
                TreeItem<Product> categoryItem = new TreeItem<>(tmp);
                fillCategoryTree(SubC, categoryItem);
                rootItem.getChildren().add(categoryItem);
                for(Product p : dbManager.getProductsByCategory(SubC)){
                    categoryItem.getChildren().add(new TreeItem<>(p));
                }
            }
        } catch (SQLException | NoSuchDBEntryException e) {
            showErrorDialog(e);
        }

    }
    
    private void fillproductTreeTableView()  {


        productTreeTableView.addEventHandler(javafx.scene.input.KeyEvent.KEY_RELEASED, event -> {
            if(event.getCode() == (KeyCode.DELETE)) {
                if (productTreeTableView.getSelectionModel().getSelectedItem() == null) return;

                    deleteProductOrCategory(productTreeTableView.getSelectionModel().getSelectedItem().getValue());
                }


        });

        productTreeTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, event-> {
            if(productTreeTableView.getSelectionModel().getSelectedItem() == null) return;
            if(event.getClickCount() ==2){
                if(productTreeTableView.getSelectionModel().getSelectedItem().getValue().getId() != -1){
                    newProduct((productTreeTableView.getSelectionModel().getSelectedItem().getValue()));
                }
            }
        });

        
        TreeTableColumn<Product, String> produktCol =
                new TreeTableColumn<>("Produkt");
        produktCol.setPrefWidth(300);
        produktCol.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Product, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getName())
        );

        TreeTableColumn<Product, String> timeCol =
                new TreeTableColumn<>("Dauer");
        timeCol.setPrefWidth(60);
        timeCol.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Product, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getMinutes() == 0? "" : minutesToTimeString(param.getValue().getValue().getMinutes()))
        );


        TreeTableColumn<Product, String> PriceCol =
                new TreeTableColumn<>("Preis");
        PriceCol.setPrefWidth(60);
        PriceCol.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Product, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getPricePerUnit() == -1? "" : "€ " +Float.toString(param.getValue().getValue().getPricePerUnit()))
        );


        productTreeTableView.getColumns().setAll(produktCol,timeCol, PriceCol);
        TreeItem<Product> rootItem = new TreeItem<>();
        productTreeTableView.setRoot(rootItem);
        productTreeTableView.setShowRoot(false);

        fillCategoryTree(null, productTreeTableView.getRoot());
    }

    private void deleteProductOrCategory(Product product) {
        try {
            if(product.getId() == - 1) dbManager.delete(product.getCategory());
            dbManager.delete(product);
        } catch (SQLException e) {
            showErrorDialog(e);
        }

        fillproductTreeTableView();
    }

    private void addTreeListener() {
        navigation.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if(newValue == null) return;
                TreeItem<Bill> selectedItem = (TreeItem<Bill>) newValue;
                selectedBill = selectedItem.getValue();
                loadBill(selectedBill);
            } catch (ClassCastException ex) {
                //User clicked on a TreeNode that's not a Bill
            } catch (Throwable e) {

               showErrorDialog(e);
            }
        });

        navigation.addEventHandler(javafx.scene.input.KeyEvent.KEY_RELEASED, event -> {
            if(event.getCode() == (KeyCode.DELETE)) {
                if (navigation.getSelectionModel().getSelectedItem() == null) return;
                deleteCustomerOrBill(navigation.getSelectionModel().getSelectedItem().getValue());
            }
        });

        navigation.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getClickCount() == 2){
                TreeItem<?> selectedItem = navigation.getSelectionModel().getSelectedItem();
                if(selectedItem.getValue() instanceof Bill)       {
                    newBill((Bill) selectedItem.getValue());
                } else if( selectedItem.getValue() instanceof Customer){
                    newCustomer((Customer) selectedItem.getValue());
                }
            }
        });
        fillCustomerTree();
    }

    private void deleteCustomerOrBill(Object value) {
        try {
            if(value instanceof Customer) {
                dbManager.delete((Customer) value);
            } else if( value instanceof Bill) {
                dbManager.delete((Bill) value);
            }
        } catch (SQLException e) {
            showErrorDialog(e);
        }
    }

    private void loadBill(Bill b) throws NoSuchDBEntryException, SQLException {
        billDate.setText(snf.format(b.getDate()));

        costumerName.setText(b.getCustomer().getFirstname());
        customerLastName.setText(b.getCustomer().getLastname());
        customerTitle.setText(b.getCustomer().getTitle());
        costumerAddress.setText(b.getCustomer().getAddressLine());
        costumerCity.setText(b.getCustomer().getCity());
        customerZipCode.setText(b.getCustomer().getZip());

        title.setText(b.getType() + " #" + b.getNumber());

        showPositionTable(b);



        billText.setText(b.getText());

    }

    private void deletePosition(Position p) {
        try {
            if(p.getId() == -1) {
                dbManager.delete(p.getProject());
            }
            dbManager.delete(p);
            showPositionTable(selectedBill);
        } catch (SQLException | NoSuchDBEntryException e) {
            showErrorDialog(e);
        }
    }

    private void showPositionTable(Bill b) throws SQLException, NoSuchDBEntryException {


        TreeItem<Position> rootItem = new TreeItem<>();
        positionTreeTableView.setRoot(rootItem);
        positionTreeTableView.setShowRoot(false);

        positionTreeTableView.addEventHandler(javafx.scene.input.KeyEvent.KEY_RELEASED, event -> {

            if(event.getCode() == (KeyCode.DELETE)) {
                if (positionTreeTableView.getSelectionModel().getSelectedItem() == null) return;
                if (positionTreeTableView.getSelectionModel().getSelectedItem().getValue().getProduct().getName().startsWith("->")) {
                    ErrLabel.setText("Du kannst das nicht löschen!");
                } else {
                    deletePosition(positionTreeTableView.getSelectionModel().getSelectedItem().getValue());
                }
            }

        });


        positionTreeTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(positionTreeTableView.getSelectionModel().getSelectedItem() == null) return;
            if(event.getClickCount() == 2){
                Position value = positionTreeTableView.getSelectionModel().getSelectedItem().getValue();
                if(value.getProduct().getName().startsWith("-> Neues Projekt..."))    {
                    newProject(null);
                } else if(value.getProduct().getName().startsWith("-> Neue Position...")) {
                    newPossition(null,positionTreeTableView.getSelectionModel().getSelectedItem().getParent().getValue().getProject());
                } else {
                    if(value.getId()==-1){
                        newProject(value.getProject());
                    } else {
                        newPossition(value, value.getProject());
                    }
                }
            }
        });
        ArrayList<Project> projectArrayList = dbManager.getProjectsByBill(b);
        for(Project p : projectArrayList)       {
            TreeItem<Position> projectItem = new TreeItem<>(new Position(-1,-1,-1,new Product(-1,p.getName(),"",-1,p.getMinutes(),null),p));
            positionTreeTableView.getRoot().getChildren().add(projectItem);
            TreeItem<Position> descriptionItem = new TreeItem<>(new Position(-1,-1,-1,new Product(-1,p.getDescription(),"",-1,0,null),p));
            projectItem.setExpanded(true);

            projectItem.getChildren().add(descriptionItem);
            for(Position pos : dbManager.getPositionByProject(p)){
                projectItem.getChildren().add(new TreeItem<>(pos));
            }
            TreeItem<Position> newPositionItem =  new TreeItem<>(new Position(-1,-1,-1,new Product(-1,"-> Neue Position...","",-1,0,null),p));


            projectItem.getChildren().add(newPositionItem);

        }
        TreeItem<Position> newPorjectItem = new TreeItem<>(new Position(-1,-1,-1,new Product(-1,"-> Neues Projekt...","",-1,0,null),null));

        positionTreeTableView.getRoot().getChildren().add(newPorjectItem);

        TreeTableColumn<Position, String> countCol =
                new TreeTableColumn<>("Anzahl");
        countCol.setPrefWidth(60);
        countCol.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Position, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getAmount()==-1 ? "" : Integer.toString(param.getValue().getValue().getAmount()) + " " + param.getValue().getValue().getProduct().getUnit())
        );

        TreeTableColumn<Position, String> produktCol =
                new TreeTableColumn<>("Produkt");
        produktCol.setPrefWidth(300);
        produktCol.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Position, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getProduct().getName())
        );


        TreeTableColumn<Position, String> PriceCol =
                new TreeTableColumn<>("Preis");
        PriceCol.setPrefWidth(60);
        PriceCol.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Position, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getPrice() == -1? "" : "€ " +Float.toString(param.getValue().getValue().getPrice()))
        );

        TreeTableColumn<Position, String> timeCol =
                new TreeTableColumn<>("Dauer");
        timeCol.setPrefWidth(60);
        timeCol.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Position, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getProduct().getMinutes() == 0? "" : minutesToTimeString(param.getValue().getValue().getProduct().getMinutes() * param.getValue().getValue().getAmount()))
        );

        positionTreeTableView.getColumns().setAll(produktCol,countCol, PriceCol, timeCol);

        calcCosts(b);

    }

    private void calcCosts(Bill b) throws NoSuchDBEntryException, SQLException {
        lblPrice.setText("€ "+Math.round(b.getTotal()));
        lblUst.setText("€ "+Math.round(b.getTotal()*0.2));
        lblTotal.setText("€ "+Math.round(b.getTotal()*1.2));
        lblTime.setText(minutesToTimeString(b.getTotalMinutes()));
    }

    private void fillCustomerTree(){
        TreeItem selectedItem = navigation.getSelectionModel().getSelectedItem();

        try {
            ArrayList<Customer> customers = dbManager.getAllCustomers();

            TreeItem rootItem = new TreeItem<> ("");
            rootItem.setExpanded(true);
            for(Customer c : customers){
                TreeItem<Object> customerItem = new TreeItem<>(c);
                rootItem.getChildren().add(customerItem);
                customerItem.setExpanded(true);
                ArrayList<Bill> bills =  dbManager.getBillsPerCustomer(c);
                for (Bill b : bills) {
                    TreeItem<Object> item = new TreeItem<> (b);
                    customerItem.getChildren().add(item);
                }
            }
            navigation.setRoot(rootItem);
            navigation.setShowRoot(false);

            navigation.getSelectionModel().selectFirst();
            if(selectedItem != null) navigation.getSelectionModel().select(selectedItem);
        } catch (SQLException | NoSuchDBEntryException e) {
            showErrorDialog(e);
        }


    }

    public static  void showErrorDialog(Throwable t) {
        FXMLLoader loader = new FXMLLoader(Object.class.getResource("/graphical/ErrorDialog/ErrorDialog.fxml"));
        Parent root = null;
        try {
            root =loader.load();
        } catch (IOException e) {
         e.printStackTrace();
        }

        Stage stage = new Stage();
        stage.setTitle("Fehlermeldung");
        stage.setResizable(false);
        Scene scene = new Scene(root);

        ErrorDialogController edc =  loader.getController();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);

        edc.getErrMsgTextArea().setText(t.getMessage());
        edc.getSupportInfoTextArea().setText(sw.toString());

        stage.setScene(scene);

        stage.centerOnScreen();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.show();
    }

    public void makePDF(){
        try {
            if(selectedBill != null){
                Writer.writeBill(selectedBill);
            } else {
                ErrLabel.setText("Bitte wählen Sie die Rechnung aus die sie Drucken möchten.");
            }
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }

    public void newBill(){
        newBill(null);
    }

    public void newBill(Bill bill){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphical/creation/BillCreation/BillCreationDialog.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            showErrorDialog(e);
        }

        Stage stage = new Stage();
        stage.setTitle("Neuer Kunde");
        stage.setResizable(false);
        Scene scene = new Scene(root);

        BillCreationController controller =  loader.getController();
        if(bill != null) controller.setBill(bill);

        stage.setScene(scene);

        stage.centerOnScreen();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.showAndWait();
        fillCustomerTree();
    }

    public void newCategory(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphical/creation/CategoryCreation/CategoryCreationDialog.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            showErrorDialog(e);
        }

        Stage stage = new Stage();
        stage.setTitle("Neue Kategorie");
        stage.setResizable(false);
        Scene scene = new Scene(root);

        //CategoryCreationController controller =  loader.getController();


        stage.setScene(scene);

        stage.centerOnScreen();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.showAndWait();
        fillproductTreeTableView();
    }

    public void newCustomer(){
        newCustomer(null);
    }

    public void newCustomer(Customer c){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphical/creation/CustomerCreation/CustomerCreationDialog.fxml"));
        Parent root = null;

        try {
            root = loader.load();
        } catch (IOException e) {
            showErrorDialog(e);
        }


        Stage stage = new Stage();
        stage.setTitle("Neuer Kunde");
        stage.setResizable(false);
        Scene scene = new Scene(root);

        CustomerCreationController controller =  loader.getController();
        if(c != null) controller.setCustomer(c);

        stage.setScene(scene);

        stage.centerOnScreen();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.showAndWait();
        fillCustomerTree();
    }

    public void newProduct(){
         newProduct(null);
    }

    public void newProduct(Product p){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphical/creation/ProductCreation/ProductCreationDialog.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            showErrorDialog(e);
        }

        Stage stage = new Stage();
        stage.setTitle("Neues Produkt");
        stage.setResizable(false);
        Scene scene = new Scene(root);

        ProductCreationController controller =  loader.getController();
        if(p != null) controller.setProduct(p);

        stage.setScene(scene);

        stage.centerOnScreen();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.showAndWait();
        fillproductTreeTableView();
    }

    public void newProject(){
        newProject(null);
    }

    public void newProject(Project project){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphical/creation/ProjectCreation/ProjectCreationDialog.fxml"));
        Parent root = null;
        try {
            root =  loader.load();
        } catch (IOException e) {
            showErrorDialog(e);
        }

        Stage stage = new Stage();
        stage.setTitle("Neues Projekt");
        stage.setResizable(false);
        Scene scene = new Scene(root);

        ProjectCreationController controller =  loader.getController();
        controller.setBill(selectedBill);
        if(project !=null) controller.setProject(project);

        stage.setScene(scene);

        stage.centerOnScreen();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.showAndWait();
        try {
            showPositionTable(selectedBill);
        } catch (SQLException | NoSuchDBEntryException e) {
            showErrorDialog(e);
        }
    }

    @FXML
    public void newPossition(Position position, Project project){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphical/creation/PossitionCreation/PossitionCreationDialog.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            showErrorDialog(e);
        }

        Stage stage = new Stage();
        stage.setTitle("Neue Possition");
        stage.setResizable(false);
        Scene scene = new Scene(root);

        PossitionCreationController controller =  loader.getController();
        controller.setProject(project);
        if(position != null) controller.setPosition(position);
        stage.setScene(scene);

        stage.centerOnScreen();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.showAndWait();
        try {
            showPositionTable(selectedBill);
        } catch (Throwable e) {
            showErrorDialog(e);
        }
    }

    public void newPossition() {
        if(positionTreeTableView.getSelectionModel().getSelectedItem().getValue().getProject()!=null)
            newPossition(positionTreeTableView.getSelectionModel().getSelectedItem().getValue(), positionTreeTableView.getSelectionModel().getSelectedItem().getValue().getProject());
    }

    public String minutesToTimeString(int min){

        min = Math.abs(min);
        String stunden = ""+(min / 60);
        String minuten = ""+min%60;

        if(stunden.length() == 1)
            stunden = "0" + stunden;
        if(minuten.length() == 1)
            minuten = "0" + minuten;
        return stunden + ":" +minuten;
    }
}
