import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                String dbURL = "jdbc:mysql://localhost:3306/personenerfassung";
                String username = "root";
                String password = "";
                connection = DriverManager.getConnection(dbURL, username, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
