import React from 'react';
import { Container, Typography } from '@mui/material';
import ContactButtons from '../components/ContactButtons';

function ContactPage() {
  return (
    <Container>
      <Typography variant="h4" sx={{ my: 4 }}>
        Liên hệ tư vấn
      </Typography>
      <ContactButtons />
      <Typography variant="body2" sx={{ mt: 2 }}>
        Chúng tôi luôn sẵn sàng hỗ trợ bạn qua Facebook, Zalo hoặc Hotline.
      </Typography>
    </Container>
  );
}

export default ContactPage;