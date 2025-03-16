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

public class cityTableController {
    @FXML
    private TableView<city> cityTable;
    @FXML
    private TableColumn<city, Integer> zipColumn;
    @FXML
    private TableColumn<city, String> nameColumn;
    @FXML
    private TextField zipCodeField;
    @FXML
    private TextField nameField;

    private ObservableList<city> cities = FXCollections.observableArrayList();
    private final String dbURL = "jdbc:mysql://127.0.0.1:3306/foodDeliveryService";
    private final String dbUsername = "root";
    private final String dbPassword = "Sh12344321";

    public void initialize() {
        configureTable();
        loadData();
    }

    private void configureTable() {
        zipColumn.setCellValueFactory(new PropertyValueFactory<>("zipCode"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        cityTable.setItems(cities);
        cityTable.setEditable(true);
    }

    private void loadData() {
        cities.clear();
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM City ORDER BY ZipCode")) {
            while (rs.next()) {
                cities.add(new city(rs.getInt("ZipCode"), rs.getString("Name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdd() {
        String zipCode = zipCodeField.getText().trim();
        String cityName = nameField.getText().trim();

        if (zipCode.isEmpty() || cityName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill in all fields.");
            return;
        }
        try {
            int ZipCode = Integer.parseInt(zipCode);

            // Clear the text fields after adding the item
            zipCodeField.clear();
            nameField.clear();

            addCity(ZipCode, cityName);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Invalid input: Please enter valid zip code");
        }
    }

    private void addCity(Integer zipCode, String cityName) {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO City (ZipCode, Name) VALUES (?, ?)")) {
            stmt.setInt(1, zipCode);
            stmt.setString(2, cityName);
            stmt.executeUpdate();
            loadData();
        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Entry Error", "A city with this ZIP code already exists. Please enter a unique ZIP code.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Cannot add or update city: Invalid ZIP code.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while adding the city: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        city selected = cityTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deleteSelectedCity(selected.getZipCode());
        }
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleClearAll() {
        clearAllCities();
    }

    private void deleteSelectedCity(int zipCode) {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM City WHERE ZipCode = " + zipCode);
            loadData();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while deleting the city: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void clearAllCities() {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM City");
            cities.clear(); // Clear the observable list
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while clearing all cities: " + e.getMessage());
        }
    }

    @FXML
    public void updateCityName(TableColumn.CellEditEvent<city, String> event) {
        city City = event.getRowValue();
        City.setName(event.getNewValue());
        updateMenuItem(City);
    }

    private void updateMenuItem(city City) {
        String sql = "UPDATE City SET Name = ? WHERE ZipCode = ?";
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, City.getName());
            stmt.setInt(2, City.getZipCode());
        } catch (SQLException e) {
            System.out.println("Error updating city: " + e.getMessage());
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
