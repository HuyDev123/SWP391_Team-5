// Mock data for DNA testing system
// This file replaces server-side data storage with client-side storage

// Local storage keys
const STORAGE_KEYS = {
  APPOINTMENTS: 'dna_appointments',
  KITS: 'dna_kits',
  RESULTS: 'dna_results',
  USERS: 'dna_users',
  CURRENT_USER: 'dna_current_user'
};

// Default data to initialize if storage is empty
const DEFAULT_DATA = {
  appointments: [
    {
      id: 'APT-20240525-001',
      customerName: 'Nguyễn Văn A',
      service: 'Xét nghiệm ADN huyết thống',
      date: '25/05/2024',
      collectionMethod: 'Tự thu thập mẫu',
      status: 'da-dat',
      createdAt: '2024-05-20'
    },
    {
      id: 'APT-20240601-001',
      customerName: 'Trần Thị B',
      service: 'Xét nghiệm ADN quan hệ cha con',
      date: '01/06/2024',
      collectionMethod: 'Thu thập tại cơ sở y tế',
      status: 'dang-cho-lay-mau',
      createdAt: '2024-05-28'
    },
    {
      id: 'APT-20240610-001',
      customerName: 'Lê Văn C',
      service: 'Xét nghiệm ADN quan hệ anh em',
      date: '10/06/2024',
      collectionMethod: 'Thu thập tại nhà',
      status: 'da-nhan-mau',
      createdAt: '2024-06-05'
    },
    {
      id: 'APT-20240615-001',
      customerName: 'Phạm Thị D',
      service: 'Xét nghiệm ADN huyết thống',
      date: '15/06/2024',
      collectionMethod: 'Tự thu thập mẫu',
      status: 'da-hoan',
      createdAt: '2024-06-10'
    }
  ],
  
  kits: [
    {
      kit_code: 'KIT-20240525-001',
      appointment_id: 'APT-20240525-001',
      staff: 'Nguyễn Văn X',
      is_self_collected: true,
      send_date: '26/05/2024',
      receive_date: '28/05/2024',
      sample_count: 2,
      status: 'da-xu-ly',
      notes: 'Mẫu đạt chất lượng tốt'
    },
    {
      kit_code: 'KIT-20240601-001',
      appointment_id: 'APT-20240601-001',
      staff: 'Trần Văn Y',
      is_self_collected: false,
      send_date: '01/06/2024',
      receive_date: '03/06/2024',
      sample_count: 3,
      status: 'da-tra-ket-qua',
      notes: 'Đã hoàn thành xét nghiệm'
    },
    {
      kit_code: 'KIT-20240610-001',
      appointment_id: 'APT-20240610-001',
      staff: 'Lê Thị Z',
      is_self_collected: false,
      send_date: '10/06/2024',
      receive_date: '',
      sample_count: 2,
      status: 'da-gui',
      notes: 'Đang chờ nhận mẫu'
    }
  ],
  
  results: [
    {
      kit_code: 'KIT-20240525-001',
      pdf_url: 'data/results/KIT-20240525-001.pdf',
      created_at: '2024-05-30'
    },
    {
      kit_code: 'KIT-20240601-001',
      pdf_url: 'data/results/KIT-20240601-001.pdf',
      created_at: '2024-06-05'
    }
  ],
  
  users: [
    {
      id: 1,
      username: 'admin',
      password: 'admin123',
      fullname: 'Administrator',
      email: 'admin@dnatest.com',
      phone: '0987654321',
      role: 'admin'
    },
    {
      id: 2,
      username: 'staff1',
      password: 'staff123',
      fullname: 'Nhân viên 1',
      email: 'staff1@dnatest.com',
      phone: '0987654322',
      role: 'staff'
    },
    {
      id: 3,
      username: 'customer1',
      password: 'customer123',
      fullname: 'Khách hàng 1',
      email: 'customer1@gmail.com',
      phone: '0987654323',
      role: 'customer'
    }
  ]
};

// Initialize data in local storage if not exists
function initializeData() {
  if (!localStorage.getItem(STORAGE_KEYS.APPOINTMENTS)) {
    localStorage.setItem(STORAGE_KEYS.APPOINTMENTS, JSON.stringify(DEFAULT_DATA.appointments));
  }
  
  if (!localStorage.getItem(STORAGE_KEYS.KITS)) {
    localStorage.setItem(STORAGE_KEYS.KITS, JSON.stringify(DEFAULT_DATA.kits));
  }
  
  if (!localStorage.getItem(STORAGE_KEYS.RESULTS)) {
    localStorage.setItem(STORAGE_KEYS.RESULTS, JSON.stringify(DEFAULT_DATA.results));
  }
  
  if (!localStorage.getItem(STORAGE_KEYS.USERS)) {
    localStorage.setItem(STORAGE_KEYS.USERS, JSON.stringify(DEFAULT_DATA.users));
  }
}

// Get all appointments
function getAppointments() {
  return JSON.parse(localStorage.getItem(STORAGE_KEYS.APPOINTMENTS) || '[]');
}

// Get appointment by ID
function getAppointmentById(id) {
  const appointments = getAppointments();
  return appointments.find(appointment => appointment.id === id) || null;
}

// Save appointment
function saveAppointment(appointment) {
  const appointments = getAppointments();
  const index = appointments.findIndex(a => a.id === appointment.id);
  
  if (index !== -1) {
    appointments[index] = appointment;
  } else {
    appointments.push(appointment);
  }
  
  localStorage.setItem(STORAGE_KEYS.APPOINTMENTS, JSON.stringify(appointments));
}

// Update appointment status
function updateAppointmentStatus(id, status) {
  const appointment = getAppointmentById(id);
  if (appointment) {
    appointment.status = status;
    saveAppointment(appointment);
    return true;
  }
  return false;
}

// Get all kits
function getKits() {
  return JSON.parse(localStorage.getItem(STORAGE_KEYS.KITS) || '[]');
}

// Get kit by code
function getKitByCode(code) {
  const kits = getKits();
  return kits.find(kit => kit.kit_code === code) || null;
}

// Get kits by appointment ID
function getKitsByAppointmentId(appointmentId) {
  const kits = getKits();
  return kits.filter(kit => kit.appointment_id === appointmentId);
}

// Save kit
function saveKit(kit) {
  const kits = getKits();
  const index = kits.findIndex(k => k.kit_code === kit.kit_code);
  
  if (index !== -1) {
    kits[index] = kit;
  } else {
    kits.push(kit);
  }
  
  localStorage.setItem(STORAGE_KEYS.KITS, JSON.stringify(kits));
}

// Update kit status
function updateKitStatus(code, status) {
  const kit = getKitByCode(code);
  if (kit) {
    kit.status = status;
    saveKit(kit);
    return true;
  }
  return false;
}

// Get all results
function getResults() {
  return JSON.parse(localStorage.getItem(STORAGE_KEYS.RESULTS) || '[]');
}

// Get result by kit code
function getResultByKitCode(kitCode) {
  const results = getResults();
  return results.find(result => result.kit_code === kitCode) || null;
}

// Save result
function saveResult(result) {
  const results = getResults();
  const index = results.findIndex(r => r.kit_code === result.kit_code);
  
  if (index !== -1) {
    results[index] = result;
  } else {
    results.push(result);
  }
  
  localStorage.setItem(STORAGE_KEYS.RESULTS, JSON.stringify(results));
}

// Get current user
function getCurrentUser() {
  return JSON.parse(localStorage.getItem(STORAGE_KEYS.CURRENT_USER) || 'null');
}

// Set current user
function setCurrentUser(user) {
  localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(user));
}

// Login
function login(username, password) {
  const users = JSON.parse(localStorage.getItem(STORAGE_KEYS.USERS) || '[]');
  const user = users.find(u => u.username === username && u.password === password);
  
  if (user) {
    setCurrentUser(user);
    return user;
  }
  
  return null;
}

// Logout
function logout() {
  localStorage.removeItem(STORAGE_KEYS.CURRENT_USER);
}

// Initialize data when the script loads
initializeData(); 