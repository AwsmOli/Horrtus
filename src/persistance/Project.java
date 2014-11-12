package persistance;


import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: olfad
 * Date: 19.11.13
 * Time: 11:24
 * To change this template use File | Settings | File Templates.
 */
public class Project {
    private int id;
    private String name;



    private Bill bill;
    private String description;
    private ArrayList<Position> positions;
    public String price;

    public float getPrice() throws NoSuchDBEntryException, SQLException {
        float retVal = 0;
        SQLiteDBManager sqLiteDBManager = new SQLiteDBManager();

        for(Position p : sqLiteDBManager.getPositionByProject(this)) {
            retVal += p.getPrice();
        }
        return retVal;
    }

    public Project(int id,String name, Bill bill, String description) {
        this.id = id;
        this.name = name;
        this.bill = bill== null? new Bill():bill;
        this.description = description;
        positions = new ArrayList<>();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }

    public void setPositions(ArrayList<Position> positions) {
        this.positions = positions;
    }



    public int getMinutes() throws NoSuchDBEntryException, SQLException {
        int ratVal = 0;
        SQLiteDBManager sqLiteDBManager = new SQLiteDBManager();
        for(Position p : sqLiteDBManager.getPositionByProject(this)  ){
            ratVal +=  p.getProduct().getMinutes() * p.getAmount();
        }
        return ratVal;
    }
}
