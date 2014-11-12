package persistance;


/**
 * Created with IntelliJ IDEA.
 * User: olfad
 * Date: 19.11.13
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */
public class Position {
    private int id;
    private float price;
    private int amount;
    private Product product;
    private Project project;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Position(int id, float price, int amount, Product product, Project project) {

        this.id = id;
        this.price = price;
        this.amount = amount;
        this.product = product;
        this.project = project;
    }

    public float getPricePerUnit(){
        return price / amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (id != position.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
