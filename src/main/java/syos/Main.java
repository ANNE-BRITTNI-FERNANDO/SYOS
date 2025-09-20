package syos;

import syos.cli.AuthenticationCLI;

/**
 * SYOS (Store Your Outstanding Stock) Management System
 * Main entry point for the application
 * 
 * This class provides the primary entry point for the SYOS system.
 * It initializes and starts the CLI interface for user interaction.
 * 
 * @author SYOS Development Team
 * @version 1.0
 * @since 2025-09-20
 */
public class Main {
    
    /**
     * Application metadata
     */
    private static final String APP_NAME = "SYOS - Store Your Outstanding Stock";
    private static final String APP_VERSION = "1.0.0";
    private static final String APP_DESCRIPTION = "Comprehensive Stock Management System";
    
    /**
     * Main entry point for the SYOS application
     * 
     * @param args Command line arguments (currently not used)
     */
    public static void main(String[] args) {
        // Display application banner
        displayBanner();
        
        try {
            // Initialize and start the CLI interface
            System.out.println("Initializing SYOS system...");
            AuthenticationCLI cli = new AuthenticationCLI();
            
            System.out.println("Starting SYOS CLI interface...");
            cli.start();
            
        } catch (Exception e) {
            System.err.println("\n❌ Failed to start SYOS system!");
            System.err.println("Error: " + e.getMessage());
            
            // Show troubleshooting tips
            displayTroubleshootingTips();
            
            System.exit(1);
        }
        
        System.out.println("\n👋 Thank you for using SYOS! Goodbye!");
    }
    
    /**
     * Display the application banner with system information
     */
    private static void displayBanner() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                              ║");
        System.out.println("║   ███████╗██╗   ██╗ ██████╗ ███████╗                        ║");
        System.out.println("║   ██╔════╝╚██╗ ██╔╝██╔═══██╗██╔════╝                        ║");
        System.out.println("║   ███████╗ ╚████╔╝ ██║   ██║███████╗                        ║");
        System.out.println("║   ╚════██║  ╚██╔╝  ██║   ██║╚════██║                        ║");
        System.out.println("║   ███████║   ██║   ╚██████╔╝███████║                        ║");
        System.out.println("║   ╚══════╝   ╚═╝    ╚═════╝ ╚══════╝                        ║");
        System.out.println("║                                                              ║");
        System.out.println("║              Store Your Outstanding Stock                    ║");
        System.out.println("║                                                              ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║  Application: %-47s║%n", APP_NAME);
        System.out.printf("║  Version:     %-47s║%n", APP_VERSION);
        System.out.printf("║  Description: %-47s║%n", APP_DESCRIPTION);
        System.out.println("║  Build Date:  September 20, 2025                            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    /**
     * Display troubleshooting tips when the application fails to start
     */
    private static void displayTroubleshootingTips() {
        System.out.println("\n🔧 Troubleshooting Tips:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("1. ✅ Verify MySQL database is running");
        System.out.println("   - Check if MySQL service is started");
        System.out.println("   - Verify database 'syos_db' exists");
        System.out.println();
        System.out.println("2. ✅ Check database connection settings");
        System.out.println("   - File: src/main/resources/config.properties");
        System.out.println("   - Verify username, password, and URL");
        System.out.println();
        System.out.println("3. ✅ Ensure all dependencies are available");
        System.out.println("   - MySQL Connector JAR");
        System.out.println("   - HikariCP JAR");
        System.out.println("   - SLF4J JARs");
        System.out.println();
        System.out.println("4. ✅ Verify database schema is set up");
        System.out.println("   - Run: java -cp \"target/dependency/*;target/classes\" syos.setup.DatabaseSetup");
        System.out.println();
        System.out.println("5. ✅ Check Java classpath");
        System.out.println("   - Ensure target/classes and target/dependency/* are included");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}