package foodDeliveryService;
import java.text.ParseException;
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
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.util.StringConverter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.fxml.FXML;

public class OfferTableController {
    @FXML
    private TableView<Offer> OfferTable;
    @FXML
    private TableColumn<Offer, Integer> offerIdCol;
    @FXML
    private TableColumn<Offer, Date> startDateCol;
    @FXML
    private TableColumn<Offer, Date> endDateCol;
    @FXML
    private TableColumn<Offer, String> percentCol;
    @FXML
    private TableColumn<Offer, Integer> restIdCol;


    @FXML
    private TextField startDateText;
    @FXML
    private TextField restIdText;
    @FXML
    private TextField DiscountText;
    @FXML
    private TextField endDateText;


    private ObservableList<Offer> offers = FXCollections.observableArrayList();
    private final String dbURL = "jdbc:mysql://127.0.0.1:3306/foodDeliveryService";
    private final String dbUsername = "root";
    private final String dbPassword = "Sh12344321";

    @FXML
    public void initialize() {
        configureTable();
        loadData();
    }

    private void configureTable() {
        // Ensure the property names match the getter method names in the Order class
        offerIdCol.setCellValueFactory(new PropertyValueFactory<>("Offer_ID"));
        // Configure cell factories for Date columns
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("Start_Date"));
        startDateCol.setCellFactory(TextFieldTableCell.forTableColumn(new DateStringConverter()));
        startDateCol.setOnEditCommit(this::updateStartDate);


        endDateCol.setCellValueFactory(new PropertyValueFactory<>("End_Date"));
        endDateCol.setCellFactory(TextFieldTableCell.forTableColumn(new DateStringConverter()));
        endDateCol.setOnEditCommit(this::updateEndDate);



        percentCol.setCellValueFactory(new PropertyValueFactory<>("Percent_Discount"));
        percentCol.setCellFactory(TextFieldTableCell.forTableColumn());
        restIdCol.setCellValueFactory(new PropertyValueFactory<>("RestaurantID"));


        OfferTable.setItems(offers);
        OfferTable.setEditable(true);
    }

    private void loadData() {
        offers.clear();
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Offer ORDER BY Offer_ID")) {
            while (rs.next()) {
                int offerID = rs.getInt("Offer_ID");
                Date startDate = rs.getDate("Start_Date");
                Date endDate = rs.getDate("End_Date");
                String percentDiscount = rs.getString("Percent_Discount");
                int restaurantID = rs.getInt("RestaurantID");

                // Create an Offer object with the retrieved data and add it to the offers list
                Offer offer = new Offer(offerID, startDate, endDate, percentDiscount, restaurantID);
                offers.add(offer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdd() {
        String startDate = startDateText.getText().trim();
        String endDate = endDateText.getText().trim();
        String percent = DiscountText.getText().trim();
        String restId = restIdText.getText().trim();

        if (startDate.isEmpty() || endDate.isEmpty() || percent.isEmpty() || restId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill in all fields.");
            return;
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            int resId = Integer.parseInt(restId);
            Date stDate = new Date(dateFormat.parse(startDate).getTime());
            Date edDate = new Date(dateFormat.parse(endDate).getTime());
            startDateText.clear();
            endDateText.clear();
            DiscountText.clear();
            restIdText.clear();
            if (isValidPercentage(percent)) {
                addOffer(stDate, edDate, percent, resId);
            } else {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid percentage (0-100).");
                // Refresh the table to revert the invalid input
                loadData();
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Invalid input: Please enter valid numbers for restaurant Id");
        } catch (ParseException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Invalid date format: Please enter dates in the format yyyy-mm-dd.");
        }



    }




    private void addOffer(Date startDate, Date endDate, String percent, Integer restId) {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Offer (Start_Date, End_Date, Percent_Discount, RestaurantID) VALUES (?, ?, ?, ?)")) {
            stmt.setDate(1, new java.sql.Date(startDate.getTime()));
            stmt.setDate(2, new java.sql.Date(endDate.getTime()));
            stmt.setString(3, percent);
            stmt.setInt(4, restId);
            stmt.executeUpdate();

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
            PreparedStatement updateOrderLineStmt = conn.prepareStatement(updateOrderLineQuery);

            String updateOrderTableQuery = "UPDATE OrderTable " +
                    "SET Cost = (SELECT SUM(OrderLine.totalPrice) " +
                    "            FROM OrderLine " +
                    "            WHERE OrderTable.Order_id = OrderLine.orderId) " +
                    "WHERE EXISTS (SELECT 1 FROM OrderLine WHERE OrderTable.Order_id = OrderLine.orderId)";
            PreparedStatement updateOrderTableStmt = conn.prepareStatement(updateOrderTableQuery);

            updateOrderLineStmt.executeUpdate();
            updateOrderTableStmt.executeUpdate();

            loadData();

            orderLineController controller1 = new orderLineController();
            OrderTableController controller2 = new OrderTableController();
            controller1.loadData();
            controller2.loadData();
            interfaceController controller3 = new interfaceController();
            controller3.displayProfit();
            controller3.displayChart();

            restaurantsStatusController controller4 = new restaurantsStatusController();
            controller4.loadData();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while adding the Offer: " + e.getMessage());
        }
    }



    @FXML
    private void handleDelete() {
        Offer selected = OfferTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deleteoffer(selected.getOffer_ID());
        }
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleClearAll() {
        clearAllOfferTable();
    }

    private void deleteoffer(int OfferId) {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Offer WHERE Offer_ID = " + OfferId);
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearAllOfferTable() {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Offer");
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void updateStartDate(TableColumn.CellEditEvent<Offer, Date> event) {
        Offer offer = event.getRowValue();
        Date oldValue = event.getOldValue(); // Store the old value
        Date newValue = event.getNewValue();

        if (newValue != null && isValidDateFormat(newValue)) {
            offer.setStart_Date(newValue);
            updateOffer(offer);
        } else {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid date format. Please enter dates in the format yyyy-MM-dd.");

            // Revert the cell value to the old value
            event.getTableView().getItems().set(event.getTablePosition().getRow(), offer);

            // Refresh the table to revert the invalid input
            loadData();
        }
    }

    @FXML
    public void updateEndDate(TableColumn.CellEditEvent<Offer, Date> event) {
        Offer offer = event.getRowValue();
        Date oldValue = event.getOldValue(); // Store the old value
        Date newValue = event.getNewValue();

        if (newValue != null && isValidDateFormat(newValue)) {
            offer.setEnd_Date(newValue);
            updateOffer(offer);
        } else {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid date format. Please enter dates in the format yyyy-MM-dd.");

            // Revert the cell value to the old value
            event.getTableView().getItems().set(event.getTablePosition().getRow(), offer);

            // Refresh the table to revert the invalid input
            loadData();
        }
    }

    @FXML
    public void updatePercentage(TableColumn.CellEditEvent<Offer, String> event) {
        Offer offer = event.getRowValue();
        String oldValue = event.getOldValue(); // Store the old value
        String newValue = event.getNewValue();

        if (isValidPercentage(newValue)) {
            offer.setPercent_Discount(newValue);
            updateOffer(offer);
        } else {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid percentage (0-100).");

            event.getTableView().getItems().set(event.getTablePosition().getRow(), offer);

            loadData();
        }
    }

    private void updateOffer(Offer offer) {
        // Check if the start date and end date are in the correct format
        if (!isValidDateFormat(offer.getStart_Date()) || !isValidDateFormat(offer.getEnd_Date())) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid date format. Please enter dates in the format yyyy-MM-dd.");
            return;
        }

        // Proceed with updating the offer in the database
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement("UPDATE Offer SET Start_Date = ?, End_Date = ?, Percent_Discount = ? WHERE Offer_ID = ?")) {
            stmt.setDate(1, new java.sql.Date(offer.getStart_Date().getTime()));
            stmt.setDate(2, new java.sql.Date(offer.getEnd_Date().getTime()));
            stmt.setString(3, offer.getPercent_Discount());
            stmt.setInt(4, offer.getOffer_ID());
            stmt.executeUpdate();


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
            PreparedStatement updateOrderLineStmt = conn.prepareStatement(updateOrderLineQuery);

            String updateOrderTableQuery = "UPDATE OrderTable " +
                    "SET Cost = (SELECT SUM(OrderLine.totalPrice) " +
                    "            FROM OrderLine " +
                    "            WHERE OrderTable.Order_id = OrderLine.orderId) " +
                    "WHERE EXISTS (SELECT 1 FROM OrderLine WHERE OrderTable.Order_id = OrderLine.orderId)";
            PreparedStatement updateOrderTableStmt = conn.prepareStatement(updateOrderTableQuery);

            updateOrderLineStmt.executeUpdate();
            updateOrderTableStmt.executeUpdate();

            loadData();

            orderLineController controller1 = new orderLineController();
            OrderTableController controller2 = new OrderTableController();
            controller1.loadData();
            controller2.loadData();
            interfaceController controller3 = new interfaceController();
            controller3.displayProfit();
            controller3.displayChart();
            restaurantsStatusController controller4 = new restaurantsStatusController();
            controller4.loadData();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while updating the Offer: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private boolean isValidDateFormat(Date date) {
        if (date == null) {
            return false; // Date is null, not a valid format
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.format(date); // This will throw a ParseException if the format is incorrect
            return true; // If no exception was thrown, the format is valid
        } catch (Exception e) {
            return false; // Date format is incorrect
        }
    }
    private boolean isValidPercentage(String input) {
        try {
            int percent = Integer.parseInt(input);
            return percent >= 0 && percent <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public class DateStringConverter extends StringConverter<Date> {

        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public String toString(Date object) {
            if (object != null) {
                return dateFormat.format(object);
            } else {
                return "";
            }
        }

        @Override
        public Date fromString(String string) {
            try {
                return dateFormat.parse(string);
            } catch (ParseException e) {
                return null;
            }
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(contentText);
        alert.showAndWait();
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

















