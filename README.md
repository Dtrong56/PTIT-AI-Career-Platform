# PTIT AI Career Platform

Nền tảng hỗ trợ sinh viên PTIT năm 3, 4 đánh giá năng lực, luyện phỏng vấn AI và hoàn thiện CV dựa trên phân tích CV và Job Description (JD), được xây dựng theo kiến trúc Microservices và Event-Driven Architecture.

## 1. Giới thiệu

**PTIT AI Career Platform** là đề tài Đồ án Tốt nghiệp nhằm xây dựng một nền tảng hỗ trợ sinh viên trong quá trình chuẩn bị thực tập và ứng tuyển vị trí Intern/Fresher.

Hệ thống tập trung vào ba nhóm chức năng chính:

- Phân tích và so sánh **CV – JD**.
- Luyện **phỏng vấn giả lập với AI**.
- Chẩn đoán và **tối ưu CV theo hướng ATS**.

Ngoài ra, hệ thống cung cấp Dashboard dành cho Nhà trường/Khoa nhằm thống kê tình hình kỹ năng và kết quả của sinh viên.

---

## 2. Mục tiêu

Hệ thống hướng đến các mục tiêu:

- Tự động bóc tách thông tin từ CV và JD.
- Tính toán mức độ tương thích giữa CV và JD.
- Xác định các kỹ năng còn thiếu của sinh viên.
- Sinh câu hỏi phỏng vấn dựa trên CV và JD.
- Nhận câu trả lời bằng văn bản hoặc giọng nói.
- Chuyển đổi giọng nói thành văn bản bằng Speech-to-Text.
- Đánh giá câu trả lời dựa trên bộ tiêu chí định lượng.
- Phân tích CV theo một số yếu tố cơ bản của ATS.
- Gợi ý từ khóa còn thiếu và cải thiện cách mô tả dự án/kinh nghiệm theo mô hình STAR.
- Cung cấp thống kê tổng hợp phục vụ Nhà trường/Khoa.

---

## 3. Các chức năng chính

### 3.1. CV – JD Matching

Sinh viên có thể:

1. Upload CV.
2. Chọn hoặc nhập JD.
3. Hệ thống bóc tách thông tin từ CV/JD.
4. Phân tích kỹ năng, kinh nghiệm và các thông tin liên quan.
5. Tính toán Match Score.
6. Hiển thị các kỹ năng phù hợp.
7. Xác định Skill Gap giữa CV và JD.

Kết quả phân tích sử dụng các kỹ thuật NLP, Embedding và Vector Search.

### 3.2. AI Mock Interview

Hệ thống cung cấp phòng phỏng vấn giả lập với AI.

Quy trình cơ bản:

```text
CV + JD
   ↓
AI phân tích hồ sơ
   ↓
Sinh câu hỏi phỏng vấn
   ↓
Sinh viên trả lời
   ↓
Speech-to-Text (nếu trả lời bằng giọng nói)
   ↓
AI đánh giá câu trả lời
   ↓
Điểm số + nhận xét + gợi ý cải thiện
```

Các tiêu chí đánh giá có thể bao gồm:

- Độ chính xác về mặt kỹ thuật.
- Tư duy và logic.
- Cấu trúc câu trả lời.
- Mức độ đáp ứng mô hình STAR.
- Từ khóa và nội dung liên quan đến vị trí tuyển dụng.

### 3.3. CV Audit & ATS Optimization

Hệ thống phân tích CV dựa trên các yếu tố:

- Keyword Density & Keyword Gap.
- Các kỹ năng quan trọng có trong JD nhưng chưa xuất hiện trong CV.
- Cấu trúc CV.
- Khả năng đọc hiểu của CV.
- Các mục thông tin cơ bản như:
  - Summary
  - Skills
  - Experience
  - Projects
  - Education

Hệ thống tạo **CV Audit Report**, bao gồm:

- Điểm đánh giá.
- Từ khóa nên bổ sung.
- Các vấn đề về cấu trúc.
- Nhận xét về nội dung.
- Gợi ý cải thiện mô tả dự án/kinh nghiệm theo mô hình STAR.

Hệ thống không tự động viết lại toàn bộ CV thành một file PDF mới.

### 3.4. Dashboard

Dashboard dành cho Nhà trường/Khoa cung cấp các thống kê tổng hợp như:

- Số lượng CV đã được phân tích.
- Top các kỹ năng sinh viên còn thiếu.
- Phổ kỹ năng theo vị trí công việc.
- Điểm phỏng vấn trung bình.
- Thống kê kết quả theo nhóm vị trí:
  - Frontend
  - Backend
  - Mobile
  - Data
  - QA/Tester
  - ...

---

# 4. Kiến trúc hệ thống

Hệ thống được xây dựng theo kiến trúc **Microservices**, kết hợp **Event-Driven Architecture** thông qua RabbitMQ.

Các service chính:

```text
                         ┌─────────────────┐
                         │    Frontend     │
                         │ React / NextJS  │
                         └────────┬────────┘
                                  │
                                  ▼
                         ┌─────────────────┐
                         │   API Gateway   │
                         └────────┬────────┘
                                  │
              ┌───────────────────┼───────────────────┐
              │                   │                   │
              ▼                   ▼                   ▼
      ┌──────────────┐    ┌───────────────┐   ┌───────────────┐
      │   Identity   │    │     Core      │   │   Dashboard   │
      │   Service    │    │   Platform    │   │    Service    │
      └──────────────┘    └───────┬───────┘   └───────────────┘
                                  │
                                  │ RabbitMQ
                                  ▼
                         ┌─────────────────┐
                         │   AI Engine     │
                         │    Service      │
                         └────────┬────────┘
                                  │
                    ┌─────────────┼─────────────┐
                    ▼             ▼             ▼
                 LLM/API      Embedding     Qdrant
                                Model       Vector DB
```

### Nguyên tắc chính

- **API Gateway**: Điểm vào chung của hệ thống và routing request.
- **Identity Service**: Xác thực, quản lý tài khoản và phân quyền.
- **Core Platform Service**: Xử lý nghiệp vụ chính của nền tảng.
- **AI Engine Service**: Xử lý các tác vụ AI/NLP, Embedding, Vector Search, RAG và tương tác với LLM.
- **Dashboard Service**: Tổng hợp và cung cấp dữ liệu thống kê.
- **RabbitMQ**: Message Broker phục vụ các tác vụ xử lý bất đồng bộ.
- **MySQL**: Database quan hệ cho các service cần lưu trữ dữ liệu nghiệp vụ.
- **Qdrant**: Vector Database phục vụ lưu trữ và tìm kiếm vector embedding.

---

# 5. Cấu trúc Repository

Repository được tổ chức theo dạng monorepo:

```text
PTIT-AI-Career-Platform/
│
├── frontend/
│
├── api-gateway/
│
├── indentity-service/
│
├── core-platform-service/
│
├── ai-engine-service/
│
├── dashboard-service/
│
├── infrastructure/
│
├── docs/
│
├── docker-compose.yml
│
├── .gitignore
│
└── README.md
```

## Các module

| Module | Vai trò |
|---|---|
| `frontend` | Web Application |
| `api-gateway` | API Gateway và routing |
| `indentity-service` | Authentication & Authorization |
| `core-platform-service` | Nghiệp vụ chính |
| `ai-engine-service` | AI/NLP/LLM/Embedding/RAG |
| `dashboard-service` | Báo cáo và thống kê |
| `infrastructure` | Các thành phần hạ tầng dùng chung |
| `docs` | Tài liệu kỹ thuật và tài liệu đồ án |

> Lưu ý: tên thư mục hiện tại là `indentity-service`. Nếu giữ nguyên tên này thì tất cả cấu hình và tài liệu cần thống nhất theo tên hiện tại.

---

# 6. Công nghệ sử dụng

## Frontend

- ReactJS / NextJS
- JavaScript / TypeScript
- REST API

## Backend

- Java 21
- Spring Boot 4.x
- Spring Data JPA
- Spring Security
- Spring Cloud Gateway
- Maven

## AI

- Spring AI
- LLM API
- Embedding Model
- NLP
- RAG
- Vector Search
- Speech-to-Text

## Infrastructure

- Docker
- Docker Compose
- MySQL
- Qdrant
- RabbitMQ

---

# 7. Yêu cầu môi trường

Cần cài đặt:

- Git
- JDK 21
- Maven hoặc Maven Wrapper
- Node.js và npm
- Docker Desktop
- Docker Compose

Kiểm tra:

```powershell
java -version
docker --version
docker compose version
node --version
npm --version
```

---

# 8. Clone project

```powershell
git clone <REPOSITORY_URL>
cd PTIT-AI-Career-Platform
```

---

# 9. Khởi chạy Infrastructure

Các thành phần infrastructure được chạy bằng Docker Compose.

```powershell
docker compose up -d
```

Kiểm tra container:

```powershell
docker compose ps
```

Các thành phần chính:

```text
MySQL
Qdrant
RabbitMQ
```

Dừng infrastructure:

```powershell
docker compose down
```

> Database và vector data được lưu trong Docker Volume trên từng máy phát triển. Docker Volume không được commit lên Git.

---

# 10. Database

Hệ thống sử dụng mô hình Database-per-Service.

Các database chính:

```text
identity_db
core_db
dashboard_db
```

Ví dụ:

```text
MySQL
│
├── identity_db
├── core_db
└── dashboard_db
```

Mỗi service chỉ nên truy cập database thuộc quyền quản lý của service đó.

Dữ liệu giữa các service được trao đổi thông qua API hoặc Event/Message thay vì truy cập trực tiếp database của service khác.

---

# 11. Qdrant

Qdrant được sử dụng làm Vector Database cho các chức năng:

- Lưu trữ embedding.
- Tìm kiếm vector tương đồng.
- Hỗ trợ CV – JD Matching.
- Hỗ trợ RAG.

Qdrant được chạy thông qua Docker Compose.

Không cần cài đặt Qdrant trực tiếp trên máy.

---

# 12. RabbitMQ

RabbitMQ được sử dụng để xử lý các tác vụ bất đồng bộ, đặc biệt là các tác vụ AI có thời gian xử lý lớn.

Ví dụ:

```text
Upload CV
   ↓
Core Platform Service
   ↓
RabbitMQ
   ↓
AI Engine Service
   ↓
Phân tích CV
   ↓
Lưu kết quả
```

Không bắt buộc mọi request đều phải đi qua RabbitMQ.

RabbitMQ chủ yếu được sử dụng cho các tác vụ có thời gian xử lý dài hoặc cần xử lý bất đồng bộ.

---

# 13. Configuration

Các thông tin cấu hình phụ thuộc môi trường không được hard-code trực tiếp vào source code.

Ví dụ:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/identity_db
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}
```

Các API key của AI provider phải được cấu hình thông qua environment variable.

Ví dụ:

```powershell
$env:GOOGLE_API_KEY="YOUR_API_KEY"
```

Không commit API key lên GitHub.

---

# 14. Chạy Backend

Mỗi microservice có thể được chạy độc lập bằng Maven Wrapper.

Ví dụ:

```powershell
cd identity-service
.\mvnw.cmd spring-boot:run
```

Core Platform:

```powershell
cd core-platform-service
.\mvnw.cmd spring-boot:run
```

AI Engine:

```powershell
cd ai-engine-service
.\mvnw.cmd spring-boot:run
```

Dashboard:

```powershell
cd dashboard-service
.\mvnw.cmd spring-boot:run
```

API Gateway:

```powershell
cd api-gateway
.\mvnw.cmd spring-boot:run
```

---

# 15. Port

Các port development hiện tại:

| Service | Port |
|---|---:|
| API Gateway | `8080` |
| Identity Service | `8081` |
| Core Platform Service | `8082` |
| AI Engine Service | `8083` |
| Dashboard Service | `8084` |
| MySQL | `3307` |
| RabbitMQ | `5672` |
| Qdrant | `6333` |

> Các port có thể thay đổi trong quá trình phát triển. Khi thay đổi cần cập nhật lại README và configuration tương ứng.

---

# 16. Quy trình phát triển

Quy trình khuyến nghị:

```text
1. Pull code mới nhất
        ↓
2. Chạy Docker infrastructure
        ↓
3. Chạy các service cần thiết
        ↓
4. Phát triển chức năng
        ↓
5. Test local
        ↓
6. Commit
        ↓
7. Push branch
        ↓
8. Pull Request
        ↓
9. Review
        ↓
10. Merge
```

---

# 17. Git Workflow

Không commit trực tiếp lên `main` khi phát triển chức năng.

Đặt tên branch theo chức năng:

```text
feature/cv-analysis
feature/ai-interview
feature/authentication
feature/dashboard
fix/login-error
fix/cv-parser
```

Ví dụ:

```powershell
git checkout -b feature/cv-analysis
```

Sau khi hoàn thành:

```powershell
git add .
git commit -m "feat: implement CV JD matching"
git push -u origin feature/cv-analysis
```

Sau đó tạo Pull Request để merge vào `main`.

---

# 18. Quy tắc Commit

Sử dụng Conventional Commits khi có thể:

```text
feat: thêm chức năng mới
fix: sửa lỗi
refactor: refactor code
docs: cập nhật tài liệu
test: thêm hoặc sửa test
chore: công việc cấu hình/hạ tầng
```

Ví dụ:

```text
feat: implement CV JD matching
feat: add interview evaluation
fix: resolve authentication issue
docs: update project setup
chore: update docker compose
```

---

# 19. Dữ liệu thử nghiệm

Hệ thống dự kiến sử dụng:

- 30–50 JD thuộc các vị trí Intern/Fresher.
- 20–30 CV mẫu.
- Nhiều trường hợp CV khác nhau để đánh giá:
  - CV phù hợp.
  - CV thiếu kỹ năng.
  - CV có vấn đề về cấu trúc.
  - CV có định dạng khó đọc.

Dữ liệu thử nghiệm cần được tổ chức để các thành viên có thể tái tạo môi trường test mà không phụ thuộc vào database local của một cá nhân.

---

# 20. Đánh giá hệ thống

Các chỉ số dự kiến được sử dụng trong quá trình thực nghiệm:

### AI

- Cosine Similarity.
- Match Score.
- AI Service Latency.
- Chất lượng kết quả đánh giá câu trả lời.

### Hệ thống

- API Response Time.
- RabbitMQ Throughput.
- CPU Usage.
- RAM Usage.
- Thời gian xử lý các tác vụ bất đồng bộ.

---

# 21. Trạng thái phát triển

| Thành phần | Trạng thái |
|---|---|
| Project structure | 🟢 Đã thiết lập |
| Docker Compose | 🟢 Đã thiết lập |
| MySQL | 🟢 Đã thiết lập |
| Qdrant | 🟢 Đã thiết lập |
| RabbitMQ | 🟢 Đã thiết lập |
| API Gateway | 🟡 Đang phát triển |
| Identity Service | 🟡 Đang phát triển |
| Core Platform Service | 🟡 Đang phát triển |
| AI Engine Service | 🟡 Đang phát triển |
| Dashboard Service | 🟡 Đang phát triển |
| Frontend | 🟡 Đang phát triển |
| CV – JD Matching | ⚪ Chưa triển khai |
| AI Mock Interview | ⚪ Chưa triển khai |
| CV Audit | ⚪ Chưa triển khai |
| Dashboard | ⚪ Chưa triển khai |

---

# 22. Thành viên

| Thành viên | Phụ trách |
|---|---|
| Member 1 | ... |
| Member 2 | ... |
| Member 3 | ... |

---

# 23. Đồ án

**Tên đề tài:**

> Xây dựng nền tảng đánh giá năng lực, luyện phỏng vấn AI và hoàn thiện CV cho sinh viên PTIT năm 3, 4 dựa trên phân tích bài đăng tuyển dụng (JD) và kiến trúc Microservices.

**Thời gian thực hiện:** 28/09/2026 – 06/12/2026.

**Đơn vị:** Học viện Công nghệ Bưu chính Viễn thông (PTIT)

**Mục đích:** Đồ án tốt nghiệp đại học.
