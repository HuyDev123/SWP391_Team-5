import React, { useState } from 'react';
import { Container, Typography, TextField, Button, Box } from '@mui/material';

function RegisterPage() {
  const [form, setForm] = useState({
    email: '',
    password: '',
    name: '',
    address: '',
    phone: '',
  });

  const [success, setSuccess] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // Ở đây bạn có thể gọi API đăng ký, hiện tại chỉ demo
    setSuccess(true);
    setForm({
      email: '',
      password: '',
      name: '',
      address: '',
      phone: '',
    });
  };

  return (
    <Container maxWidth="sm">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" gutterBottom>
          Đăng ký tài khoản
        </Typography>
        <form onSubmit={handleSubmit}>
          <TextField
            label="Gmail"
            name="email"
            type="email"
            fullWidth
            required
            margin="normal"
            value={form.email}
            onChange={handleChange}
          />
          <TextField
            label="Mật khẩu"
            name="password"
            type="password"
            fullWidth
            required
            margin="normal"
            value={form.password}
            onChange={handleChange}
          />
          <TextField
            label="Họ tên"
            name="name"
            fullWidth
            required
            margin="normal"
            value={form.name}
            onChange={handleChange}
          />
          <TextField
            label="Địa chỉ"
            name="address"
            fullWidth
            required
            margin="normal"
            value={form.address}
            onChange={handleChange}
          />
          <TextField
            label="Số điện thoại"
            name="phone"
            fullWidth
            required
            margin="normal"
            value={form.phone}
            onChange={handleChange}
          />
          <Button type="submit" variant="contained" color="primary" fullWidth sx={{ mt: 2 }}>
            Đăng ký
          </Button>
        </form>
        {success && (
          <Typography color="success.main" sx={{ mt: 2 }}>
            Đăng ký thành công!
          </Typography>
        )}
      </Box>
    </Container>
  );
}

export default RegisterPage;