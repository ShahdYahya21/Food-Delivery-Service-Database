package foodDeliveryService;

public class MenuItem {
    private int item_No;
    private String item_Name;
    private double price;
    private String type;
    private int restaurantId;
    private int offerID;

    public MenuItem(int item_No, String item_Name, String type, double price, int restaurantId,int offerID) {
        this.item_No = item_No;
        this.item_Name = item_Name;
        this.price = price;
        this.type = type;
        this.restaurantId = restaurantId;
        this.offerID = offerID;
    }

    public int getItem_No() {
        return item_No;
    }

    public void setItem_No(int item_No) {
        this.item_No = item_No;
    }

    public String getItem_Name() {
        return item_Name;
    }

    public void setItem_Name(String item_Name) {
        this.item_Name = item_Name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }
    public int getOfferID() {
        return offerID;
    }
    public void setOfferID(int offerID) {
        this.offerID = offerID;
    }
    @Override
    public String toString() {
        return "MenuItem{" +
                "item_No=" + item_No +
                ", item_Name='" + item_Name + '\'' +
                ", price=" + price +
                ", type='" + type + '\'' +
                ", restaurantId=" + restaurantId +
                ", offerID=" + offerID +
                '}';
    }
}