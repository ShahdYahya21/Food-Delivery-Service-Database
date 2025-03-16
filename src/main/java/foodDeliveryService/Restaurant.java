package foodDeliveryService;

public class Restaurant{
    private int restaurantID;
    private String restName;
    private String cuisineType;
    private String openingHours;
    private int phone;
    private int cityZipCode;

    public Restaurant(int restaurantID, String restName, String cuisineType, String openingHours, int phone, int cityZipCode) {
        this.restaurantID = restaurantID;
        this.restName = restName;
        this.cuisineType = cuisineType;
        this.openingHours = openingHours;
        this.phone = phone;
        this.cityZipCode = cityZipCode;
    }


    public int getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(int restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public int getCityZipCode() {
        return cityZipCode;
    }

    public void setCityZipCode(int cityZipCode) {
        this.cityZipCode = cityZipCode;
    }


    @Override
    public String toString() {
        return "Restaurant{" +
                "restaurantID=" + restaurantID +
                ", restName='" + restName + '\'' +
                ", cuisineType='" + cuisineType + '\'' +
                ", openingHours='" + openingHours + '\'' +
                ", phone='" + phone + '\'' +
                ", cityZipCode=" + cityZipCode +
                '}';
    }
}