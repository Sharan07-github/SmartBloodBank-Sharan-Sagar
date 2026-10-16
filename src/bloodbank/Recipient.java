package bloodbank;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

/**
 * Recipient class represents a blood recipient with encapsulated properties
 * Handles all recipient-related database operations
 */
public class Recipient {
    
    // Private member variables (Encapsulation)
    private int recipientId;
    private String name;
    private String bloodGroup;
    private String contact;
    private int requiredUnits;
    private LocalDate requestDate;
    
    // Constructors
    public Recipient() {}
    
    public Recipient(String name, String bloodGroup, String contact, int requiredUnits) {
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.contact = contact;
        this.requiredUnits = requiredUnits;
        this.requestDate = LocalDate.now();
    }
    
    public Recipient(int recipientId, String name, String bloodGroup, String contact, int requiredUnits, LocalDate requestDate) {
        this.recipientId = recipientId;
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.contact = contact;
        this.requiredUnits = requiredUnits;
        this.requestDate = requestDate;
    }
    
    // Getter and Setter methods (Encapsulation)
    public int getRecipientId() { return recipientId; }
    public void setRecipientId(int recipientId) { this.recipientId = recipientId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public int getRequiredUnits() { return requiredUnits; }
    public void setRequiredUnits(int requiredUnits) { this.requiredUnits = requiredUnits; }
    
    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }
    
    /**
     * Add new recipient to database using prepared statement
     * @return true if successful, false otherwise
     */
    public boolean addRecipient() {
        String sql = "INSERT INTO Recipients (Name, BloodGroup, Contact, RequiredUnits, RequestDate) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, this.name);
            pstmt.setString(2, this.bloodGroup);
            pstmt.setString(3, this.contact);
            pstmt.setInt(4, this.requiredUnits);
            pstmt.setDate(5, Date.valueOf(this.requestDate));
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Recipient added successfully!");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding recipient: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Process blood request - check availability and update stock
     * @return true if request fulfilled, false otherwise
     */
    public boolean processBloodRequest() {
        // First check if enough blood is available
        if (!isBloodAvailable()) {
            System.out.println("Insufficient blood units available for " + this.bloodGroup);
            return false;
        }
        
        // Update blood stock by reducing required units
        String sql = "UPDATE BloodStock SET AvailableUnits = AvailableUnits - ? WHERE BloodGroup = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, this.requiredUnits);
            pstmt.setString(2, this.bloodGroup);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Blood request processed successfully!");
                System.out.println("Allocated " + this.requiredUnits + " units of " + this.bloodGroup + " to " + this.name);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error processing blood request: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Check if required blood units are available in stock
     * @return true if available, false otherwise
     */
    public boolean isBloodAvailable() {
        String sql = "SELECT AvailableUnits FROM BloodStock WHERE BloodGroup = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, this.bloodGroup);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int availableUnits = rs.getInt("AvailableUnits");
                return availableUnits >= this.requiredUnits;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking blood availability: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get all recipients from database
     * @return List of all recipients
     */
    public static List<Recipient> getAllRecipients() {
        List<Recipient> recipients = new ArrayList<>();
        String sql = "SELECT * FROM Recipients ORDER BY RequestDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Recipient recipient = new Recipient();
                recipient.setRecipientId(rs.getInt("RecipientID"));
                recipient.setName(rs.getString("Name"));
                recipient.setBloodGroup(rs.getString("BloodGroup"));
                recipient.setContact(rs.getString("Contact"));
                recipient.setRequiredUnits(rs.getInt("RequiredUnits"));
                
                Date requestDate = rs.getDate("RequestDate");
                if (requestDate != null) {
                    recipient.setRequestDate(requestDate.toLocalDate());
                }
                
                recipients.add(recipient);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving recipients: " + e.getMessage());
        }
        
        return recipients;
    }
    
    /**
     * Search recipients by blood group
     * @param bloodGroup Blood group to search for
     * @return List of recipients with specified blood group
     */
    public static List<Recipient> searchByBloodGroup(String bloodGroup) {
        List<Recipient> recipients = new ArrayList<>();
        String sql = "SELECT * FROM Recipients WHERE BloodGroup = ? ORDER BY RequestDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, bloodGroup);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Recipient recipient = new Recipient();
                recipient.setRecipientId(rs.getInt("RecipientID"));
                recipient.setName(rs.getString("Name"));
                recipient.setBloodGroup(rs.getString("BloodGroup"));
                recipient.setContact(rs.getString("Contact"));
                recipient.setRequiredUnits(rs.getInt("RequiredUnits"));
                
                Date requestDate = rs.getDate("RequestDate");
                if (requestDate != null) {
                    recipient.setRequestDate(requestDate.toLocalDate());
                }
                
                recipients.add(recipient);
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching recipients: " + e.getMessage());
        }
        
        return recipients;
    }
    
    /**
     * Update recipient information
     * @return true if successful, false otherwise
     */
    public boolean updateRecipient() {
        String sql = "UPDATE Recipients SET Name=?, BloodGroup=?, Contact=?, RequiredUnits=? WHERE RecipientID=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, this.name);
            pstmt.setString(2, this.bloodGroup);
            pstmt.setString(3, this.contact);
            pstmt.setInt(4, this.requiredUnits);
            pstmt.setInt(5, this.recipientId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Recipient updated successfully!");
                return true;
            } else {
                System.out.println("No recipient found with ID: " + this.recipientId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating recipient: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete recipient from database
     * @param recipientId ID of recipient to delete
     * @return true if successful, false otherwise
     */
    public static boolean deleteRecipient(int recipientId) {
        String sql = "DELETE FROM Recipients WHERE RecipientID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, recipientId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Recipient deleted successfully!");
                return true;
            } else {
                System.out.println("No recipient found with ID: " + recipientId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting recipient: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get compatible blood groups for this recipient
     * @return List of compatible blood groups
     */
    public List<String> getCompatibleBloodGroups() {
        List<String> compatible = new ArrayList<>();
        
        switch (this.bloodGroup) {
            case "AB+":
                compatible.add("A+"); compatible.add("A-"); compatible.add("B+"); 
                compatible.add("B-"); compatible.add("AB+"); compatible.add("AB-"); 
                compatible.add("O+"); compatible.add("O-");
                break;
            case "AB-":
                compatible.add("A-"); compatible.add("B-"); compatible.add("AB-"); compatible.add("O-");
                break;
            case "A+":
                compatible.add("A+"); compatible.add("A-"); compatible.add("O+"); compatible.add("O-");
                break;
            case "A-":
                compatible.add("A-"); compatible.add("O-");
                break;
            case "B+":
                compatible.add("B+"); compatible.add("B-"); compatible.add("O+"); compatible.add("O-");
                break;
            case "B-":
                compatible.add("B-"); compatible.add("O-");
                break;
            case "O+":
                compatible.add("O+"); compatible.add("O-");
                break;
            case "O-":
                compatible.add("O-");
                break;
        }
        
        return compatible;
    }
    
    @Override
    public String toString() {
        return String.format("Recipient{ID=%d, Name='%s', BloodGroup='%s', Contact='%s', RequiredUnits=%d, RequestDate=%s}",
                recipientId, name, bloodGroup, contact, requiredUnits, requestDate);
    }
}