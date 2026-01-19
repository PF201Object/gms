package gymsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:gym_database.db";
    private static Connection conn = null;
    
    // Add static initializer to load driver
    static {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQLite JDBC driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load SQLite JDBC driver: " + e.getMessage());
        }
    }
    
    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(DB_URL);
            // Enable foreign keys
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }
    
    public static void initializeDatabase() {
        try (Connection c = getConnection(); Statement stmt = c.createStatement()) {
            
            System.out.println("Creating database tables...");
            
            // Create users table (for login)
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "fullname TEXT NOT NULL," +
                    "email TEXT," +
                    "user_type TEXT DEFAULT 'Admin')";
            
            // Create members table
            String createMembersTable = "CREATE TABLE IF NOT EXISTS members (" +
                    "member_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "full_name TEXT NOT NULL," +
                    "email TEXT," +
                    "phone TEXT," +
                    "address TEXT," +
                    "age INTEGER," +
                    "gender TEXT," +
                    "membership_type TEXT," +
                    "join_date TEXT," +  // Changed from DATE to TEXT for SQLite
                    "expiry_date TEXT," + // Changed from DATE to TEXT for SQLite
                    "status TEXT DEFAULT 'ACTIVE')";
            
            // Create payments table
            String createPaymentsTable = "CREATE TABLE IF NOT EXISTS payments (" +
                    "payment_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "member_id INTEGER," +
                    "amount REAL," +
                    "payment_date TEXT," +  // Changed from DATE to TEXT
                    "payment_type TEXT," +
                    "month TEXT," +
                    "status TEXT DEFAULT 'PENDING'," +
                    "FOREIGN KEY(member_id) REFERENCES members(member_id) ON DELETE CASCADE)";
            
            stmt.execute(createUsersTable);
            System.out.println("✓ Users table created");
            
            stmt.execute(createMembersTable);
            System.out.println("✓ Members table created");
            
            stmt.execute(createPaymentsTable);
            System.out.println("✓ Payments table created");
            
            // Insert default admin if not exists - FIXED VERSION
            try {
                String insertAdmin = "INSERT OR IGNORE INTO users (username, password, fullname, email, user_type) " +
                        "VALUES ('admin', 'admin123', 'Administrator', 'admin@gym.com', 'Admin')";
                int rowsInserted = stmt.executeUpdate(insertAdmin);
                
                if (rowsInserted > 0) {
                    System.out.println("✓ Default admin user created (admin/admin123)");
                } else {
                    System.out.println("✓ Admin user already exists");
                }
                
                // Verify admin exists
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE username = 'admin'");
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("✓ Admin verification: User 'admin' exists in database");
                }
                
            } catch (SQLException e) {
                System.err.println("Error creating admin user: " + e.getMessage());
                // Try alternative method
                createAdminUserManually();
            }
            
            System.out.println("Database initialized successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createAdminUserManually() {
        System.out.println("Trying alternative method to create admin...");
        try (Connection c = getConnection()) {
            // Check if admin exists
            String checkSql = "SELECT username FROM users WHERE username = 'admin'";
            Statement checkStmt = c.createStatement();
            ResultSet rs = checkStmt.executeQuery(checkSql);
            
            if (!rs.next()) {
                // Admin doesn't exist, create it
                String insertSql = "INSERT INTO users (username, password, fullname, email, user_type) " +
                        "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = c.prepareStatement(insertSql);
                pstmt.setString(1, "admin");
                pstmt.setString(2, "admin123");
                pstmt.setString(3, "Administrator");
                pstmt.setString(4, "admin@gym.com");
                pstmt.setString(5, "Admin");
                pstmt.executeUpdate();
                pstmt.close();
                System.out.println("✓ Admin user created via prepared statement");
            } else {
                System.out.println("✓ Admin already exists");
            }
            checkStmt.close();
        } catch (SQLException ex) {
            System.err.println("Failed to create admin: " + ex.getMessage());
        }
    }
    
    public static boolean validateLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection c = getConnection(); PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            boolean result = rs.next();
            System.out.println("Login attempt for '" + username + "': " + (result ? "SUCCESS" : "FAILED"));
            return result;
        } catch (SQLException e) {
            System.err.println("Login error for '" + username + "': " + e.getMessage());
            return false;
        }
    }
    
    // Add member to database
    public static boolean addMember(String fullName, String email, String phone, 
                                    String address, int age, String gender, 
                                    String membershipType, String joinDate, String expiryDate) {
        String sql = "INSERT INTO members(full_name, email, phone, address, age, gender, membership_type, join_date, expiry_date) " +
                     "VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection c = getConnection(); PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, fullName);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, address);
            pstmt.setInt(5, age);
            pstmt.setString(6, gender);
            pstmt.setString(7, membershipType);
            pstmt.setString(8, joinDate);
            pstmt.setString(9, expiryDate);
            pstmt.executeUpdate();
            System.out.println("Member added: " + fullName);
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding member: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all members
    public static List<String[]> getAllMembers() {
        List<String[]> members = new ArrayList<>();
        String sql = "SELECT * FROM members ORDER BY member_id DESC";
        try (Connection c = getConnection(); Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String[] row = new String[11];
                row[0] = String.valueOf(rs.getInt("member_id"));
                row[1] = rs.getString("full_name");
                row[2] = rs.getString("email");
                row[3] = rs.getString("phone");
                row[4] = rs.getString("address");
                row[5] = String.valueOf(rs.getInt("age"));
                row[6] = rs.getString("gender");
                row[7] = rs.getString("membership_type");
                row[8] = rs.getString("join_date");
                row[9] = rs.getString("expiry_date");
                row[10] = rs.getString("status");
                members.add(row);
            }
            System.out.println("Retrieved " + members.size() + " members");
        } catch (SQLException e) {
            System.err.println("Error getting members: " + e.getMessage());
            e.printStackTrace();
        }
        return members;
    }
    
    // Update member status
    public static boolean updateMemberStatus(int memberId, String status) {
        String sql = "UPDATE members SET status = ? WHERE member_id = ?";
        try (Connection c = getConnection(); PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, memberId);
            int rows = pstmt.executeUpdate();
            System.out.println("Updated status for member ID " + memberId + " to " + status + " (" + rows + " rows affected)");
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating member status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Add payment
    public static boolean addPayment(int memberId, double amount, String paymentDate, 
                                     String paymentType, String month, String status) {
        String sql = "INSERT INTO payments(member_id, amount, payment_date, payment_type, month, status) " +
                     "VALUES(?,?,?,?,?,?)";
        try (Connection c = getConnection(); PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, paymentDate);
            pstmt.setString(4, paymentType);
            pstmt.setString(5, month);
            pstmt.setString(6, status);
            pstmt.executeUpdate();
            System.out.println("Payment added for member ID " + memberId);
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all payments
    public static List<String[]> getAllPayments() {
        List<String[]> payments = new ArrayList<>();
        String sql = "SELECT p.*, m.full_name FROM payments p LEFT JOIN members m ON p.member_id = m.member_id ORDER BY p.payment_id DESC";
        try (Connection c = getConnection(); Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String[] row = new String[8];
                row[0] = String.valueOf(rs.getInt("payment_id"));
                row[1] = String.valueOf(rs.getInt("member_id"));
                row[2] = rs.getString("full_name");
                row[3] = String.valueOf(rs.getDouble("amount"));
                row[4] = rs.getString("payment_date");
                row[5] = rs.getString("payment_type");
                row[6] = rs.getString("month");
                row[7] = rs.getString("status");
                payments.add(row);
            }
            System.out.println("Retrieved " + payments.size() + " payments");
        } catch (SQLException e) {
            System.err.println("Error getting payments: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }
    
    // Get dashboard statistics
    public static int[] getDashboardStats() {
        int[] stats = new int[4];
        try (Connection c = getConnection(); Statement stmt = c.createStatement()) {
            // Total members
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM members");
            stats[0] = rs.getInt(1);
            
            // Active members
            rs = stmt.executeQuery("SELECT COUNT(*) FROM members WHERE status = 'ACTIVE'");
            stats[1] = rs.getInt(1);
            
            // Total revenue
            rs = stmt.executeQuery("SELECT SUM(amount) FROM payments WHERE status = 'PAID'");
            stats[2] = (int) rs.getDouble(1);
            
            // Pending payments
            rs = stmt.executeQuery("SELECT COUNT(*) FROM payments WHERE status = 'PENDING'");
            stats[3] = rs.getInt(1);
            
            System.out.println("Dashboard stats: " + stats[0] + " members, " + stats[1] + " active, $" + stats[2] + " revenue, " + stats[3] + " pending payments");
            
        } catch (SQLException e) {
            System.err.println("Error getting dashboard stats: " + e.getMessage());
            e.printStackTrace();
        }
        return stats;
    }
    
    // FIXED: ensureAdminExists method - remove the "Not supported yet" exception
    public static void ensureAdminExists() {
        System.out.println("Ensuring admin user exists...");
        try (Connection c = getConnection()) {
            // Check if admin exists
            String checkSql = "SELECT id FROM users WHERE username = 'admin'";
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(checkSql);
            
            if (!rs.next()) {
                // Admin doesn't exist, create it
                String insertSql = "INSERT INTO users (username, password, fullname, email, user_type) " +
                        "VALUES ('admin', 'admin123', 'Administrator', 'admin@gym.com', 'Admin')";
                stmt.executeUpdate(insertSql);
                System.out.println("✓ Admin user created via ensureAdminExists()");
            } else {
                System.out.println("✓ Admin user already exists (ID: " + rs.getInt("id") + ")");
            }
            
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error ensuring admin exists: " + e.getMessage());
        }
    }
    
    // Reset database completely
    public static void resetDatabase() {
        System.out.println("Resetting database...");
        try (Connection c = getConnection(); Statement stmt = c.createStatement()) {
            // Drop tables if they exist
            stmt.execute("DROP TABLE IF EXISTS payments");
            stmt.execute("DROP TABLE IF EXISTS members");
            stmt.execute("DROP TABLE IF EXISTS users");
            
            System.out.println("Tables dropped. Recreating...");
            
            // Recreate tables
            initializeDatabase();
            
            System.out.println("Database reset complete!");
        } catch (SQLException e) {
            System.err.println("Error resetting database: " + e.getMessage());
        }
    }
    
}