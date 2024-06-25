import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Date;

public class Order {
    private Connector con;
    private static final Logger LOGGER = Logger.getLogger(Order.class.getName());

    public Order() {
        this.con = new Connector();
    }

    public int createOrder(int custId, double discount) {
        String sql = "INSERT INTO ORDERS(custId, discount, amount) VALUES (?, ?, 0)";
        try (PreparedStatement stmt = this.con.getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, custId);
            stmt.setDouble(2, discount);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating order: ", e);
        }
        return -1;
    }

    public boolean addOrderItem(int orderId, int productId, float productPrice, int quantity, float tax) {
        String sql = "INSERT INTO ORDER_ITEMS(orderId, productId, productPrice, quantity, tax) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, productId);
            stmt.setFloat(3, productPrice);
            stmt.setInt(4, quantity);
            stmt.setFloat(5, tax);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding order item: ", e);
            return false;
        }
    }

    public boolean updateOrderTotal(int orderId,double discount, double amount,String paymentMethod) {
        String sql = "UPDATE ORDERS SET amount = ?,discount = ?, paymentMethod = ? WHERE orderId = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setDouble(2, discount);            
            stmt.setString(3, paymentMethod);
            stmt.setInt(4, orderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating order total: ", e);
            return false;
        }
    }

    public ResultSet getOrderItems(int orderId) {
        String sql = "SELECT p.name as name, oi.productPrice as price, oi.tax as tax, oi.quantity as quantity, (oi.productPrice * oi.quantity) as total FROM ORDER_ITEMS oi INNER JOIN PRODUCTS p ON oi.productId = p.productId WHERE oi.orderId = ?";
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, orderId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving Order Items by ID: ", e);
            return null;
        }
    }

    public ResultSet getOrderDetails(int orderId) {
        String sql = "SELECT c.name as name, o.orderDate as orderDate FROM ORDERS o JOIN CUSTOMERS c ON c.custId = o.custId WHERE orderId = ?";
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, orderId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving Order Details by ID: ", e);
            return null;
        }
    }

    public ResultSet getBills() {
        try {
            String sql = "SELECT orderId,orderDate,paymentMethod,discount,amount FROM ORDERS ";
            return con.executeQuery(sql);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving All orders: ", e);
            return null;
        }
    }
    public ResultSet getBillsByCustomerId(int custId) {
        String sql = "SELECT orderId,orderDate,paymentMethod,discount,amount FROM ORDERS WHERE custId = ?";
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, custId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving orders by customer ID: ", e);
            return null;
        }
    }

    public ResultSet getBillsByDate(Date date) {
        String sql = "SELECT * FROM ORDERS WHERE DATE(orderDate) = ?";
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setDate(1, date);
            return stmt.executeQuery();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving orders by date: ", e);
            return null;
        }
    }

    public ResultSet getBillsByDateRange(Date startDate, Date endDate) {
        String sql = "SELECT * FROM ORDERS WHERE DATE(orderDate) >= ? AND DATE(orderDate) <= ?";
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            return stmt.executeQuery();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving orders by date range: ", e);
            return null;
        }
    }
    public ResultSet getTotalRevenue(){
        try{
            String query = "SELECT COUNT(orderId) AS totalOrders, SUM(amount) AS totalRevenue,SUM(discount) AS totalDiscounts, AVG(amount) AS avgOrderValue FROM ORDERS";
            return con.executeQuery(query);
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error Getting Total Revenue : ", e);
            return null;
        }
    }
    public ResultSet getMostSold(){
        try{
            String query = "SELECT p.productId, p.name, SUM(oi.quantity) AS totalSold " +
                            "FROM PRODUCTS p LEFT JOIN ORDER_ITEMS oi ON p.productId = oi.productId " +
                            "GROUP BY p.productId HAVING totalSold > 0 ORDER BY totalSold DESC LIMIT 1";
            return con.executeQuery(query);
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error Retrieving Most Sold Product: ", e);
            return null;
        }
    }
    public ResultSet getLeastSold(){
        try{
            String query = "SELECT p.productId, p.name, SUM(oi.quantity) AS totalSold " +
                        "FROM PRODUCTS p LEFT JOIN ORDER_ITEMS oi ON p.productId = oi.productId " +
                        "GROUP BY p.productId HAVING totalSold > 0 ORDER BY totalSold ASC   LIMIT 1";
            return con.executeQuery(query);
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error Getting Least Sold Product : ", e);
            return null;
        }
    }
    public boolean updateCouponCode(int orderId, String couponCode) {
        String sql = "UPDATE ORDERS SET couponCode = ? WHERE orderId = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, couponCode);
            pstmt.setInt(2, orderId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error Updating coupon code for order ID: " + orderId, e);
            return false;
        }
    }    
    public void closeConnection(){
        con.closeConnection();
    }
}
