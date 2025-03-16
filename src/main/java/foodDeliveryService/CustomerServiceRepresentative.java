package foodDeliveryService;

public class CustomerServiceRepresentative extends Employee {
    private int salary;

    // Constructor
    public CustomerServiceRepresentative(int empID, String empName, String phone, String email, int workHours, int salary) {
        super(empID, empName, phone, email, workHours);
        this.salary = salary;
    }

    // Getters and Setters
    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}
