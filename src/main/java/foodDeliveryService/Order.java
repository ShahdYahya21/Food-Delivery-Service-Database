package foodDeliveryService;

import java.sql.Date;
import java.sql.Time;

public class Order {
    private Integer Order_id;
    private Integer SNN;
    private Integer RestaurantID;
    private String AddressDelivery;
    private Double Cost;
    private Date Date;
    private Time Time;

    public Order(int Order_id, int SNN, int RestaurantID,String AddressDelivery, Double Cost, Date Date, Time Time) {
        this.Order_id = Order_id;
        this.SNN = SNN;
        this.RestaurantID = RestaurantID;
        this.AddressDelivery = AddressDelivery;
        this.Cost = Cost;
        this.Date = Date;
        this.Time = Time;
    }


    public int getOrder_id() {
        return Order_id;
    }

    public void setOrder_id(int order_id) {
        Order_id = order_id;
    }

    public int getSNN() {
        return SNN;
    }

    public void setSNN(int SNN) {
        this.SNN = SNN;
    }

    public int getRestaurantID() {
        return RestaurantID;
    }

    public void setRestaurantID(int restaurantID) {
        RestaurantID = restaurantID;
    }


    public String getAddressDelivery() {
        return AddressDelivery;
    }

    public void setAddressDelivery(String addressDelivery) {
        AddressDelivery = addressDelivery;
    }

    public Double getCost() {
        return Cost;
    }

    public void setCost(Double cost) {
        Cost = cost;
    }

    public Date getDate() {
        return Date;
    }

    public void setDate(Date date) {
        Date = date;
    }

    public Time getTime() {
        return Time;
    }

    public void setTime(Time time) {
        Time = time;
    }
}
