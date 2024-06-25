By, 
	SUNIL KUMAR S,
	B.TECH - IT,
	GOVERNMENT COLLEGE OF TECHNOLOGY,COIMBATORE

## INSTALLATION

Install Database and Table using Install.java

```$java Install.java```

Enter the username and password 
The username and password will be stored in db.properties and will be retrieved by Connector.java

## RUNNING THE APPLICATION

Compile all java files

```$javac *.java``

Run the Main APPLICATION

```$java Main.java or $java Main```



### Super Market Billing System

## Overview

The Super Market Billing System is a Java application designed to manage various tasks within a supermarket environment, including bill creation, viewing bills, updating inventory, managing user information, and generating reports. This application provides a console-based user interface to interact with these functionalities.

### Features
    Create a Bill: Generate a new bill for a customer.
    View Bills: Display the list of all generated bills.
    Update Inventory: Modify the inventory details.
    Update User: Update customer details.
    View Report: Generate and view reports based on product, customer, and order data.
    Exit: Exit the application.

## Getting Started

## Prerequisites

    Java Development Kit (JDK) 8 or higher installed.
    A SQL database setup with the required tables and data.

## Main Components
    Main.java: The main class containing the application entry point and menu logic.
    Product: Manages product-related operations and interactions with the database.
    Customer: Manages customer-related operations and interactions with the database.
    Order: Manages order-related operations and interactions with the database.
    BillCreator: Handles the creation of new bills.
    BillViewer: Handles the viewing of existing bills.
    InventoryManager: Handles updates to the inventory.
    Report: Generates and displays reports based on the current data.
    Helper: Contains helper methods for common operations like displaying customer lists and retrieving customer IDs.

## Usage

    Welcome Message: Upon starting the application, a welcome message is displayed.
    Menu: The main menu presents six options:
    Create a Bill: Follow the prompts to create a new bill.
    View Bills: Choose this option to list all bills.
    Update Inventory: Update inventory details through a series of prompts.
    Update User: Update customer information.
    View Report: Generate and view various reports.
    Exit: Exit the application gracefully.
    


## Setup
        ```

### Step 3: Compile and Install
1. **Open Terminal in project root folder**:
    ```sh
    java Install.java
    ```

2. **Compile the application**:
    ```sh
    javac *.java
    ```

3. **Run the application**:
    ```sh
    java Main
    ```

## Menu Structure

## Main Menu 
```
Welcome to the Super Market Billing System!

1. Create a Bill
2. View Bills
3. Update Inventory
4. Update User
5. View Report
6. Exit

Enter Your Choice: 
```
## Option 2 Menu
```
View Bills
Choose filter option:
1. All Bills
2. Filter by Customer ID
3. Filter by Date
4. Filter by Date Range
5. Back to Main Menu
```
## Option 3 Menu
```
Update Inventory

Choose an option:
1. Add a Product
2. Update a Product
3. Bulk Restock
4. Back to Main Menu
```
## Option 5 Menu
```
View Report
1. Customer Reports
2. Product Report
3. Store Report
4. Back to Main Menu
Enter Your Choice:
```




### Detailed Menu Options
1. **Create a Bill**:
    - Generate a new bill for a customer.
  
2. **View Bills**:
    - Display the list of all generated bills.

3. **Update Inventory**:
    - Modify the inventory details.

4. **Update User**:
    - Update customer details.

5. **View Report**:
    - Generate and view reports based on product, customer, and order data.

6. **Exit**:
    - Exit the application.

## Individual Java Files

## Main.java
- **Description**: Entry point for the application, handles main menu and user interaction.
- **Classes Used**: Product, Customer, Order, BillCreator, BillViewer, InventoryManager, Report, Helper.
- **Methods**:
  - `displayMenu()`: Displays the main menu options.
  - `main()`: Main method, contains the application loop and handles user choices.
  - `updateUser(Customer customer)`: Updates customer details based on user input.

## Product.java
- **Description**: Manages product-related operations and interactions with the database.
- **Methods**:
  - `addProduct()`: Adds a new product to the inventory.
  - `updateProduct()`: Updates existing product details.
  - `listProducts()`: Lists all products.
  - `closeConnection()`: Closes the database connection.

## Customer.java
- **Description**: Manages customer-related operations and interactions with the database.
- **Methods**:
  - `addCustomer()`: Adds a new customer.
  - `updateCustomer(int custId, String newName, String newPhone)`: Updates existing customer details.
  - `listCustomers()`: Lists all customers.
  - `closeConnection()`: Closes the database connection.

## Order.java
- **Description**: Manages order-related operations and interactions with the database.
- **Methods**:
  - `createOrder()`: Creates a new order.
  - `viewOrder()`: Views existing orders.
  - `closeConnection()`: Closes the database connection.

## BillCreator.java
- **Description**: Handles the creation of new bills.
- **Methods**:
  - `createBill()`: Prompts the user for bill details and creates a new bill.

## BillViewer.java
- **Description**: Handles the viewing of existing bills.
- **Methods**:
  - `viewBills()`: Displays the list of all generated bills.

## InventoryManager.java
- **Description**: Handles updates to the inventory.
- **Methods**:
  - `updateInventory()`: Prompts the user for inventory details and updates the inventory.

## Report.java
- **Description**: Generates and displays reports based on the current data.
- **Methods**:
  - `getReport()`: Generates and displays various reports.

## Helper.java
- **Description**: Contains helper methods for common operations.
- **Methods**:
  - `displayCustomerList(Customer customer)`: Displays a list of customers.
  - `getExistingCustomerId(Customer customer, Scanner input)`: Retrieves an existing customer ID based on user input.

## Error Handling
The application includes basic error handling using try-catch blocks, especially for updating customer details. Errors are logged using the Java Logger class.

## Closing Resources
Before exiting the application, connections to the database are closed to ensure there are no memory leaks or open connections left.

## Logging
The application uses the java.util.logging package to log important events, especially errors during customer updates.

