-- Sample Data for Smart Blood Bank Management System
-- Run this script after creating tables to populate with sample data

-- Insert Sample Donors
INSERT INTO Donors (Name, Age, BloodGroup, Contact, LastDonationDate) VALUES 
('John Doe', 25, 'O+', '9876543210', DATE '2024-01-15');

INSERT INTO Donors (Name, Age, BloodGroup, Contact, LastDonationDate) VALUES 
('Jane Smith', 30, 'A+', '8765432109', DATE '2024-02-10');

INSERT INTO Donors (Name, Age, BloodGroup, Contact, LastDonationDate) VALUES 
('Mike Johnson', 28, 'B+', '7654321098', DATE '2024-01-20');

INSERT INTO Donors (Name, Age, BloodGroup, Contact, LastDonationDate) VALUES 
('Sarah Wilson', 35, 'AB+', '6543210987', DATE '2024-02-05');

INSERT INTO Donors (Name, Age, BloodGroup, Contact, LastDonationDate) VALUES 
('David Brown', 40, 'O-', '5432109876', DATE '2024-01-30');

-- Insert Sample Recipients
INSERT INTO Recipients (Name, BloodGroup, Contact, RequiredUnits, RequestDate) VALUES 
('Patient Alpha', 'O+', '9988776655', 2, SYSDATE);

INSERT INTO Recipients (Name, BloodGroup, Contact, RequiredUnits, RequestDate) VALUES 
('Patient Beta', 'A+', '8877665544', 1, SYSDATE);

INSERT INTO Recipients (Name, BloodGroup, Contact, RequiredUnits, RequestDate) VALUES 
('Patient Gamma', 'B-', '7766554433', 3, SYSDATE);

-- Update BloodStock with initial units
UPDATE BloodStock SET AvailableUnits = 10 WHERE BloodGroup = 'O+';
UPDATE BloodStock SET AvailableUnits = 8 WHERE BloodGroup = 'A+';
UPDATE BloodStock SET AvailableUnits = 6 WHERE BloodGroup = 'B+';
UPDATE BloodStock SET AvailableUnits = 4 WHERE BloodGroup = 'AB+';
UPDATE BloodStock SET AvailableUnits = 7 WHERE BloodGroup = 'O-';
UPDATE BloodStock SET AvailableUnits = 5 WHERE BloodGroup = 'A-';
UPDATE BloodStock SET AvailableUnits = 3 WHERE BloodGroup = 'B-';
UPDATE BloodStock SET AvailableUnits = 2 WHERE BloodGroup = 'AB-';

COMMIT;

-- Display sample data insertion success
SELECT 'Sample data inserted successfully!' AS Status FROM DUAL;
SELECT 'Total Donors: ' || COUNT(*) AS DonorCount FROM Donors;
SELECT 'Total Recipients: ' || COUNT(*) AS RecipientCount FROM Recipients;
SELECT 'Total Blood Stock: ' || SUM(AvailableUnits) AS TotalUnits FROM BloodStock;