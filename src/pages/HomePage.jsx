import React from 'react';
import { Container, Typography, Button, Box } from '@mui/material';
import KnowledgeSection from '../components/KnowledgeSection';
import SampleGuide from '../components/SampleGuide';
import banner from '../assets/banner.jpg';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import ContactInfoSection from '../components/ContactInfoSection';


function HomePage({ onOpenBooking }) {
  return (
    
    <Container>
      {/* <ContactInfoSection /> */}
      <Box sx={{ my: 4 }}>
        <img
    src={banner}
    alt="Banner ADN"
    style={{
      width: '100%',
      borderRadius: 16,
      marginBottom: 24,
      boxShadow: '0 4px 24px #0002'
    }}
  />
        <Typography variant="h3" gutterBottom>
          Dịch vụ xét nghiệm ADN uy tín
        </Typography>
        <Typography variant="body1" gutterBottom>
          Chúng tôi cung cấp các dịch vụ xét nghiệm ADN nhanh chóng, chính xác và bảo mật.
        </Typography>
        <Button
  variant="contained"
  color="warning"
  size="large"
  startIcon={<EventAvailableIcon />}
  sx={{
    fontSize: 22,
    px: 5,
    py: 1.5,
    borderRadius: 8, // Bo góc
    boxShadow: '0 4px 16px #0004', // Bóng đổ
    pointerEvents: 'auto',
    transition: 'all 0.2s',
    '&:hover': {
      backgroundColor: '#ff9800', // Màu khi hover
      transform: 'scale(1.05)',   // Phóng to nhẹ khi hover
      boxShadow: '0 8px 24px #0006', // Bóng đổ mạnh hơn khi hover
    },
  }}
  onClick={onOpenBooking}
>
  Đặt lịch xét nghiệm
</Button>
      </Box>
      {/* Các phần khác như kiến thức ADN, hướng dẫn lấy mẫu sẽ thêm sau */}
      <KnowledgeSection />
      <SampleGuide /> 
      
    </Container>
    
  );
}

export default HomePage;