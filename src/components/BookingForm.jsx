import React, { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, MenuItem } from '@mui/material';

const services = [
  { value: 'cha-con', label: 'Xét nghiệm cha - con' },
  { value: 'anh-em', label: 'Xét nghiệm anh - em' },
  { value: 'huyet-thong', label: 'Xét nghiệm huyết thống khác' },
];

function BookingForm({ open, onClose, onSuccess }) {
  const [form, setForm] = useState({
    name: '',
    phone: '',
    service: '',
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // Ở đây bạn có thể gọi API, hiện tại chỉ demo
    onSuccess();
    setForm({ name: '', phone: '', service: '' });
  };

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Đặt lịch xét nghiệm</DialogTitle>
      <form onSubmit={handleSubmit}>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Họ tên"
            name="name"
            fullWidth
            required
            value={form.name}
            onChange={handleChange}
          />
          <TextField
            margin="dense"
            label="Số điện thoại"
            name="phone"
            fullWidth
            required
            value={form.phone}
            onChange={handleChange}
          />
          <TextField
            margin="dense"
            label="Loại dịch vụ"
            name="service"
            select
            fullWidth
            required
            value={form.service}
            onChange={handleChange}
          >
            {services.map((option) => (
              <MenuItem key={option.value} value={option.value}>
                {option.label}
              </MenuItem>
            ))}
          </TextField>
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose}>Hủy</Button>
          <Button type="submit" variant="contained">Đặt lịch</Button>
        </DialogActions>
      </form>
    </Dialog>
  );
}

export default BookingForm;