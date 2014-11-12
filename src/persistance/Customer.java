package persistance;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: awli
 * Date: 29.10.13
 * Time: 08:44
 * To change this template use File | Settings | File Templates.
 */
public class Customer {
    private int number;
    private String firstname;
    private String lastname;
    private String company;
    private String title;
    private String addressLine;
    private String addressLine2;
    private String zip;
    private String city;
    private String email;
    private String fax;
    private String phone;

    public Customer(int number, String firstname, String lastname, String company, String title, String addressLine,
                    String addressLine2, String zip, String city, String email, String fax, String phone) {
        this.number = number;
        this.firstname = firstname;
        this.lastname = lastname;
        this.company = company;
        this.title = title;
        this.addressLine = addressLine;
        this.addressLine2 = addressLine2;
        this.zip = zip;
        this.city = city;
        this.email = email;
        this.fax = fax;
        this.phone = phone;
    }

    public int getNumber() {

        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSalutation () {
        String retVal = "";

        if(title != null){
            retVal += title;
        }
        if(firstname != null){
           retVal += " " + firstname;
        }
        if(lastname != null){
            retVal += " " + lastname;
        } else {
            return null;
        }
        return retVal;
    }

    @Override
    public String toString() {
        String retVal = "";

        if(firstname != null && !firstname.isEmpty()){
            retVal += firstname;
        }
        if(lastname != null && !lastname.isEmpty()){
            retVal += " " + lastname;
        } else if (company != null && !company.isEmpty()) {
            return company;
        }
        return retVal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (number != customer.number) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return number;
    }
}
