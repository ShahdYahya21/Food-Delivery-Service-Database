## Food Delivery Management System
### Overview
- This project is a food delivery management system with an admin interface to manage customers, employees, restaurants, drivers, orders, and payments. The admin can add, delete, modify, refresh, and clear all records. The dashboard provides insights on total revenue, ratings, orders, and delivery times.
---
### Database Relationships
#### **1. Customer ↔ OrderTable (One-to-Many)**  
- A customer can place multiple orders, but each order belongs to a single customer.  
- **Relationship:** `OrderTable.customerId → Customer.customerId`

#### **2. OrderTable ↔ OrderLine (One-to-Many)**  
- An order can have multiple items, and each item (OrderLine) is linked to a single order.  
- **Relationship:** `OrderLine.orderId → OrderTable.orderId`

#### **3. OrderLine ↔ MenuItem (Many-to-One)**  
- Each OrderLine references one MenuItem, but the same MenuItem can appear in multiple OrderLines.  
- **Relationship:** `OrderLine.menuItemID → MenuItem.Item_ID`

#### **4. MenuItem ↔ Restaurant (Many-to-One)**  
- A restaurant can offer multiple menu items, but each menu item belongs to one restaurant.  
- **Relationship:** `MenuItem.RestaurantID → Restaurant.RestaurantID`

#### **5. OrderTable ↔ Restaurant (Many-to-One)**  
- A restaurant can receive many orders, but each order is placed at one restaurant.  
- **Relationship:** `OrderTable.RestaurantID → Restaurant.RestaurantID`

#### **6. OrderTable ↔ Payment (One-to-One)**  
- Each order has one payment, and each payment is linked to exactly one order.  
- **Relationship:** `Payment.orderId → OrderTable.orderId`

#### **7. OrderTable ↔ driverToOrder ↔ Drivers (Many-to-Many via driverToOrder)**  
- A driver can deliver multiple orders, and an order can be delivered by multiple drivers (via `driverToOrder`).  
- **Relationship:**  
  - `driverToOrder.orderId → OrderTable.orderId`  
  - `driverToOrder.driverId → Drivers.Driver_ID`

#### **8. Restaurant ↔ Offer (One-to-Many)**  
- A restaurant can create multiple offers, but each offer belongs to a single restaurant.  
- **Relationship:** `Offer.RestaurantID → Restaurant.RestaurantID`

#### **9. MenuItem ↔ Offer (Many-to-One)**  
- Multiple menu items can be part of the same offer, but each menu item can only have one offer.  
- **Relationship:** `MenuItem.OfferID → Offer.Offer_ID`

#### **10. Customer ↔ Rating ↔ Restaurant (Many-to-Many via Rating)**  
- A customer can rate multiple restaurants, and each restaurant can receive multiple ratings.  
- **Relationship:**  
  - `Rating.customerId → Customer.customerId`  
  - `Rating.RestaurantID → Restaurant.RestaurantID`

#### **11. Customer ↔ CustomerServiceRepresentatives (Many-to-One)**  
- A customer service representative assists multiple customers, but each customer is linked to only one representative.  
- **Relationship:** `Customer.customerServiceRepresentative → CustomerServiceRepresentatives.CSR_ID`

#### **12. Employees ↔ Drivers (One-to-One)**  
- A driver is also an employee, so there is a one-to-one relationship between `Employees` and `Drivers`.  
- **Relationship:** `Drivers.Driver_ID → Employees.Emp_ID`

#### **13. Restaurant ↔ City (Many-to-One)**  
- A city can have multiple restaurants, but each restaurant is located in one city.  
- **Relationship:** `Restaurant.CityZipCode → City.ZipCode`


#### ERD Diagram
![ERD Diagram](https://github.com/user-attachments/assets/f06d84c8-f75a-4274-ad3d-22d891fbbeb0)

---

### Features of the Admin Interface

#### 1. Login Interface
- Ensures that the user is an admin.

<img src="https://github.com/user-attachments/assets/c8bfab5d-ed4f-478a-be57-c772e4d829ca" width="400" />

---

#### 2. Dashboard Overview

<img width="500" alt="Dashboard Overview" src="https://github.com/user-attachments/assets/776b1b39-79b8-41d3-96c6-f8c6212bf214" />

Displays key statistics:
- **Number of Customers**
- **Number of Restaurants**
- **Number of Orders**
- **Total Revenue**
- **Graph for total revenue per month with a dropdown to select a specific month**
- **A sidebar menu for quick access to various tables**
- **Two additional tables to display statistics for restaurants and drivers**


---

#### 3. Navigation Panel
Sidebar menu for easy access to different sections:
- **Cities**
- **Customers**
- **Employees**
- **Customer Service Representatives**
- **Drivers**
- **Restaurants**
- **Menu Items**
- **Orders**
- **Order Lines**
- **Payments**
- **Offers**
- **Ratings**  
For example, here are the tables for **Customers** and **Orders**:

<img width="400" alt="Customer Table" src="https://github.com/user-attachments/assets/7b7ffeb8-3305-4ac1-9c53-1281b94aeae5" />
<img width="400" alt="Order Table" src="https://github.com/user-attachments/assets/d1bc2ed2-9a08-480a-8c1f-ed5cfe9bf0e9" />

---

#### 4. Detailed Analytics & Reports

#### a. Restaurant Statistics
- Displays restaurant-specific statistics in a specific month:
  - **Total Revenue**
  - **Average Rating**
  - **Number of Orders**

<img width="400" alt="Restaurant Stats" src="https://github.com/user-attachments/assets/2930cc25-77bc-4697-a741-f3007b65b05d" />
<img width="400" alt="image" src="https://github.com/user-attachments/assets/ee64cacb-e380-4756-8036-159eddde8b5f" />


#### b. Driver Statistics
- Displays driver-specific statistics in a specific month:
  - **Number of Orders**
  - **Average Delivery Time**

<img width="400" alt="Driver Stats" src="https://github.com/user-attachments/assets/fd8f009f-de32-414e-8f04-ab6e49b0746d" />
<img width="400" alt="image" src="https://github.com/user-attachments/assets/2c3176f2-ad8d-48c4-902e-94e07ff5ba8c" />

---
### Technologies Used

#### **1. SQL**
- Manages the database, creating tables, storing data (e.g., customers, orders), and running queries for reports and analytics.

#### **2. Java**
- Handles the backend logic, such as user authentication, data management, and connecting the frontend (FXML) with the database.

#### **3. FXML**
- Designs the user interface (UI) of the admin panel, allowing easy interaction with tables, buttons, and forms.




