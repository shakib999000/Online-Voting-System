package com.onlinevotingsystem.database;

import java.sql.*;
import java.util.Properties;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "online_voting_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    // Initialize database and create tables
    public static void initializeDatabase() {
        try {
            // First connect without specific database to create it
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();

            // Create database if not exists
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("✅ Database checked/created: " + DB_NAME);

            stmt.close();
            conn.close();

            // Now connect to the specific database
            connection = DriverManager.getConnection(DB_URL + DB_NAME, USERNAME, PASSWORD);

            // Create all tables
            createTables();

            // Insert sample data
            insertSampleData();

            System.out.println("✅ Database initialized successfully!");

        } catch (SQLException e) {
            System.err.println("❌ Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Create all required tables
    private static void createTables() throws SQLException {
        Statement stmt = connection.createStatement();

        // 1. Users Table
        String usersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                full_name VARCHAR(100) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                role ENUM('VOTER', 'ADMIN') DEFAULT 'VOTER',
                phone VARCHAR(20),
                address TEXT,
                date_of_birth DATE,
                nid_number VARCHAR(20) UNIQUE,
                is_verified BOOLEAN DEFAULT FALSE,
                is_eligible BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                INDEX idx_email (email),
                INDEX idx_role (role)
            )
            """;

        // 2. Elections Table
        String electionsTable = """
            CREATE TABLE IF NOT EXISTS elections (
                id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(200) NOT NULL,
                description TEXT,
                start_date DATETIME NOT NULL,
                end_date DATETIME NOT NULL,
                status ENUM('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED') DEFAULT 'UPCOMING',
                created_by INT,
                total_votes INT DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (created_by) REFERENCES users(id),
                INDEX idx_status (status),
                INDEX idx_dates (start_date, end_date)
            )
            """;

        // 3. Candidates Table
        String candidatesTable = """
            CREATE TABLE IF NOT EXISTS candidates (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(100) NOT NULL,
                party VARCHAR(100),
                position VARCHAR(100) NOT NULL,
                election_id INT,
                photo_url VARCHAR(255),
                manifesto TEXT,
                vote_count INT DEFAULT 0,
                is_active BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (election_id) REFERENCES elections(id) ON DELETE CASCADE,
                INDEX idx_election (election_id),
                INDEX idx_position (position)
            )
            """;

        // 4. Votes Table
        String votesTable = """
            CREATE TABLE IF NOT EXISTS votes (
                id INT AUTO_INCREMENT PRIMARY KEY,
                voter_id INT NOT NULL,
                candidate_id INT NOT NULL,
                election_id INT NOT NULL,
                voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                ip_address VARCHAR(45),
                is_verified BOOLEAN DEFAULT TRUE,
                UNIQUE KEY unique_vote (voter_id, election_id),
                FOREIGN KEY (voter_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE,
                FOREIGN KEY (election_id) REFERENCES elections(id) ON DELETE CASCADE,
                INDEX idx_voter_election (voter_id, election_id),
                INDEX idx_election_candidate (election_id, candidate_id)
            )
            """;

        // 5. OTP Table
        String otpTable = """
            CREATE TABLE IF NOT EXISTS otp_codes (
                id INT AUTO_INCREMENT PRIMARY KEY,
                email VARCHAR(100) NOT NULL,
                otp_code VARCHAR(6) NOT NULL,
                purpose ENUM('REGISTRATION', 'PASSWORD_RESET', 'VOTE_VERIFICATION') DEFAULT 'REGISTRATION',
                expires_at DATETIME NOT NULL,
                is_used BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                INDEX idx_email_otp (email, otp_code),
                INDEX idx_expires (expires_at)
            )
            """;

        // 6. Issues/Complaints Table
        String issuesTable = """
            CREATE TABLE IF NOT EXISTS issues (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                election_id INT,
                title VARCHAR(200) NOT NULL,
                description TEXT NOT NULL,
                type ENUM('TECHNICAL', 'FRAUD', 'ACCESS', 'OTHER') DEFAULT 'OTHER',
                status ENUM('PENDING', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') DEFAULT 'PENDING',
                priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') DEFAULT 'MEDIUM',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                resolved_at DATETIME,
                resolution_note TEXT,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (election_id) REFERENCES elections(id) ON DELETE SET NULL,
                INDEX idx_status (status),
                INDEX idx_user (user_id)
            )
            """;

        // 7. Notifications Table
        String notificationsTable = """
            CREATE TABLE IF NOT EXISTS notifications (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT,
                title VARCHAR(200) NOT NULL,
                message TEXT NOT NULL,
                type ENUM('SYSTEM', 'ELECTION', 'ISSUE', 'VOTE') DEFAULT 'SYSTEM',
                is_read BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                INDEX idx_user_read (user_id, is_read)
            )
            """;

            String sql = """
        CREATE TABLE IF NOT EXISTS election_status_log (
            id INT AUTO_INCREMENT PRIMARY KEY,
            election_id INT NOT NULL,
            old_status VARCHAR(20) NOT NULL,
            new_status VARCHAR(20) NOT NULL,
            reason TEXT,
            changed_by INT,
            changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (election_id) REFERENCES elections(id) ON DELETE CASCADE,
            FOREIGN KEY (changed_by) REFERENCES users(id) ON DELETE SET NULL
        )
        """;




        // 8. Voter Eligibility Table
        String eligibilityTable = """
            CREATE TABLE IF NOT EXISTS voter_eligibility (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                election_id INT NOT NULL,
                is_eligible BOOLEAN DEFAULT TRUE,
                reason TEXT,
                checked_by INT,
                checked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                UNIQUE KEY unique_eligibility (user_id, election_id),
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (election_id) REFERENCES elections(id) ON DELETE CASCADE,
                FOREIGN KEY (checked_by) REFERENCES users(id) ON DELETE SET NULL
            )
            """;

        // Execute all table creation queries
        stmt.executeUpdate(usersTable);
        stmt.executeUpdate(electionsTable);
        stmt.executeUpdate(candidatesTable);
        stmt.executeUpdate(votesTable);
        stmt.executeUpdate(otpTable);
        stmt.executeUpdate(issuesTable);
        stmt.executeUpdate(notificationsTable);
        stmt.executeUpdate(eligibilityTable);

        stmt.close();
        System.out.println("✅ All tables created successfully!");
    }

    // Insert sample data for testing
    private static void insertSampleData() {
        try {
            Statement stmt = connection.createStatement();

            // Check if admin already exists
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users WHERE email = 'admin@ovs.com'");
            rs.next();
            if (rs.getInt("count") == 0) {

                // Insert Admin User
                String insertAdmin = """
                    INSERT INTO users (full_name, email, password, role, phone, is_verified) 
                    VALUES ('System Administrator', 'admin@ovs.com', 'admin123', 'ADMIN', '+8801XXXXXXXXX', TRUE)
                    """;
                stmt.executeUpdate(insertAdmin);

                // Insert Sample Voter
                String insertVoter = """
                    INSERT INTO users (full_name, email, password, role, phone, address, is_verified) 
                    VALUES ('John Doe', 'voter@example.com', 'voter123', 'VOTER', '+8801XXXXXXXXX', 'Dhaka, Bangladesh', TRUE)
                    """;
                stmt.executeUpdate(insertVoter);

                // Insert Sample Election
                String insertElection = """
                    INSERT INTO elections (title, description, start_date, end_date, status, created_by) 
                    VALUES ('Student Union Election 2024', 'Annual student union election for 2024', 
                            '2024-12-01 09:00:00', '2024-12-01 17:00:00', 'UPCOMING', 1)
                    """;
                stmt.executeUpdate(insertElection);

                // Insert Sample Candidates
                String insertCandidates = """
                    INSERT INTO candidates (name, email, party, position, election_id, manifesto) VALUES
                    ('Alice Johnson', 'alice@example.com', 'Student Alliance', 'President', 1, 'Better campus facilities and student welfare'),
                    ('Bob Smith', 'bob@example.com', 'Youth Front', 'President', 1, 'Digital transformation and innovation'),
                    ('Carol Davis', 'carol@example.com', 'Student Alliance', 'Vice President', 1, 'Academic excellence and research'),
                    ('David Wilson', 'david@example.com', 'Youth Front', 'Vice President', 1, 'Sports and cultural activities')
                    """;
                stmt.executeUpdate(insertCandidates);

                System.out.println("✅ Sample data inserted successfully!");
            } else {
                System.out.println("✅ Sample data already exists!");
            }

            stmt.close();

        } catch (SQLException e) {
            System.err.println("❌ Error inserting sample data: " + e.getMessage());
        }
    }

    // Get database connection
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(DB_URL + DB_NAME, USERNAME, PASSWORD);
            } catch (SQLException e) {
                System.err.println("❌ Failed to get database connection: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }

    // Close connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing connection: " + e.getMessage());
        }
    }

    // Test connection
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("❌ Connection test failed: " + e.getMessage());
            return false;
        }
    }

    // Get database statistics
    public static void showDatabaseStats() {
        try {
            Connection conn = getConnection();
            DatabaseMetaData metaData = conn.getMetaData();

            System.out.println("\\n📊 DATABASE STATISTICS:");
            System.out.println("Database: " + DB_NAME);
            System.out.println("URL: " + metaData.getURL());
            System.out.println("Driver: " + metaData.getDriverName());
            System.out.println("Version: " + metaData.getDriverVersion());

            // Count records in each table
            String[] tables = {"users", "elections", "candidates", "votes", "issues", "notifications"};
            Statement stmt = conn.createStatement();

            for (String table : tables) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM " + table);
                if (rs.next()) {
                    System.out.println(table.toUpperCase() + ": " + rs.getInt("count") + " records");
                }
            }

            stmt.close();

        } catch (SQLException e) {
            System.err.println("❌ Error getting database stats: " + e.getMessage());
        }
    }
}