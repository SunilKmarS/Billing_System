import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InventoryManager {
    private static final Logger LOGGER = Logger.getLogger(InventoryManager.class.getName());
    private final Product product;
    private final Scanner input;

    public InventoryManager(Product product, Scanner input) {
        this.product = product;
        this.input = input;
    }

    public void updateInventory() {
        System.out.println("\nUpdate Inventory\n");
        System.out.println("Choose an option:");
        System.out.println("1. Add a Product");
        System.out.println("2. Update a Product");
        System.out.println("3. Bulk Restock");
        System.out.println("4. Back to Main Menu");

        int choice = input.nextInt();

        switch (choice) {
            case 1:
                addProduct();
                break;
            case 2:
                updateProduct();
                break;
            case 3:
                bulkRestock();
                break;
            case 4:
                System.out.println("Returning to main menu...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private void addProduct() {
        try {
            System.out.println("Enter Product Name: ");
            String name = input.next();
            System.out.println("Enter Original Price: ");
            double originalPrice = input.nextDouble();
            System.out.println("Enter Tax: ");
            double tax = input.nextDouble();
            System.out.println("Enter Stock: ");
            int stock = input.nextInt();

            if (product.addProduct(name, originalPrice, tax, stock)) {
                System.out.println("Product added successfully.");
            } else {
                LOGGER.log(Level.SEVERE, "Failed to add product.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding product: ", e);
        }
    }

    private void updateProduct() {
        try {
            System.out.println("Choose Product to Update:");
            Helper.displayProductList(product);
            int productId = Helper.getExistingProductId(product, input,"\nEnter Product ID to Update (0 if none): ");
            if(productId==0) return;
            if (product.getProductById(productId).next()) {
                System.out.println("Choose attribute to update:");
                System.out.println("1. Name");
                System.out.println("2. Original Price");
                System.out.println("3. Tax");
                System.out.println("4. Stock");
                int attributeChoice = input.nextInt();

                switch (attributeChoice) {
                    case 1:
                        updateProductName(productId);
                        break;
                    case 2:
                        updateProduct(productId, "originalPrice");
                        break;
                    case 3:
                        updateProduct(productId, "tax");
                        break;
                    case 4:
                        updateProductStock(productId);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating product: ", e);
        }
    }

    

    private void updateProductName(int productId) {
        try {
            System.out.println("Enter new Product Name: ");
            String newName = input.next();
            if (product.updateProductName(productId, newName)) {
                System.out.println("Product name updated successfully.");
            } else {
                LOGGER.log(Level.SEVERE, "Failed to update product name.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating product name: ", e);
        }
    }

    private void updateProduct(int productId, String columnName) {
        try {
            System.out.println("Enter new " + columnName + ": ");
            double newValue = input.nextDouble();
            if (product.updateProduct(productId, columnName, newValue)) {
                System.out.println("Product " + columnName + " updated successfully.");
            } else {
                LOGGER.log(Level.SEVERE, "Failed to update product " + columnName + ".");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating product " + columnName + ": ", e);
        }
    }

    private void updateProductStock(int productId) {
        try {
            System.out.println("Enter new Stock: ");
            int newStock = input.nextInt();
            if (product.updateProductStock(productId, newStock)) {
                System.out.println("Product stock updated successfully.");
            } else {
                LOGGER.log(Level.SEVERE, "Failed to update product stock.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating product stock: ", e);
        }
    }

    private void bulkRestock() {
        try {
            while (true) {
                Helper.displayProductList(product);
                int productId = Helper.getExistingProductId(product, input,"Enter Product ID to restock (or 0 to finish): ");
                if (productId == 0) {
                    break;
                }
                System.out.println("Enter Stock to add: ");
                int additionalStock = input.nextInt();
                if (product.updateProductStock(productId, product.getStock(productId) + additionalStock)) {
                    System.out.println("Product stock updated successfully.");
                } else {
                    LOGGER.log(Level.SEVERE, "Failed to update product stock.");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during bulk restocking: ", e);
        }
    }
}
