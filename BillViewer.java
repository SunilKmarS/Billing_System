import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BillViewer {
    private static final Logger LOGGER = Logger.getLogger(BillViewer.class.getName());
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Scanner input;  
    public Order order;
    public Customer customer;

    public BillViewer(Customer customer,Order order,Scanner input) {
        this.customer = customer;
        this.order = order;
        this.input = input;
    }

    public void viewBills() {
        System.out.println("\nView Bills\n");
        System.out.println("Choose filter option:");
        System.out.println("1. All Bills");
        System.out.println("2. Filter by Customer ID");
        System.out.println("3. Filter by Date");
        System.out.println("4. Filter by Date Range");
        System.out.println("5. Back to Main Menu");

        int choice = input.nextInt();
        switch (choice) {
            case 1:
                displayFilteredBills(order.getBills());
                break;
            case 2:    
                Helper.displayCustomerList(customer);
                int custId = Helper.getExistingCustomerId(customer, input);
                displayFilteredBills(order.getBillsByCustomerId(custId));
                break;
            case 3:
                viewBillsByDate();
                break;
            case 4:
                viewBillsByDateRange();
                break;
            case 5:
                System.out.println("Returning to main menu...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private void viewBillsByDate() {
        input.nextLine();
        System.out.print("Enter Date (yyyy-MM-dd): ");
        String dateStr = input.next().trim();

        try {
            Date date = dateFormat.parse(dateStr);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            displayFilteredBills(order.getBillsByDate(sqlDate));
        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, "Invalid date format. Please enter date in yyyy-MM-dd format.");
        }
    }

    private void viewBillsByDateRange() {
        System.out.print("Enter Start Date (yyyy-MM-dd): ");
        input.nextLine();
        String startDateStr = input.nextLine();

        System.out.print("Enter End Date (yyyy-MM-dd): ");
        String endDateStr = input.nextLine();

        try {
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);
            java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
            java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
            displayFilteredBills(order.getBillsByDateRange(sqlStartDate, sqlEndDate));
        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, "Invalid date format. Please enter date in yyyy-MM-dd format.");
        }
    }

    private void displayFilteredBills(ResultSet bills) {
        try {
            if (!bills.next()) {
                System.out.println("\nNo Bills Found !\n");
                return;
            }
            System.out.println("\nFiltered Bills\n");
            do {
                double amount = bills.getDouble("amount");
                double discount = bills.getDouble("discount");
                double total = (amount*100)/(100-discount);
                Helper.displayBill(order, bills.getInt("orderId"), discount, total);
            } while (bills.next());
            bills.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error displaying filtered bills: ", e);
        }
    }
    
    
}

