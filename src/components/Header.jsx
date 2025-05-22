import React, { useState } from 'react';
import { AppBar, Toolbar, Button, Box, Typography, useMediaQuery, Drawer, List, ListItem, IconButton, Container } from '@mui/material';
import { Link, useLocation } from 'react-router-dom';
import HomeIcon from '@mui/icons-material/Home';
import ContactPhoneIcon from '@mui/icons-material/ContactPhone';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import LoginIcon from '@mui/icons-material/Login';
import LogoutIcon from '@mui/icons-material/Logout';
import MenuIcon from '@mui/icons-material/Menu';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import EmailIcon from '@mui/icons-material/Email';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import logo from '../assets/logo.png';

function Header() {
  const userName = localStorage.getItem('userName');
  const location = useLocation();
  const isMobile = useMediaQuery('(max-width:960px)');
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const isActive = (path) => {
    return location.pathname === path;
  };

  const handleLogout = () => {
    localStorage.removeItem('userName');
    localStorage.removeItem('userEmail');
    window.location.reload();
  };

  const navItems = [
    { title: 'TRANG CHỦ', path: '/', icon: <HomeIcon />, show: true },
    { title: 'LIÊN HỆ', path: '/contact', icon: <ContactPhoneIcon />, show: true },
    { title: 'ĐĂNG KÝ', path: '/register', icon: <PersonAddIcon />, show: !userName },
    { title: 'ĐĂNG NHẬP', path: '/login', icon: <LoginIcon />, show: !userName },
  ];

  const drawer = (
    <Box onClick={handleDrawerToggle} sx={{ textAlign: 'center' }}>
      <Box sx={{ my: 2 }}>
        <img src={logo} alt="Logo" style={{ height: 40, borderRadius: 8, background: '#fff' }} />
      </Box>
      <List>
        {navItems.filter(item => item.show).map((item) => (
          <ListItem key={item.title} disablePadding>
            <Button
              component={Link}
              to={item.path}
              sx={{
                width: '100%',
                color: isActive(item.path) ? 'primary.main' : 'inherit',
                fontWeight: isActive(item.path) ? 700 : 600,
                py: 1.5,
                justifyContent: 'flex-start',
                px: 4,
                '& .MuiSvgIcon-root': { mr: 1 }
              }}
              startIcon={item.icon}
            >
              {item.title}
            </Button>
          </ListItem>
        ))}
        {userName && (
          <ListItem disablePadding>
            <Button
              onClick={handleLogout}
              sx={{
                width: '100%',
                color: 'inherit',
                fontWeight: 600,
                py: 1.5,
                justifyContent: 'flex-start',
                px: 4,
                '& .MuiSvgIcon-root': { mr: 1 }
              }}
              startIcon={<LogoutIcon />}
            >
              ĐĂNG XUẤT
            </Button>
          </ListItem>
        )}
      </List>
    </Box>
  );

  return (
    <AppBar position="static" sx={{ background: '#1976d2', py: 1 }}>
      <Container maxWidth="xl">
        <Toolbar sx={{ minHeight: 'unset', px: { xs: 1, sm: 2 }, display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap' }}>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <img 
              src={logo} 
              alt="Logo" 
              style={{ height: 40, marginRight: 24, borderRadius: 8, background: '#fff' }} 
            />
            
            {isMobile ? (
              <IconButton
                color="inherit"
                aria-label="open drawer"
                edge="start"
                onClick={handleDrawerToggle}
              >
                <MenuIcon />
              </IconButton>
            ) : (
              <>
                {navItems.filter(item => item.show).map((item) => (
                  <Button 
                    key={item.title}
                    component={Link} 
                    to={item.path} 
                    color="inherit"
                    startIcon={item.icon}
                    sx={{ 
                      fontWeight: 600, 
                      mx: 1,
                      borderRadius: 2,
                      px: 1.5,
                      py: 0.7,
                      position: 'relative',
                      '&::after': isActive(item.path) ? {
                        content: '""',
                        position: 'absolute',
                        bottom: 0,
                        left: '15%',
                        width: '70%',
                        height: '3px',
                        backgroundColor: '#fff',
                        borderRadius: '3px'
                      } : {},
                      transition: 'transform 0.3s, background-color 0.3s',
                      '&:hover': {
                        backgroundColor: 'rgba(255, 255, 255, 0.1)',
                        transform: 'translateY(-2px)'
                      }
                    }}
                  >
                    {item.title}
                  </Button>
                ))}
                
                {userName && (
                  <>
                    <Typography variant="subtitle1" color="#fff" sx={{ fontWeight: 600, mx: 2 }}>
                      Xin chào, {userName}!
                    </Typography>
                    <Button
                      color="inherit"
                      startIcon={<LogoutIcon />}
                      onClick={handleLogout}
                      sx={{
                        ml: 1,
                        fontWeight: 600,
                        borderRadius: 2,
                        px: 1.5,
                        py: 0.7,
                        transition: 'all 0.3s',
                        '&:hover': {
                          backgroundColor: 'rgba(255, 255, 255, 0.2)',
                          transform: 'translateY(-2px)'
                        }
                      }}
                    >
                      ĐĂNG XUẤT
                    </Button>
                  </>
                )}
              </>
            )}
          </Box>
          
          {!isMobile && (
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
                    0123 456 789
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
      Địa chỉ phòng khám
    </Typography>
    <Typography variant="caption" color="#fff">
      7 Đ.D1, TP. Thủ Đức, TP. Hồ Chí Minh
    </Typography>
  </Box>
</Box>
            </Box>
          )}
        </Toolbar>
      </Container>
      
      <Drawer
        variant="temporary"
        open={mobileOpen}
        onClose={handleDrawerToggle}
        ModalProps={{ keepMounted: true }}
        sx={{
          display: { xs: 'block', md: 'none' },
          '& .MuiDrawer-paper': { boxSizing: 'border-box', width: 280 },
        }}
      >
        {drawer}
      </Drawer>
    </AppBar>
  );
}

export default Header;