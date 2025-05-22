import React, { useState } from 'react';
import { Container, Box, Typography, TextField, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';

function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();
  

  const handleSubmit = (e) => {
  e.preventDefault();
  // Lấy danh sách tài khoản đã đăng ký
  const users = JSON.parse(localStorage.getItem('users') || '[]');
  // Tìm tài khoản khớp
  const user = users.find(u => u.email === email && u.password === password);
  if (user) {
    // Lưu họ tên và email vào localStorage
    localStorage.setItem('userName', user.name);
    localStorage.setItem('userEmail', user.email);
    navigate('/');
    window.location.reload();
  } else {
    alert('Email hoặc mật khẩu không đúng!');
  }
};

  return (
    <Container maxWidth="xs">
      <Box sx={{ mt: 8, p: 4, boxShadow: 3, borderRadius: 2, bgcolor: '#fff' }}>
        <Typography variant="h5" gutterBottom>Đăng nhập</Typography>
        <form onSubmit={handleSubmit}>
          <TextField
            label="Email"
            fullWidth
            margin="normal"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
          />
          <TextField
            label="Mật khẩu"
            type="password"
            fullWidth
            margin="normal"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
          />
          <Button type="submit" variant="contained" color="primary" fullWidth sx={{ mt: 2 }}>
            Đăng nhập
          </Button>
        </form>
      </Box>
    </Container>
  );
}

export default LoginPage;