import React from 'react';
import { Box, Typography, Card, CardContent, Stack } from '@mui/material';
import InfoIcon from '@mui/icons-material/Info';
import { useNavigate } from 'react-router-dom';

function KnowledgeSection() {
  const navigate = useNavigate();
  
  const knowledgeList = [
    {
      id: 1,
      title: 'ADN là gì?',
      content: 'ADN (DNA) là vật chất di truyền mang thông tin di truyền của sinh vật.',
    },
    {
      id: 2,
      title: 'Ý nghĩa xét nghiệm ADN',
      content: 'Xét nghiệm ADN giúp xác định quan hệ huyết thống, hỗ trợ pháp lý, y học,...',
    },
    {
      id: 3,
      title: 'Độ chính xác của xét nghiệm ADN',
      content: 'Kết quả xét nghiệm ADN có độ chính xác lên tới 99,999%.',
    },
  ];

  return (
    <Box sx={{ my: 4 }}>
      <Typography variant="h5" gutterBottom align="center" sx={{ fontWeight: 700 }}>
        Chia sẻ kiến thức ADN
      </Typography>
      <Stack spacing={3} alignItems="center">
        {knowledgeList.map((item) => (
          <Card
            key={item.id}
            sx={{
              width: '100%',
              maxWidth: 600,
              boxShadow: 4,
              borderRadius: 3,
              cursor: 'pointer',
              transition: 'transform 0.2s, box-shadow 0.2s',
              '&:hover': {
                transform: 'scale(1.03)',
                boxShadow: 8,
                bgcolor: '#f5f5f5',
              },
            }}
            onClick={() => navigate(`/knowledge/${item.id}`)}
          >
            <CardContent sx={{ display: 'flex', alignItems: 'center' }}>
              <InfoIcon color="primary" sx={{ fontSize: 40, mr: 2 }} />
              <Box>
                <Typography variant="h6" sx={{ fontWeight: 600 }}>
                  {item.title}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {item.content}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        ))}
      </Stack>
    </Box>
  );
}

export default KnowledgeSection;