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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.*;

public class DriversStatusController {
    @FXML
    private TableView<DriversStatus> DriversStatusTable;
    @FXML
    private TableColumn<DriversStatus,Integer> driverIDCol;
    @FXML
    private TableColumn<DriversStatus,Integer> numOfOrdersCol;
    @FXML
    private TableColumn<DriversStatus,Double> avgTimeDeliveredCol;

    @FXML
    private ChoiceBox<String> monthChoiceBox;

    private ObservableList<DriversStatus> DriversStatusArray = FXCollections.observableArrayList();
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
        driverIDCol.setCellValueFactory(new PropertyValueFactory<>("driverId"));
        numOfOrdersCol.setCellValueFactory(new PropertyValueFactory<>("numOfOrders"));
        avgTimeDeliveredCol.setCellValueFactory(new PropertyValueFactory<>("avgTimeDelivered"));

        DriversStatusTable.setItems(DriversStatusArray);
    }

    private void getMonth(ActionEvent actionEvent) {
        String month = monthChoiceBox.getValue();
        if(month.equals("default")) {
            loadData();
        }
        loadNewTable(month);

    }
    void loadData() {
        DriversStatusArray.clear();
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT driverId,COUNT(orderid) AS NumberOfOrders, AVG(timeDelivered) AS AvgDeliveredTime\n" +
                     "FROM driverToOrder\n" +
                     "GROUP BY driverToOrder.driverId;")) {
            while (rs.next()) {
                DriversStatusArray.add(new DriversStatus(rs.getInt("driverId"), rs.getInt("NumberOfOrders"), rs.getDouble("AvgDeliveredTime")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void loadNewTable(String month) {
        String monthQuery = "";
        if (month.equals("January")) {
            monthQuery = "AND MONTH(OrderTable.Date) = 1";
        } else if (month.equals("February")) {
            monthQuery = "AND MONTH(OrderTable.Date) = 2";
        } else if (month.equals("March")) {
            monthQuery = "AND MONTH(OrderTable.Date) = 3";
        } else if (month.equals("April")) {
            monthQuery = "AND MONTH(OrderTable.Date) = 4";
        } else if (month.equals("May")) {
            monthQuery = "AND MONTH(OrderTable.Date) = 5";
        } else if (month.equals("June")) {
            monthQuery = "AND MONTH(OrderTable.Date) = 6";
        } else if (month.equals("July")) {
            monthQuery = "AND MONTH(OrderTable.Date) = 7";
        } else if (month.equals("August")) {
            monthQuery = "AND MONTH(OrderTable.Date) = 8";
        } else if (month.equals("September")) {
            monthQuery = "AND MONTH(OrderTable.Date) = 9";
        }

        String query = "SELECT driverToOrder.driverId, " +
                "COUNT(driverToOrder.orderid) AS NumberOfOrders, " +
                "AVG(driverToOrder.timeDelivered) AS AvgDeliveredTime " +
                "FROM driverToOrder, OrderTable " +
                "WHERE driverToOrder.orderid = OrderTable.Order_id " +
                monthQuery +
                " GROUP BY driverToOrder.driverId;";


        DriversStatusArray.clear();
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                DriversStatusArray.add(new DriversStatus(rs.getInt("driverId"), rs.getInt("NumberOfOrders"), rs.getDouble("AvgDeliveredTime")));
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






