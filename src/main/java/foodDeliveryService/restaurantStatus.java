package foodDeliveryService;

public class restaurantStatus {
    private int restaurantId;
    private double totalPrice;
    private double avgRate;
    private int numOfOrders;

    public restaurantStatus() {
    }

    // Parameterized constructor
    public restaurantStatus(int restaurantId, double totalPrice, double avgRate, int numOfOrders) {
        this.restaurantId = restaurantId;
        this.totalPrice = totalPrice;
        this.avgRate = avgRate;
        this.numOfOrders = numOfOrders;
    }

    public restaurantStatus(int driverId, int numberOfOrders, double avgDeliveredTime) {
    }

    // Getters and Setters
    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getAvgRate() {
        return avgRate;
    }

    public void setAvgRate(double avgRate) {
        this.avgRate = avgRate;
    }

    public int getNumOfOrders() {
        return numOfOrders;
    }

    public void setNumOfOrders(int numOfOrders) {
        this.numOfOrders = numOfOrders;
    }



    @Override
    public String toString() {
        return "restaurantStatus{" +
                "restaurantId=" + restaurantId +
                ", totalPrice=" + totalPrice +
                ", avgRate=" + avgRate +
                ", numOfOrders=" + numOfOrders +
                '}';
    }
}
