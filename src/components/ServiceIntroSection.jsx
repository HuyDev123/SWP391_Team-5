import React from 'react';
import { Box, Typography, Grid, Card, CardContent, Button } from '@mui/material';
import FamilyRestroomIcon from '@mui/icons-material/FamilyRestroom';
import MedicalInformationIcon from '@mui/icons-material/MedicalInformation';
import BiotechIcon from '@mui/icons-material/Biotech';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import { useNavigate } from 'react-router-dom';

function ServiceIntroSection() {
  const navigate = useNavigate();
  
  const services = [
    {
      id: 1,
      title: 'Xét nghiệm quan hệ huyết thống',
      description: 'Xác định chính xác mối quan hệ cha-con, mẹ-con, anh chị em ruột với độ chính xác lên đến 99.999%',
      icon: <FamilyRestroomIcon sx={{ fontSize: 50, color: '#1565c0' }} />,
      color: '#bbdefb'
    },
    {
      id: 2,
      title: 'Xét nghiệm tầm soát bệnh di truyền',
      description: 'Phát hiện sớm các yếu tố rủi ro về bệnh di truyền như ung thư, tim mạch, tiểu đường,...',
      icon: <MedicalInformationIcon sx={{ fontSize: 50, color: '#2e7d32' }} />,
      color: '#c8e6c9'
    },
    {
      id: 3,
      title: 'Xét nghiệm trước sinh',
      description: 'Kiểm tra sàng lọc các bất thường nhiễm sắc thể và di truyền của thai nhi',
      icon: <BiotechIcon sx={{ fontSize: 50, color: '#6a1b9a' }} />,
      color: '#e1bee7'
    }
  ];

  return (
    <Box sx={{ py: 6, bgcolor: '#f8f9fa', borderRadius: 4, mb: 6 }}>
      <Typography 
        variant="h4" 
        gutterBottom 
        align="center" 
        sx={{ 
          fontWeight: 700,
          mb: 3,
          position: 'relative',
          '&:after': {
            content: '""',
            position: 'absolute',
            width: '80px',
            height: '4px',
            borderRadius: '2px',
            backgroundColor: '#ff9800',
            bottom: '-10px',
            left: 'calc(50% - 40px)'
          }
        }}
      >
        Dịch vụ xét nghiệm ADN của chúng tôi
      </Typography>
      
      <Typography 
        variant="subtitle1" 
        align="center" 
        sx={{ 
          maxWidth: '800px', 
          mx: 'auto', 
          mb: 5, 
          px: 3,
          color: '#555'
        }}
      >
        Chúng tôi cung cấp đa dạng dịch vụ xét nghiệm ADN với quy trình chuẩn quốc tế, 
        đảm bảo kết quả chính xác tuyệt đối, bảo mật thông tin và chi phí hợp lý.
      </Typography>
      
      <Grid container spacing={3} sx={{ px: { xs: 2, md: 4 } }}>
        {services.map(service => (
          <Grid item xs={12} sm={6} md={4} key={service.id}>
            <Card 
              sx={{ 
                height: '100%', 
                display: 'flex', 
                flexDirection: 'column',
                borderRadius: 3,
                transition: 'transform 0.3s, box-shadow 0.3s',
                '&:hover': {
                  transform: 'translateY(-8px)',
                  boxShadow: '0 12px 20px rgba(0,0,0,0.1)'
                },
                position: 'relative',
                overflow: 'visible',
              }}
            >
              <Box 
                sx={{ 
                  position: 'absolute', 
                  top: -20, 
                  left: 'calc(50% - 35px)',
                  width: 70,
                  height: 70,
                  borderRadius: '50%',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  bgcolor: 'white',
                  boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
                  zIndex: 2,
                  border: `4px solid ${service.color}`
                }}
              >
                {service.icon}
              </Box>
              
              <CardContent sx={{ pt: 5, pb: 2, px: 3, textAlign: 'center' }}>
                <Typography variant="h6" sx={{ fontWeight: 600, mb: 2, mt: 1 }}>
                  {service.title}
                </Typography>
                <Typography variant="body2" color="text.secondary" paragraph>
                  {service.description}
                </Typography>
              </CardContent>
              
              <Box sx={{ flexGrow: 1 }} />
              
              <Box sx={{ p: 2, pt: 0, textAlign: 'center' }}>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', mb: 2 }}>
                  <CheckCircleOutlineIcon color="success" sx={{ mr: 1, fontSize: 16 }} />
                  <Typography variant="caption" color="text.secondary">
                    Kết quả chính xác &gt; 99%
                  </Typography>
                </Box>
              </Box>
            </Card>
          </Grid>
        ))}
      </Grid>
      
      <Box sx={{ textAlign: 'center', mt: 5 }}>
        <Button 
          variant="outlined"
          color="primary"
          size="large"
          sx={{ 
            borderRadius: 8, 
            px: 4,
            '&:hover': {
              backgroundColor: '#e3f2fd'
            }
          }}
          onClick={() => navigate('/contact')}
        >
          Tư vấn miễn phí
        </Button>
      </Box>
    </Box>
  );
}

export default ServiceIntroSection;