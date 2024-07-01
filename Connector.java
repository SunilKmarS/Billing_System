import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.FileInputStream;

public class Connector {
    Connection con;
    PreparedStatement command;
    private static final Logger LOGGER = Logger.getLogger(Product.class.getName());

    public Connector() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("db.properties"));
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/billing", username, password);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error Initializing MySQL Connection: ", e);
        }
        this.command = null;
    }

    public ResultSet executeQuery(String query) {
        try {
            Statement stmt = con.createStatement();
            return stmt.executeQuery(query);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error Retrieving Query: ", e);
            return null;
        }
    }

    public boolean executeStatement(PreparedStatement sql) {
        try {
            return (sql.executeUpdate() > 0);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error Executing Statement: ", e);
            return false;
        }
    }

    public PreparedStatement prepareStatement(String sql) {
        try {
            PreparedStatement sqlStatement = con.prepareStatement(sql);
            return sqlStatement;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error Preparing Statement: ", e);
            return null;
        }
    }

    public Connection getConnection() {
        return con;
    }

    public void closeConnection() {
        try {
            con.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error Closing the Connection: ", e);
        }
    }
}
