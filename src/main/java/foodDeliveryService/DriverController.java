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

public class DriverController {

    @FXML
    private TableView<Driver> driverTable;
    @FXML
    private TableColumn<Driver, Integer> driverIdCol;
    @FXML
    private TableColumn<Driver, Integer> wageCol;
    @FXML
    private TableColumn<Driver, String> licenseNumberCol;
    @FXML
    private TableColumn<Driver, String> vehicleTypeCol;

    @FXML
    private TextField driverIdText;
    @FXML
    private TextField wageText;
    @FXML
    private TextField licenseNumberText;
    @FXML
    private TextField vehicleTypeText;

    private ObservableList<Driver> drivers = FXCollections.observableArrayList();
    private final String dbURL = "jdbc:mysql://127.0.0.1:3306/foodDeliveryService";
    private final String dbUsername = "root";
    private final String dbPassword = "Sh12344321";

    @FXML
    public void initialize() {
        configureTable();
        loadData();
    }

    private void configureTable() {
        driverIdCol.setCellValueFactory(new PropertyValueFactory<>("empID"));
        wageCol.setCellValueFactory(new PropertyValueFactory<>("wage"));
        wageCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        licenseNumberCol.setCellValueFactory(new PropertyValueFactory<>("licenseNum"));
        vehicleTypeCol.setCellValueFactory(new PropertyValueFactory<>("vehicleType"));
        vehicleTypeCol.setCellFactory(TextFieldTableCell.forTableColumn());

        driverTable.setItems(drivers);
        driverTable.setEditable(true);

    }

    private void loadData() {
        drivers.clear();
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Drivers");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int driverId = rs.getInt("Driver_ID");
                String licenseNumber = rs.getString("License_Num");
                String vehicleType = rs.getString("Vehicle_Type");
                int wage = rs.getInt("Wage");

                // Fetch employee details from the Employees table using the driverId
                PreparedStatement stmt1 = conn.prepareStatement("SELECT * FROM Employees WHERE Emp_ID = ?");
                stmt1.setInt(1, driverId);
                ResultSet rsEmp = stmt1.executeQuery();

                // Check if there is a matching employee
                if (rsEmp.next()) {
                    String empName = rsEmp.getString("Emp_Name");
                    String phone = rsEmp.getString("Phone");
                    String email = rsEmp.getString("Email");
                    int workHours = rsEmp.getInt("Work_Hours");

                    // Create a new Driver object with fetched data
                    Driver driver = new Driver(driverId, empName, phone, email, workHours, wage, licenseNumber, vehicleType);

                    drivers.add(driver);
                }
            }
            driverTable.setItems(drivers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleAdd() {
        try {
            String Did = driverIdText.getText().trim();
            String Wage = wageText.getText().trim();
            String licenseNumber = licenseNumberText.getText().trim();
            String vehicleType = vehicleTypeText.getText().trim();

            if (Did.isEmpty() || Wage.isEmpty() || licenseNumber.isEmpty() || vehicleType.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill in all fields.");
                return;
            }

            int driverId = Integer.parseInt(Did);
            int wage = Integer.parseInt(Wage);

            // Check if the driver ID exists in the employee table
            if (!employeeExists(driverId)) {
                showAlert(Alert.AlertType.ERROR, "Invalid Employee ID", "The entered employee ID does not exist.");
                return;
            }

            addDriver(driverId, licenseNumber, vehicleType, wage);

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Invalid input: Please enter valid numbers for driver ID and wage.");
        } catch (NullPointerException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "A text field is null. Please check your input.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while accessing the database: " + e.getMessage());
        } finally {
            // Clear the text fields after adding the driver
            driverIdText.clear();
            licenseNumberText.clear();
            wageText.clear();
            vehicleTypeText.clear();
        }
    }

    private boolean employeeExists(int employeeId) throws SQLException {
        String query = "SELECT * FROM Employees WHERE Emp_ID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Return true if the employee exists, false otherwise
            }
        }
    }



    private void addDriver(int driverId, String licenseNumber, String vehicleType, int wage) throws SQLException {
        String query = "INSERT INTO Drivers (Driver_ID, License_Num, Vehicle_Type, Wage) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, driverId);
            stmt.setString(2, licenseNumber);
            stmt.setString(3, vehicleType);
            stmt.setInt(4, wage);
            stmt.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Entry Error", "A driver with this ID already exists. Please enter a unique ID.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while adding the driver: " + e.getMessage());
        }
        loadData();
        driverTable.refresh();

    }

    private void deleteDriver(int driverId) {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Drivers WHERE Driver_ID = " + driverId);
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearAllDrivers() {
            try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM Drivers");
                loadData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    @FXML
    private void handleDelete() {
        Driver selected = driverTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deleteDriver(selected.getEmpID());
        }
    }


    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleClearAll() {
        clearAllDrivers();
    }

    @FXML
    public void updateVehicleType(TableColumn.CellEditEvent<Driver, String> event) {
        Driver driver = event.getRowValue();
        driver.setVehicleType(event.getNewValue());
        updateDriver(driver);
    }

    @FXML
    public void updateWage(TableColumn.CellEditEvent<Driver, String> event) {
        Driver driver = event.getRowValue();
        driver.setWage(Integer.parseInt(event.getNewValue()));
        updateDriver(driver);
    }


    private void updateDriver(Driver driver) {
        String sql = "UPDATE Drivers SET License_Num = ?, Vehicle_Type = ?, Wage = ? WHERE Driver_ID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, driver.getLicenseNum());
            stmt.setString(2, driver.getVehicleType());
            stmt.setDouble(3, driver.getWage());
            stmt.setInt(4, driver.getEmpID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while updating the Driver: " + e.getMessage());

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

