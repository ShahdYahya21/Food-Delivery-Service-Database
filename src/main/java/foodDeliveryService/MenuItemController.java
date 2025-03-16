package foodDeliveryService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.DoubleStringConverter;

import java.sql.*;

public class MenuItemController {

    @FXML
    private TableView<MenuItem> menuItem_table;

    @FXML
    private TableColumn<MenuItem, Integer> idColumn;
    @FXML
    private TableColumn<MenuItem, String> nameColumn;
    @FXML
    private TableColumn<MenuItem, Double> priceColumn;
    @FXML
    private TableColumn<MenuItem, String> typeColumn;
    @FXML
    private TableColumn<MenuItem, Integer> restaurantIdColumn;
    @FXML
    private TableColumn<MenuItem, Integer> offerIdCol;
    @FXML
    private TextField nameField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField restaurantIdField;
    @FXML
    private TextField offerIdText;

    private ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();
    private final String dbURL = "jdbc:mysql://127.0.0.1:3306/foodDeliveryService";
    private final String dbUsername = "root";
    private final String dbPassword = "Sh12344321";

    @FXML
    public void initialize() {
        configureTable();
        loadData();
    }

    private void configureTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("item_No"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("item_Name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        restaurantIdColumn.setCellValueFactory(new PropertyValueFactory<>("restaurantId"));
        offerIdCol.setCellValueFactory(new PropertyValueFactory<>("offerID"));


        menuItem_table.setItems(menuItems);
        menuItem_table.setEditable(true);
    }

    private void loadData() {
        menuItems.clear();
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM MenuItem ORDER BY item_No")) {
            while (rs.next()) {
                menuItems.add(new MenuItem(rs.getInt("item_No"), rs.getString("name"), rs.getString("type"), rs.getDouble("price"), rs.getInt("restaurantId"), rs.getInt("OfferID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        String type = typeField.getText().trim();
        String priceText = priceField.getText().trim();
        String restaurantIdText = restaurantIdField.getText().trim();
        String offerId = offerIdText.getText().trim();

        if (name.isEmpty() || type.isEmpty() || priceText.isEmpty() || restaurantIdText.isEmpty() || offerId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int restaurantId = Integer.parseInt(restaurantIdText);
            int offer_Id = Integer.parseInt(offerId);

            addMenuItem(name, type, price, restaurantId, offer_Id);
            // Clear the text fields after adding the item
            nameField.clear();
            priceField.clear();
            typeField.clear();
            restaurantIdField.clear();
            offerIdText.clear();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Invalid input: Please enter valid numbers for price and restaurant ID.");
       }
    }


    @FXML
    private void handleDelete() {
        MenuItem selected = menuItem_table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deleteMenuItem(selected.getItem_No());
        }
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleClearAll() {
        clearAllMenuItems();
    }

    private void addMenuItem(String name, String type, double price, int restaurantId, int offerId) {
        String insertQuery = "INSERT INTO MenuItem (name, type, price, restaurantId, OfferID) VALUES (?, ?, ?, ?, ?)";
        String updateOrderLineQuery ="UPDATE OrderLine " +
                "JOIN MenuItem ON OrderLine.menuItemId = MenuItem.item_No " +
                "LEFT JOIN Offer ON MenuItem.OfferID = Offer.Offer_ID " +
                "JOIN OrderTable ON OrderLine.orderId = OrderTable.Order_id " +
                "SET OrderLine.totalPrice = CASE " +
                "WHEN OrderTable.Date BETWEEN Offer.Start_Date AND Offer.End_Date AND Offer.Percent_Discount IS NOT NULL THEN " +
                "OrderLine.quantity * MenuItem.price * (1 - Offer.Percent_Discount / 100) " +
                "ELSE " +
                "OrderLine.quantity * MenuItem.price " +
                "END";
        String updateOrderTableQuery = "UPDATE OrderTable " +
                "SET Cost = (SELECT SUM(OrderLine.totalPrice) " +
                "            FROM OrderLine " +
                "            WHERE OrderTable.Order_id = OrderLine.orderId) " +
                "WHERE EXISTS (SELECT 1 FROM OrderLine WHERE OrderTable.Order_id = OrderLine.orderId)";

        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(insertQuery);
             PreparedStatement updateOrderLineStmt = conn.prepareStatement(updateOrderLineQuery);
             PreparedStatement updateOrderTableStmt = conn.prepareStatement(updateOrderTableQuery)) {

            stmt.setString(1, name);
            stmt.setString(2, type);
            stmt.setDouble(3, price);
            stmt.setInt(4, restaurantId);
            stmt.setInt(5, offerId);
            stmt.executeUpdate();

            updateOrderLineStmt.executeUpdate();
            updateOrderTableStmt.executeUpdate();
            loadData();
            orderLineController controller1 = new orderLineController();
            OrderTableController controller2 = new OrderTableController();
            controller1.loadData();
            controller2.loadData();
            restaurantsStatusController controller3 = new restaurantsStatusController();
            controller3.loadData();


        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Entry Error", "A menu item with this name already exists for the specified restaurant. Please enter a unique name.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Cannot add or update menu item: Invalid Restaurant ID.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while adding the menu item: " + e.getMessage());
        }
    }


    private void deleteMenuItem(int itemId) {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM MenuItem WHERE item_No = " + itemId);
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearAllMenuItems() {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM MenuItem");
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void updateName(TableColumn.CellEditEvent<MenuItem, String> event) {
        MenuItem menuItem = event.getRowValue();
        menuItem.setItem_Name(event.getNewValue());
        updateMenuItem(menuItem);
    }
    @FXML
    public void updatePrice(TableColumn.CellEditEvent<MenuItem, Double> event) {
        MenuItem menuItem = event.getRowValue();
        menuItem.setPrice(event.getNewValue());
        updateMenuItem(menuItem);
    }

    private void updateMenuItem(MenuItem menuItem) {
        String sql = "UPDATE MenuItem SET name = ?, price = ? WHERE item_No = ?";
        String updateOrderLineQuery = "UPDATE OrderLine " +
                "JOIN MenuItem ON OrderLine.menuItemId = MenuItem.item_No " +
                "LEFT JOIN Offer ON MenuItem.OfferID = Offer.Offer_ID " +
                "JOIN OrderTable ON OrderLine.orderId = OrderTable.Order_id " +
                "SET OrderLine.totalPrice = CASE " +
                "WHEN OrderTable.Date BETWEEN Offer.Start_Date AND Offer.End_Date AND Offer.Percent_Discount IS NOT NULL THEN " +
                "OrderLine.quantity * MenuItem.price * (1 - Offer.Percent_Discount / 100) " +
                "ELSE " +
                "OrderLine.quantity * MenuItem.price " +
                "END";
        String updateOrderTableQuery = "UPDATE OrderTable " +
                "SET Cost = (SELECT SUM(OrderLine.totalPrice) " +
                "            FROM OrderLine " +
                "            WHERE OrderTable.Order_id = OrderLine.orderId) " +
                "WHERE EXISTS (SELECT 1 FROM OrderLine WHERE OrderTable.Order_id = OrderLine.orderId)";

        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql);
             PreparedStatement updateOrderLineStmt = conn.prepareStatement(updateOrderLineQuery);
             PreparedStatement updateOrderTableStmt = conn.prepareStatement(updateOrderTableQuery)) {

            stmt.setString(1, menuItem.getItem_Name());
            stmt.setDouble(2, menuItem.getPrice());
            stmt.setInt(3, menuItem.getItem_No());

            stmt.executeUpdate();

            updateOrderLineStmt.executeUpdate();
            updateOrderTableStmt.executeUpdate();

            loadData();
            orderLineController controller1 = new orderLineController();
            OrderTableController controller2 = new OrderTableController();
            controller1.loadData();
            controller2.loadData();
            interfaceController controller3 = new interfaceController();
            controller3.displayProfit();
            interfaceController interfaceCtrl = new interfaceController();
            interfaceCtrl.displayProfit();
            interfaceCtrl.displayChart();


        } catch (SQLException e) {
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
    void backtoDashboard(MouseEvent mouseEvent) {
        try {

            // Load the FXML file for the register page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interface.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage currentStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();

            // Create a new scene with the loaded FXML content
            Scene scene = new Scene(root);

            // Set the scene on the current stage and show it
            currentStage.setScene(scene);
            currentStage.show();

        } catch (Exception e) {
            System.err.println("Failed to load the Register FXML");
            e.printStackTrace();
        }
    }
}
