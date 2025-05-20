import React from 'react';
import { Grid, Box, Typography } from '@mui/material';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import EmailIcon from '@mui/icons-material/Email';
import LocationOnIcon from '@mui/icons-material/LocationOn';

const infoList = [
  {
    icon: <AccessTimeIcon sx={{ fontSize: 48, color: '#f7931e' }} />,
    title: 'Hỗ trợ tư vấn 24/7',
    subtitle: 'Hoàn toàn MIỄN PHÍ',
  },
  {
    icon: <EmailIcon sx={{ fontSize: 48, color: '#f7931e' }} />,
    title: '0123 456 789789',
    subtitle: 'benhvienfpt@gmail.com',
  },
  {
    icon: <LocationOnIcon sx={{ fontSize: 48, color: '#f7931e' }} />,
    title: '7 đường D1, Long Thạnh Mỹ',
    subtitle: 'TP. Thủ Đức, TP. Hồ Chí Minh',
  },
];

function ContactInfoSection() {
  return (
    <Box sx={{ my: 4, px: { xs: 1, md: 4 } }}>
      <Grid container spacing={2} justifyContent="center">
        {infoList.map((item, idx) => (
          <Grid item xs={12} md={4} key={idx}>
            <Box
              sx={{
                display: 'flex',
                alignItems: 'center',
                gap: 2,
                borderRight: idx < infoList.length - 1 ? '1px solid #eee' : 'none',
                pr: idx < infoList.length - 1 ? 2 : 0,
                pl: idx > 0 ? 2 : 0,
                minHeight: 100,
              }}
            >
              {item.icon}
              <Box>
                <Typography variant="subtitle1" fontWeight={700}>
                  {item.title}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {item.subtitle}
                </Typography>
              </Box>
            </Box>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
}

export default ContactInfoSection;