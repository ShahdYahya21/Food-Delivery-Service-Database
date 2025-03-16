package foodDeliveryService;

public class Customer {
    private int customerId;
    private String customerName;
    private String address;
    private String phone;
    private String email;
    private int customerServiceRepresentativeId;

    // Constructor
    public Customer(int customerId, String customerName, String address, String phone, String email, int customerServiceRepresentativeId) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.customerServiceRepresentativeId = customerServiceRepresentativeId;
    }

    // Getters and Setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCustomerServiceRepresentative() {
        return customerServiceRepresentativeId;
    }

    public void setCustomerServiceRepresentative(int customerServiceRepresentative) {
        this.customerServiceRepresentativeId = customerServiceRepresentative;
    }

    // toString method
    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", customerServiceRepresentative='" + customerServiceRepresentativeId + '\'' +
                '}';
    }
}
