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

public class CustomerController {
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> customerIdCol;
    @FXML
    private TableColumn<Customer, String> customerNameCol;
    @FXML
    private TableColumn<Customer, String> phoneCol;
    @FXML
    private TableColumn<Customer, String> addressCol;
    @FXML
    private TableColumn<Customer, String> emailCol;
    @FXML
    private TableColumn<Customer, Integer> customerServiceRepresentativeIdCol;

    @FXML
    private TextField CustomerIdText;
    @FXML
    private TextField customerServiceRepresentativeIdText;
    @FXML
    private TextField emailText;
    @FXML
    private TextField phoneText;
    @FXML
    private TextField addressText;
    @FXML
    private TextField customerNameText;

    private ObservableList<Customer> customers = FXCollections.observableArrayList();
    private final String dbURL = "jdbc:mysql://127.0.0.1:3306/foodDeliveryService";
    private final String dbUsername = "root";
    private final String dbPassword = "Sh12344321";

    @FXML
    public void initialize() {
        configureTable();
        loadData();
    }

    private void configureTable() {
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setCellFactory(TextFieldTableCell.forTableColumn());
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressCol.setCellFactory(TextFieldTableCell.forTableColumn());
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        customerServiceRepresentativeIdCol.setCellValueFactory(new PropertyValueFactory<>("customerServiceRepresentative"));



        customerTable.setItems(customers);
        customerTable.setEditable(true);

    }
    private void loadData() {
        customers.clear();
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Customer");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int customerId = rs.getInt("customerId");
                String customerName = rs.getString("customerName");
                String address = rs.getString("address");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                int CSR_Id = rs.getInt("customerServiceRepresentative");

                Customer customer = new Customer(customerId, customerName, address, phone, email, CSR_Id);

                customers.add(customer);

            }
            customerTable.setItems(customers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdd() {
        try {
            String customerId = CustomerIdText.getText().trim();
            String customerName = customerNameText.getText().trim();
            String address = addressText.getText().trim();
            String phone = phoneText.getText().trim();
            String email = emailText.getText().trim();
            String CSR_Id = customerServiceRepresentativeIdText.getText().trim();



            if (customerId.isEmpty() || customerName.isEmpty() || address.isEmpty() || phone.isEmpty() || email.isEmpty()|| CSR_Id.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill in all fields.");
                return;
            }

            int custId = Integer.parseInt(customerId);
            int crsId = Integer.parseInt(CSR_Id);

            // Check if the driver ID exists in the employee table
            if (!CustomerServiceRepresentativeExists(crsId)) {
                showAlert(Alert.AlertType.ERROR, "Invalid Employee ID", "The entered Customer Service Representative ID does not exist.");
                return;
            }

            addCustomer(custId, customerName, address, phone,email,crsId);

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Invalid input: Please enter valid numbers for customer ID and Customer Service Representative Id.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while accessing the database: " + e.getMessage());
        } finally {
            // Clear the text fields after adding the driver
            CustomerIdText.clear();
            customerNameText.clear();
            addressText.clear();
            phoneText.clear();
            emailText.clear();
            customerServiceRepresentativeIdText.clear();

        }
    }


    private boolean CustomerServiceRepresentativeExists(int csrId) throws SQLException {
        String query = "SELECT * FROM CustomerServiceRepresentatives WHERE CSR_ID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, csrId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Return true if the employee exists, false otherwise
            }
        }
    }
    private void addCustomer(int custId, String customerName, String address, String phone, String email, int csrId) throws SQLException {
        String query = "INSERT INTO Customer (customerId, customerName, address, phone, email, customerServiceRepresentative) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, custId);
            stmt.setString(2, customerName);
            stmt.setString(3, address);
            stmt.setString(4, phone);
            stmt.setString(5, email);
            stmt.setInt(6, csrId);


            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Entry Error", "A customer with this ID already exists. Please enter a unique ID.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while adding the customer: " + e.getMessage());
        }

        loadData();
        interfaceController controller = new interfaceController();
        controller.displayNumOfCustomers();
    }

    private void deleteCustomer(int custId) {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Customer WHERE customerId = " + custId);
            loadData();
            interfaceController controller = new interfaceController();
            controller.displayNumOfCustomers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearAllCustomers() {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Customer");
            loadData();
            interfaceController controller = new interfaceController();
            controller.displayNumOfCustomers();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleDelete() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deleteCustomer(selected.getCustomerId());
        }
    }


    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleClearAll() {
        clearAllCustomers();
    }

    @FXML
    public void updateCustomerName(TableColumn.CellEditEvent<Customer, String> event) {
        Customer customer = event.getRowValue();
        customer.setCustomerName(event.getNewValue());
        updateCustomer(customer);
    }

    @FXML
    public void updateAddress(TableColumn.CellEditEvent<Customer, String> event) {
        Customer customer = event.getRowValue();
        customer.setAddress(event.getNewValue());
        updateCustomer(customer);
    }
    @FXML
    public void updatePhone(TableColumn.CellEditEvent<Customer, String> event) {
        Customer customer = event.getRowValue();
        customer.setPhone(event.getNewValue());
        updateCustomer(customer);
    }

    @FXML
    public void updateEamil(TableColumn.CellEditEvent<Customer, String> event) {
        Customer customer = event.getRowValue();
        customer.setEmail(event.getNewValue());
        updateCustomer(customer);
    }


    private void updateCustomer(Customer customer) {
        String sql = "UPDATE Customer SET customerName = ?, address = ?, phone = ?, email = ? WHERE customerId = ?";
        try (Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getEmail());
            stmt.setInt(5, customer.getCustomerId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while updating the customer: " + e.getMessage());
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

            currentStage.setScene(scene);
            currentStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

