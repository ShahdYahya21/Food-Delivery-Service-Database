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

import java.sql.*;

public class RestaurantTableController {
    @FXML
    private TableView<Restaurant> RestaurantTable;
    @FXML
    private TableColumn<Restaurant, Integer> idColumn;
    @FXML
    private TableColumn<Restaurant, String> nameColumn;

    @FXML
    private TableColumn<Restaurant, String> cuisineColumn;

    @FXML
    private TableColumn<Restaurant, String> hoursColumn;

    @FXML
    private TableColumn<Restaurant, Integer> phoneColumn;
    @FXML
    private TableColumn<Restaurant, Integer> cityZipColumn;
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField cuisineField;
    @FXML
    private TextField hoursField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField cityZipField;

    private ObservableList<Restaurant> restaurants = FXCollections.observableArrayList();
    private final String dbURL = "jdbc:mysql://127.0.0.1:3306/foodDeliveryService";
    private final String dbUsername = "root";
    private final String dbPassword = "Sh12344321";

    public void initialize() {
        configureTable();
        loadData();
    }

    private void configureTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("restaurantID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("restName"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        cuisineColumn.setCellValueFactory(new PropertyValueFactory<>("cuisineType"));
        cuisineColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        hoursColumn.setCellValueFactory(new PropertyValueFactory<>("openingHours"));
        hoursColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        cityZipColumn.setCellValueFactory(new PropertyValueFactory<>("cityZipCode"));

        RestaurantTable.setItems(restaurants);
        RestaurantTable.setEditable(true);
    }

    private void loadData() {
        restaurants.clear();
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Restaurant ORDER BY RestaurantID")) {
            while (rs.next()) {
                restaurants.add(new Restaurant(
                        rs.getInt("RestaurantID"),
                        rs.getString("RestName"),
                        rs.getString("CuisineType"),
                        rs.getString("OpeningHours"),
                        rs.getInt("Phone"),
                        rs.getInt("CityZipCode")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdd() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String cuisine = cuisineField.getText().trim();
        String hours = hoursField.getText().trim();
        String phone = phoneField.getText().trim();
        String cityZip = cityZipField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || cuisine.isEmpty() || hours.isEmpty() || phone.isEmpty() || cityZip.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill in all fields.");
            return;
        }
        try {
            int restId = Integer.parseInt(id);
            int phoneNum = Integer.parseInt(phone);
            int cityZipNum = Integer.parseInt(cityZip);
            idField.clear();
            nameField.clear();
            cuisineField.clear();
            hoursField.clear();
            phoneField.clear();
            cityZipField.clear();

            addRestaurant(restId, name, cuisine, hours, phoneNum, cityZipNum);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Invalid input: Please enter valid numbers for Restaurant Id, Phone number and City ZipCode");
        }

    }


    private void addRestaurant(int id, String name, String cuisine, String hours, int phone, int cityZipCode) {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Restaurant (RestaurantID, RestName, CuisineType, OpeningHours, Phone, CityZipCode) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setString(3, cuisine);
            stmt.setString(4, hours);
            stmt.setInt(5, phone);
            stmt.setInt(6, cityZipCode);
            stmt.executeUpdate();
            loadData();
            interfaceController controller = new interfaceController();
            controller.displayNumOfRestaurant();

        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                showAlert(Alert.AlertType.ERROR, "Duplicate ID Error", "A restaurant with this ID already exists. Please enter a unique ID.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Cannot add or update restaurant item: Invalid Zip Code");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while adding the restaurant: " + e.getMessage());
        }
    }


    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleDelete() {
        Restaurant selected = RestaurantTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deleteSelectedRestaurant(selected.getRestaurantID());
        }
    }


    private void deleteSelectedRestaurant(int restId) {
        Restaurant selected = RestaurantTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM Restaurant WHERE RestaurantID = " + restId);
                loadData();
                interfaceController controller = new interfaceController();
                controller.displayNumOfRestaurant();
            } catch (SQLException e) {
                System.out.println("Database operation error: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while deleting the restaurant: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleClearAll() {
        clearAllRestaurants();
    }

    private void clearAllRestaurants() {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Restaurant");
            loadData();
            interfaceController controller = new interfaceController();
            controller.displayNumOfRestaurant();
        } catch (SQLException e) {
            System.out.println("Database operation error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while deleting the restaurant: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    public void updateName(TableColumn.CellEditEvent<Restaurant, String> event) {
        Restaurant restaurant = event.getRowValue();
        restaurant.setRestName(event.getNewValue());
        updateOrder(restaurant);
    }

    @FXML
    public void updateCuisine(TableColumn.CellEditEvent<Restaurant, String> event){
        Restaurant restaurant = event.getRowValue();
        restaurant.setCuisineType(event.getNewValue());
        updateOrder(restaurant);
    }

    @FXML
    public void updateHours(TableColumn.CellEditEvent<Restaurant, String> event){
        Restaurant restaurant = event.getRowValue();
        restaurant.setOpeningHours(event.getNewValue());
        updateOrder(restaurant);
    }


    private void updateOrder(Restaurant restaurant) {
        String sql = "UPDATE Restaurant SET RestName = ?, CuisineType = ?, OpeningHours = ?, Phone = ?, CityZipCode = ? WHERE RestaurantID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, restaurant.getRestName());
            stmt.setString(2, restaurant.getCuisineType());
            stmt.setString(3, restaurant.getOpeningHours());
            stmt.setInt(4, restaurant.getPhone());
            stmt.setInt(5, restaurant.getCityZipCode());
            stmt.setInt(6, restaurant.getRestaurantID());
            stmt.executeUpdate();
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
    void backToDashboard(MouseEvent mouseEvent) {
        try {
            System.out.println("Attempting to go back to Register");

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

            System.out.println("Successfully went back to Register");
        } catch (Exception e) {
            System.err.println("Failed to load the Register FXML");
            e.printStackTrace();
        }
    }



}


