CREATE DATABASE DNAGenealogyDB;
GO

USE DNAGenealogyDB;
GO

CREATE TABLE Account (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    GoogleID NVARCHAR(100) NOT NULL UNIQUE,  -- sub từ Google
    Email NVARCHAR(100) NOT NULL UNIQUE,
    FullName NVARCHAR(100),
    [Role] NVARCHAR(10) NOT NULL CHECK (Role IN ('Customer', 'Staff', 'Manager', 'Admin')),
    [Status] NVARCHAR(10) NOT NULL DEFAULT 'Active' CHECK (Status IN ('Active', 'Inactive')),
);
go

CREATE TABLE InternalUserInfo (
    InternalID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL,                             -- Khóa ngoại tới bảng User
    StaffCode NVARCHAR(20) NOT NULL UNIQUE,          -- Mã số nhân viên
    FullName NVARCHAR(100),
    Position NVARCHAR(50),                           -- Chức danh: Admin/Manager/Staff
    FOREIGN KEY (UserID) REFERENCES Account(UserID)
);

CREATE TABLE CustomerInfo (
    CustomerID INT IDENTITY(1,1),
    UserID INT NOT NULL,                             -- Khóa ngoại đến bảng User
    FullName NVARCHAR(100) NOT NULL,
	Email NVARCHAR(100) NOT NULL PRIMARY KEY,
    Phone NVARCHAR(20),
    [Address] NVARCHAR(255),
    FOREIGN KEY (UserID) REFERENCES Account(UserID)
);
go 

CREATE TABLE Appointment (
    AppointmentID NVARCHAR(20) PRIMARY KEY,
	FullName NVARCHAR(100) NOT NULL,
	Email NVARCHAR(100) NOT NULL UNIQUE,
    Phone NVARCHAR(20),
    StaffCode NVARCHAR(20) NOT NULL,
    ServiceID NVARCHAR(20) NOT NULL,
    AppointmentDate DATE NOT NULL,
    AppointmentTime TIME NOT NULL,
    [Status] NVARCHAR(20) NOT NULL DEFAULT N'Đã đặt'
    CHECK (Status IN (N'Đã đặt', N'Đang chờ lấy mẫu', N'Đã nhận mẫu', N'Đã hoàn thành', N'Đã hủy')),
    TestType NVARCHAR(20) NOT NULL CHECK (TestType IN (N'Hành chính', N'Dân sự')),
    SampleCollectionMethod NVARCHAR(30) NOT NULL CHECK (SampleCollectionMethod IN (N'Tại nhà', N'Tại cơ sở y tế')),
    [Address] NVARCHAR(255) NOT NULL,
    NumberOfTesters INT NOT NULL,
    Notes NVARCHAR(MAX) NULL,
    FOREIGN KEY (Email) REFERENCES CustomerInfo(Email),
    FOREIGN KEY (StaffCode) REFERENCES InternalUserInfo(StaffCode),
    FOREIGN KEY (ServiceID) REFERENCES Service(ServiceID)
);

go

CREATE TABLE Service (
    ServiceID NVARCHAR(20) PRIMARY KEY,
    ServiceName NVARCHAR(100) NOT NULL,
    [Description] NVARCHAR(MAX) NULL,
    Duration INT NULL, -- Duration in days
    Price INT NOT NULL,
    Category NVARCHAR(50) NULL,
    [Status] NVARCHAR(10) NOT NULL DEFAULT 'Active' CHECK (Status IN ('Active', 'Inactive')),
);
go 

CREATE TABLE Kit (
    KitCode NVARCHAR(20) PRIMARY KEY,           -- Mã kit
    SentDate DATE NOT NULL,                     -- Ngày gửi
    ReceivedDate DATE NULL,                     -- Ngày nhận (có thể NULL)
    SampleCount INT NOT NULL,                   -- Số mẫu
    StaffCode NVARCHAR(20) NOT NULL,            -- Mã nhân viên quản lý
    [Status] NVARCHAR(30) NOT NULL CHECK (Status IN (N'Chờ gửi', N'Đã gửi', N'Đã nhận mẫu', N'Lỗi mẫu', N'Đã xử lý', N'Đã trả kết quả')), -- Trạng thái
    ResultFilePath NVARCHAR(255) NULL, -- Đường dẫn file (hoặc tên file) kết quả xét nghiệm
    FOREIGN KEY (StaffCode) REFERENCES InternalUserInfo(StaffCode) -- Nếu có bảng nhân viên
);

go

CREATE TABLE [Sample] (
    SampleID NVARCHAR(20) PRIMARY KEY,              -- Mã mẫu
    KitCode NVARCHAR(20) NOT NULL,                  -- Mã kit liên kết
    SampleType NVARCHAR(50) NOT NULL,               -- Loại mẫu
    CollectedBy NVARCHAR(50) NULL,                  -- Người lấy mẫu (StaffCode hoặc tên)
    CollectedDate DATE NULL,                        -- Ngày lấy mẫu
    Status NVARCHAR(20) NOT NULL DEFAULT N'Chờ lấy' CHECK (Status IN (N'Chờ lấy', N'Đã lấy', N'Đang phân tích', N'Hoàn thành', N'Lỗi')),  -- Trạng thái
    Notes NVARCHAR(MAX) NULL,                       -- Ghi chú
    FOREIGN KEY (KitCode) REFERENCES Kit(KitCode)
);

go









INSERT INTO Service (ServiceName, [Description], Duration, Price, Category, [Status])
VALUES
(N'Xét nghiệm cha con', N'Xác định mối quan hệ huyết thống giữa cha và con với độ chính xác 99.99%', NULL, 5000000, N'Dân sự', N'Active'),
(N'Xét nghiệm mẹ con', N'Xác định mối quan hệ huyết thống giữa mẹ và con với công nghệ tiên tiến', NULL, 5000000, N'Dân sự', N'Active'),
(N'Xét nghiệm anh chị em', N'Xác định mối quan hệ huyết thống giữa các anh chị em ruột', NULL, 4000000, N'Dân sự', N'Active'),
(N'Xét nghiệm ông bà - cháu', N'Xác định mối quan hệ huyết thống giữa ông bà và cháu', NULL, 3000000, N'Dân sự', N'Active'),

(N'Xét nghiệm cha con', N'Xác định mối quan hệ huyết thống giữa cha và con với độ chính xác 99.99%. Áp dụng trong các thủ tục hành chính, pháp lý.', NULL, 5000000, N'Hành chính', N'Active'),
(N'Xét nghiệm mẹ con', N'Xác định mối quan hệ huyết thống giữa mẹ và con với công nghệ tiên tiến. Áp dụng trong các thủ tục hành chính, pháp lý.', NULL, 5000000, N'Hành chính', N'Active'),
(N'Xét nghiệm anh chị em', N'Xác định mối quan hệ huyết thống giữa các anh chị em ruột. Áp dụng trong các thủ tục hành chính, pháp lý.', NULL, 4000000, N'Hành chính', N'Active'),
(N'Xét nghiệm ông bà - cháu', N'Xác định mối quan hệ huyết thống giữa ông bà và cháu. Áp dụng trong các thủ tục hành chính, pháp lý.', NULL, 3000000, N'Hành chính', N'Active');

go

