package bloodbank;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

/**
 * Donor class represents a blood donor with encapsulated properties
 * Handles all donor-related database operations
 */
public class Donor {
    
    // Private member variables (Encapsulation)
    private int donorId;
    private String name;
    private int age;
    private String bloodGroup;
    private String contact;
    private LocalDate lastDonationDate;
    
    // Constructors
    public Donor() {}
    
    public Donor(String name, int age, String bloodGroup, String contact) {
        this.name = name;
        this.age = age;
        this.bloodGroup = bloodGroup;
        this.contact = contact;
    }
    
    public Donor(int donorId, String name, int age, String bloodGroup, String contact, LocalDate lastDonationDate) {
        this.donorId = donorId;
        this.name = name;
        this.age = age;
        this.bloodGroup = bloodGroup;
        this.contact = contact;
        this.lastDonationDate = lastDonationDate;
    }
    
    // Getter and Setter methods (Encapsulation)
    public int getDonorId() { return donorId; }
    public void setDonorId(int donorId) { this.donorId = donorId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public LocalDate getLastDonationDate() { return lastDonationDate; }
    public void setLastDonationDate(LocalDate lastDonationDate) { this.lastDonationDate = lastDonationDate; }
    
    /**
     * Add new donor to database using prepared statement
     * @return true if successful, false otherwise
     */
    public boolean addDonor() {
        String sql = "INSERT INTO Donors (Name, Age, BloodGroup, Contact, LastDonationDate) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, this.name);
            pstmt.setInt(2, this.age);
            pstmt.setString(3, this.bloodGroup);
            pstmt.setString(4, this.contact);
            pstmt.setDate(5, this.lastDonationDate != null ? Date.valueOf(this.lastDonationDate) : null);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Donor added successfully!");
                
                // Update blood stock after donation
                updateBloodStockAfterDonation();
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding donor: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Update blood stock after donation (1 unit per donation)
     */
    private void updateBloodStockAfterDonation() {
        String sql = "UPDATE BloodStock SET AvailableUnits = AvailableUnits + 1 WHERE BloodGroup = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, this.bloodGroup);
            pstmt.executeUpdate();
            System.out.println("Blood stock updated: +1 unit of " + this.bloodGroup);
            
        } catch (SQLException e) {
            System.err.println("Error updating blood stock: " + e.getMessage());
        }
    }
    
    /**
     * Search donors by blood group
     * @param bloodGroup Blood group to search for
     * @return List of donors with specified blood group
     */
    public static List<Donor> searchByBloodGroup(String bloodGroup) {
        List<Donor> donors = new ArrayList<>();
        String sql = "SELECT * FROM Donors WHERE BloodGroup = ? ORDER BY LastDonationDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, bloodGroup);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Donor donor = new Donor();
                donor.setDonorId(rs.getInt("DonorID"));
                donor.setName(rs.getString("Name"));
                donor.setAge(rs.getInt("Age"));
                donor.setBloodGroup(rs.getString("BloodGroup"));
                donor.setContact(rs.getString("Contact"));
                
                Date lastDonationDate = rs.getDate("LastDonationDate");
                if (lastDonationDate != null) {
                    donor.setLastDonationDate(lastDonationDate.toLocalDate());
                }
                
                donors.add(donor);
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching donors: " + e.getMessage());
        }
        
        return donors;
    }
    
    /**
     * Get all donors from database
     * @return List of all donors
     */
    public static List<Donor> getAllDonors() {
        List<Donor> donors = new ArrayList<>();
        String sql = "SELECT * FROM Donors ORDER BY DonorID";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Donor donor = new Donor();
                donor.setDonorId(rs.getInt("DonorID"));
                donor.setName(rs.getString("Name"));
                donor.setAge(rs.getInt("Age"));
                donor.setBloodGroup(rs.getString("BloodGroup"));
                donor.setContact(rs.getString("Contact"));
                
                Date lastDonationDate = rs.getDate("LastDonationDate");
                if (lastDonationDate != null) {
                    donor.setLastDonationDate(lastDonationDate.toLocalDate());
                }
                
                donors.add(donor);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving donors: " + e.getMessage());
        }
        
        return donors;
    }
    
    /**
     * Update donor information
     * @return true if successful, false otherwise
     */
    public boolean updateDonor() {
        String sql = "UPDATE Donors SET Name=?, Age=?, BloodGroup=?, Contact=?, LastDonationDate=? WHERE DonorID=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, this.name);
            pstmt.setInt(2, this.age);
            pstmt.setString(3, this.bloodGroup);
            pstmt.setString(4, this.contact);
            pstmt.setDate(5, this.lastDonationDate != null ? Date.valueOf(this.lastDonationDate) : null);
            pstmt.setInt(6, this.donorId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating donor: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete donor from database
     * @param donorId ID of donor to delete
     * @return true if successful, false otherwise
     */
    public static boolean deleteDonor(int donorId) {
        String sql = "DELETE FROM Donors WHERE DonorID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, donorId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Donor deleted successfully!");
                return true;
            } else {
                System.out.println("No donor found with ID: " + donorId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting donor: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if donor can donate (minimum 3 months gap)
     * @return true if can donate, false otherwise
     */
    public boolean canDonate() {
        if (lastDonationDate == null) {
            return true; // First time donor
        }
        
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
        return lastDonationDate.isBefore(threeMonthsAgo);
    }
    
    /**
     * Validate blood group format
     * @param bloodGroup Blood group to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidBloodGroup(String bloodGroup) {
        String[] validGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String valid : validGroups) {
            if (valid.equals(bloodGroup)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("Donor{ID=%d, Name='%s', Age=%d, BloodGroup='%s', Contact='%s', LastDonation=%s}",
                donorId, name, age, bloodGroup, contact, lastDonationDate);
    }
}