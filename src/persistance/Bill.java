package persistance;

import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: olfad
 * Date: 19.11.13
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
public class Bill {
    private int number;
    private Customer customer;
    private Date date;
    private String text;
    private ArrayList<Project> projects;
    private String type;

    public Bill(int number, Customer customer, String type, Date date, String text) {
        this.number = number;
        this.customer = customer;
        this.date = date;
        this.text = text;
        this.type = type;
        projects = new ArrayList<Project>();

    }

    public Bill() {

    }


    public void addProjects(Project project) {
        projects.add(project);
    }

    public String getNumber() {
               String reVal = date.toString() + number;
        reVal = reVal.replaceAll("-", "");
        return reVal;
    }

    public int getNumberReel(){
        return number;
    }


    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return getNumber();
    }

    public float getTotal() throws NoSuchDBEntryException, SQLException {
        float retVal = 0;
        SQLiteDBManager sqLiteDBManager = new SQLiteDBManager();

        for(Project project : sqLiteDBManager.getProjectsByBill(this)) {
            retVal +=  project.getPrice();
        }
        return retVal;
    }

    public int getTotalMinutes() throws NoSuchDBEntryException, SQLException {
        int retVal = 0;
        SQLiteDBManager sqLiteDBManager = new SQLiteDBManager();

        for(Project project : sqLiteDBManager.getProjectsByBill(this)) {
            retVal  +=  project.getMinutes();
        }
        return retVal;

    }
}
