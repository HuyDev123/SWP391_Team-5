import React from 'react';
import { Box, Typography, Paper, Grid, Card, CardContent, CardMedia, Stepper, Step, StepLabel, StepContent, Button } from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ScienceIcon from '@mui/icons-material/Science';
import CleanHandsIcon from '@mui/icons-material/CleanHands';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import HealthAndSafetyIcon from '@mui/icons-material/HealthAndSafety';

// URL hình ảnh mẫu - bạn có thể thay thế bằng ảnh thực
const sampleImages = [
  'https://img.freepik.com/free-photo/medical-banner-with-doctor-holding-dna-strands_23-2149611232.jpg',
  'https://img.freepik.com/free-vector/realistic-dna-concept_23-2148491119.jpg',
  'https://img.freepik.com/free-photo/side-view-scientist-with-test-tube_23-2148648851.jpg'
];

const steps = [
  {
    label: 'Chuẩn bị tấm bông sạch hoặc bộ lấy mẫu',
    description: 'Sử dụng tấm bông sạch, que bông y tế hoặc bộ lấy mẫu chuyên dụng được cung cấp bởi trung tâm xét nghiệm.',
    icon: <CleanHandsIcon fontSize="large" />,
    color: '#3f51b5'
  },
  {
    label: 'Chà mạnh vào mặt trong má',
    description: 'Chà mạnh tấm bông vào mặt trong má trong khoảng 30-60 giây để lấy được đủ tế bào biểu mô.',
    icon: <HealthAndSafetyIcon fontSize="large" />,
    color: '#00897b'
  },
  {
    label: 'Để khô tự nhiên trong không khí',
    description: 'Để mẫu khô tự nhiên trong không khí sạch, tránh ánh nắng mặt trời trực tiếp và nhiệt độ cao.',
    icon: <ScienceIcon fontSize="large" />,
    color: '#e65100'
  },
  {
    label: 'Đóng gói và gửi mẫu đến phòng xét nghiệm',
    description: 'Đặt mẫu vào túi giấy hoặc phong bì sạch (không dùng túi nhựa) và gửi đến phòng xét nghiệm trong vòng 24-48 giờ.',
    icon: <LocalShippingIcon fontSize="large" />,
    color: '#2e7d32'
  }
];

function SampleGuide() {
  const [activeStep, setActiveStep] = React.useState(0);

  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const handleReset = () => {
    setActiveStep(0);
  };

  return (
    <Box sx={{ my: 6, pb: 4 }}>
      <Box sx={{ 
        bgcolor: '#f5f5f5', 
        py: 4, 
        px: { xs: 2, md: 6 }, 
        borderRadius: 4,
        boxShadow: '0 3px 10px rgba(0,0,0,0.08)',
        border: '1px solid #e0e0e0'
      }}>
        <Box textAlign="center" mb={5}>
          <Typography 
            variant="h4" 
            gutterBottom 
            sx={{ 
              fontWeight: 700,
              color: '#1565c0',
              position: 'relative',
              display: 'inline-block',
              '&:after': {
                content: '""',
                position: 'absolute',
                width: '60%',
                height: '4px',
                borderRadius: '2px',
                backgroundColor: '#ff9800',
                bottom: -8,
                left: '20%'
              }
            }}
          >
            <ScienceIcon sx={{ fontSize: 35, verticalAlign: 'middle', mr: 1 }} />
            Hướng dẫn lấy mẫu xét nghiệm ADN
          </Typography>
          <Typography variant="subtitle1" color="text.secondary" sx={{ mt: 2, maxWidth: '800px', mx: 'auto' }}>
            Việc lấy mẫu đúng cách giúp đảm bảo kết quả xét nghiệm ADN chính xác nhất. 
            Hãy làm theo các bước hướng dẫn sau đây.
          </Typography>
        </Box>
        
        <Grid container spacing={4}>
          <Grid item xs={12} md={6}>
            <Paper 
              elevation={3} 
              sx={{ 
                borderRadius: 3, 
                overflow: 'hidden',
                height: '100%'
              }}
            >
              <Stepper activeStep={activeStep} orientation="vertical" sx={{ p: 3 }}>
                {steps.map((step, index) => (
                  <Step key={step.label}>
                    <StepLabel 
                      StepIconProps={{ 
                        sx: { 
                          color: step.color,
                          '&.Mui-active': { color: step.color },
                          '&.Mui-completed': { color: step.color }
                        }
                      }}
                    >
                      <Typography variant="h6" sx={{ fontWeight: 600 }}>
                        {step.label}
                      </Typography>
                    </StepLabel>
                    <StepContent>
                      <Box sx={{ display: 'flex', alignItems: 'flex-start', mb: 2 }}>
                        <Box sx={{ 
                          mr: 2, 
                          bgcolor: step.color, 
                          color: 'white', 
                          p: 1, 
                          borderRadius: 2,
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center'
                        }}>
                          {step.icon}
                        </Box>
                        <Typography>{step.description}</Typography>
                      </Box>
                      <Box sx={{ mb: 2 }}>
                        <div>
                          <Button
                            variant="contained"
                            onClick={handleNext}
                            sx={{ mt: 1, mr: 1, bgcolor: step.color }}
                          >
                            {index === steps.length - 1 ? 'Hoàn thành' : 'Tiếp theo'}
                          </Button>
                          <Button
                            disabled={index === 0}
                            onClick={handleBack}
                            sx={{ mt: 1, mr: 1 }}
                          >
                            Quay lại
                          </Button>
                        </div>
                      </Box>
                    </StepContent>
                  </Step>
                ))}
              </Stepper>
              {activeStep === steps.length && (
                <Box sx={{ p: 3 }}>
                  <Typography sx={{ mt: 2, mb: 1 }}>
                    Bạn đã hoàn thành tất cả các bước hướng dẫn lấy mẫu xét nghiệm ADN.
                  </Typography>
                  <Button onClick={handleReset} sx={{ mt: 1, mr: 1 }}>
                    Xem lại từ đầu
                  </Button>
                </Box>
              )}
            </Paper>
          </Grid>
          
          <Grid item xs={12} md={6}>
            <Box>
              <Card 
                elevation={5}
                sx={{ 
                  mb: 3, 
                  borderRadius: 3,
                  border: '1px solid #e0e0e0',
                  transition: '0.3s',
                  '&:hover': {
                    transform: 'translateY(-5px)',
                    boxShadow: '0 12px 20px rgba(0,0,0,0.1)'
                  }
                }}
              >
                <CardMedia
                  component="img"
                  height="220"
                  image={sampleImages[0]}
                  alt="Phương pháp lấy mẫu"
                />
                <CardContent>
                  <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>
                    Tại sao cần tuân thủ quy trình?
                  </Typography>
                  <Typography variant="body2">
                    Tuân thủ đúng quy trình lấy mẫu sẽ giúp đảm bảo kết quả xét nghiệm ADN chính xác 
                    và tránh phải lấy mẫu lại. Việc này giúp tiết kiệm thời gian và chi phí cho quý khách.
                  </Typography>
                </CardContent>
              </Card>
              
              <Grid container spacing={2}>
                {[1, 2].map((index) => (
                  <Grid item xs={6} key={index}>
                    <Card 
                      elevation={4}
                      sx={{ 
                        borderRadius: 3,
                        height: '100%',
                        transition: '0.3s',
                        '&:hover': {
                          transform: 'scale(1.03)',
                          boxShadow: '0 8px 16px rgba(0,0,0,0.1)'
                        }
                      }}
                    >
                      <CardMedia
                        component="img"
                        height="140"
                        image={sampleImages[index]}
                        alt={`Hình ảnh hướng dẫn ${index}`}
                      />
                      <CardContent>
                        <Typography variant="subtitle1" gutterBottom sx={{ fontWeight: 600 }}>
                          {index === 1 ? 'Lưu ý quan trọng' : 'Các vật dụng cần thiết'}
                        </Typography>
                        <Box sx={{ display: 'flex', alignItems: 'flex-start', mb: 1 }}>
                          <CheckCircleIcon color="success" sx={{ mr: 1, mt: 0.3, fontSize: 16 }} />
                          <Typography variant="body2">
                            {index === 1 ? 'Không ăn uống trước khi lấy mẫu 30 phút' : 'Tấm bông sạch hoặc que lấy mẫu'}
                          </Typography>
                        </Box>
                        <Box sx={{ display: 'flex', alignItems: 'flex-start' }}>
                          <CheckCircleIcon color="success" sx={{ mr: 1, mt: 0.3, fontSize: 16 }} />
                          <Typography variant="body2">
                            {index === 1 ? 'Rửa tay sạch trước khi thực hiện' : 'Túi giấy hoặc phong bì sạch để đựng mẫu'}
                          </Typography>
                        </Box>
                      </CardContent>
                    </Card>
                  </Grid>
                ))}
              </Grid>
            </Box>
          </Grid>
        </Grid>
      </Box>
    </Box>
  );
}

export default SampleGuide;