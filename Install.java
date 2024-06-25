import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

public class Install {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter MySQL username: ");
        String username = scanner.nextLine();
        System.out.print("Enter MySQL password: ");
        String password = scanner.nextLine();

        String url = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the MySQL server successfully.");
            
            saveCredentials(username, password);

            executeSqlScript(connection, "install.sql");
            
            System.out.println("Database installed successfully.");
        } catch (Exception e) {
            System.err.println("Connection to MySQL server failed.");
            e.printStackTrace();
        }
        scanner.close();
    }

    private static void executeSqlScript(Connection connection, String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String line;
            try (Statement stmt = connection.createStatement()) {
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    if (line.trim().endsWith(";")) {  
                        String sql = sb.toString();
                        stmt.execute(sql);
                        sb.setLength(0); 
                    }
                }
                if (sb.length() > 0) {
                    stmt.execute(sb.toString());
                }
            } catch (SQLException e) {
                System.err.println("Failed to execute SQL statement.");
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.err.println("Failed to read SQL script file.");
            e.printStackTrace();
        }
    }

    private static void saveCredentials(String username, String password) {
        Properties properties = new Properties();
        properties.setProperty("username", username);
        properties.setProperty("password", password);
        try (PrintWriter writer = new PrintWriter(new FileWriter("db.properties"))) {
            properties.store(writer, "Database Credentials");
        } catch (IOException e) {
            System.err.println("Failed to save credentials.");
            e.printStackTrace();
        }
    }
}
