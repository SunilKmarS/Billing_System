import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Product{
    Connector con;
    private static final Logger LOGGER = Logger.getLogger(Product.class.getName());
    
    public Product(){
        this.con = new Connector();
    }
    public int getStock(int productId){
        try{
            String sqlQuery = String.format("SELECT stock from PRODUCTS WHERE productId=%s",productId);
            ResultSet result= this.con.executeQuery(sqlQuery);
            result.next();
            return result.getInt(1);
        }catch(Exception e){
            System.out.println(e);
        }
        return 0;
    }
    public float getFinalPrice(int productId){
        try{
            String sqlQuery = String.format("SELECT finalPrice from PRODUCTS WHERE productId=%s",productId);
            ResultSet result= this.con.executeQuery(sqlQuery);
            result.next();
            return result.getInt(1);
        }catch(Exception e){
            System.out.println(e);
        }
        return 0;
    }
    public float getTax(int productId){
        try{
            String sqlQuery = String.format("SELECT tax from PRODUCTS WHERE productId=%s",productId);
            ResultSet result= this.con.executeQuery(sqlQuery);
            result.next();
            return result.getInt(1);
        }catch(Exception e){
            System.out.println(e);
        }
        return 0;
    }
    public String getName(int productId) {
        String sql = "SELECT name FROM PRODUCTS WHERE productId = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching product name for product ID: " + productId, e);
        }
        return null;
    }
    public boolean addProduct(String name,double originalPrice,double tax,int stock){
        try {
            String sql= String.format("INSERT INTO PRODUCTS(name,originalPrice,tax,stock) VALUES(?,?,?,?);");
            PreparedStatement sqlStatement = this.con.prepareStatement(sql);
            sqlStatement.setString(1,name);
            sqlStatement.setDouble(2, originalPrice);
            sqlStatement.setDouble(3,tax);
            sqlStatement.setInt(4,stock);
            return (this.con.executeStatement(sqlStatement));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding product: ", e);
            return false;
        }
    }
    public ResultSet getAllProducts() {
        String query = "SELECT * FROM PRODUCTS";
        try {
            return con.executeQuery(query);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving products: ", e);
            return null;
        }
    }

    public ResultSet getProductById(int productId) {
        String sql = "SELECT * FROM PRODUCTS WHERE productId = ?";
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, productId);
            return stmt.executeQuery();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving product by ID: ", e);
            return null;
        }
    }

    public boolean updateProductName(int productId, String newName) {
        String sql = "UPDATE PRODUCTS SET name = ? WHERE productId = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setInt(2, productId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            } else {
                LOGGER.warning("Failed to update product name.");
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating product name: ", e);
            return false;
        }
    }

    public boolean updateProduct(int productId, String columnName, double newValue) {
        String sql = "UPDATE PRODUCTS SET " + columnName + " = ? WHERE productId = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDouble(1, newValue);
            stmt.setInt(2, productId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            } else {
                LOGGER.warning("Failed to update product " + columnName + ".");
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating product " + columnName + ": ", e);
            return false;
        }
    }

    public boolean updateProductStock(int productId, int newStock) {
        String sql = "UPDATE PRODUCTS SET stock = ? WHERE productId = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, newStock);
            stmt.setInt(2, productId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                LOGGER.info("Product stock updated successfully.");
                return true;
            } else {
                LOGGER.warning("Failed to update product stock.");
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating product stock: ", e);
            return false;
        }
    }

    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM PRODUCTS WHERE productId = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                LOGGER.info("Product deleted successfully.");
                return true;
            } else {
                LOGGER.warning("Failed to delete product.");
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting product: ", e);
            return false;
        }
    }
    public ResultSet getProductReport(){
        try{
            String query = "SELECT p.productId, p.name, p.originalPrice, p.tax, p.finalPrice, p.stock, " +
                       "SUM(oi.quantity) AS totalSold, " +
                       "SUM(oi.itemTotal * (1 - IFNULL(o.discount, 0) / 100)) AS revenueGenerated " +
                       "FROM PRODUCTS p " +
                       "LEFT JOIN ORDER_ITEMS oi ON p.productId = oi.productId " +
                       "LEFT JOIN ORDERS o ON oi.orderId = o.orderId " +
                       "GROUP BY p.productId";
            return con.executeQuery(query);
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error Retrieving Product Report: ", e);
            return null;
        }
    }
    public ResultSet getProductStocks(){
        try{
            String query = "SELECT productId, name, stock FROM PRODUCTS";
            return con.executeQuery(query);
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error Retrieving Products Stock: ", e);
            return null;
        }
    }

    public boolean updateStock(int productId, int quantity) {
        try {
            PreparedStatement stmt = con.prepareStatement("UPDATE PRODUCTS SET stock = stock + ? WHERE productId = ?");
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error Updating Stock: ", e);
            return false;
        }
    }
    public void closeConnection(){
        con.closeConnection();
    }
}