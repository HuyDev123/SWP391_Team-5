import React from 'react';
import { Box, Typography, Card, CardContent, Grid } from '@mui/material';
import InfoIcon from '@mui/icons-material/Info';

const knowledgeList = [
  {
    title: 'ADN là gì?',
    content: 'ADN (DNA) là vật chất di truyền mang thông tin di truyền của sinh vật.',
  },
  {
    title: 'Ý nghĩa xét nghiệm ADN',
    content: 'Xét nghiệm ADN giúp xác định quan hệ huyết thống, hỗ trợ pháp lý, y học,...',
  },
  {
    title: 'Độ chính xác của xét nghiệm ADN',
    content: 'Kết quả xét nghiệm ADN có độ chính xác lên tới 99,999%.',
  },
];

function KnowledgeSection() {
  return (
    <Box sx={{ my: 4 }}>
      <Typography variant="h5" gutterBottom>
        Chia sẻ kiến thức ADN
      </Typography>
      <Grid container spacing={2}>
        {knowledgeList.map((item, idx) => (
          <Grid item xs={12} md={4} key={idx}>
            <Card sx={{ boxShadow: 3, borderRadius: 3, minHeight: 180 }}>
              <CardContent sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
  <InfoIcon color="primary" sx={{ fontSize: 40, mb: 1 }} />
  <Typography variant="h6" align="center">{item.title}</Typography>
  <Typography variant="body2" align="center">{item.content}</Typography>
</CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
}

export default KnowledgeSection;
