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

public class CustomerServiceRepresentativeController {
    @FXML
    private TableView<CustomerServiceRepresentative> customerServiceRepresentativeTable;
    @FXML
    private TableColumn<CustomerServiceRepresentative, Integer> customerServiceRepresentativeIdCol;
    @FXML
    private TableColumn<CustomerServiceRepresentative, Integer> salaryCol;


    @FXML
    private TextField customerServiceRepresentativeIdText;
    @FXML
    private TextField salaryText;


    private ObservableList<CustomerServiceRepresentative> CustomerServiceRepresentatives = FXCollections.observableArrayList();
    private final String dbURL = "jdbc:mysql://127.0.0.1:3306/foodDeliveryService";
    private final String dbUsername = "root";
    private final String dbPassword = "Sh12344321";

    @FXML
    public void initialize() {
        configureTable();
        loadData();
    }
    private void configureTable() {
        customerServiceRepresentativeIdCol.setCellValueFactory(new PropertyValueFactory<>("empID"));
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salary"));
        salaryCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        customerServiceRepresentativeTable.setItems(CustomerServiceRepresentatives);
        customerServiceRepresentativeTable.setEditable(true);

    }
      void loadData() {
        CustomerServiceRepresentatives.clear();
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CustomerServiceRepresentatives");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int CSR_ID = rs.getInt("CSR_ID");
                int Salary = rs.getInt("Salary");

                // Fetch employee details from the Employees table using the driverId
                PreparedStatement stmt1 = conn.prepareStatement("SELECT * FROM Employees WHERE Emp_ID = ?");
                stmt1.setInt(1, CSR_ID);
                ResultSet rsEmp = stmt1.executeQuery();

                // Check if there is a matching employee
                if (rsEmp.next()) {
                    String empName = rsEmp.getString("Emp_Name");
                    String phone = rsEmp.getString("Phone");
                    String email = rsEmp.getString("Email");
                    int workHours = rsEmp.getInt("Work_Hours");

                    // Create a new Driver object with fetched data
                    CustomerServiceRepresentative CSR = new CustomerServiceRepresentative(CSR_ID, empName, phone, email, workHours, Salary);

                    CustomerServiceRepresentatives.add(CSR);
                }
            }
            customerServiceRepresentativeTable.setItems(CustomerServiceRepresentatives);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdd() {
        try {
            String csrID = customerServiceRepresentativeIdText.getText().trim();
            String salary = salaryText.getText().trim();


            if (csrID.isEmpty() || salary.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill in all fields.");
                return;
            }

            int CSR_ID = Integer.parseInt(csrID);
            int Salary = Integer.parseInt(salary);

            // Check if the driver ID exists in the employee table
            if (!employeeExists(CSR_ID)) {
                showAlert(Alert.AlertType.ERROR, "Invalid Employee ID", "The entered employee ID does not exist.");
                return;
            }

            addCustomerServiceRepresentative(CSR_ID, Salary);

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Invalid input: Please enter valid numbers for Customer Service Representative ID and Salary.");
        } catch (NullPointerException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "A text field is null. Please check your input.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while accessing the database: " + e.getMessage());
        } finally {
            // Clear the text fields after adding the driver
            customerServiceRepresentativeIdText.clear();
            salaryText.clear();

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

    private void addCustomerServiceRepresentative(int CSR_ID, int Salary) throws SQLException {
        String query = "INSERT INTO CustomerServiceRepresentatives (CSR_ID, Salary) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, CSR_ID);
            stmt.setInt(2, Salary);
            stmt.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Entry Error", "A Customer Service Representative with this ID already exists. Please enter a unique ID.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while adding the driver: " + e.getMessage());
        }
        loadData();
        customerServiceRepresentativeTable.refresh();

    }
    private void deleteCustomerServiceRepresentative(int CSR_ID) {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM CustomerServiceRepresentatives WHERE CSR_ID = " + CSR_ID);
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearAllCustomerServiceRepresentatives() {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM CustomerServiceRepresentatives");
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        CustomerServiceRepresentative selected = customerServiceRepresentativeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deleteCustomerServiceRepresentative(selected.getEmpID());
        }
    }
    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleClearAll() {
        clearAllCustomerServiceRepresentatives();
    }

    @FXML
    public void updateSalary(TableColumn.CellEditEvent<CustomerServiceRepresentative, Integer> event) {
        CustomerServiceRepresentative CSR = event.getRowValue();
        CSR.setSalary(event.getNewValue());
        updateCustomerServiceRepresentative(CSR);
    }


    private void updateCustomerServiceRepresentative(CustomerServiceRepresentative CSR) {
        String sql = "UPDATE CustomerServiceRepresentatives SET Salary = ? WHERE CSR_ID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, CSR.getSalary());
            stmt.setInt(2, CSR.getEmpID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while updating the Customer Service Representative: " + e.getMessage());
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

