import React from 'react';
import { Box, Typography, List, ListItem, ListItemText } from '@mui/material';
import ScienceIcon from '@mui/icons-material/Science';
import FaceIcon from '@mui/icons-material/Face';
import DryCleaningIcon from '@mui/icons-material/DryCleaning';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import { ListItemIcon } from '@mui/material';

function SampleGuide() {
  return (
    <Box sx={{ my: 4 }}>
      <Typography variant="h5" gutterBottom>
        Hướng dẫn lấy mẫu xét nghiệm ADN
      </Typography>
      <List>
  <ListItem>
    <ListItemIcon>
      <ScienceIcon color="primary" />
    </ListItemIcon>
    <ListItemText primary="1. Chuẩn bị tăm bông sạch hoặc bộ lấy mẫu do trung tâm cung cấp." />
  </ListItem>
  <ListItem>
    <ListItemIcon>
      <FaceIcon color="primary" />
    </ListItemIcon>
    <ListItemText primary="2. Quệt nhẹ tăm bông vào mặt trong má khoảng 30 giây." />
  </ListItem>
  <ListItem>
    <ListItemIcon>
      <DryCleaningIcon color="primary" />
    </ListItemIcon>
    <ListItemText primary="3. Để tăm bông khô tự nhiên, cho vào túi đựng sạch." />
  </ListItem>
  <ListItem>
    <ListItemIcon>
      <LocalShippingIcon color="primary" />
    </ListItemIcon>
    <ListItemText primary="4. Ghi rõ thông tin mẫu và gửi về trung tâm." />
  </ListItem>
</List>
    </Box>
  );
}

export default SampleGuide;