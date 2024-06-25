import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BillCreator {
    private static final Logger LOGGER = Logger.getLogger(BillCreator.class.getName());
    private Scanner input;
    private Product product;
    private Customer customer;
    private Order order;

    public BillCreator(Product product, Customer customer, Order order, Scanner input) {
        this.input = input;
        this.product = product;
        this.customer = customer;
        this.order = order;
    }

    public void createBill() {
        int custId = getCustomerId();
        if (custId == -1) return;

        Helper.displayProductList(product);
        Map<Integer, Integer> productQuantities = new HashMap<>();
        double totalAmount = 0.0;
        while (true) {
            int productId = Helper.getExistingProductId(product, input,"\nEnter Product ID (or 0 to finish): ");
            if (productId == 0) break;

            System.out.print("Enter quantity: ");
            int quantity = input.nextInt();

            if(quantity < 1){
                System.out.println("\nProduct Not Added.");
                continue;
            }

            if (productQuantities.containsKey(productId)) {
                quantity += productQuantities.get(productId);
            }

            if (!checkStock(productId, quantity)) {
                System.out.println("Out of stock. Current Stock : "+ product.getStock(productId));
                continue;
            }

            productQuantities.put(productId, quantity);
        }
        if (productQuantities.isEmpty()) {
            System.out.println("\nBill Creation cancelled.");
            return;
        }

        for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();
            float productPrice = product.getFinalPrice(productId);
            totalAmount += productPrice * quantity;
        }

        displayBillPreview(productQuantities, totalAmount);

        System.out.println("\nProceed with this bill? (yes/no)");
        String proceed = input.next();
        if (!proceed.equalsIgnoreCase("yes")) {
            System.out.println("\nBill creation cancelled.");
            return;
        }
        int orderId = order.createOrder(custId, 0);
        if (orderId == -1) {
            LOGGER.log(Level.SEVERE, "Failed to create order.");
            return;
        }
        for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();
            float productPrice = product.getFinalPrice(productId);
            float tax = product.getTax(productId);

            if (!order.addOrderItem(orderId, productId, productPrice, quantity, tax)) {
                LOGGER.log(Level.SEVERE, "Failed to add order item for product ID: " + productId);
                return;
            }
            product.updateStock(productId, -quantity); 
        }

        double discount = 0;
        String couponCode = null;
        System.out.println("Add discount? (yes/no)");
        String discountResponse = input.next();
        if (discountResponse.equalsIgnoreCase("yes")) {
            System.out.println("Choose discount type:\n1. Customer discount\n2. Coupon discount");
            int discountType = input.nextInt();
            if (discountType == 1) {
                do{
                System.out.print("Enter discount percentage: ");
                discount = input.nextDouble();
                if(discount <0 || discount > 100){
                    System.out.println("\nInvalid Discount . Discount Range 0% - 100%");
                }
                }while(discount <0 || discount > 100);
            } else if (discountType == 2) {
                input.nextLine();
                System.out.print("Enter coupon code (0 if none): ");
                String coupon = input.nextLine();
                if(!coupon.equals("0")){
                    discount = applyCoupon(custId,coupon);
                    if (discount > 0) {
                        couponCode = coupon;
                    }
                }
            }
        }
        Helper.displayBill(order,orderId, discount, totalAmount);
        processPayment(orderId, totalAmount, discount);
        if (couponCode != null) {
            order.updateCouponCode(orderId, couponCode);
        }
        generateCoupon(custId, totalAmount);
        System.out.println("\nBill created successfully!");
    }

    private int getCustomerId() {
        System.out.print("Is the customer new? (y/n): ");
        String customerStatus = input.next();

        if (customerStatus.equalsIgnoreCase("y")) {
            return addNewCustomer();
        } else if (customerStatus.equalsIgnoreCase("n")) {
            Helper.displayCustomerList(customer);
            return Helper.getExistingCustomerId(customer,input);
        } else {
            System.out.println("Invalid input. Please enter 'y' or 'n'.");
            return getCustomerId();
        }
    }

    private int addNewCustomer() {
        input.nextLine();
        System.out.print("Enter Customer Name: ");
        String name = input.nextLine();
        System.out.print("Enter Customer Phone: ");
        String phone = input.nextLine();
        System.out.println("\nCustomer added successfully.\n");
        return customer.addCustomer(name, phone);
    }

    

    private boolean checkStock(int productId, int quantity) {
        return product.getStock(productId) >= quantity;
    }
    private void displayBillPreview(Map<Integer, Integer> productQuantities, double totalAmount) {
        System.out.println("\nBill Preview:");
        System.out.println("\nS.No\tProduct Name\t\tPrice\tTax\tQuantity\tItem Total");
        int serialNo = 1;
        for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();
            String productName = product.getName(productId); 
            float productPrice = product.getFinalPrice(productId);
            float productTax = product.getTax(productId);
            double itemTotal = productPrice * quantity;
            System.out.printf("%d\t%-15s\t\t%.2f\t%.2f%%\t%d\t\t%.2f\n", serialNo, productName, productPrice, productTax, quantity, itemTotal);
            serialNo++;
        }
        System.out.printf("\nTotal: %.2f\n", totalAmount);
    }
    


    private double applyCoupon(int custId,String couponCode) {
        ResultSet rs = customer.getCoupon(custId, couponCode);
        try {
            if (rs.next()) {
                double discount = rs.getDouble("discount");
                customer.removeCoupon(couponCode);
                System.out.println("Coupon applied successfully. Discount: " + discount + "%");
                return discount;
            } else {
                System.out.println("Invalid coupon code.");
                return 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error applying coupon: ", e);
            return 0;
        }
    }

    private void processPayment(int orderId, double totalAmount, double discount) {
        String[] paymentMethods = {"Cash", "UPI", "Credit Card", "Coupon"};
        System.out.println("Payment Method:\n1. Cash\n2. UPI\n3. Credit Card\n4. Coupon");
        System.out.print("Enter the Payment Method: ");
        int payment = input.nextInt();
        double discountAmount = (totalAmount * discount) / 100;
        double grandTotal = totalAmount - discountAmount;
        if (!order.updateOrderTotal(orderId,discount,grandTotal, paymentMethods[payment - 1])) {
            LOGGER.log(Level.SEVERE, "Failed to update order total.");
        }
    }

    private void generateCoupon(int custId, double totalAmount) {
        if (totalAmount > 10000 && totalAmount < 100000) {
            String couponCode = "DISC2.5" + System.currentTimeMillis();
            customer.addCoupon(custId, couponCode, 2.5);
            System.out.println("Coupon generated for 2.5% discount: " + couponCode);
        }
        if (totalAmount > 100000) {
            String couponCode = "DISC5" + System.currentTimeMillis();
            customer.addCoupon(custId, couponCode, 5);
            System.out.println("Coupon generated for 5% discount: " + couponCode);
         }
    }
}
