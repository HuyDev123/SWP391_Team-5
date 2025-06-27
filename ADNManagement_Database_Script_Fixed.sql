-- ========================================
-- ADN MANAGEMENT SYSTEM DATABASE SCRIPT
-- Script hoàn chỉnh bao gồm TestResult
-- ========================================

-- Tạo database
USE master;
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = N'ADNManagement')
BEGIN
    ALTER DATABASE ADNManagement SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE ADNManagement;
END
GO

CREATE DATABASE ADNManagement;
GO

USE ADNManagement;
GO

-- ========================================
-- TẠO CÁC BẢNG (khớp với Entity Java)
-- ========================================

-- Bảng User (không có bảng Role riêng)
CREATE TABLE [User] (
    id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    role_id INT NOT NULL,
    is_active BIT,
    created_at DATETIME
);

-- Bảng Service
CREATE TABLE Service (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    price DECIMAL(18,2),
    is_active BIT
);

-- Bảng Booking
CREATE TABLE Booking (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT,
    booking_date DATETIME,
    status NVARCHAR(100),
    staff_id INT,
    note NVARCHAR(MAX),
    full_name NVARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(20),
    is_administrative BIT,
    is_center_collected BIT,
    address NVARCHAR(500),
    center_sample_date DATE,
    center_sample_time TIME,
    FOREIGN KEY (user_id) REFERENCES [User](id),
    FOREIGN KEY (staff_id) REFERENCES [User](id)
);

-- Bảng KitType
CREATE TABLE KitType (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255),
    description NVARCHAR(MAX)
);

-- Bảng Service_KitType
CREATE TABLE Service_KitType (
    service_id INT NOT NULL,
    kit_type_id INT NOT NULL,
    PRIMARY KEY (service_id, kit_type_id),
    FOREIGN KEY (service_id) REFERENCES Service(id),
    FOREIGN KEY (kit_type_id) REFERENCES KitType(id)
);

-- Bảng Booking_Service
CREATE TABLE Booking_Service (
    booking_id INT NOT NULL,
    service_id INT NOT NULL,
    PRIMARY KEY (booking_id, service_id),
    FOREIGN KEY (booking_id) REFERENCES Booking(id),
    FOREIGN KEY (service_id) REFERENCES Service(id)
);

-- Bảng KitItem
CREATE TABLE KitItem (
    id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL,
    service_id INT NOT NULL,
    kit_code VARCHAR(255) UNIQUE,
    kit_type_id INT NOT NULL,
    delivery_status NVARCHAR(100),
    send_date DATE,
    receive_date DATE,
    note NVARCHAR(MAX),
    FOREIGN KEY (booking_id, service_id) REFERENCES Booking_Service(booking_id, service_id),
    FOREIGN KEY (kit_type_id) REFERENCES KitType(id)
);

-- Bảng Participant
CREATE TABLE Participant (
    id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL,
    user_id INT,
    full_name NVARCHAR(255),
    gender NVARCHAR(10),
    birthday DATE,
    phone VARCHAR(20),
    email VARCHAR(255),
    address NVARCHAR(MAX),
    cccd_number VARCHAR(50),
    cccd_issued_date DATE,
    cccd_issued_place NVARCHAR(255),
    relationship NVARCHAR(50),
    photo_url VARCHAR(255),
    note NVARCHAR(MAX),
    FOREIGN KEY (booking_id) REFERENCES Booking(id),
    FOREIGN KEY (user_id) REFERENCES [User](id)
);

-- Bảng TestSample
CREATE TABLE TestSample (
    id INT IDENTITY(1,1) PRIMARY KEY,
    service_id INT NOT NULL,
    participant_id INT NOT NULL,
    sample_code VARCHAR(100),
    sample_type NVARCHAR(50),
    collected_by INT,
    collected_at DATETIME,
    note NVARCHAR(MAX),
    FOREIGN KEY (service_id) REFERENCES Service(id),
    FOREIGN KEY (participant_id) REFERENCES Participant(id),
    FOREIGN KEY (collected_by) REFERENCES [User](id)
);

-- Bảng TestResult (cho hệ thống quản lý kết quả)
CREATE TABLE TestResult (
    id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL,
    service_id INT NOT NULL,
    result_code VARCHAR(255) UNIQUE NOT NULL,
    probability_percentage DECIMAL(5,2),
    conclusion NVARCHAR(MAX),
    detailed_analysis NVARCHAR(MAX),
    test_method NVARCHAR(255),
    lab_technician NVARCHAR(255),
    reviewed_by NVARCHAR(255),
    review_date DATETIME,
    pdf_file_path VARCHAR(500),
    status NVARCHAR(50) DEFAULT 'PENDING', -- PENDING, COMPLETED, REVIEWED, DELIVERED
    notes NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (booking_id, service_id) REFERENCES Booking_Service(booking_id, service_id)
);

-- ========================================
-- THÊM DỮ LIỆU MẪU
-- ========================================

-- Thêm Users (roleId: 1=Admin, 2=Manager, 3=Staff, 4=Customer)
INSERT INTO [User] (full_name, email, role_id, is_active, created_at) VALUES
(N'Nguyễn Văn Admin', 'admin@adnlab.com', 1, 1, GETDATE()),
(N'Trần Thị Manager', 'manager@adnlab.com', 2, 1, GETDATE()),
(N'Lê Văn Staff', 'staff@adnlab.com', 3, 1, GETDATE()),
(N'Phạm Thị Customer', 'customer@gmail.com', 4, 1, GETDATE()),
(N'Hoàng Văn Khách', 'khach@gmail.com', 4, 1, GETDATE()),
(N'Dr. Nguyễn Văn A', 'technician1@adnlab.com', 3, 1, GETDATE()),
(N'Dr. Trần Thị B', 'technician2@adnlab.com', 3, 1, GETDATE());

-- Thêm Services
INSERT INTO Service (name, description, price, is_active) VALUES
(N'Xét nghiệm ADN Cha - Con', 
 N'Xác định quan hệ huyết thống cha - con. Thời gian: Khoảng 4 ngày làm việc. Bảo mật tuyệt đối. Độ chính xác 99.99%.', 
 5000000, 1),
(N'Xét nghiệm ADN Mẹ - Con', 
 N'Xác định quan hệ huyết thống mẹ - con. Thời gian: Khoảng 4 ngày làm việc. Bảo mật tuyệt đối. Độ chính xác 99.99%.', 
 5000000, 1),
(N'Xét nghiệm ADN Anh/Chị - Em', 
 N'Xác định quan hệ huyết thống anh chị em. Thời gian: Khoảng 5 ngày làm việc. Bảo mật tuyệt đối. Độ chính xác 99.99%.', 
 4000000, 1),
(N'Xét nghiệm ADN Ông/Bà - Cháu', 
 N'Xác định quan hệ huyết thống ông bà cháu. Thời gian: Khoảng 5 ngày làm việc. Bảo mật tuyệt đối. Độ chính xác 99.99%.', 
 4000000, 1);

-- Thêm KitTypes
INSERT INTO KitType (name, description) VALUES
(N'GlobalFiler™ (Applied Biosystems)', N'Kit xét nghiệm ADN chính xác cao'),
(N'PowerPlex® Fusion (Promega)', N'Kit xét nghiệm ADN đa năng'),
(N'Investigator® 24plex QS (Qiagen)', N'Kit xét nghiệm ADN nhanh'),
(N'QIAamp DNA Mini Kit (Qiagen)', N'Kit chiết xuất ADN tiêu chuẩn');

-- Thêm Service_KitType
INSERT INTO Service_KitType (service_id, kit_type_id) VALUES
(1, 1), (1, 2),
(2, 2), (2, 3), 
(3, 3), (3, 4),
(4, 1), (4, 4);

-- Thêm Bookings mẫu
INSERT INTO Booking (user_id, booking_date, status, full_name, email, phone, 
                    is_administrative, is_center_collected, center_sample_date, center_sample_time) VALUES
(4, GETDATE(), N'Đã xác nhận', N'Phạm Thị Customer', 'customer@gmail.com', '0901234570', 
 0, 1, '2024-01-15', '09:00:00'),
(5, GETDATE(), N'Đang xử lý', N'Hoàng Văn Khách', 'khach@gmail.com', '0901234571', 
 1, 0, NULL, NULL),
(4, DATEADD(day, -7, GETDATE()), N'Hoàn thành', N'Phạm Thị Customer', 'customer@gmail.com', '0901234570', 
 0, 1, '2024-01-08', '10:00:00'),
(5, DATEADD(day, -14, GETDATE()), N'Hoàn thành', N'Hoàng Văn Khách', 'khach@gmail.com', '0901234571', 
 0, 1, '2024-01-01', '14:00:00');

-- Thêm địa chỉ cho booking lấy mẫu tại nhà
UPDATE Booking SET address = N'999 Đường Test, Quận 6, TP.HCM' WHERE id = 2;

-- Thêm Booking_Service
INSERT INTO Booking_Service (booking_id, service_id) VALUES
(1, 1), -- Booking 1 - Xét nghiệm Cha-Con
(2, 3), -- Booking 2 - Xét nghiệm Anh/Chị-Em  
(3, 2), -- Booking 3 - Xét nghiệm Mẹ-Con
(4, 4); -- Booking 4 - Xét nghiệm Ông/Bà-Cháu

-- Thêm Participants
INSERT INTO Participant (booking_id, full_name, gender, birthday, relationship, phone) VALUES
(1, N'Phạm Văn Bố', N'Nam', '1980-05-15', N'Bố', '0901111111'),
(1, N'Phạm Thị Con', N'Nữ', '2010-08-20', N'Con', '0902222222'),
(2, N'Hoàng Văn Anh', N'Nam', '1990-03-10', N'Anh', '0903333333'),
(2, N'Hoàng Thị Em', N'Nữ', '1995-07-25', N'Em', '0904444444'),
(3, N'Phạm Thị Mẹ', N'Nữ', '1985-12-10', N'Mẹ', '0905555555'),
(3, N'Phạm Văn Con2', N'Nam', '2015-06-30', N'Con', '0906666666'),
(4, N'Hoàng Văn Ông', N'Nam', '1950-01-20', N'Ông', '0907777777'),
(4, N'Hoàng Thị Cháu', N'Nữ', '2000-09-15', N'Cháu', '0908888888');

-- Thêm KitItems
INSERT INTO KitItem (booking_id, service_id, kit_code, kit_type_id, delivery_status, send_date, receive_date) VALUES
(1, 1, 'KIT001', 1, N'Đã nhận', '2024-01-10', '2024-01-12'),
(2, 3, 'KIT002', 3, N'Đang vận chuyển', '2024-01-20', NULL),
(3, 2, 'KIT003', 2, N'Đã nhận', '2024-01-05', '2024-01-07'),
(4, 4, 'KIT004', 4, N'Đã nhận', '2023-12-28', '2023-12-30');

-- Thêm TestSamples
INSERT INTO TestSample (service_id, participant_id, sample_code, sample_type, collected_at) VALUES
(1, 1, 'SAMPLE001', N'Nước bọt', '2024-01-12 09:30:00'),
(1, 2, 'SAMPLE002', N'Nước bọt', '2024-01-12 09:35:00'),
(3, 3, 'SAMPLE003', N'Nước bọt', '2024-01-21 10:00:00'),
(3, 4, 'SAMPLE004', N'Nước bọt', '2024-01-21 10:05:00'),
(2, 5, 'SAMPLE005', N'Nước bọt', '2024-01-07 14:20:00'),
(2, 6, 'SAMPLE006', N'Nước bọt', '2024-01-07 14:25:00'),
(4, 7, 'SAMPLE007', N'Nước bọt', '2023-12-30 11:10:00'),
(4, 8, 'SAMPLE008', N'Nước bọt', '2023-12-30 11:15:00');

-- Thêm TestResults mẫu
INSERT INTO TestResult (booking_id, service_id, result_code, probability_percentage, conclusion, 
                       detailed_analysis, test_method, lab_technician, status, created_at) VALUES
(3, 2, 'RS2024001', 99.95, N'Xét nghiệm xác nhận quan hệ huyết thống mẹ - con', 
 N'Phân tích 23 marker STR cho thấy sự tương đồng cao trong ADN. Kết quả khẳng định mối quan hệ huyết thống mẹ - con với độ tin cậy 99.95%.', 
 N'STR Analysis - 23 markers', N'Dr. Nguyễn Văn A', 'COMPLETED', DATEADD(day, -5, GETDATE())),

(4, 4, 'RS2024002', 98.75, N'Xét nghiệm xác nhận quan hệ huyết thống ông - cháu', 
 N'Phân tích ADN mitochondrial và Y-chromosome cho thấy sự liên kết gia đình. Kết quả khẳng định mối quan hệ ông - cháu với độ tin cậy 98.75%.', 
 N'mtDNA + Y-STR Analysis', N'Dr. Trần Thị B', 'REVIEWED', DATEADD(day, -3, GETDATE())),

(1, 1, 'RS2024003', 99.99, N'Xét nghiệm xác nhận quan hệ huyết thống cha - con', 
 N'Phân tích 24 marker STR cho thấy sự khớp hoàn hảo trong các allele. Kết quả khẳng định mối quan hệ cha - con với độ tin cậy 99.99%.', 
 N'STR Analysis - 24 markers', N'Dr. Nguyễn Văn A', 'DELIVERED', DATEADD(day, -1, GETDATE())),

(2, 3, 'RS2024004', NULL, N'Đang xử lý mẫu xét nghiệm', 
 N'Mẫu ADN đã được tiếp nhận và đang trong quá trình phân tích. Dự kiến hoàn thành trong 2-3 ngày tới.', 
 N'STR Analysis', N'Dr. Trần Thị B', 'PENDING', GETDATE());

-- Cập nhật review information cho kết quả đã duyệt
UPDATE TestResult 
SET reviewed_by = N'Dr. Nguyễn Văn Admin', review_date = DATEADD(day, -2, GETDATE())
WHERE id IN (2, 3);

-- ========================================
-- TẠO INDEX ĐỂ TỐI ƯU HIỆU SUẤT
-- ========================================

CREATE INDEX IX_User_Email ON [User](email);
CREATE INDEX IX_User_RoleId ON [User](role_id);
CREATE INDEX IX_Booking_Status ON Booking(status);
CREATE INDEX IX_Booking_Date ON Booking(booking_date);
CREATE INDEX IX_KitItem_Code ON KitItem(kit_code);
CREATE INDEX IX_TestSample_Code ON TestSample(sample_code);
CREATE INDEX IX_TestResult_Code ON TestResult(result_code);
CREATE INDEX IX_TestResult_Status ON TestResult(status);
CREATE INDEX IX_TestResult_Technician ON TestResult(lab_technician);
CREATE INDEX IX_TestResult_CreatedAt ON TestResult(created_at);

-- ========================================
-- TẠO STORED PROCEDURES (Tùy chọn)
-- ========================================

-- Procedure lấy thống kê kết quả theo trạng thái
CREATE PROCEDURE sp_GetResultStatistics
AS
BEGIN
    SELECT 
        status,
        COUNT(*) as count
    FROM TestResult 
    GROUP BY status;
END;
GO

-- Procedure tìm kiếm kết quả
CREATE PROCEDURE sp_SearchResults
    @Status NVARCHAR(50) = NULL,
    @Technician NVARCHAR(255) = NULL,
    @SearchText NVARCHAR(255) = NULL
AS
BEGIN
    SELECT 
        tr.id,
        tr.result_code,
        tr.probability_percentage,
        tr.conclusion,
        tr.lab_technician,
        tr.status,
        tr.created_at,
        b.full_name as customer_name,
        s.name as service_name
    FROM TestResult tr
    INNER JOIN Booking b ON tr.booking_id = b.id
    INNER JOIN Service s ON tr.service_id = s.id
    WHERE 
        (@Status IS NULL OR tr.status = @Status)
        AND (@Technician IS NULL OR tr.lab_technician LIKE '%' + @Technician + '%')
        AND (@SearchText IS NULL OR b.full_name LIKE '%' + @SearchText + '%')
    ORDER BY tr.created_at DESC;
END;
GO

PRINT 'Database ADNManagement đã được tạo thành công!';
PRINT 'Script hoàn chỉnh bao gồm 10 bảng + TestResult Management System';
PRINT 'Tổng số bảng: 10 (bao gồm TestResult)';
PRINT 'Tổng số stored procedures: 2';
PRINT 'Đã thêm dữ liệu mẫu cho testing';
PRINT '========================================';
PRINT 'READY FOR ADN MANAGEMENT SYSTEM!';
PRINT '🧬 Database khớp hoàn toàn với Java Entities';
PRINT '📊 Results Management System sẵn sàng';
PRINT '🔗 API endpoints có thể kết nối ngay';
PRINT '========================================';