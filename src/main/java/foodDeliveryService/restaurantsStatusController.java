package foodDeliveryService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.sql.*;

public class restaurantsStatusController {
    @FXML
    private TableView<restaurantStatus> restaurantsStatusTable;
    @FXML
    private TableColumn<restaurantStatus,Integer> restaurantIdCol;
    @FXML
    private TableColumn<restaurantStatus,Double> totalRevenueCol;
    @FXML
    private TableColumn<restaurantStatus,Double> averageRatingCol;
    @FXML
    private TableColumn<restaurantStatus,Integer> numOfOrdersCol;
    @FXML
    private ChoiceBox<String> monthChoiceBox;

    private ObservableList<restaurantStatus> restaurantsStatusArray = FXCollections.observableArrayList();
    private final String dbURL = "jdbc:mysql://127.0.0.1:3306/foodDeliveryService";
    private final String dbUsername = "root";
    private final String dbPassword = "Sh12344321";

    @FXML

    private String Months[] = {"default","January","February","March","April","May", "June", "July","August","September"};

    public void initialize() {
        configureTable();
        loadData();
        monthChoiceBox.getItems().addAll(Months);
        monthChoiceBox.setOnAction(this::getMonth);
    }

    private void configureTable() {
        restaurantIdCol.setCellValueFactory(new PropertyValueFactory<>("restaurantId"));
        totalRevenueCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        averageRatingCol.setCellValueFactory(new PropertyValueFactory<>("avgRate"));
        numOfOrdersCol.setCellValueFactory(new PropertyValueFactory<>("numOfOrders"));

        restaurantsStatusTable.setItems(restaurantsStatusArray);
    }

    private void getMonth(ActionEvent actionEvent) {
        String month = monthChoiceBox.getValue();
        if(month.equals("default")) {
            loadData();
        }
        loadNewTable(month);

    }
    void loadData() {
        restaurantsStatusArray.clear();

        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword)) {
            String query1 = "SELECT " +
                    "    r.RestaurantID, " +
                    "    AVG(rtg.evaluationScore) AS AverageRating, " +
                    "    COUNT(DISTINCT ot.Order_id) AS NumberOfOrders " +
                    "FROM " +
                    "    Restaurant r " +
                    "LEFT JOIN " +
                    "    OrderTable ot ON r.RestaurantID = ot.RestaurantID " +
                    "LEFT JOIN " +
                    "    Rating rtg ON r.RestaurantID = rtg.RestaurantId " +
                    "GROUP BY " +
                    "    r.RestaurantID";

            String query2 = "SELECT " +
                    "    RestaurantID, " +
                    "    SUM(Cost) AS TotalCost " +
                    "FROM " +
                    "    OrderTable " +
                    "GROUP BY " +
                    "    RestaurantID";

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(query1)) {
                    while (rs.next()) {
                        int restaurantID = rs.getInt("RestaurantID");
                        double averageRating = rs.getDouble("AverageRating");
                        int numberOfOrders = rs.getInt("NumberOfOrders");

                        restaurantsStatusArray.add(new restaurantStatus(restaurantID, 0, averageRating, numberOfOrders));
                    }
                }

                try (ResultSet rs = stmt.executeQuery(query2)) {
                    int index = 0;
                    while (rs.next()) {
                        double totalCost = rs.getDouble("TotalCost");
                        // Update the total cost for the corresponding restaurant
                        restaurantsStatusArray.get(index).setTotalPrice(totalCost);
                        index++;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    private void loadNewTable(String month) {
        restaurantsStatusArray.clear();
        String monthQuery = "";
        if (month.equals("January")) {
            monthQuery = "AND MONTH(ot.Date) = 1";
        } else if (month.equals("February")) {
            monthQuery = "AND MONTH(ot.Date) = 2";
        } else if (month.equals("March")) {
            monthQuery = "AND MONTH(ot.Date) = 3";
        } else if (month.equals("April")) {
            monthQuery = "AND MONTH(ot.Date) = 4";
        } else if (month.equals("May")) {
            monthQuery = "AND MONTH(ot.Date) = 5";
        } else if (month.equals("June")) {
            monthQuery = "AND MONTH(ot.Date) = 6";
        } else if (month.equals("July")) {
            monthQuery = "AND MONTH(ot.Date) = 7";
        } else if (month.equals("August")) {
            monthQuery = "AND MONTH(ot.Date) = 8";
        } else if (month.equals("September")) {
            monthQuery = "AND MONTH(ot.Date) = 9";
        }

        String query = "SELECT\n" +
                "    r.RestaurantID,\n" +
                "    Sum(ot.Cost) AS TotalRevenue,\n" +
                "    Avg(rtg.evaluationScore) AS AverageRating,\n" +
                "    Count(ot.Order_id) AS NumberOfOrders\n" +
                "FROM Restaurant r,\n" +
                "     OrderTable ot,\n" +
                "     Rating rtg\n" +
                "WHERE r.RestaurantID = ot.RestaurantID \n" +
                "AND r.RestaurantID = rtg.RestaurantId " +
                monthQuery +
                " GROUP BY r.RestaurantID;"; // Added space before GROUP BY


        restaurantsStatusArray.clear();
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                restaurantsStatusArray.add(new restaurantStatus(rs.getInt("RestaurantID"), rs.getDouble("TotalRevenue"), rs.getDouble("AverageRating"), rs.getInt("NumberOfOrders")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void backToDashBoard(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interface.fxml"));
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











