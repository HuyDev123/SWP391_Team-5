import React from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, Typography } from '@mui/material';

function SuccessDialog({ open, onClose }) {
  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Đặt lịch thành công!</DialogTitle>
      <DialogContent>
        <Typography>
          Cảm ơn bạn đã đặt lịch xét nghiệm. Chúng tôi sẽ liên hệ với bạn trong thời gian sớm nhất.
        </Typography>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} variant="contained" color="primary">
          Đóng
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default SuccessDialog;