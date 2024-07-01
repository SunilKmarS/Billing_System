import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    static Scanner input = new Scanner(System.in);
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static void displayMenu() {
        System.out.println("\n1. Create a Bill");
        System.out.println("2. View Bills");
        System.out.println("3. Update Inventory");
        System.out.println("4. Update User");
        System.out.println("5. View Report");
        System.out.println("6. Exit");
        System.out.print("\nEnter Your Choice: ");
    }

    public static void main(String[] args) {
        int choice = 0;
        System.out.println("Welcome to the Super Market Billing System!");
        Product product = new Product();
        Customer customer = new Customer();
        Order order = new Order();
        BillCreator billCreator = new BillCreator(product,customer,order,input);
        BillViewer billViewer = new BillViewer(customer,order,input);
        InventoryManager inventoryManager = new InventoryManager(product, input);
        Report report = new Report(product, customer, order, input);
        while (choice != 6) {
            displayMenu();
            choice = input.nextInt();
            switch (choice) {
                case 1:
                    billCreator.createBill();
                    break;
                case 2:
                    billViewer.viewBills();
                    break;
                case 3:
                    inventoryManager.updateInventory();
                    break;
                case 4:
                    updateUser(customer);
                    break;
                case 5:
                    report.getReport();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        product.closeConnection();
        customer.closeConnection();
        order.closeConnection();
    }

    private static void updateUser(Customer customer) {
        Helper.displayCustomerList(customer);
        try {
            int custId = Helper.getExistingCustomerId(customer, input);
            System.out.print("Enter new Name: ");
            input.nextLine();
            String newName = input.nextLine();

            System.out.print("Enter new Phone Number: ");
            String newPhone = input.nextLine();

            if (customer.updateCustomer(custId, newName, newPhone)) {
                System.out.println("Customer details updated successfully.");
            } else {
                System.out.println("Failed to update customer details.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating customer: ", e);
        }
    }

}
