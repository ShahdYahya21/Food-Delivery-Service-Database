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

import java.sql.*;

public class ratingController {

    @FXML
    private TableView<Rating> ratingTable;
    @FXML
    private TableColumn<Rating, Integer> ratingIdCol;
    @FXML
    private TableColumn<Rating, Integer> customerIdCol;
    @FXML
    private TableColumn<Rating, Integer> restaurantIdCol;
    @FXML
    private TableColumn<Rating, String> ratingTextCol;
    @FXML
    private TableColumn<Rating, Integer> evaluationScoreCol;


    private ObservableList<Rating> ratings = FXCollections.observableArrayList();
    private final String dbURL = "jdbc:mysql://localhost:3306/foodDeliveryService";
    private final String dbUsername = "root";
    private final String dbPassword = "Sh12344321";

    public void initialize() {
        configureTable();
        loadData();
    }

    private void configureTable() {
        ratingIdCol.setCellValueFactory(new PropertyValueFactory<>("ratingId"));
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        restaurantIdCol.setCellValueFactory(new PropertyValueFactory<>("RestaurantId"));
        ratingTextCol.setCellValueFactory(new PropertyValueFactory<>("ratingText"));
        evaluationScoreCol.setCellValueFactory(new PropertyValueFactory<>("evaluationScore"));

        ratingTable.setItems(ratings);
        ratingTable.setEditable(true);

    }

    private void loadData() {
        ratings.clear();
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Rating")) {
            while (rs.next()) {
                ratings.add(new Rating(
                        rs.getInt("ratingId"),
                        rs.getInt("customerId"),
                        rs.getInt("RestaurantId"),
                        rs.getString("ratingText"),
                        rs.getInt("evaluationScore")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   // INSERT INTO Rating (customerId, RestaurantId, ratingText, evaluationScore)

    @FXML
    private void handleDelete() {
        Rating selected = ratingTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deleteRating(selected.getRatingId());
        }
    }

    private void deleteRating(int ratingId) {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Rating WHERE ratingId = " + ratingId);
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClearAll() {
        clearAllRatings();
    }

    private void clearAllRatings() {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Rating");
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadData();
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
