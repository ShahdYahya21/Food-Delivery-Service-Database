package foodDeliveryService;

public class monthlyProfit {
    private int month;
    private double[] weeklyProfits;

    public monthlyProfit(int month, double[] weeklyProfits) {
        this.month = month;
        this.weeklyProfits = weeklyProfits;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double[] getWeeklyProfits() {
        return weeklyProfits;
    }

    public void setWeeklyProfits(double[] weeklyProfits) {
        this.weeklyProfits = weeklyProfits;
    }
}
