import React from 'react';
import { AppBar, Toolbar, Button, Box, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import EmailIcon from '@mui/icons-material/Email';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import logo from '../assets/logo.png'; // Đường dẫn logo, đổi lại nếu cần

function Header() {
  return (
    <AppBar position="static" sx={{ background: '#1976d2', py: 1 }}>
      <Toolbar sx={{ minHeight: 'unset', px: 2, display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap' }}>
        {/* Bên trái: Logo + menu */}
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <img src={logo} alt="Logo" style={{ height: 40, marginRight: 24, borderRadius: 8, background: '#fff' }} />
          <Button color="inherit" component={Link} to="/" sx={{ fontWeight: 600, mx: 1 }}>
            TRANG CHỦ
          </Button>
          <Button color="inherit" component={Link} to="/contact" sx={{ fontWeight: 600, mx: 1 }}>
            LIÊN HỆ
          </Button>
          <Button color="inherit" component={Link} to="/register" sx={{ fontWeight: 600, mx: 1 }}>
            ĐĂNG KÝ
          </Button>
        </Box>
        {/* Bên phải: Thông tin liên hệ */}
        <Box sx={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap' }}>
          <Box sx={{ display: 'flex', alignItems: 'center', mx: 2 }}>
            <AccessTimeIcon sx={{ color: '#f7931e', fontSize: 28, mr: 1 }} />
            <Box>
              <Typography variant="subtitle2" fontWeight={700} color="#fff">
                Hỗ trợ tư vấn 24/7
              </Typography>
              <Typography variant="caption" color="#fff">
                Hoàn toàn MIỄN PHÍ
              </Typography>
            </Box>
          </Box>
          <Box sx={{ display: 'flex', alignItems: 'center', mx: 2 }}>
            <EmailIcon sx={{ color: '#f7931e', fontSize: 28, mr: 1 }} />
            <Box>
              <Typography variant="subtitle2" fontWeight={700} color="#fff">
                0123 456 789789
              </Typography>
              <Typography variant="caption" color="#fff">
                benhvienfpt@gmail.com
              </Typography>
            </Box>
          </Box>
          <Box sx={{ display: 'flex', alignItems: 'center', mx: 2 }}>
            <LocationOnIcon sx={{ color: '#f7931e', fontSize: 28, mr: 1 }} />
            <Box>
              <Typography variant="subtitle2" fontWeight={700} color="#fff">
                7 đường D1, Long Thạnh Mỹ
              </Typography>
              <Typography variant="caption" color="#fff">
                TP. Thủ Đức, TP. Hồ Chí Minh
              </Typography>
            </Box>
          </Box>
        </Box>
      </Toolbar>
    </AppBar>
  );
}

export default Header;