import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Report {
    private static final Logger LOGGER = Logger.getLogger(Report.class.getName());
    private final Scanner input;
    private final Product product;
    private final Customer customer;
    private final Order order;

    public Report(Product product, Customer customer, Order order, Scanner input) {
        this.input = input;
        this.product = product;
        this.customer = customer;
        this.order = order;
    }

    public void getReport() {
        System.out.println("\nView Report\n1. Customer Reports\n2. Product Report\n3. Store Report\n4. Back to Main Menu\nEnter Your Choice: ");
        int choice = input.nextInt();
        switch (choice) {
            case 1 -> customerReport();
            case 2 -> productReport();
            case 3 -> storeReport();
            case 4 -> {}
            default -> System.out.println("Invalid choice. Please enter a number between 1 and 4.");
        }
    }

    private void customerReport() {
        System.out.println("1. Overall Report\n2. Specific Customer Report\n3. Back to Report Menu\nEnter your Choice:");
        int choice = input.nextInt();
        switch (choice) {
            case 1 -> overallCustomerReport();
            case 2 -> {     
                Helper.displayCustomerList(customer);
                int custId = Helper.getExistingCustomerId(customer, input);
                specificCustomerReport(custId);
            }
            case 3 -> {}
            default -> System.out.println("Invalid choice. Please enter a number between 1 and 3.");
        }
    }

    private void overallCustomerReport() {
        try (ResultSet rs = customer.getOverallReport()) {
            if (!rs.isBeforeFirst()) {
                System.out.println("No data found.");
                return;
            }
            System.out.println("\nOverall Report :");
            System.out.println("\nCustomerID\tCustomer Name\tPhone Number\tTotalOrders\tTotalSpent\tAvgOrderValue");
            while (rs.next()) {
                System.out.printf("%-10d\t%-15s\t%-15s\t%-11d\t%-10.2f\t%-13.2f\n",
                        rs.getInt("custId"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getInt("totalOrders"),
                        rs.getDouble("totalSpent"),
                        rs.getDouble("avgOrderValue"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error Getting Overall Report: ", e);
        }
    }

    private void specificCustomerReport(int custId) {
        try (ResultSet customerRs = customer.getCustomer(custId)) {
            if (!customerRs.isBeforeFirst()) {
                System.out.println("\nCustomer not found.\n");
                return;
            }
            if (customerRs.next()) {
                System.out.printf("\nCustomerID: %-10d\nName: %-15s\nPhone: %-15s\n",
                        customerRs.getInt("custId"),
                        customerRs.getString("name"),
                        customerRs.getString("phone"));
            }

            try (ResultSet orderRs = order.getBillsByCustomerId(custId)) {
                if (!orderRs.isBeforeFirst()) {
                    System.out.println("\nNo orders found for this customer.");
                } else {
                    System.out.println("\nOrderID\tOrderDate\t\tAmount\t\tPaymentMethod\tDiscount");
                    while (orderRs.next()) {
                        System.out.printf("%d\t%-10s\t%-10.2f\t%-15s\t%-8.2f\n",
                                orderRs.getInt("orderId"),
                                orderRs.getTimestamp("orderDate"),
                                orderRs.getDouble("amount"),
                                orderRs.getString("paymentMethod"),
                                orderRs.getDouble("discount"));
                    }
                }
            }

            String mostPreferredPayment = customer.getMostPreferedPayment(custId);
            System.out.printf("\nMost Preferred Payment Method: %s\n", mostPreferredPayment != null ? mostPreferredPayment : "No data found.");

            try (ResultSet productRs = customer.getMostBoughtProduct(custId)) {
                if (productRs.isBeforeFirst() && productRs.next()) {
                    System.out.printf("\nMost Bought Product: %s (ID: %d) - Quantity Bought: %d\n",
                            productRs.getString("name"),
                            productRs.getInt("productId"),
                            productRs.getInt("totalBought"));
                } else {
                    System.out.println("\nNo Most Bought product data found for this customer.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error Getting Specific Customer Report: ", e);
        }
    }

    private void productReport() {
        try (ResultSet rs = product.getProductReport()) {
            if (!rs.isBeforeFirst()) {
                System.out.println("\nNo product data found.");
                return;
            }
            System.out.println("ProductID\tName\t\tOriginalPrice\tTax\t\tFinalPrice\tStock\tTotalSold\tRevenueGenerated");
            while (rs.next()) {
                System.out.printf("%-10d\t%-15s\t%-13.2f\t%-8.2f\t%-10.2f\t%d\t%-10d\t%-15.2f\n",
                        rs.getInt("productId"),
                        rs.getString("name"),
                        rs.getDouble("originalPrice"),
                        rs.getDouble("tax"),
                        rs.getDouble("finalPrice"),
                        rs.getInt("stock"),
                        rs.getInt("totalSold"),
                        rs.getDouble("revenueGenerated"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error Getting Product Report: ", e);
        }
    }
    private void storeReport() {
        try (ResultSet rs = order.getTotalRevenue()) {
            if (rs.next()) {
                System.out.printf("\nTotal Orders: %-10d\nTotal Revenue: %-10.2f\nTotal Discounts: %-10.2f\nAverage Order Value: %-10.2f\n",
                        rs.getInt("totalOrders"),
                        rs.getDouble("totalRevenue"),
                        rs.getDouble("totalDiscounts"),
                        rs.getDouble("avgOrderValue"));
            } else {
                System.out.println("\nNo store revenue data found.");
            }
            printMostSoldProduct();
            printLeastSoldProduct();
            Helper.displayProductList(product);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error Getting Store Report: ", e);
        }
    }

    private void printMostSoldProduct() {
        try (ResultSet mostSoldRs = order.getMostSold()) {
            if (mostSoldRs.next()) {
                System.out.printf("\nMost Sold Product: %s (ID: %d) - Quantity Sold: %d\n",
                        mostSoldRs.getString("name"),
                        mostSoldRs.getInt("productId"),
                        mostSoldRs.getInt("totalSold"));
            } else {
                System.out.println("\nNo most sold product data found.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error Getting Most Sold Product: ", e);
        }
    }

    private void printLeastSoldProduct() {
        try (ResultSet leastSoldRs = order.getLeastSold()) {
            if (leastSoldRs.next()) {
                System.out.printf("\nLeast Sold Product: %s (ID: %d) - Quantity Sold: %d\n",
                        leastSoldRs.getString("name"),
                        leastSoldRs.getInt("productId"),
                        leastSoldRs.getInt("totalSold"));
            } else {
                System.out.println("\nNo least sold product data found.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error Getting Least Sold Product: ", e);
        }
    }
}
