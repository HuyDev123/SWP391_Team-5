import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import { AnimatePresence } from 'framer-motion';
import { Container } from '@mui/material';
import Header from './components/Header';
import HomePage from './pages/HomePage';
import ContactPage from './pages/ContactPage';
import RegisterPage from './pages/RegisterPage';
import LoginPage from './pages/LoginPage';
import KnowledgeDetailPage from './pages/KnowledgeDetailPage';
import BookingForm from './components/BookingForm';
import SuccessDialog from './components/SuccessDialog';
import PageTransition from './components/PageTransition';

// Wrapper component để sử dụng AnimatePresence với Routes
function AnimatedRoutes() {
  const location = useLocation();
  const [openBooking, setOpenBooking] = useState(false);
  const [openSuccess, setOpenSuccess] = useState(false);

  const handleOpenBooking = () => setOpenBooking(true);
  const handleCloseBooking = () => setOpenBooking(false);
  
  const handleSuccess = () => {
    setOpenBooking(false);
    setOpenSuccess(true);
  };

  const handleCloseSuccess = () => {
    setOpenSuccess(false);
  };

  return (
    <>
      <AnimatePresence mode="wait">
        <Routes location={location} key={location.pathname}>
          <Route 
            path="/" 
            element={
              <PageTransition>
                <HomePage onOpenBooking={handleOpenBooking} />
              </PageTransition>
            } 
          />
          <Route 
            path="/contact" 
            element={
              <PageTransition>
                <ContactPage />
              </PageTransition>
            } 
          />
          <Route 
            path="/register" 
            element={
              <PageTransition>
                <RegisterPage />
              </PageTransition>
            } 
          />
          <Route 
            path="/login" 
            element={
              <PageTransition>
                <LoginPage />
              </PageTransition>
            } 
          />
          <Route 
            path="/knowledge/:id" 
            element={
              <PageTransition>
                <KnowledgeDetailPage />
              </PageTransition>
            } 
          />
        </Routes>
      </AnimatePresence>

      <BookingForm
        open={openBooking}
        onClose={handleCloseBooking}
        onSuccess={handleSuccess}
      />
      <SuccessDialog
        open={openSuccess}
        onClose={handleCloseSuccess}
      />
    </>
  );
}

function App() {
  return (
    <Router>
      <Header />
      <Container>
        <AnimatedRoutes />
      </Container>
    </Router>
  );
}

export default App;