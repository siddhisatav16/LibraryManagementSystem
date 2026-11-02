import com.finlogic.config.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class InitDB {
    public static void main(String[] args) {
        try (Connection c = DBConnection.getConnection();
             Statement stmt = c.createStatement()) {
            
            try {
                stmt.execute("ALTER TABLE members ADD COLUMN password VARCHAR(255) DEFAULT '123456'");
                System.out.println("Added password column.");
            } catch (Exception e) { System.out.println("Password column might already exist."); }

            try {
                stmt.execute("ALTER TABLE members ADD COLUMN role VARCHAR(50) DEFAULT 'user'");
                System.out.println("Added role column.");
            } catch (Exception e) { System.out.println("Role column might already exist."); }

            // Insert admin if not exists
            try {
                stmt.execute("INSERT INTO members (name, email, phone, password, role) VALUES ('Admin', 'admin@libraryos.com', 0, 'admin123', 'admin')");
                System.out.println("Inserted admin user.");
            } catch (Exception e) { System.out.println("Admin user might already exist."); }
            
            System.out.println("DB Setup Complete.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 
