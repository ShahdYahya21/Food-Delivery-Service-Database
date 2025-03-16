package foodDeliveryService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class interfaceController {
    @FXML
    public Label totalProfitLabel;
    @FXML
    private Label numOfCustLabel;
    @FXML
    private Label numOfRestLabel;
    @FXML
    private Label numberOfOrdersLabel;
    @FXML
    private LineChart<String, Double> totalRevenueChart;

    @FXML
    private ChoiceBox<String> monthChoiceBox;
    private String Months[] = {"January","February","March","April","May", "June", "July","August","September"};
    Map<Integer, List<Double>> chartData = new HashMap<>();
    private final String dbURL = "jdbc:mysql://127.0.0.1:3306/foodDeliveryService";
    private final String dbUsername = "root";
    private final String dbPassword = "Sh12344321";

    @FXML
    public void initialize() {
        monthChoiceBox.getItems().addAll(Months);
        displayNumOfCustomers();
        displayNumOfRestaurant();
        displayNumOfOrders();
        displayProfit();
        displayChart();
    }
    private int getNumberOfCustomers() {
        int numberOfCustomers = 0;
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS num_of_customers FROM Customer");
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                numberOfCustomers = rs.getInt("num_of_customers");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numberOfCustomers;
    }

    private int getNumberOfRestaurant() {
        int numberOfRestaurants = 0;
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS num_of_restaurant FROM Restaurant");
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                numberOfRestaurants = rs.getInt("num_of_restaurant");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numberOfRestaurants;
    }
    private int getNumberOfOrders() {
        int numberOfOrders = 0;
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS num_of_orders FROM OrderTable");
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                numberOfOrders = rs.getInt("num_of_orders");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numberOfOrders;
    }
    void displayNumOfCustomers() {
        int numberOfCustomers = getNumberOfCustomers();
        numOfCustLabel.setText(""+numberOfCustomers);
    }
    void displayNumOfRestaurant() {
        int numberOfRestaurants = getNumberOfRestaurant();
        numOfRestLabel.setText(""+numberOfRestaurants);
    }



    void displayChart() {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement("SELECT MONTH(Date) AS Month, WEEK(Date, 1) AS Week, SUM(Cost) AS WeeklyProfit " +
                     "FROM OrderTable " +
                     "GROUP BY Month, Week " +
                     "ORDER BY Month, Week");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int month = rs.getInt("Month");
                double weeklyProfit = rs.getDouble("WeeklyProfit");

                if (!chartData.containsKey(month)) {
                    chartData.put(month, new ArrayList<>());
                }

                chartData.get(month).add(weeklyProfit);
            }

            monthChoiceBox.setOnAction(this::getMonth);

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
double getTotalProfit() {
        double totalProfit = 0;
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement("SELECT SUM(Cost) AS TotalCost FROM OrderTable");
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                totalProfit = rs.getDouble("TotalCost");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalProfit;
    }

    void displayProfit() {
        double totalProfit = getTotalProfit();
        totalProfitLabel.setText(String.format("%.1f", totalProfit));
    }

    void displayNumOfOrders() {
        int numberOfOrders = getNumberOfOrders();
        numberOfOrdersLabel.setText(""+numberOfOrders);
    }

    private void getMonth(ActionEvent actionEvent) {
        String month = monthChoiceBox.getValue();
        XYChart.Series<String, Double> series = new XYChart.Series<>();
        if (month.equals("January")) {
            List<Double> weeklyProfits = chartData.get(1);
            for (int i = 0; i < weeklyProfits.size(); i++) {
                series.getData().add(new XYChart.Data<>("week " + (i + 1), weeklyProfits.get(i)));
            }

            totalRevenueChart.getData().clear();
            totalRevenueChart.getData().add(series);
        }
        else if (month.equals("February")) {
            List<Double> weeklyProfits = chartData.get(2);
            for (int i = 0; i < weeklyProfits.size(); i++) {
                series.getData().add(new XYChart.Data<>("week " + (i + 1), weeklyProfits.get(i)));
            }

            totalRevenueChart.getData().clear();
            totalRevenueChart.getData().add(series);
        }
        else if (month.equals("March")) {
            List<Double> weeklyProfits = chartData.get(3);
            for (int i = 0; i < weeklyProfits.size(); i++) {
                series.getData().add(new XYChart.Data<>("week " + (i + 1), weeklyProfits.get(i)));
            }

            totalRevenueChart.getData().clear();
            totalRevenueChart.getData().add(series);
        }
        else if (month.equals("April")) {
            List<Double> weeklyProfits = chartData.get(4);
            for (int i = 0; i < weeklyProfits.size(); i++) {
                series.getData().add(new XYChart.Data<>("week " + (i + 1), weeklyProfits.get(i)));
            }

            totalRevenueChart.getData().clear();
            totalRevenueChart.getData().add(series);
        }
        else if (month.equals("May")) {
            List<Double> weeklyProfits = chartData.get(5);
            for (int i = 0; i < weeklyProfits.size(); i++) {
                series.getData().add(new XYChart.Data<>("week " + (i + 1), weeklyProfits.get(i)));
            }

            totalRevenueChart.getData().clear();
            totalRevenueChart.getData().add(series);
        }
        else if (month.equals("June")) {
            List<Double> weeklyProfits = chartData.get(6);
            for (int i = 0; i < weeklyProfits.size(); i++) {
                series.getData().add(new XYChart.Data<>("week " + (i + 1), weeklyProfits.get(i)));
            }

            totalRevenueChart.getData().clear();
            totalRevenueChart.getData().add(series);

        }
        else if (month.equals("July")) {
            List<Double> weeklyProfits = chartData.get(7);
            for (int i = 0; i < weeklyProfits.size(); i++) {
                series.getData().add(new XYChart.Data<>("week " + (i + 1), weeklyProfits.get(i)));
            }

            totalRevenueChart.getData().clear();
            totalRevenueChart.getData().add(series);

        }
        else if (month.equals("August")) {
            List<Double> weeklyProfits = chartData.get(8);
            for (int i = 0; i < weeklyProfits.size(); i++) {
                series.getData().add(new XYChart.Data<>("week " + (i + 1), weeklyProfits.get(i)));
            }

            totalRevenueChart.getData().clear();
            totalRevenueChart.getData().add(series);

        }
        else if (month.equals("September")) {
            List<Double> weeklyProfits = chartData.get(9);
            for (int i = 0; i < weeklyProfits.size(); i++) {
                series.getData().add(new XYChart.Data<>("week " + (i + 1), weeklyProfits.get(i)));
            }

            totalRevenueChart.getData().clear();
            totalRevenueChart.getData().add(series);

        }
        }



    @FXML
    void goToCities(MouseEvent mouseEvent) {
        try {
            // Load the FXML file for the menu item table
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/city.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage currentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

            // Create a new scene with the loaded FXML content
            Scene scene = new Scene(root);

            // Set the scene on the current stage and show it
            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void goToCustomers(MouseEvent mouseEvent) {
        try {
            // Load the FXML file for the menu item table
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Customer.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage currentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

            // Create a new scene with the loaded FXML content
            Scene scene = new Scene(root);

            // Set the scene on the current stage and show it
            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToCsr(MouseEvent mouseEvent) {
        try {
            // Load the FXML file for the menu item table
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/customerServiceRepresentative.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage currentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

            // Create a new scene with the loaded FXML content
            Scene scene = new Scene(root);

            // Set the scene on the current stage and show it
            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void GoToDrivers(MouseEvent mouseEvent) {
        try {
            // Load the FXML file for the menu item table
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Driver.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage currentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

            // Create a new scene with the loaded FXML content
            Scene scene = new Scene(root);

            // Set the scene on the current stage and show it
            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void goToEmployees(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Employee.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);

            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void goToMenuItems(MouseEvent mouseEvent) {
        try {
            // Load the FXML file for the menu item table
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuItem.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage currentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

            // Create a new scene with the loaded FXML content
            Scene scene = new Scene(root);

            // Set the scene on the current stage and show it
            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void goToOffers(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Offer.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);

            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void goToOrders(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/order.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);

            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void goToOrderLines(MouseEvent mouseEvent) {
        try {
            // Load the FXML file for the menu item table
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/orderLine.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage currentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

            // Create a new scene with the loaded FXML content
            Scene scene = new Scene(root);

            // Set the scene on the current stage and show it
            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void goToPayments(MouseEvent mouseEvent) {
        try {
            // Load the FXML file for the menu item table
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/payment.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage currentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

            // Create a new scene with the loaded FXML content
            Scene scene = new Scene(root);

            // Set the scene on the current stage and show it
            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void goToRatings(MouseEvent mouseEvent) {
        try {
            // Load the FXML file for the menu item table
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Rating.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage currentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

            // Create a new scene with the loaded FXML content
            Scene scene = new Scene(root);

            // Set the scene on the current stage and show it
            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void goToRestaurants(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Restaurant.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);

            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void goToRestStatusTable(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/restaurantStatus.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);

            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void goToDriversStatusTable(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DriversStatus.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);

            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
