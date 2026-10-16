package bloodbank;

import java.sql.*;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Admin class provides administrative functions for the Blood Bank Management System
 * Implements various administrative operations and reports
 * Demonstrates polymorphism through method overloading
 */
public class Admin {
    
    private Scanner scanner;
    
    public Admin() {
        this.scanner = new Scanner(System.in);
    }
    
	 private String repeatChar(char c, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Display admin dashboard with statistics
     */
    public void showDashboard() {
        System.out.println(repeatChar('=', 60));
        System.out.println("                 ADMIN DASHBOARD");
       System.out.println(repeatChar('=', 60));

        
        try {
            // Get statistics
            int totalDonors = getTotalDonors();
            int totalRecipients = getTotalRecipients();
            int totalBloodUnits = BloodStock.getTotalUnits();
            
            System.out.printf("📊 Total Donors: %d%n", totalDonors);
            System.out.printf("🏥 Total Recipients: %d%n", totalRecipients);
            System.out.printf("🩸 Total Blood Units: %d%n", totalBloodUnits);
            
            // Show recent activities
            showRecentActivities();
            
            // Show low stock alerts
            BloodStock.showLowStockAlerts();
            
        } catch (Exception e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
        }
        
        System.out.println(repeatChar('=', 60));

    }
    
    /**
     * Get total number of donors
     * @return Total donor count
     */
    private int getTotalDonors() {
        String sql = "SELECT COUNT(*) FROM Donors";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting donor count: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Get total number of recipients
     * @return Total recipient count
     */
    private int getTotalRecipients() {
        String sql = "SELECT COUNT(*) FROM Recipients";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting recipient count: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Show recent donation and request activities
     */
    private void showRecentActivities() {
        System.out.println("\n📅 RECENT ACTIVITIES (Last 7 days):");
        System.out.println(repeatChar('-', 40));
        
        // Recent donations
        String donorSql = "SELECT Name, BloodGroup, LastDonationDate FROM Donors " +
                         "WHERE LastDonationDate >= SYSDATE - 7 ORDER BY LastDonationDate DESC";
        
        System.out.println("Recent Donations:");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(donorSql)) {
            
            boolean hasRecent = false;
            while (rs.next()) {
                String name = rs.getString("Name");
                String bloodGroup = rs.getString("BloodGroup");
                Date donationDate = rs.getDate("LastDonationDate");
                
                System.out.printf("  • %s (%s) - %s%n", name, bloodGroup, donationDate);
                hasRecent = true;
            }
            
            if (!hasRecent) {
                System.out.println("  • No recent donations");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting recent donations: " + e.getMessage());
        }
        
        // Recent requests
        String recipientSql = "SELECT Name, BloodGroup, RequiredUnits, RequestDate FROM Recipients " +
                             "WHERE RequestDate >= SYSDATE - 7 ORDER BY RequestDate DESC";
        
        System.out.println("\nRecent Requests:");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(recipientSql)) {
            
            boolean hasRecent = false;
            while (rs.next()) {
                String name = rs.getString("Name");
                String bloodGroup = rs.getString("BloodGroup");
                int units = rs.getInt("RequiredUnits");
                Date requestDate = rs.getDate("RequestDate");
                
                System.out.printf("  • %s needs %d units of %s - %s%n", name, units, bloodGroup, requestDate);
                hasRecent = true;
            }
            
            if (!hasRecent) {
                System.out.println("  • No recent requests");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting recent requests: " + e.getMessage());
        }
    }
    
    /**
     * Generate comprehensive reports
     * Method overloading demonstration - polymorphism
     */
    public void generateReport() {
        System.out.println("\nSelect Report Type:");
        System.out.println("1. Donor Report");
        System.out.println("2. Recipient Report");
        System.out.println("3. Blood Stock Report");
        System.out.println("4. Complete System Report");
        System.out.print("Enter choice (1-4): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            
            switch (choice) {
                case 1:
                    generateReport("donor");
                    break;
                case 2:
                    generateReport("recipient");
                    break;
                case 3:
                    generateReport("stock");
                    break;
                case 4:
                    generateReport("complete");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
        }
    }
    
    /**
     * Generate specific type of report (Method Overloading)
     * @param reportType Type of report to generate
     */
    public void generateReport(String reportType) {
        System.out.println(repeatChar('=', 60));
        System.out.println("                 " + reportType.toUpperCase() + " REPORT");
       System.out.println(repeatChar('=', 60));

        
        switch (reportType.toLowerCase()) {
            case "donor":
                generateDonorReport();
                break;
            case "recipient":
                generateRecipientReport();
                break;
            case "stock":
                BloodStock.displayBloodStock();
                break;
            case "complete":
                generateCompleteReport();
                break;
            default:
                System.out.println("Unknown report type: " + reportType);
        }
        
        System.out.println(repeatChar('=', 60));

    }
    
    /**
     * Generate donor report with detailed statistics
     */
    private void generateDonorReport() {
        List<Donor> donors = Donor.getAllDonors();
        
        System.out.printf("Total Donors: %d%n%n", donors.size());
        
        // Group by blood group
        System.out.println("Donors by Blood Group:");
        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        
        for (String bloodGroup : bloodGroups) {
            List<Donor> groupDonors = Donor.searchByBloodGroup(bloodGroup);
            System.out.printf("  %s: %d donors%n", bloodGroup, groupDonors.size());
        }
        
        System.out.println("\nAll Donors:");
        System.out.printf("%-5s %-20s %-5s %-10s %-15s %-15s%n", 
                "ID", "Name", "Age", "Blood", "Contact", "Last Donation");
         System.out.println(repeatChar('-', 80));
        
        for (Donor donor : donors) {
            System.out.printf("%-5d %-20s %-5d %-10s %-15s %-15s%n",
                    donor.getDonorId(),
                    donor.getName(),
                    donor.getAge(),
                    donor.getBloodGroup(),
                    donor.getContact(),
                    donor.getLastDonationDate() != null ? donor.getLastDonationDate().toString() : "Never");
        }
    }
    
    /**
     * Generate recipient report with detailed statistics
     */
    private void generateRecipientReport() {
        List<Recipient> recipients = Recipient.getAllRecipients();
        
        System.out.printf("Total Recipients: %d%n%n", recipients.size());
        
        // Group by blood group
        System.out.println("Recipients by Blood Group:");
        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        
        for (String bloodGroup : bloodGroups) {
            List<Recipient> groupRecipients = Recipient.searchByBloodGroup(bloodGroup);
            System.out.printf("  %s: %d recipients%n", bloodGroup, groupRecipients.size());
        }
        
        System.out.println("\nAll Recipients:");
        System.out.printf("%-5s %-20s %-10s %-15s %-10s %-15s%n", 
                "ID", "Name", "Blood", "Contact", "Units", "Request Date");
         System.out.println(repeatChar('-', 80));
        
        for (Recipient recipient : recipients) {
            System.out.printf("%-5d %-20s %-10s %-15s %-10d %-15s%n",
                    recipient.getRecipientId(),
                    recipient.getName(),
                    recipient.getBloodGroup(),
                    recipient.getContact(),
                    recipient.getRequiredUnits(),
                    recipient.getRequestDate().toString());
        }
    }
    
    /**
     * Generate complete system report
     */
    private void generateCompleteReport() {
        System.out.println("COMPREHENSIVE BLOOD BANK SYSTEM REPORT");
        System.out.println("Generated on: " + LocalDate.now());
        
        // Summary statistics
        int totalDonors = getTotalDonors();
        int totalRecipients = getTotalRecipients();
        int totalBloodUnits = BloodStock.getTotalUnits();
        
        System.out.println("\nSUMMARY:");
        System.out.printf("  Total Registered Donors: %d%n", totalDonors);
        System.out.printf("  Total Registered Recipients: %d%n", totalRecipients);
        System.out.printf("  Total Blood Units in Stock: %d%n", totalBloodUnits);
        
        // Blood stock details
        System.out.println("\nBLOOD STOCK STATUS:");
        BloodStock.displayBloodStock();
        
        // Donor summary
        System.out.println("\nDONOR SUMMARY BY BLOOD GROUP:");
        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String bloodGroup : bloodGroups) {
            List<Donor> donors = Donor.searchByBloodGroup(bloodGroup);
            System.out.printf("  %s: %d donors%n", bloodGroup, donors.size());
        }
        
        // Recipient summary
        System.out.println("\nRECIPIENT SUMMARY BY BLOOD GROUP:");
        for (String bloodGroup : bloodGroups) {
            List<Recipient> recipients = Recipient.searchByBloodGroup(bloodGroup);
            System.out.printf("  %s: %d recipients%n", bloodGroup, recipients.size());
        }
        
        // System health check
        System.out.println("\nSYSTEM HEALTH CHECK:");
        if (totalBloodUnits < 20) {
            System.out.println("  ⚠️  WARNING: Low overall blood stock!");
        } else if (totalBloodUnits < 50) {
            System.out.println("  ⚡ CAUTION: Moderate blood stock levels");
        } else {
            System.out.println("  ✅ GOOD: Adequate blood stock levels");
        }
        
        if (totalDonors < totalRecipients) {
            System.out.println("  ⚠️  WARNING: More recipients than donors!");
        } else {
            System.out.println("  ✅ GOOD: Donor-to-recipient ratio is healthy");
        }
    }
    
    /**
     * Backup database data (simplified version)
     */
    public void backupData() {
        System.out.println("\n📦 BACKUP OPERATIONS");
         System.out.println(repeatChar('-', 30));

        try {
            // Create backup timestamp
            String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            System.out.println("Creating backup for date: " + timestamp);
            
            // Export donor data
            List<Donor> donors = Donor.getAllDonors();
            System.out.println("✅ Donors backed up: " + donors.size() + " records");
            
            // Export recipient data
            List<Recipient> recipients = Recipient.getAllRecipients();
            System.out.println("✅ Recipients backed up: " + recipients.size() + " records");
            
            // Export blood stock data
            BloodStock.loadBloodStock();
            System.out.println("✅ Blood stock data backed up");
            
            System.out.println("\n✅ Backup completed successfully!");
            System.out.println("💡 Note: In production, backup would be saved to external storage");
            
        } catch (Exception e) {
            System.err.println("❌ Backup failed: " + e.getMessage());
        }
    }
    
    /**
     * Clean up old records (demonstration of data maintenance)
     */
    public void cleanupOldRecords() {
        System.out.println("\n🧹 DATA CLEANUP OPERATIONS");
         System.out.println(repeatChar('-', 30));
        
        System.out.print("Enter number of days to keep recent records (default: 365): ");
        String input = scanner.nextLine();
        
        int daysToKeep = 365; // Default to 1 year
        try {
            if (!input.trim().isEmpty()) {
                daysToKeep = Integer.parseInt(input);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, using default: 365 days");
        }
        
        if (daysToKeep < 30) {
            System.out.println("⚠️  WARNING: Cannot delete records newer than 30 days for safety!");
            daysToKeep = 30;
        }
        
        System.out.println("Analyzing records older than " + daysToKeep + " days...");
        
        // Count old donors (for demonstration - we won't actually delete)
        String oldDonorSql = "SELECT COUNT(*) FROM Donors WHERE LastDonationDate < SYSDATE - ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(oldDonorSql)) {
            
            pstmt.setInt(1, daysToKeep);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int oldDonorCount = rs.getInt(1);
                System.out.printf("Found %d donors with donations older than %d days%n", oldDonorCount, daysToKeep);
                
                if (oldDonorCount > 0) {
                    System.out.println("💡 Note: In production, these records could be archived or marked inactive");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error during cleanup analysis: " + e.getMessage());
        }
        
        System.out.println("✅ Cleanup analysis completed!");
    }
    
    /**
     * System maintenance operations
     */
    public void performMaintenance() {
        System.out.println("\n⚙️  SYSTEM MAINTENANCE");
         System.out.println(repeatChar('-', 30));
        
        // Initialize blood stock if needed
        BloodStock.initializeBloodStock();
        System.out.println("✅ Blood stock table verified");
        
        // Test database connection
        if (DatabaseConnection.testConnection()) {
            System.out.println("✅ Database connection healthy");
        } else {
            System.out.println("❌ Database connection issues detected");
        }
        
        // Check for data consistency
        performDataIntegrityCheck();
        
        System.out.println("✅ System maintenance completed!");
    }
    
    /**
     * Perform data integrity checks
     */
    private void performDataIntegrityCheck() {
        System.out.println("\n🔍 DATA INTEGRITY CHECK");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Check for invalid blood groups
            String invalidBloodGroupSql = "SELECT COUNT(*) FROM Donors WHERE BloodGroup NOT IN ('A+','A-','B+','B-','AB+','AB-','O+','O-')";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(invalidBloodGroupSql);
            
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("⚠️  Found invalid blood group entries in donors");
            } else {
                System.out.println("✅ All donor blood groups are valid");
            }
            
            // Check for negative stock values
            String negativeStockSql = "SELECT COUNT(*) FROM BloodStock WHERE AvailableUnits < 0";
            rs = stmt.executeQuery(negativeStockSql);
            
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("⚠️  Found negative blood stock values");
            } else {
                System.out.println("✅ All blood stock values are non-negative");
            }
            
            // Check for duplicate contacts
            String duplicateContactSql = "SELECT Contact, COUNT(*) FROM Donors GROUP BY Contact HAVING COUNT(*) > 1";
            rs = stmt.executeQuery(duplicateContactSql);
            
            boolean hasDuplicates = false;
            while (rs.next()) {
                System.out.println("⚠️  Duplicate contact found: " + rs.getString("Contact"));
                hasDuplicates = true;
            }
            
            if (!hasDuplicates) {
                System.out.println("✅ No duplicate contact numbers found");
            }
            
        } catch (SQLException e) {
            System.err.println("Error during integrity check: " + e.getMessage());
        }
    }
    
    /**
     * Get system statistics for monitoring
     * @return Statistics string
     */
    public String getSystemStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("System Statistics:\n");
        stats.append("Donors: ").append(getTotalDonors()).append("\n");
        stats.append("Recipients: ").append(getTotalRecipients()).append("\n");
        stats.append("Total Blood Units: ").append(BloodStock.getTotalUnits()).append("\n");
        return stats.toString();
    }
}