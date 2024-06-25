import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Customer {
    private Connector con;
    private static final Logger LOGGER = Logger.getLogger(Customer.class.getName());

    public Customer() {
        this.con = new Connector();
    }

    public int addCustomer(String name, String phone) {
        String sql = "INSERT INTO CUSTOMERS(name, phone) VALUES (?, ?)";
        try (PreparedStatement stmt = con.getConnection().prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, phone);
            this.con.executeStatement(stmt);
            ResultSet rs = stmt.getGeneratedKeys();
            int custId=0;
            if (rs.next()) {
                custId = rs.getInt(1);
            }
            return custId;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding customer: ", e);
            return 0;
        }
    }

    public ResultSet getCustomer(int custId) {
        
        try{
            String query = "SELECT * FROM CUSTOMERS WHERE custId = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, custId);
            return stmt.executeQuery();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting customer: ", e);
            return null;
        }
    }
    public ResultSet getCustomerList() {
        String sqlQuery = "SELECT * FROM CUSTOMERS";
        try{
            return this.con.executeQuery(sqlQuery);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting Customer List: ", e);
            return null;
        }
    }
    public boolean updateCustomer(int userId, String newName, String newPhone) {
        String sql = "UPDATE CUSTOMERS SET name = ?, phone = ? WHERE custId = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setString(2, newPhone);
            stmt.setInt(3, userId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            } else {
                LOGGER.warning("Failed to update customer details.");
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating customer details: ", e);
            return false;
        }
    }
    public ResultSet getOverallReport(){
        try{
            String query = "SELECT c.custId, c.name, c.phone, COUNT(o.orderId) AS totalOrders, SUM(o.amount) AS totalSpent, AVG(o.amount) AS avgOrderValue " +
                           "FROM CUSTOMERS c LEFT JOIN ORDERS o ON c.custId = o.custId " +
                           "GROUP BY c.custId";
            return con.executeQuery(query);
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error Retrieving Overall Report : ", e);
            return null;
        }
    }
    public String getMostPreferedPayment(int custId){
        try{
            String paymentQuery = "SELECT paymentMethod, COUNT(paymentMethod) AS methodCount " +
                        "FROM ORDERS WHERE custId = ? GROUP BY paymentMethod ORDER BY methodCount DESC LIMIT 1";
            PreparedStatement paymentStmt = con.prepareStatement(paymentQuery);
            paymentStmt.setInt(1, custId);
            ResultSet rs = paymentStmt.executeQuery();
            if(rs.next()){
                return rs.getString(1);
            }
            return null;
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error Retrieving Most Preferred Payment Method : ", e);
            return null;
        }
    }
    public ResultSet getMostBoughtProduct(int custId){
        try{
            String productQuery = "SELECT oi.productId, p.name, SUM(oi.quantity) AS totalBought " +
                                    "FROM ORDER_ITEMS oi JOIN PRODUCTS p ON oi.productId = p.productId " +
                                    "JOIN ORDERS o ON oi.orderId = o.orderId WHERE o.custId = ? " +
                                    "GROUP BY oi.productId ORDER BY totalBought DESC LIMIT 1";
            PreparedStatement productStmt = con.prepareStatement(productQuery);
            productStmt.setInt(1, custId);
            return productStmt.executeQuery();
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error Retrieving Most Bought Product: ", e);
            return null;
        }
    }
    public ResultSet getCoupon(int custId, String couponCode) {
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM COUPONS WHERE custId = ? AND couponCode = ?");
            stmt.setInt(1, custId);
            stmt.setString(2, couponCode);
            return stmt.executeQuery();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error Retrieving Coupon : ", e);
            return null;
        }
    }

    public boolean removeCoupon(String couponCode) {
        try {
            PreparedStatement stmt = con.prepareStatement("DELETE FROM COUPONS WHERE couponCode = ?");
            stmt.setString(1, couponCode);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error Removing Coupon: ", e);
            return false;
        }
    }

    public boolean addCoupon(int custId, String couponCode, double discount) {
        try {
            PreparedStatement stmt = con.prepareStatement("INSERT INTO COUPONS (custId, couponCode, discount) VALUES (?, ?, ?)");
            stmt.setInt(1, custId);
            stmt.setString(2, couponCode);
            stmt.setDouble(3, discount);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error Adding Coupon: ", e);
            return false;
        }
    }
    public void closeConnection(){
        con.closeConnection();
    }
}
