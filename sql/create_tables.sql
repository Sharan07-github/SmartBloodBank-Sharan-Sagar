-- Smart Blood Bank Management System - Oracle SQL Table Creation Script
-- Run this script as your database user to create all necessary tables

-- Create Donors Table
CREATE TABLE Donors (
    DonorID NUMBER PRIMARY KEY,
    Name VARCHAR2(100) NOT NULL,
    Age NUMBER(3) CHECK (Age >= 18 AND Age <= 65),
    BloodGroup VARCHAR2(3) NOT NULL CHECK (BloodGroup IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')),
    Contact VARCHAR2(15) NOT NULL,
    LastDonationDate DATE,
    CONSTRAINT chk_contact_length CHECK (LENGTH(Contact) >= 10)
);

-- Create Recipients Table
CREATE TABLE Recipients (
    RecipientID NUMBER PRIMARY KEY,
    Name VARCHAR2(100) NOT NULL,
    BloodGroup VARCHAR2(3) NOT NULL CHECK (BloodGroup IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')),
    Contact VARCHAR2(15) NOT NULL,
    RequiredUnits NUMBER(3) CHECK (RequiredUnits > 0),
    RequestDate DATE DEFAULT SYSDATE,
    CONSTRAINT chk_recipient_contact CHECK (LENGTH(Contact) >= 10)
);

-- Create BloodStock Table
CREATE TABLE BloodStock (
    BloodGroup VARCHAR2(3) PRIMARY KEY CHECK (BloodGroup IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')),
    AvailableUnits NUMBER(5) DEFAULT 0 CHECK (AvailableUnits >= 0)
);

-- Create Sequences for Auto-increment IDs
CREATE SEQUENCE donor_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE recipient_seq START WITH 1 INCREMENT BY 1;

-- Create Triggers for Auto-increment
CREATE OR REPLACE TRIGGER donor_id_trigger
    BEFORE INSERT ON Donors
    FOR EACH ROW
BEGIN
    :NEW.DonorID := donor_seq.NEXTVAL;
END;
/

CREATE OR REPLACE TRIGGER recipient_id_trigger
    BEFORE INSERT ON Recipients
    FOR EACH ROW
BEGIN
    :NEW.RecipientID := recipient_seq.NEXTVAL;
END;
/

-- Initialize BloodStock with all blood groups
INSERT INTO BloodStock (BloodGroup, AvailableUnits) VALUES ('A+', 0);
INSERT INTO BloodStock (BloodGroup, AvailableUnits) VALUES ('A-', 0);
INSERT INTO BloodStock (BloodGroup, AvailableUnits) VALUES ('B+', 0);
INSERT INTO BloodStock (BloodGroup, AvailableUnits) VALUES ('B-', 0);
INSERT INTO BloodStock (BloodGroup, AvailableUnits) VALUES ('AB+', 0);
INSERT INTO BloodStock (BloodGroup, AvailableUnits) VALUES ('AB-', 0);
INSERT INTO BloodStock (BloodGroup, AvailableUnits) VALUES ('O+', 0);
INSERT INTO BloodStock (BloodGroup, AvailableUnits) VALUES ('O-', 0);

COMMIT;

-- Display success message
SELECT 'Tables created successfully!' AS Status FROM DUAL;