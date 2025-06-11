Table User {
  id int [pk, increment]
  full_name varchar
  email varchar [unique, not null]
  role_id int [ref: > Role.id]
  is_active boolean
  created_at datetime
}

Table Role {
  id int [pk, increment]           // Ví dụ: "ADMIN", "MANAGER", "STAFF", "CUSTOMER"
  name varchar                     // Tên vai trò hiển thị
}

Table Service {
  id int [pk, increment]
  name varchar
  description text
  price decimal
  is_active boolean
}

Table Booking {
  id int [pk, increment]
  user_id int [pk, ref: > User.id]
  booking_date datetime
  sample_method varchar // "Tự thu mẫu" | "Cơ sở thu mẫu tại cơ sở"
  status varchar 
  staff_id int [ref: > User.id] // optional, staff quản lý
  note text
  created_at datetime
  updated_at datetime
}

Table Booking_Service {
  booking_id int [ref: > Booking.id]
  service_id int [ref: > Service.id]
  test_result_id int [ref: > TestResult.id, unique]
  primary key (booking_id, service_id)
}

Table KitType {
  id int [pk, increment]
  name varchar
  description text
}

Table Service_KitType {
  service_id int [ref: > Service.id]
  kit_type_id int [ref: > KitType.id]
  primary key (service_id, kit_type_id)
}

Table KitItem {
  id int [pk, increment]
  booking_id int [ref: > Booking_Service.booking_id]
  service_id int [ref: > Booking_Service.service_id]
  kit_code varchar
  kit_type_id int [ref: > KitType.id]
  delivery_status varchar
}

Table TestSample {
  id int [pk, increment]
  participant_id int [ref: > Participant.id]
  sample_code varchar
  sample_type varchar // Ví dụ: "blood", "urine", "saliva", "nasal_swab"
  collected_by int [ref: > User.id] // Staff hoặc Customer
  collected_at datetime
  note text
}

Table Participant {
  id int [pk, increment]
  booking_id int [ref: > Booking_Service.booking_id]
  service_id int [ref: > Booking_Service.service_id]
  full_name varchar        // Họ và tên
  gender varchar           // Nam/Nữ/Khác
  birthday date            // Ngày sinh
  phone varchar            // Số điện thoại liên hệ
  email varchar            // Email (nếu có)
  address text             // Địa chỉ liên hệ
  id_type varchar          // Loại giấy tờ: CCCD/CMND, hộ chiếu...
  id_number varchar        // Số giấy tờ tùy thân
  id_issued_date date      // Ngày cấp giấy tờ
  id_issued_place varchar  // Nơi cấp giấy tờ
  relationship varchar     // Quan hệ với người đặt (nếu không phải bản thân)
  photo_url varchar        // Link ảnh chân dung (nếu cần)
  note text                // Ghi chú thêm (nếu có)
}

Table TestResult {
  id int [pk, increment]
  result_summary varchar
  result_file varchar
  note text
}

Table Feedback {
  id int [pk, increment]
  booking_id int [ref: > Booking.id]
  user_id int [ref: > User.id]
  staff_id int [ref: > User.id]
  rating int
  comment text
  created_at datetime
}

Table Report {
  id int [pk, increment]
  report_type varchar
  generated_by int [ref: > User.id]
  generated_at datetime
  file_url varchar
}

Table Blog {
  id int [pk, increment]
  author_id int [ref: > User.id]      // Người viết bài
  title varchar [not null]            // Tiêu đề bài viết
  content text                        // Nội dung
  is_published boolean                // Trạng thái đã xuất bản hay chưa
  created_at datetime                 // Thời gian tạo bài viết
}
