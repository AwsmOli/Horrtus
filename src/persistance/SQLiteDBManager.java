package persistance;

/**
 * This Class handles the DB Connection
 * User: olfad
 * Date: 22.11.13
 * Time: 10:46
 */


import java.sql.*;
import java.util.ArrayList;
import java.util.Date;


/**
 * @author Oli
 */
public class SQLiteDBManager {
    Connection cn;
    Statement st;

    private void init() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        cn = DriverManager.getConnection("jdbc:sqlite:./Horrtus.db");
        st = cn.createStatement();
        cn.setAutoCommit(true);
    }

    private void close() {
        try {
            st.close();
            cn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createDB() {
        try {
            init();
            System.out.println("Creating Tables...");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS Positions (" +
                    "Pos_ID INTEGER  PRIMARY KEY AUTOINCREMENT, " +
                    "Pos_Price, " +
                    "Pos_Amount, " +
                    "Pos_Pro_" +
                    "Product, " +
                    "Pos_P_Project)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS Projects (" +
                    "P_ID INTEGER  PRIMARY KEY AUTOINCREMENT, " +
                    "P_Name, " +
                    "P_B_Bill, " +
                    "P_Description)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS Products (" +
                    "Pro_ID INTEGER  PRIMARY KEY AUTOINCREMENT, " +
                    "Pro_Name, Pro_Unit, " +
                    "Pro_PricePerUnit, " +
                    "Pro_Time, "+
                    "Pro_C_Category)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS Categories (" +
                    "C_Name PRIMARY KEY, " +
                    "C_C_ParentCategory)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS Bills (" +
                    "B_Number INTEGER  PRIMARY KEY AUTOINCREMENT, " +
                    "B_C_Customer, " +
                    "B_Type, " +
                    "B_Date, " +
                    "B_Text)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS Customers (" +
                    "C_Number INTEGER  PRIMARY KEY AUTOINCREMENT, " +
                    "C_Firstname, " +
                    "C_Lastname, " +
                    "C_Company, " +
                    "C_Title, " +
                    "C_AdressLine, " +
                    "C_AdressLine2, " +
                    "C_ZIP, " +
                    "C_City, " +
                    "C_EMail, " +
                    "C_Fax, " +
                    "C_Phone)");

            System.out.println("Creating Succsessfull!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public ArrayList<Position> getAllPositions() throws SQLException, NoSuchDBEntryException {
        try {
            init();

            ResultSet resultSet = st.executeQuery("SELECT * FROM Positions");
            ArrayList<Position> retVal = new ArrayList<>();

            while (resultSet.next()) {

                int id = resultSet.getInt("Pos_ID ");
                float price = resultSet.getFloat("Pos_Price");
                int amount = resultSet.getInt("Pos_Amount ");
                Product product = getProductById(resultSet.getInt("Pos_Pro_Product"));
                Project project = getProjectById(resultSet.getInt("Pos_P_Project"));

                Position p = new Position(id, price, amount, product, project);
                retVal.add(p);
            }
            resultSet.close();
            return retVal;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }

    public Position getPositionById(int id) throws SQLException, NoSuchDBEntryException {
        try {
            init();
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM Position WHERE Pos_ID = ?");
            ps.setInt(1, id);

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                float price = resultSet.getFloat("Pos_Price");
                int amount = resultSet.getInt("Pos_Amount ");
                Product product = getProductById(resultSet.getInt("Pos_Pro_Product"));
                Project project = getProjectById(resultSet.getInt("Pos_P_Project"));
                resultSet.close();
                return new Position(id, price, amount, product, project);
            } else {
                resultSet.close();
                throw new NoSuchDBEntryException("No Position with ID: " + id);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }

    public ArrayList<Project> getAllProjects() throws SQLException, NoSuchDBEntryException {
        try {
            init();
            ResultSet resultSet = st.executeQuery("SELECT * FROM Projects");
            ArrayList<Project> retVal = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("P_ID");
                String name = resultSet.getString("P_Name");
                Bill bill = getBillByID(resultSet.getInt("P_B_Bill"));
                String desc = resultSet.getString("P_Description");

                Project p = new Project(id, name, bill, desc);
                retVal.add(p);
            }
            resultSet.close();
            return retVal;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }

    public Project getProjectById(int id) throws SQLException, NoSuchDBEntryException {
        try {
            init();
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM Projects WHERE P_ID = ?");
            ps.setInt(1, id);

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                Bill bill = getBillByID(resultSet.getInt("P_B_Bill"));
                String name = resultSet.getString("P_Name");
                String desc = resultSet.getString("P_Description");
                resultSet.close();
                return new Project(id, name, bill, desc);
            } else {
                resultSet.close();
                throw new NoSuchDBEntryException("No Project with ID: " + id);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }


    public ArrayList<Product> getAllProducts() throws SQLException, NoSuchDBEntryException {
        try {
            init();
            ResultSet resultSet = st.executeQuery("SELECT * FROM Products");
            ArrayList<Product> retVal = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("Pro_ID");
                String name = resultSet.getString("Pro_Name");
                String unit = resultSet.getString("Pro_Unit");
                float price = resultSet.getFloat("Pro_PricePerUnit");
                int time = resultSet.getInt("Pro_Time");
                Category category = getCategoryById(resultSet.getString("Pro_C_Category"));

                Product p = new Product(id, name, unit, price, time, category);
                retVal.add(p);
            }
            resultSet.close();
            return retVal;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }

    public ArrayList<Product> getProductsByCategory(Category c) throws SQLException, NoSuchDBEntryException {
        try {
            init();

            PreparedStatement ps = cn.prepareStatement("SELECT * FROM Products WHERE Pro_C_Category = ?");
            ps.setString(1, c.getName());
            ResultSet resultSet = ps.executeQuery();
            ArrayList<Product> retVal = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("Pro_ID");
                String name = resultSet.getString("Pro_Name");
                String unit = resultSet.getString("Pro_Unit");
                float price = resultSet.getFloat("Pro_PricePerUnit");
                int time = resultSet.getInt("Pro_Time");


                Product p = new Product(id, name, unit, price,time, c);
                retVal.add(p);
            }
            resultSet.close();
            return retVal;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }

    public Product getProductById(int id) throws SQLException, NoSuchDBEntryException {
        try {
            init();
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM Products WHERE Pro_ID = ?");
            ps.setInt(1, id);

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("Pro_Name");
                String unit = resultSet.getString("Pro_Unit");
                float price = resultSet.getFloat("Pro_PricePerUnit");
                int time = resultSet.getInt("Pro_Time");
                 Category category = getCategoryById(resultSet.getString("Pro_C_Category"));
                resultSet.close();
                return new Product(id, name, unit, price, time, category);
            } else {
                resultSet.close();
                throw new NoSuchDBEntryException("No Product with ID: " + id);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }

    public ArrayList<Category> getAllCategories() throws SQLException, NoSuchDBEntryException {
        try {
            init();
            ResultSet resultSet = st.executeQuery("SELECT * FROM Categories");
            ArrayList<Category> retVal = new ArrayList<>();

            while (resultSet.next()) {
                String name = resultSet.getString("C_Name");
                Category parentCategory = getCategoryById(resultSet.getString("C_C_ParentCategory"));

                Category c = new Category(name, parentCategory);
                retVal.add(c);
            }
            resultSet.close();
            return retVal;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }

    public ArrayList<Category> getCategoriesbyCategory(Category c) throws SQLException, NoSuchDBEntryException {
        try {
            init();

            PreparedStatement ps = cn.prepareStatement("SELECT * FROM Categories WHERE C_C_ParentCategory = ?");
            if (c != null)
                ps.setString(1, c.getName());
            else
                ps.setString(1, "");
            ResultSet resultSet = ps.executeQuery();
            ArrayList<Category> retVal = new ArrayList<>();

            while (resultSet.next()) {
                String name = resultSet.getString("C_Name");
                Category cat = new Category(name, c);
                retVal.add(cat);
            }
            resultSet.close();
            return retVal;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }

    public Category getCategoryById(String id) throws NoSuchDBEntryException, SQLException {
        try {
            init();
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM Categories WHERE C_Name = ?");
            ps.setString(1, id);

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("C_Name");
                Category parentCategory;
                try {
                    parentCategory = getCategoryById(resultSet.getString("C_C_ParentCategory"));
                } catch (NoSuchDBEntryException e) {
                    parentCategory = null;
                }
                resultSet.close();
                return new Category(name, parentCategory);
            } else {
                resultSet.close();
                //throw new NoSuchDBEntryException("No Category with ID: " + id);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }

    public ArrayList<Bill> getAllBills() throws SQLException, NoSuchDBEntryException {
        try {
            init();
            ResultSet resultSet = st.executeQuery("SELECT * FROM Bills");
            ArrayList<Bill> retVal = new ArrayList<>();

            while (resultSet.next()) {
                int number = resultSet.getInt("B_Number");
                Customer customer = getCustomerByID(resultSet.getInt("B_C_Customer"));
                Date date = resultSet.getDate("B_Date");
                String text = resultSet.getString("B_Text");
                String type = resultSet.getString("B_Type");

                Bill b = new Bill(number, customer, type, date, text);

                retVal.add(b);
            }
            resultSet.close();
            return retVal;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }

    public ArrayList<Bill> getBillsPerCustomer(Customer c) throws SQLException, NoSuchDBEntryException {
        try {
            init();

            PreparedStatement ps = cn.prepareStatement("SELECT * FROM Bills WHERE B_C_Customer = ?");
            ps.setInt(1, c.getNumber());

            ResultSet resultSet = ps.executeQuery();

            ArrayList<Bill> retVal = new ArrayList<>();

            while (resultSet.next()) {
                int number = resultSet.getInt("B_Number");
                Date date = resultSet.getDate("B_Date");
                String text = resultSet.getString("B_Text");
                String type = resultSet.getString("B_Type");

                Bill b = new Bill(number, c, type, date, text);
                retVal.add(b);
            }
            resultSet.close();
            return retVal;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }

    public Bill getBillByID(int id) throws SQLException, NoSuchDBEntryException {
        try {
            init();
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM Bills WHERE B_Number = ?");
            ps.setInt(1, id);

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                int number = resultSet.getInt("B_Number");
                Customer customer = getCustomerByID(resultSet.getInt("B_C_Customer"));
                String type = resultSet.getString("B_Type");
                Date date = resultSet.getDate("B_Date");
                String text = resultSet.getString("B_Text");

                resultSet.close();
                return new Bill(number, customer, type, date, text);
            } else {
                resultSet.close();
                throw new NoSuchDBEntryException("No Bill with ID: " + id);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }


    public ArrayList<Customer> getAllCustomers() throws SQLException {
        try {
            init();
            ResultSet resultSet = st.executeQuery("SELECT * FROM Customers");
            ArrayList<Customer> retVal = new ArrayList<>();
            while (resultSet.next()) {

                int number = resultSet.getInt("C_Number");
                String firstname = resultSet.getString("C_Firstname");
                String lastname = resultSet.getString("C_Lastname");
                String company = resultSet.getString("C_Company");
                String title = resultSet.getString("C_Title");
                String adressLine = resultSet.getString("C_AdressLine");
                String adressLine2 = resultSet.getString("C_AdressLine2");
                String zip = resultSet.getString("C_ZIP");
                String city = resultSet.getString("C_City");
                String email = resultSet.getString("C_EMail");
                String fax = resultSet.getString("C_Fax");
                String phone = resultSet.getString("C_Phone");

                Customer c = new Customer(number, firstname, lastname, company, title, adressLine, adressLine2, zip, city, email, fax, phone);
                retVal.add(c);
            }
            resultSet.close();
            return retVal;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }

    public Customer getCustomerByID(int id) throws SQLException, NoSuchDBEntryException {
        try {
            init();
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM Customers WHERE C_Number = ?");
            ps.setInt(1, id);

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                int number = resultSet.getInt("C_Number");
                String firstname = resultSet.getString("C_Firstname");
                String lastname = resultSet.getString("C_Lastname");
                String company = resultSet.getString("C_Company");
                String title = resultSet.getString("C_Title");
                String adressLine = resultSet.getString("C_AdressLine");
                String adressLine2 = resultSet.getString("C_AdressLine2");
                String zip = resultSet.getString("C_ZIP");
                String city = resultSet.getString("C_City");
                String email = resultSet.getString("C_EMail");
                String fax = resultSet.getString("C_Fax");
                String phone = resultSet.getString("C_Phone");

                resultSet.close();
                return new Customer(number, firstname, lastname, company, title, adressLine, adressLine2, zip, city, email, fax, phone);
            } else {
                resultSet.close();
                throw new NoSuchDBEntryException("No Customer with ID: " + id);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;

    }


    /**
     * ***********************************************
     * *             STORING METHODS                 *
     * ***********************************************
     */


    public void store(Object item) throws SQLException {
        if (item instanceof Bill) {
            Bill bill = (Bill) item;
            if (bill.getNumberReel() == -1)
                store(bill);
            else
                update(bill);
        } else if (item instanceof Customer) {
            Customer customer = (Customer) item;
            if (customer.getNumber() == -1)
                store((Customer) item);
            else
                update(customer);
        } else if (item instanceof Project) {
            Project project = (Project) item;
            if (project.getId() == -1)
                store(project);
            else
                update(project);
        } else if (item instanceof Position) {
            Position position = (Position) item;
            if (position.getId() == -1)
                store(position);
            else
                update(position);
        } else if (item instanceof Category) {
            Category category = (Category) item;
            store(category);
        } else if (item instanceof Product) {
            Product product = (Product) item;
            if (product.getId() == -1)
                store(product);
            else
                update(product);
        }

    }

    public void delete(Bill bill) throws SQLException{
        try {
            init();
            PreparedStatement ps = cn.prepareStatement("Delete from Bills WHERE B_Number = ?");
            ps.setInt(1, bill.getNumberReel());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
    public void delete(Category category) throws SQLException{
        try {
            init();
            PreparedStatement ps = cn.prepareStatement("Delete from Categories WHERE C_Name = ?");
            ps.setString(1, category.getName());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
    public void delete(Customer customer) throws SQLException{
        try {
            init();
            PreparedStatement ps = cn.prepareStatement("Delete from Customers WHERE C_Number = ?");
            ps.setInt(1, customer.getNumber());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
    public void delete(Position position) throws SQLException{
        try {
            init();
            PreparedStatement ps = cn.prepareStatement("Delete from Positions WHERE Pos_ID = ?");
            ps.setInt(1, position.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
    public void delete(Product product) throws SQLException{
        product.setCategory(null);
        update(product);
    }
    public void delete(Project project) throws SQLException{
        try {
            init();
            PreparedStatement ps = cn.prepareStatement("Delete from Projects WHERE P_ID = ?");
            ps.setInt(1, project.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void update(Product product) throws SQLException {
        try {
            init();
            PreparedStatement ps = cn.prepareStatement("UPDATE Products SET Pro_Name = ?, Pro_Unit = ?, Pro_PricePerUnit = ?, Pro_Time = ?, Pro_C_Category = ? WHERE Pro_ID = ?;");
            ps.setString(1, product.getName());
            ps.setString(2, product.getUnit());
            ps.setFloat(3, product.getPricePerUnit());
            ps.setInt(5, product.getMinutes());
            if(product.getCategory() == null) ps.setString(5, "");
            else ps.setString(5, product.getCategory().getName());

            ps.setInt(6, product.getId());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }

    }

    private void update(Position position) throws SQLException {
        try {
            init();
            PreparedStatement ps = cn.prepareStatement("UPDATE Positions SET Pos_Price = ?, Pos_Amount = ?, Pos_Pro_Product = ?, Pos_P_Project = ? WHERE Pos_ID = ?;");
            ps.setFloat(1, position.getPrice());
            ps.setInt(2, position.getAmount());
            ps.setInt(3, position.getProduct().getId());
            ps.setInt(4, position.getProject().getId());

            ps.setInt(5, position.getId());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();

        }

    }

    private void update(Project project) throws SQLException {
        try {
            init();
            PreparedStatement ps = cn.prepareStatement("UPDATE Projects SET P_Name = ?, P_B_Bill = ?, P_Description = ? WHERE P_ID = ?;");
            ps.setString(1, project.getName());
            ps.setInt(2, project.getBill().getNumberReel());
            ps.setString(3, project.getDescription());

            ps.setInt(4, project.getId());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }

    }

    private void update(Customer customer) throws SQLException {
        try {
            init();
            PreparedStatement ps = cn.prepareStatement("UPDATE Customers SET C_Firstname = ?, " +
                    "C_Lastname= ?, C_Company = ?, C_Title = ?, C_AdressLine = ?, C_AdressLine2 = ?, " +
                    "C_ZIP = ?, C_City = ?, C_EMail = ?, C_Fax = ?, C_Phone = ? WHERE C_Number = ?;");
            ps.setString(1, customer.getFirstname());
            ps.setString(2, customer.getLastname());
            ps.setString(3, customer.getCompany());
            ps.setString(4, customer.getTitle());
            ps.setString(5, customer.getAddressLine());
            ps.setString(6, customer.getAddressLine2());
            ps.setString(7, customer.getZip());
            ps.setString(8, customer.getCity());
            ps.setString(9, customer.getEmail());
            ps.setString(10, customer.getFax());
            ps.setString(11, customer.getPhone());

            ps.setInt(12, customer.getNumber());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }


    }

    private void update(Bill bill) throws SQLException {
        try {
            init();

            PreparedStatement ps = cn.prepareStatement("UPDATE Bills SET B_C_Customer = ?, B_Date = ?, B_Text = ?, B_Type= ? WHERE B_Number = ?;", Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, bill.getCustomer().getNumber());
            ps.setDate(2, new java.sql.Date(bill.getDate().getTime()));
            ps.setString(3, bill.getText());
            ps.setString(4, bill.getType());

            ps.setInt(5, bill.getNumberReel());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }

    }

    private void store(Category item) throws SQLException {
        try {
            init();
            PreparedStatement ps = cn.prepareStatement("INSERT INTO Categories (C_Name, C_C_ParentCategory) VALUES (?, ?)");

            ps.setString(1, item.getName());
            if (item.getParent() == null) {
                ps.setString(2, "");
            } else {
                ps.setString(2, item.getParent().getName());
            }

            ps.executeUpdate();

            ps.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }

    }

    private int store(Position item) throws SQLException {
        try {
            init();
            PreparedStatement ps = cn.prepareStatement("INSERT INTO Positions (Pos_Price, Pos_Amount, Pos_Pro_Product, Pos_P_Project) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            System.out.println("Storing Position");
            ps.setFloat(1, item.getPrice());
            ps.setInt(2, item.getAmount());
            ps.setInt(3, item.getProduct().getId());
            ps.setInt(4, item.getProject().getId());

            int retVal =  ps.executeUpdate();
            ps.close();
            return retVal;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return -1;
    }

    private int store(Project item) throws SQLException {
        try {
            init();

            PreparedStatement ps = cn.prepareStatement("INSERT INTO Projects (P_Name, P_B_Bill, P_Description) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, item.getName());
            ps.setInt(2, item.getBill().getNumberReel());
            ps.setString(3, item.getDescription());

            int retVal = ps.executeUpdate();

            ps.close();
            return retVal;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return -1;
    }

    private int store(Product item) throws SQLException {
        try {
            init();

            PreparedStatement ps = cn.prepareStatement("INSERT INTO Products (Pro_Name, Pro_Unit, Pro_PricePerUnit, Pro_Time, Pro_C_Category) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, item.getName());
            ps.setString(2, item.getUnit());
            ps.setFloat(3, item.getPricePerUnit());
            ps.setInt(4, item.getMinutes());
            ps.setString(5, item.getCategory().getName());

            int retVal =  ps.executeUpdate();

            ps.close();

            return retVal;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return -1;
    }

    private int store(Customer item) throws SQLException {
        try {
            init();

            PreparedStatement ps = cn.prepareStatement("INSERT INTO Customers (C_Firstname, C_Lastname, C_Company, C_Title, C_AdressLine, C_AdressLine2, C_ZIP, C_City, C_EMail, C_Fax, C_Phone) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, item.getFirstname());
            ps.setString(2, item.getLastname());
            ps.setString(3, item.getCompany());
            ps.setString(4, item.getTitle());
            ps.setString(5, item.getAddressLine());
            ps.setString(6, item.getAddressLine2());
            ps.setString(7, item.getZip());
            ps.setString(8, item.getCity());
            ps.setString(9, item.getEmail());
            ps.setString(10, item.getFax());
            ps.setString(11, item.getPhone());

            int retVal = ps.executeUpdate();

            ps.close();
            return retVal;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return -1;
    }

    private int store(Bill item) throws SQLException {
        try {
            init();

            PreparedStatement ps = cn.prepareStatement("INSERT INTO Bills (B_C_Customer, B_Date, B_Text, B_Type) VALUES (?, ?, ? ,?)", Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, item.getCustomer().getNumber());
            ps.setDate(2, new java.sql.Date(item.getDate().getTime()));
            ps.setString(3, item.getText());
            ps.setString(4, item.getType());

            int retVal = ps.executeUpdate();

            ps.close();
            return retVal;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return -1;
    }

    public ArrayList<Position> getPositionByProject(Project p) throws SQLException, NoSuchDBEntryException {
        try {
            init();

            PreparedStatement ps = cn.prepareStatement("SELECT * FROM Positions WHERE Pos_P_Project = ?");
            ps.setInt(1, p.getId());

            ResultSet resultSet = ps.executeQuery();

            ArrayList<Position> retVal = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("Pos_ID");
                float price = resultSet.getFloat("Pos_Price");
                int amount = resultSet.getInt("Pos_Amount");
                Product product = getProductById(resultSet.getInt("Pos_Pro_Product"));
                Project project = getProjectById(resultSet.getInt("Pos_P_Project"));

                retVal.add(new Position(id, price, amount, product, project));

            }
            resultSet.close();
            return retVal;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;

    }

    public ArrayList<Project> getProjectsByBill(Bill b) throws SQLException, NoSuchDBEntryException {
        try {
            init();

            PreparedStatement ps = cn.prepareStatement("SELECT * FROM Projects WHERE P_B_Bill = ?");
            ps.setInt(1, b.getNumberReel());

            ResultSet resultSet = ps.executeQuery();
            ArrayList<Project> retVal = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("P_ID");
                String name = resultSet.getString("P_Name");
                Bill bill = getBillByID(resultSet.getInt("P_B_Bill"));
                String desc = resultSet.getString("P_Description");

                Project p = new Project(id, name, bill, desc);
                retVal.add(p);
            }
            resultSet.close();
            return retVal;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }

}