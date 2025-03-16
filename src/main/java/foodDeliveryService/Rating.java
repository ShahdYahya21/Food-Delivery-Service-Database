package foodDeliveryService;

public class Rating {
    private int ratingId;
    private int customerId;
    private int RestaurantId;
    private String ratingText;
    private int evaluationScore;

    // Constructor
    public Rating(int ratingId, int customerId, int RestaurantId, String ratingText, int evaluationScore) {
        this.ratingId = ratingId;
        this.customerId = customerId;
        this.RestaurantId = RestaurantId;
        this.ratingText = ratingText;
        this.evaluationScore = evaluationScore;
    }

    // Getters and Setters
    public int getRatingId() {
        return ratingId;
    }

    public void setRatingId(int ratingId) {
        this.ratingId = ratingId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getRestaurantId() {
        return RestaurantId;
    }

    public void setRestaurantId(int RestaurantId) {
        this.RestaurantId = RestaurantId;
    }

    public String getRatingText() {
        return ratingText;
    }

    public void setRatingText(String ratingText) {
        this.ratingText = ratingText;
    }

    public int getEvaluationScore() {
        return evaluationScore;
    }

    public void setEvaluationScore(int evaluationScore) {
        this.evaluationScore = evaluationScore;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "ratingId=" + ratingId +
                ", customerId=" + customerId +
                ", RestaurantId=" + RestaurantId +
                ", ratingText='" + ratingText + '\'' +
                ", evaluationScore=" + evaluationScore +
                '}';
    }
}
