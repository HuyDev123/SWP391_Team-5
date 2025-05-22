import React from 'react';
import { Container, Typography, Button, Box } from '@mui/material';
import KnowledgeSection from '../components/KnowledgeSection';
import SampleGuide from '../components/SampleGuide';
import banner from '../assets/banner.jpg';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import ContactInfoSection from '../components/ContactInfoSection';
import ServiceIntroSection from '../components/ServiceIntroSection';

function HomePage({ onOpenBooking }) {
  const userName = localStorage.getItem('userName');
  return (
    <>
      {/* Banner toàn màn hình */}
      <Box
        sx={{
          width: '100vw',
          position: 'relative',
          left: '50%',
          right: '50%',
          marginLeft: '-50vw',
          marginRight: '-50vw',
          mb: 4,
          height: 400, // Đảm bảo chiều cao cố định cho banner
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        <img
          src={banner}
          alt="Banner ADN"
          style={{
            width: '100%',
            height: '100%',
            display: 'block',
            borderRadius: 0,
            boxShadow: '0 4px 24px #0002',
            objectFit: 'cover',
            position: 'absolute',
            top: 0,
            left: 0,
            zIndex: 1,
          }}
        />
        <Box
          sx={{
            position: 'relative',
            zIndex: 2,
            color: '#fff',
            textAlign: 'center',
            width: '100%',
            px: 2,
          }}
        >
          <Typography variant="h3" gutterBottom sx={{ fontWeight: 'bold', textShadow: '0 2px 8px #0008' }}>
            Dịch vụ xét nghiệm ADN uy tín
          </Typography>
          <Typography variant="body1" gutterBottom sx={{ fontSize: 22, textShadow: '0 2px 8px #0008' }}>
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
              borderRadius: 8,
              boxShadow: '0 4px 16px #0004',
              pointerEvents: 'auto',
              transition: 'all 0.2s',
              mt: 2,
              '&:hover': {
                backgroundColor: '#ff9800',
                transform: 'scale(1.05)',
                boxShadow: '0 8px 24px #0006',
              },
            }}
            onClick={onOpenBooking}
          >
            Đặt lịch xét nghiệm
          </Button>
        </Box>
      </Box>

      <Container>
        {/* Phần giới thiệu dịch vụ */}
        <ServiceIntroSection />
        
        {/* Phần kiến thức ADN */}
        <KnowledgeSection />
        
        {/* Hướng dẫn lấy mẫu */}
        <SampleGuide /> 
      </Container>
    </>
  );
}

export default HomePage;