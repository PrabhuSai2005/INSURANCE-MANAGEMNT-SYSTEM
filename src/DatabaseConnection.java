import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() {
        Connection con = null;
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            con = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/InsuranceDB2", "root", "1234"
            );

            System.out.println("Database Connected Successfully!");
        }
        catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        }
        catch (SQLException e) {
            System.out.println("Connection Failed!");
            e.printStackTrace();
        }
        return con;
    }

    // Add this main method to test
    public static void main(String[] args) {
        Connection testCon = DatabaseConnection.getConnection();
        if (testCon != null) {
            System.out.println("Test: Connection is working!");
        } else {
            System.out.println("Test: Connection failed.");
        }
    }
}
