package foodDeliveryService;
import java.util.Date;

public class Offer {
    private int Offer_ID;
    private Date Start_Date;
    private Date End_Date;
    private String Percent_Discount;
    private int RestaurantID;


    public Offer(int Offer_ID, Date Start_Date, Date End_Date, String Percent_Discount, int RestaurantID) {
        this.Offer_ID = Offer_ID;
        this.Start_Date = Start_Date;
        this.End_Date = End_Date;
        this.Percent_Discount = Percent_Discount;
        this.RestaurantID = RestaurantID;
    }

    public int getOffer_ID() {
        return Offer_ID;
    }

    public void setOffer_ID(int offer_ID) {
        Offer_ID = offer_ID;
    }

    public Date getStart_Date() {
        return Start_Date;
    }

    public void setStart_Date(Date start_Date) {
        Start_Date = start_Date;
    }

    public Date getEnd_Date() {
        return End_Date;
    }

    public void setEnd_Date(Date end_Date) {
        End_Date = end_Date;
    }

    public String getPercent_Discount() {
        return Percent_Discount;
    }

    public void setPercent_Discount(String percent_Discount) {
        Percent_Discount = percent_Discount;
    }

    public int getRestaurantID() {
        return RestaurantID;
    }

    public void setRestaurantID(int restaurantID) {
        RestaurantID = restaurantID;
    }

    @Override
    public String toString() {
        return "Offer [Offer_ID=" + Offer_ID + ", Start_Date=" + Start_Date + ", End_Date=" + End_Date
                + ", Percent_Discount=" + Percent_Discount + ", RestaurantID=" + RestaurantID + "]";
    }
}