package foodDeliveryService;

public class city {
    private int zipCode;
    private String name;

public city(int zipCode, String name) {
    this.zipCode = zipCode;
    this.name = name;
    }

public int getZipCode() {
    return zipCode;
    }


public void setZipCode(int zipCode) {
    this.zipCode = zipCode;
    }

public String getName() {
    return name;
    }


public void setName(String name) {
     this.name = name;
    }


@Override
public String toString() {
    return "City{" +
           "zipCode=" + zipCode +
            ", name='" + name + '\'' +
            '}';
    }
}