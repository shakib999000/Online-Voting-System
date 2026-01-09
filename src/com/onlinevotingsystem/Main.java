package com.onlinevotingsystem;

import com.onlinevotingsystem.database.DatabaseConnection;
import com.onlinevotingsystem.gui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("   ONLINE VOTING SYSTEM - Starting...   ");

        try {

            System.out.println(" Step 1: Database initialization...");
            DatabaseConnection.initializeDatabase();


            System.out.println(" Step 2: Testing database connection...");
            if (!DatabaseConnection.testConnection()) {
                throw new Exception("Database connection failed");
            }

            System.out.println(" Step 3: Loading system statistics...");
            DatabaseConnection.showDatabaseStats();

            System.out.println(" Step 4: Launching user interface...");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    LoginFrame frame = new LoginFrame();
                    frame.setVisible(true);
                    System.out.println(" Application is now running!");
                    System.out.println("Use credentials:");
                    System.out.println("   Admin: admin@ovs.com / admin123");
                    System.out.println("   Voter: voter@example.com / voter123");
                }
            });

        } catch (Exception e) {
            System.err.println(" SYSTEM STARTUP FAILED: " + e.getMessage());
            System.err.println("   Troubleshooting tips:");
            System.err.println("   1. Check if MySQL server is running");
            System.err.println("   2. Verify database credentials in DatabaseConnection.java");
            System.err.println("   3. Ensure MySQL Connector JAR is in classpath");

            try {
                JOptionPane.showMessageDialog(
                        null,
                        "Failed to start application:\n" + e.getMessage() +
                                "\n\nPlease check MySQL installation and try again.",
                        "System Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                System.err.println("Could not show error dialog: " + ex.getMessage());
            }
        }
    }
}