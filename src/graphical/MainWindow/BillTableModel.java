package graphical.MainWindow;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import persistance.Position;
import persistance.Project;

/**
 * Created by olfad on 11.04.14.
 */
public class BillTableModel {
    private final SimpleStringProperty projectName;
    private final SimpleStringProperty productName;
    private final SimpleStringProperty amount;
    private final SimpleStringProperty singlePrice;
    private final SimpleStringProperty price;
    private final SimpleObjectProperty<Button> button;

    public BillTableModel(Project project, Position position){
        if(project != null) {
            projectName = new SimpleStringProperty(project.getName());
            Button addbutton = new Button("+");
            addbutton.setId((project != null ? project.hashCode() : 0) + "");
            button = new SimpleObjectProperty<>();

        } else {
            projectName = new SimpleStringProperty("");
            button = new SimpleObjectProperty<>(null);
        }

        if(position != null){
            productName = new SimpleStringProperty(position.getProduct().getName());
            amount = new SimpleStringProperty(position.getAmount()+" "+position.getProduct().getUnit());
            singlePrice = new SimpleStringProperty((position.getPrice()/position.getAmount())+"€");
            price = new SimpleStringProperty(position.getPrice()+"€");
        } else {
            productName = new SimpleStringProperty("");
            amount = new SimpleStringProperty("");
            singlePrice = new SimpleStringProperty("");
            price = new SimpleStringProperty("");
        }

    }

    public String getProjectName() {
        return projectName.get();
    }

    public String getProductName() {
        return productName.get();
    }

    public String getAmount() {
        return amount.get();
    }

    public String getSinglePrice() {
        return singlePrice.get();
    }

    public String getPrice() {
        return price.get();
    }

    public Button getButton() {
        return button.get();
    }

    public void setAmount(String newValue) { amount.set(newValue);}


}
