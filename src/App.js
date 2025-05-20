import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { AppBar, Toolbar, Button, Container } from '@mui/material';
import HomePage from './pages/HomePage';
import ContactPage from './pages/ContactPage';
import BookingForm from './components/BookingForm';
import SuccessDialog from './components/SuccessDialog';
import RegisterPage from './pages/RegisterPage';
import logo from './assets/logo.png';
import Header from './components/Header'; // hoặc './Header' nếu bạn để ở src

function App() {
  const [openBooking, setOpenBooking] = useState(false);
  const [openSuccess, setOpenSuccess] = useState(false);

  const handleOpenBooking = () => setOpenBooking(true);
  const handleCloseBooking = () => setOpenBooking(false);

  const handleSuccess = () => {
    setOpenBooking(false);
    setOpenSuccess(true);
  };
  const handleCloseSuccess = () => setOpenSuccess(false);

  return (
    <Router>
      <Header position="static">
  <Toolbar>
    <img src={logo} alt="Logo" style={{ height: 40, marginRight: 16, borderRadius: 8 }} />
    <Button color="inherit" component={Link} to="/">
      Trang chủ
    </Button>
    <Button color="inherit" component={Link} to="/contact">
      Liên hệ
    </Button>
    <Button color="inherit" component={Link} to="/register">
      Đăng ký
    </Button>
  </Toolbar>
</Header>
      <Container>
        <Routes>
          <Route
            path="/"
            element={
              <HomePage onOpenBooking={handleOpenBooking} />
            }
          />
          <Route path="/contact" element={<ContactPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Routes>
        <BookingForm
          open={openBooking}
          onClose={handleCloseBooking}
          onSuccess={handleSuccess}
        />
        <SuccessDialog
          open={openSuccess}
          onClose={handleCloseSuccess}
        />
      </Container>
    </Router>
  );
}

export default App;