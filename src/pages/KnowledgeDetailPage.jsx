import React from 'react';
import { useParams } from 'react-router-dom';
import { Box, Typography, Container, Paper, Divider, Grid } from '@mui/material';
import ScienceIcon from '@mui/icons-material/Science';

const knowledgeDetails = [
  {
    id: 1,
    title: 'ADN là gì?',
    detail: `ADN (hay DNA) là acid deoxyribonucleic, mang thông tin di truyền của sinh vật. Hiểu một cách đơn giản trong y học và dịch vụ xét nghiệm, ADN (DNA) là vật liệu di truyền riêng biệt của mỗi người, giống như "dấu vân tay di truyền". 

Mỗi người có ADN khác nhau (trừ trường hợp sinh đôi cùng trứng), nên các cơ sở y tế có thể dùng ADN để:

• Xét nghiệm huyết thống: Kiểm tra mối quan hệ cha – con, mẹ – con, anh – em ruột… bằng cách so sánh ADN giữa hai hay nhiều người.

• Tầm soát bệnh di truyền: Phát hiện sớm nguy cơ mắc các bệnh như ung thư, tim mạch, tiểu đường… do di truyền từ cha mẹ.

• Xác định danh tính: Trong pháp y, xác định danh tính nạn nhân trong các vụ tai nạn, mất tích…

• Xét nghiệm trước sinh: Kiểm tra các bất thường di truyền của thai nhi.

Nói nôm na: ADN là "căn cước sinh học", xét nghiệm ADN giống như đem "căn cước đó" ra so sánh để xác định mối quan hệ huyết thống hay phát hiện nguy cơ bệnh tật.`,
  },
{
  id: 2,
  title: 'Ý nghĩa xét nghiệm ADN',
  detail: `Ý nghĩa của xét nghiệm ADN trong y học và đời sống rất lớn, cụ thể như sau:

1. Xác định huyết thống (cha – con, mẹ – con, ông – cháu…)

Đây là mục đích phổ biến nhất.
Giúp xác nhận mối quan hệ ruột thịt một cách chính xác gần như tuyệt đối (99,999%).
Áp dụng trong tranh chấp pháp lý, nhận con, nhận cha/mẹ, thừa kế tài sản, hoặc chỉ để an tâm cá nhân.

2. Tầm soát và chẩn đoán bệnh di truyền

Phát hiện sớm các bệnh có yếu tố di truyền như: ung thư vú, ung thư đại trực tràng, tim mạch, tiểu đường, loãng xương…
Từ đó giúp cá nhân chủ động phòng ngừa hoặc điều trị sớm, nâng cao chất lượng sống.

3. Xét nghiệm trước sinh và sơ sinh

Phát hiện bất thường nhiễm sắc thể (như hội chứng Down) ngay từ khi thai còn trong bụng mẹ.
Xét nghiệm sơ sinh giúp phát hiện sớm các bệnh rối loạn chuyển hóa hay di truyền hiếm gặp để can thiệp kịp thời.

4. Xác định danh tính trong pháp y

Giúp nhận dạng thi thể trong các vụ tai nạn, thảm họa, hoặc xác minh người mất tích.
Sử dụng trong điều tra hình sự để xác định nghi phạm hoặc minh oan cho người vô tội.

5. Lập hồ sơ di truyền cá nhân

Phục vụ cho y học cá thể hóa (cá nhân hóa điều trị).
Phân tích ADN giúp lựa chọn thuốc phù hợp, tránh tác dụng phụ và nâng cao hiệu quả điều trị.

Tóm lại: Xét nghiệm ADN là công cụ mạnh mẽ giúp hiểu rõ về bản thân, bảo vệ sức khỏe, xác định huyết thống và danh tính, từ đó hỗ trợ cả trong y học lẫn pháp luật.`,
},
{
  id: 3,
  title: 'Độ chính xác của xét nghiệm ADN',
  detail: `Độ chính xác của xét nghiệm ADN hiện nay rất cao, đặc biệt khi được thực hiện tại các cơ sở uy tín và theo đúng quy trình chuyên môn:

1. Xét nghiệm huyết thống (cha – con, mẹ – con...)

Chính xác gần như tuyệt đối:
  Nếu có quan hệ huyết thống, độ chính xác thường đạt 99,999% trở lên.
  Nếu không có quan hệ huyết thống, độ loại trừ là 100%.
Vì vậy, được sử dụng làm bằng chứng hợp pháp trong tòa án.

2. Xét nghiệm gen bệnh lý (tầm soát bệnh di truyền)

Độ chính xác thường từ 95% – 99%, tùy loại bệnh và công nghệ sử dụng.
Kết quả có độ tin cậy cao, nhưng thường đi kèm tư vấn di truyền để hiểu rõ nguy cơ.

3. Các yếu tố ảnh hưởng đến độ chính xác

Chất lượng mẫu sinh học: Máu, niêm mạc miệng, tóc, móng… nếu bị hỏng, nhiễm bẩn sẽ ảnh hưởng kết quả.
Phòng xét nghiệm: Cần trang bị công nghệ hiện đại, kiểm soát quy trình nghiêm ngặt.
Số lượng điểm gen được phân tích: Càng nhiều loci (vị trí gen) thì độ chính xác càng cao.

Lưu ý:

Nên chọn đơn vị xét nghiệm uy tín, được cấp phép, có đội ngũ chuyên môn và trang thiết bị hiện đại.
Một số xét nghiệm phục vụ pháp lý cần lấy mẫu trực tiếp tại cơ sở và có giấy tờ tùy thân, tránh xét nghiệm ẩn danh khi cần giá trị pháp lý.

Tóm lại: Xét nghiệm ADN có độ chính xác rất cao, đặc biệt trong xác định huyết thống và tầm soát bệnh di truyền. Tuy nhiên, độ chính xác phụ thuộc vào công nghệ, mẫu xét nghiệm và đơn vị thực hiện.`,
},
];

function KnowledgeDetailPage() {
  const { id } = useParams();
  const item = knowledgeDetails.find(k => k.id === Number(id));

  if (!item) return <Typography>Không tìm thấy thông tin!</Typography>;
  
  // Chuyển đổi string thành mảng các đoạn văn để đảm bảo xuống dòng
  const paragraphs = item.detail.split('\n\n');

  return (
    <Box 
      sx={{ 
        background: 'linear-gradient(to bottom, #f0f8ff, #ffffff)',
        minHeight: '100vh',
        py: 5
      }}
    >
      <Container maxWidth="md">
        <Paper 
          elevation={3} 
          sx={{ 
            p: 4, 
            borderRadius: 3,
            boxShadow: '0 8px 24px rgba(149, 157, 165, 0.2)',
            mb: 4
          }}
        >
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <ScienceIcon sx={{ fontSize: 40, color: '#1976d2', mr: 2 }} />
                <Typography variant="h4" sx={{ fontWeight: 700 }}>
                  {item.title}
                </Typography>
              </Box>
              <Divider sx={{ mb: 3 }} />
            </Grid>
            
            {item.image && (
              <Grid item xs={12}>
                <Box 
                  sx={{ 
                    mb: 3, 
                    borderRadius: 2, 
                    overflow: 'hidden',
                    boxShadow: '0 4px 12px rgba(0,0,0,0.1)'
                  }}
                >
                  <img 
                    src={item.image} 
                    alt={item.title}
                    style={{ 
                      width: '100%', 
                      height: '300px',
                      objectFit: 'cover'
                    }} 
                  />
                </Box>
              </Grid>
            )}
            
            <Grid item xs={12}>
              {paragraphs.map((paragraph, index) => (
                <Typography
                  key={index}
                  variant="body1"
                  paragraph={true}
                  sx={{
                    fontSize: '1.05rem',
                    lineHeight: 1.7,
                    mb: 2,
                    '& + &': { mt: 2 }
                  }}
                >
                  {paragraph}
                </Typography>
              ))}
            </Grid>
          </Grid>
        </Paper>
        
        <Box 
          sx={{ 
            p: 3, 
            bgcolor: '#f5f5f5', 
            borderRadius: 2,
            border: '1px solid #e0e0e0'
          }}
        >
          <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>
            Thông tin thêm
          </Typography>
          <Typography variant="body2">
            Nếu bạn có thắc mắc về dịch vụ xét nghiệm ADN, vui lòng liên hệ với chúng tôi để được tư vấn chi tiết và miễn phí.
          </Typography>
        </Box>
      </Container>
    </Box>
  );
}

export default KnowledgeDetailPage;