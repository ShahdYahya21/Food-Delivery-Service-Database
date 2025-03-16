package foodDeliveryService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class OrderTableController {
        @FXML
        private TableView<Order> OrderTable;
        @FXML
        private TableColumn<Order, Integer> OrderNoCol;
        @FXML
        private TableColumn<Order, Integer> customerIdCol;
        @FXML
        private TableColumn<Order, Integer> RestaurantIDCol;
        @FXML
        private TableColumn<Order, String> AddressDeliveryCol;
        @FXML
        private TableColumn<Order, Double> CostCol;
        @FXML
        private TableColumn<Order, Date> dateCol;
        @FXML
        private TableColumn<Order, Time> TimeCol;

        @FXML
        private TextField Cust_ID;
        @FXML
        private TextField Rest_ID;
        @FXML
        private TextField addressDelivery;

        private ObservableList<Order> Orders = FXCollections.observableArrayList();
        private final String dbURL = "jdbc:mysql://127.0.0.1:3306/foodDeliveryService";
        private final String dbUsername = "root";
        private final String dbPassword = "Sh12344321";

        @FXML
        public void initialize() {
                configureTable();
                loadData();
        }

        private void configureTable() {
                OrderNoCol.setCellValueFactory(new PropertyValueFactory<>("order_id"));
                customerIdCol.setCellValueFactory(new PropertyValueFactory<>("SNN"));
                RestaurantIDCol.setCellValueFactory(new PropertyValueFactory<>("restaurantID"));
                AddressDeliveryCol.setCellValueFactory(new PropertyValueFactory<>("addressDelivery"));
                AddressDeliveryCol.setCellFactory(TextFieldTableCell.forTableColumn());
                CostCol.setCellValueFactory(new PropertyValueFactory<>("cost"));
                dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
                TimeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

                OrderTable.setItems(Orders);
                OrderTable.setEditable(true);
        }

        void loadData() {
                Orders.clear();
                try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM OrderTable ORDER BY Order_id")) {
                        while (rs.next()) {
                                Orders.add(new Order(
                                        rs.getInt(1),  // Order_id
                                        rs.getInt(2),  // SNN
                                        rs.getInt(3),  // RestaurantID
                                        rs.getString(4),  // AddressDelivery
                                        rs.getDouble(5),  // Cost
                                        rs.getDate(6),  // Date
                                        rs.getTime(7)   // Time
                                ));
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }

        @FXML
        private void handleAdd() {
                String customerId = Cust_ID.getText().trim();
                String restaurantId = Rest_ID.getText().trim();
                String addDelivery = addressDelivery.getText().trim();

                if (customerId.isEmpty() || restaurantId.isEmpty() || addDelivery.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill in all fields.");
                        return;
                }

                try {
                        int custId = Integer.parseInt(customerId);
                        int restId = Integer.parseInt(restaurantId);

                        if (custId <= 0 || restId <= 0) {
                                showAlert(Alert.AlertType.ERROR, "Form Error!", "Customer ID and Restaurant ID must be positive numbers.");
                                return;
                        }
                        Cust_ID.clear();
                        Rest_ID.clear();
                        addressDelivery.clear();

                        addOrder(custId, restId, addDelivery);

                } catch (NumberFormatException e) {
                        showAlert(Alert.AlertType.ERROR, "Form Error!", "Invalid input: Please enter valid numbers for Customer ID and Restaurant ID.");
                        e.printStackTrace();
                }
        }

        private void addOrder(Integer custId, Integer restId, String addDelivery) {
                LocalDateTime now = LocalDateTime.now();
                LocalDate currentDate = now.toLocalDate();
                LocalTime currentTime = now.toLocalTime();

                try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
                     PreparedStatement stmt = conn.prepareStatement("INSERT INTO OrderTable (SNN, RestaurantID, AddressDelivery, Date, Time) VALUES (?, ?, ?, ?, ?)")) {
                        stmt.setInt(1, custId);
                        stmt.setInt(2, restId);
                        stmt.setString(3, addDelivery);
                        stmt.setDate(4, Date.valueOf(currentDate));
                        stmt.setTime(5, Time.valueOf(currentTime));
                        stmt.executeUpdate();

                } catch (SQLException e) {
                        showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while adding the order: " + e.getMessage());
                }
                loadData();
                interfaceController controller = new interfaceController();
                controller.displayNumOfOrders();
                controller.displayProfit();
                restaurantsStatusController controller2 = new restaurantsStatusController();
                controller2.loadData();
                DriversStatusController controller3 = new DriversStatusController();
                controller3.loadData();        }

        @FXML
        private void handleDelete() {
                Order selected = OrderTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                        deleteOrder(selected.getOrder_id());
                }
        }

        @FXML
        private void handleRefresh() {
                loadData();
        }

        @FXML
        private void handleClearAll() {
                clearAllOrderTable();
        }

        private void deleteOrder(int orderId) {
                try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
                     Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate("DELETE FROM OrderTable WHERE Order_id = " + orderId);
                        loadData();
                        interfaceController controller = new interfaceController();
                        controller.displayNumOfOrders();
                        controller.displayProfit();
                        restaurantsStatusController controller2 = new restaurantsStatusController();
                        controller2.loadData();
                        DriversStatusController controller3 = new DriversStatusController();
                        controller3.loadData();                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }

        private void clearAllOrderTable() {
                try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
                     Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate("DELETE FROM OrderTable");
                        loadData();
                        interfaceController controller = new interfaceController();
                        controller.displayNumOfOrders();
                        controller.displayProfit();
                        restaurantsStatusController controller2 = new restaurantsStatusController();
                        controller2.loadData();
                        DriversStatusController controller3 = new DriversStatusController();
                        controller3.loadData();                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }

        @FXML
        public void updateDeliveryAddress(TableColumn.CellEditEvent<Order, String> event) {
                Order order = event.getRowValue();
                order.setAddressDelivery(event.getNewValue());
                updateOrder(order);
        }

        private void updateOrder(Order order) {
                try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
                     PreparedStatement stmt = conn.prepareStatement("UPDATE OrderTable SET AddressDelivery = ? WHERE Order_id = ?")) {
                        stmt.setString(1, order.getAddressDelivery());
                        stmt.setInt(2, order.getOrder_id());
                        stmt.executeUpdate();
                        interfaceController controller = new interfaceController();
                        controller.displayNumOfOrders();
                        controller.displayProfit();
                        restaurantsStatusController controller2 = new restaurantsStatusController();
                        controller2.loadData();
                        DriversStatusController controller3 = new DriversStatusController();
                        controller3.loadData();                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }

        private void showAlert(Alert.AlertType alertType, String title, String contentText) {
                Alert alert = new Alert(alertType);
                alert.setTitle(title);
                alert.setContentText(contentText);
                alert.showAndWait();
        }

        @FXML
        void BackToDashboard(MouseEvent mouseEvent) {
                try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/interface.fxml"));
                        Parent root = loader.load();
                        Stage currentStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                        Scene scene = new Scene(root);
                        currentStage.setScene(scene);
                        currentStage.show();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }


}
