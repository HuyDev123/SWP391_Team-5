import React from 'react';
import { Box, Button, Stack } from '@mui/material';
import FacebookIcon from '@mui/icons-material/Facebook';
import PhoneIcon from '@mui/icons-material/Phone';
import ChatIcon from '@mui/icons-material/Chat';

function ContactButtons() {
  return (
    <Box sx={{ my: 4 }}>
      <Stack direction="row" spacing={2}>
        <Button
          variant="contained"
          color="primary"
          startIcon={<FacebookIcon />}
          href="https://facebook.com/"
          target="_blank"
        >
          Facebook
        </Button>
        <Button
          variant="contained"
          color="success"
          startIcon={<ChatIcon />}
          href="https://zalo.me/"
          target="_blank"
        >
          Zalo
        </Button>
        <Button
          variant="contained"
          color="secondary"
          startIcon={<PhoneIcon />}
          href="tel:0123456789"
        >
          Hotline
        </Button>
      </Stack>
    </Box>
  );
}

export default ContactButtons;