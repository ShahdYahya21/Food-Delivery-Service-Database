package foodDeliveryService;

public class Driver extends Employee {
    private int wage;
    private String licenseNum;
    private String vehicleType;

    public Driver(int empID, String empName, String phone, String email, int workHours, int wage, String licenseNum, String vehicleType) {
        super(empID, empName, phone, email, workHours); // Call the constructor of the superclass (Employee)
        this.wage = wage;
        this.licenseNum = licenseNum;
        this.vehicleType = vehicleType;
    }




    // Getters and Setters
    public int getWage() {
        return wage;
    }

    public void setWage(int wage) {
        this.wage = wage;
    }

    public String getLicenseNum() {
        return licenseNum;
    }

    public void setLicenseNum(String licenseNum) {
        this.licenseNum = licenseNum;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    // Override toString() method
    @Override
    public String toString() {
        return "Driver{" +
                "wage=" + wage +
                ", licenseNum='" + licenseNum + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                "} " + super.toString();
    }
}
