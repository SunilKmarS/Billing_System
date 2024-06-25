import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;

public class Helper {
    private static final Logger LOGGER = Logger.getLogger(InventoryManager.class.getName());

    public static void displayBill(Order order, int orderId, double discount, double totalAmount) {
        ResultSet orderDetails = order.getOrderDetails(orderId);
        ResultSet orderList = order.getOrderItems(orderId);
        try {
            int serialNo = 1;
            while (orderDetails.next()) {
                System.out.printf("\nCustomer Name: %s\tDate: %s\n\n", orderDetails.getString("name"), orderDetails.getTimestamp("orderDate"));
            }
            System.out.println("S.No\tProduct Name\tProduct Price\tTax Included\tQuantity\tItem Total");
            while (orderList.next()) {
                System.out.printf("%d\t%-15s\t%.2f\t\t%.2f%%\t\t%d\t\t%.2f\n", serialNo, orderList.getString("name"), orderList.getDouble("price"), orderList.getDouble("tax"), orderList.getInt("quantity"), orderList.getDouble("total"));
                serialNo++;
            }
            System.out.printf("\nTotal: %.2f", totalAmount);
            System.out.printf("\nDiscount: %.2f%%", discount);
            System.out.printf("\nGrand Total: %.2f\n\n", totalAmount-((totalAmount*discount)/100));
            orderDetails.close();
            orderList.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error displaying order details: ", e);
        }
    }

    public static void displayCustomerList(Customer customer) {
        ResultSet rs = customer.getCustomerList();
        if (rs != null) {
            try {
                System.out.println("\nCustomer List");
                System.out.println("Customer Id\tName");
                while (rs.next()) {
                    int custId = rs.getInt("custId");
                    String name = rs.getString("name");
                    System.out.println(custId + "\t\t" + name);
                }
                System.out.println();
                rs.close();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error displaying customer list: ", e);
            }
        } else {
            LOGGER.warning("Failed to fetch customer list.");
        }
    }
    public static void displayProductList(Product product) {
        try {
            ResultSet products = product.getProductStocks();
            if (!products.isBeforeFirst()) {
                System.out.println("\nNo stock data found.");
                return;
            }
            System.out.println("\nAvailable products:");
            System.out.println("Product ID\tProduct Name\t\tStock");
            while (products.next()) {
                System.out.printf("%-10d\t%-20s\t%-15d\n",
                        products.getInt("productId"),
                        products.getString("name"),
                        products.getInt("stock"));
            }
            products.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving products: ", e);
        }
    }
    public static int getExistingCustomerId(Customer customer,Scanner input) {
        System.out.print("\nEnter Customer ID: ");
        int custId = input.nextInt();
        if(checkCustomer(customer,custId)){
            return custId;
        }
        System.out.println("\nInvalid Customer Id. Customer Does not Exist .");
        return getExistingCustomerId(customer,input);
    }
    public static boolean checkCustomer(Customer customer,int custId){
        try {
            if(!customer.getCustomer(custId).isBeforeFirst()){
                return false;
            }
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error Checking Customer Id: ", e);
            return false;
        }
    }
    public static int getExistingProductId(Product product, Scanner input,String prompt) {
        System.out.print(prompt);
        int productId = input.nextInt();
        if(productId == 0){
            return productId;
        }
        if (checkProduct(product, productId)) {
            return productId;
        }
        System.out.println("\nInvalid Product Id. Product does not exist.");
        return getExistingProductId(product, input,prompt);
    }
    
    public static boolean checkProduct(Product product, int productId) {
        try {
            if (!product.getProductById(productId).isBeforeFirst()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error Checking Product Id: ", e);
            return false;
        }
    }
    
}
