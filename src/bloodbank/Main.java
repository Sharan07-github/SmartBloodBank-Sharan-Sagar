package bloodbank;

import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Main class - Entry point for Smart Blood Bank Management System
 * Provides console-based menu interface for all operations
 * Demonstrates inheritance, encapsulation, polymorphism, and database integration
 */
public class Main {
    
    private static Scanner scanner = new Scanner(System.in);
    private static Admin admin = new Admin();
    
	private static String repeatChar(char c, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("🩸 Welcome to Smart Blood Bank Management System");
        System.out.println("================================================");
        
        // Test database connection first
        if (!DatabaseConnection.testConnection()) {
            System.err.println("❌ Cannot connect to database! Please check connection settings.");
            System.err.println("Update database details in DatabaseConnection.java");
            return;
        }
        
        // Initialize system
        BloodStock.initializeBloodStock();
        
        // Show admin dashboard
        admin.showDashboard();
        
        // Main application loop
        boolean running = true;
        while (running) {
            showMainMenu();
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                running = handleMenuChoice(choice);
                
                // Add pause between operations
                if (running) {
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                }
                
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a number.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }
        
        // Close database connection and exit
        DatabaseConnection.closeConnection();
        System.out.println("👋 Thank you for using Smart Blood Bank Management System!");
    }
    
    /**
     * Display main menu options
     */
    private static void showMainMenu() {
        System.out.println(repeatChar('=', 50));
        System.out.println("           BLOOD BANK MANAGEMENT MENU");
        System.out.println(repeatChar('=', 50));
        System.out.println("1. 👤 Add Donor");
        System.out.println("2. 🏥 Add Recipient");
        System.out.println("3. 📊 View Blood Stock");
        System.out.println("4. 🔍 Search Donor by Blood Group");
        System.out.println("5. 🔍 Search Recipient by Blood Group");
        System.out.println("6. 📈 Update Blood Stock");
        System.out.println("7. 📋 View All Donors");
        System.out.println("8. 📋 View All Recipients");
        System.out.println("9. 🩺 Process Blood Request");
        System.out.println("10. ⚙️  Admin Functions");
        System.out.println("11. ❌ Exit");
        System.out.println(repeatChar('=', 50));
        System.out.print("Enter your choice (1-11): ");
    }
    
    /**
     * Handle user menu selection
     * @param choice Menu choice number
     * @return true to continue, false to exit
     */
    private static boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                addDonor();
                break;
            case 2:
                addRecipient();
                break;
            case 3:
                BloodStock.displayBloodStock();
                BloodStock.showLowStockAlerts();
                break;
            case 4:
                searchDonorByBloodGroup();
                break;
            case 5:
                searchRecipientByBloodGroup();
                break;
            case 6:
                updateBloodStock();
                break;
            case 7:
                viewAllDonors();
                break;
            case 8:
                viewAllRecipients();
                break;
            case 9:
                processBloodRequest();
                break;
            case 10:
                showAdminMenu();
                break;
            case 11:
                return false; // Exit
            default:
                System.out.println("❌ Invalid choice! Please select 1-11.");
        }
        return true;
    }
    
    /**
     * Add new donor with validation
     */
    private static void addDonor() {
        System.out.println("\n➕ ADD NEW DONOR");
        System.out.println(repeatChar('-', 30));
        
        try {
            // Get donor details
            System.out.print("Enter donor name: ");
            String name = scanner.nextLine().trim();
            
            if (name.isEmpty()) {
                System.out.println("❌ Name cannot be empty!");
                return;
            }
            
            System.out.print("Enter age: ");
            int age = Integer.parseInt(scanner.nextLine());
            
            if (age < 18 || age > 65) {
                System.out.println("❌ Age must be between 18 and 65 years!");
                return;
            }
            
            System.out.print("Enter blood group (A+, A-, B+, B-, AB+, AB-, O+, O-): ");
            String bloodGroup = scanner.nextLine().trim().toUpperCase();
            
            if (!Donor.isValidBloodGroup(bloodGroup)) {
                System.out.println("❌ Invalid blood group!");
                return;
            }
            
            System.out.print("Enter contact number: ");
            String contact = scanner.nextLine().trim();
            
            if (contact.length() < 10) {
                System.out.println("❌ Contact number must be at least 10 digits!");
                return;
            }
            
            System.out.print("Enter last donation date (YYYY-MM-DD) or press Enter if first time: ");
            String dateInput = scanner.nextLine().trim();
            
            LocalDate lastDonationDate = null;
            if (!dateInput.isEmpty()) {
                try {
                    lastDonationDate = LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (DateTimeParseException e) {
                    System.out.println("❌ Invalid date format! Using null for first-time donor.");
                }
            }
            
            // Create and add donor
            Donor donor = new Donor(name, age, bloodGroup, contact);
            donor.setLastDonationDate(lastDonationDate);
            
            if (donor.addDonor()) {
                System.out.println("✅ Donor added successfully!");
                
                // Show donation eligibility
                if (donor.canDonate()) {
                    System.out.println("✅ This donor is eligible for donation!");
                } else {
                    System.out.println("⚠️  This donor must wait before next donation (3 months gap required)");
                }
            } else {
                System.out.println("❌ Failed to add donor!");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid age! Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("❌ Error adding donor: " + e.getMessage());
        }
    }
    
    /**
     * Add new recipient with validation
     */
    private static void addRecipient() {
        System.out.println("\n➕ ADD NEW RECIPIENT");
        System.out.println(repeatChar('-', 30));
        
        try {
            System.out.print("Enter recipient name: ");
            String name = scanner.nextLine().trim();
            
            if (name.isEmpty()) {
                System.out.println("❌ Name cannot be empty!");
                return;
            }
            
            System.out.print("Enter blood group needed (A+, A-, B+, B-, AB+, AB-, O+, O-): ");
            String bloodGroup = scanner.nextLine().trim().toUpperCase();
            
            if (!Donor.isValidBloodGroup(bloodGroup)) {
                System.out.println("❌ Invalid blood group!");
                return;
            }
            
            System.out.print("Enter contact number: ");
            String contact = scanner.nextLine().trim();
            
            if (contact.length() < 10) {
                System.out.println("❌ Contact number must be at least 10 digits!");
                return;
            }
            
            System.out.print("Enter required blood units: ");
            int requiredUnits = Integer.parseInt(scanner.nextLine());
            
            if (requiredUnits <= 0) {
                System.out.println("❌ Required units must be greater than 0!");
                return;
            }
            
            // Create and add recipient
            Recipient recipient = new Recipient(name, bloodGroup, contact, requiredUnits);
            
            if (recipient.addRecipient()) {
                System.out.println("✅ Recipient added successfully!");
                
                // Check blood availability
                if (recipient.isBloodAvailable()) {
                    System.out.println("✅ Required blood is available in stock!");
                    System.out.print("Do you want to process this request now? (y/n): ");
                    String response = scanner.nextLine().trim().toLowerCase();
                    
                    if (response.equals("y") || response.equals("yes")) {
                        if (recipient.processBloodRequest()) {
                            System.out.println("✅ Blood request processed successfully!");
                        }
                    }
                } else {
                    System.out.println("⚠️  Required blood units are not available in current stock");
                    System.out.println("Compatible blood groups for " + bloodGroup + ":");
                    recipient.getCompatibleBloodGroups().forEach(bg -> 
                        System.out.println("  - " + bg + ": " + BloodStock.getAvailableUnits(bg) + " units"));
                }
            } else {
                System.out.println("❌ Failed to add recipient!");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input! Please enter valid numbers.");
        } catch (Exception e) {
            System.out.println("❌ Error adding recipient: " + e.getMessage());
        }
    }
    
    /**
     * Search and display donors by blood group
     */
    private static void searchDonorByBloodGroup() {
        System.out.println("\n🔍 SEARCH DONORS BY BLOOD GROUP");
        System.out.println(repeatChar('-', 35));
        
        System.out.print("Enter blood group to search (A+, A-, B+, B-, AB+, AB-, O+, O-): ");
        String bloodGroup = scanner.nextLine().trim().toUpperCase();
        
        if (!Donor.isValidBloodGroup(bloodGroup)) {
            System.out.println("❌ Invalid blood group!");
            return;
        }
        
        List<Donor> donors = Donor.searchByBloodGroup(bloodGroup);
        
        if (donors.isEmpty()) {
            System.out.println("❌ No donors found for blood group: " + bloodGroup);
        } else {
            System.out.println("✅ Found " + donors.size() + " donor(s) for blood group: " + bloodGroup);
            System.out.println(repeatChar('-', 80));
            System.out.printf("%-5s %-20s %-5s %-15s %-15s %-15s%n", 
                    "ID", "Name", "Age", "Contact", "Last Donation", "Can Donate");
           System.out.println(repeatChar('-', 80));
            
            for (Donor donor : donors) {
                System.out.printf("%-5d %-20s %-5d %-15s %-15s %-15s%n",
                        donor.getDonorId(),
                        donor.getName(),
                        donor.getAge(),
                        donor.getContact(),
                        donor.getLastDonationDate() != null ? donor.getLastDonationDate().toString() : "Never",
                        donor.canDonate() ? "Yes" : "No");
            }
        }
    }
    
    /**
     * Search and display recipients by blood group
     */
    private static void searchRecipientByBloodGroup() {
        System.out.println("\n🔍 SEARCH RECIPIENTS BY BLOOD GROUP");
        System.out.println(repeatChar('-', 35));
        
        System.out.print("Enter blood group to search (A+, A-, B+, B-, AB+, AB-, O+, O-): ");
        String bloodGroup = scanner.nextLine().trim().toUpperCase();
        
        if (!Donor.isValidBloodGroup(bloodGroup)) {
            System.out.println("❌ Invalid blood group!");
            return;
        }
        
        List<Recipient> recipients = Recipient.searchByBloodGroup(bloodGroup);
        
        if (recipients.isEmpty()) {
            System.out.println("❌ No recipients found for blood group: " + bloodGroup);
        } else {
            System.out.println("✅ Found " + recipients.size() + " recipient(s) for blood group: " + bloodGroup);
            System.out.println(repeatChar('-', 80));
            System.out.printf("%-5s %-20s %-15s %-10s %-15s%n", 
                    "ID", "Name", "Contact", "Units", "Request Date");
            System.out.println(repeatChar('-', 80));
            
            for (Recipient recipient : recipients) {
                System.out.printf("%-5d %-20s %-15s %-10d %-15s%n",
                        recipient.getRecipientId(),
                        recipient.getName(),
                        recipient.getContact(),
                        recipient.getRequiredUnits(),
                        recipient.getRequestDate().toString());
            }
        }
    }
    
    /**
     * Update blood stock for specific blood group
     */
    private static void updateBloodStock() {
        System.out.println("\n📈 UPDATE BLOOD STOCK");
        System.out.println(repeatChar('-', 25));
        
        // Show current stock first
        BloodStock.displayBloodStock();
        
        System.out.print("\nEnter blood group to update: ");
        String bloodGroup = scanner.nextLine().trim().toUpperCase();
        
        if (!Donor.isValidBloodGroup(bloodGroup)) {
            System.out.println("❌ Invalid blood group!");
            return;
        }
        
        int currentUnits = BloodStock.getAvailableUnits(bloodGroup);
        System.out.println("Current units for " + bloodGroup + ": " + currentUnits);
        
        System.out.println("\nSelect operation:");
        System.out.println("1. Set new total units");
        System.out.println("2. Add units to current stock");
        System.out.println("3. Remove units from current stock");
        System.out.print("Enter choice (1-3): ");
        
        try {
            int operation = Integer.parseInt(scanner.nextLine());
            
            switch (operation) {
                case 1:
                    System.out.print("Enter new total units: ");
                    int newUnits = Integer.parseInt(scanner.nextLine());
                    
                    if (BloodStock.updateBloodStock(bloodGroup, newUnits)) {
                        System.out.println("✅ Blood stock updated successfully!");
                    }
                    break;
                    
                case 2:
                    System.out.print("Enter units to add: ");
                    int addUnits = Integer.parseInt(scanner.nextLine());
                    
                    if (BloodStock.addUnitsToStock(bloodGroup, addUnits)) {
                        System.out.println("✅ Units added successfully!");
                    }
                    break;
                    
                case 3:
                    System.out.print("Enter units to remove: ");
                    int removeUnits = Integer.parseInt(scanner.nextLine());
                    
                    if (BloodStock.removeUnitsFromStock(bloodGroup, removeUnits)) {
                        System.out.println("✅ Units removed successfully!");
                    }
                    break;
                    
                default:
                    System.out.println("❌ Invalid choice!");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input! Please enter valid numbers.");
        }
    }
    
    /**
     * View all donors
     */
    private static void viewAllDonors() {
        System.out.println("\n📋 ALL DONORS");
        System.out.println(repeatChar('-', 20));
        
        List<Donor> donors = Donor.getAllDonors();
        
        if (donors.isEmpty()) {
            System.out.println("❌ No donors found in the system.");
        } else {
            System.out.println("Total donors: " + donors.size());
            System.out.println(repeatChar('-', 90));
            System.out.printf("%-5s %-20s %-5s %-10s %-15s %-15s %-15s%n", 
                    "ID", "Name", "Age", "Blood", "Contact", "Last Donation", "Can Donate");
            System.out.println(repeatChar('-', 90));
            
            for (Donor donor : donors) {
                System.out.printf("%-5d %-20s %-5d %-10s %-15s %-15s %-15s%n",
                        donor.getDonorId(),
                        donor.getName(),
                        donor.getAge(),
                        donor.getBloodGroup(),
                        donor.getContact(),
                        donor.getLastDonationDate() != null ? donor.getLastDonationDate().toString() : "Never",
                        donor.canDonate() ? "Yes" : "No");
            }
        }
    }
    
    /**
     * View all recipients
     */
    private static void viewAllRecipients() {
        System.out.println("\n📋 ALL RECIPIENTS");
        System.out.println(repeatChar('-', 25));
        
        List<Recipient> recipients = Recipient.getAllRecipients();
        
        if (recipients.isEmpty()) {
            System.out.println("❌ No recipients found in the system.");
        } else {
            System.out.println("Total recipients: " + recipients.size());
            System.out.println(repeatChar('-', 25));
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
    }
    
    /**
     * Process blood request for existing recipient
     */
    private static void processBloodRequest() {
        System.out.println("\n🩺 PROCESS BLOOD REQUEST");
        System.out.println(repeatChar('-', 30));
        
        // Show all recipients first
        List<Recipient> recipients = Recipient.getAllRecipients();
        
        if (recipients.isEmpty()) {
            System.out.println("❌ No recipients found in the system.");
            return;
        }
        
        System.out.println("Available recipients:");
        System.out.println(repeatChar('-', 70));
        System.out.printf("%-5s %-20s %-10s %-10s %-15s%n", 
                "ID", "Name", "Blood", "Units", "Request Date");
        System.out.println(repeatChar('-', 70));

        for (Recipient recipient : recipients) {
            System.out.printf("%-5d %-20s %-10s %-10d %-15s%n",
                    recipient.getRecipientId(),
                    recipient.getName(),
                    recipient.getBloodGroup(),
                    recipient.getRequiredUnits(),
                    recipient.getRequestDate().toString());
        }
        
        try {
            System.out.print("\nEnter recipient ID to process request: ");
            int recipientId = Integer.parseInt(scanner.nextLine());
            
            // Find recipient
            Recipient selectedRecipient = null;
            for (Recipient r : recipients) {
                if (r.getRecipientId() == recipientId) {
                    selectedRecipient = r;
                    break;
                }
            }
            
            if (selectedRecipient == null) {
                System.out.println("❌ Recipient not found with ID: " + recipientId);
                return;
            }
            
            System.out.println("\nProcessing request for: " + selectedRecipient.getName());
            System.out.println("Blood Group: " + selectedRecipient.getBloodGroup());
            System.out.println("Required Units: " + selectedRecipient.getRequiredUnits());
            
            // Check availability and process
            if (selectedRecipient.isBloodAvailable()) {
                System.out.print("Confirm processing this request? (y/n): ");
                String confirm = scanner.nextLine().trim().toLowerCase();
                
                if (confirm.equals("y") || confirm.equals("yes")) {
                    if (selectedRecipient.processBloodRequest()) {
                        System.out.println("✅ Blood request processed successfully!");
                        
                        // Show updated stock
                        System.out.println("\nUpdated stock for " + selectedRecipient.getBloodGroup() + 
                                         ": " + BloodStock.getAvailableUnits(selectedRecipient.getBloodGroup()) + " units");
                    }
                } else {
                    System.out.println("❌ Request processing cancelled.");
                }
            } else {
                System.out.println("❌ Insufficient blood units available!");
                System.out.println("Available: " + BloodStock.getAvailableUnits(selectedRecipient.getBloodGroup()) + 
                                 ", Required: " + selectedRecipient.getRequiredUnits());
                
                System.out.println("\nCompatible blood groups available:");
                for (String bg : selectedRecipient.getCompatibleBloodGroups()) {
                    int available = BloodStock.getAvailableUnits(bg);
                    System.out.printf("  %s: %d units%n", bg, available);
                }
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid recipient ID! Please enter a valid number.");
        }
    }
    
    /**
     * Show admin functions menu
     */
    private static void showAdminMenu() {
        boolean inAdminMenu = true;
        
        while (inAdminMenu) {
            System.out.println("\n⚙️  ADMIN FUNCTIONS");
            System.out.println(repeatChar('-', 20));
            System.out.println("1. 📊 Show Dashboard");
            System.out.println("2. 📈 Generate Reports");
            System.out.println("3. 📦 Backup Data");
            System.out.println("4. 🧹 Cleanup Old Records");
            System.out.println("5. ⚙️  System Maintenance");
            System.out.println("6. 📊 System Statistics");
            System.out.println("7. 🔙 Back to Main Menu");
            System.out.print("Enter choice (1-7): ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        admin.showDashboard();
                        break;
                    case 2:
                        admin.generateReport();
                        break;
                    case 3:
                        admin.backupData();
                        break;
                    case 4:
                        admin.cleanupOldRecords();
                        break;
                    case 5:
                        admin.performMaintenance();
                        break;
                    case 6:
                        System.out.println("\n📊 SYSTEM STATISTICS");
                        System.out.println(repeatChar('-', 25));
                        System.out.println(admin.getSystemStats());
                        break;
                    case 7:
                        inAdminMenu = false;
                        break;
                    default:
                        System.out.println("❌ Invalid choice! Please select 1-7.");
                }
                
                if (inAdminMenu && choice != 7) {
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                }
                
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a number.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }
}