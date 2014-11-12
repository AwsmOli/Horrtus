package persistance;


/**
 * Created with IntelliJ IDEA.
 * User: olfad
 * Date: 19.11.13
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
public class Category {
    private String Name;
    private Category parent;

    public Category(String name, Category parent) {
        Name = name;
        this.parent = parent;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return  Name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (!Name.equals(category.Name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Name.hashCode();
    }
}
