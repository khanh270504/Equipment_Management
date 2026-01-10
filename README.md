# Hệ thống Quản lý Trang Thiết bị Trường Đại học (University Equipment Management System)

## 1. Giới thiệu

Hệ thống **Quản lý trang thiết bị trường học** là giải pháp phần mềm toàn diện giúp quản lý vòng đời của thiết bị từ lúc **đề xuất mua, nhập kho, kiểm kê, tính khấu hao cho đến khi thanh lý**.

Dự án được xây dựng với kiến trúc **Monolithic**, tách biệt Backend (Spring Boot) và Frontend (ReactJS). Hệ thống được thiết kế để dễ dàng triển khai thông qua **Docker**.

**Điểm đặc biệt:**
- **Backend:** Được xây dựng bài bản, tuân thủ các chuẩn thiết kế phần mềm (Layered Architecture, SOLID).
- **Frontend:** Được **tạo tự động bởi AI (AI-generated)** nhằm mục đích demo nhanh luồng nghiệp vụ và kiểm thử API.

---

## 2. Công nghệ sử dụng (Tech Stack)

### Backend
- **Core:** Java 21, Spring Boot 3.x
- **Database:** MySQL 8.0
- **ORM:** Spring Data JPA (Hibernate)
- **Security:** Spring Security, JWT (JSON Web Token)
- **Extension:** Apache POI (Xử lý Excel), Lombok

### Frontend (AI generated code)
- **Framework:** ReactJS (Vite)
- **HTTP Client:** Axios

### DevOps & Tools
- **Containerization:** Docker, Docker Compose
- **Build Tool:** Maven (Backend), NPM (Frontend)

---

## 3. Chức năng chính

1. **Quản lý thiết bị:**
    - Thêm mới, cập nhật, xóa, xem chi tiết thiết bị.
    - Quản lý trạng thái (Mới, Đang sử dụng, Hỏng, Đang sửa chữa, Đã thanh lý, ...).
2. **Quản lý quy trình mua sắm:**
    - Người dùng tạo **Yêu cầu đề xuất mua**.
    - Quản lý phê duyệt đề xuất.
    - Nhập kho tự động từ đề xuất đã duyệt.
3. **Quản lý tài chính & Kho:**
    - **Tính khấu hao:** Tự động tính giá trị còn lại dựa trên thời gian sử dụng.
    - **Kiểm kê:** Đối chiếu số lượng thực tế và hệ thống.
    - **Thanh lý:** Xử lý các thiết bị hết hạn hoặc hỏng hóc không thể sửa chữa.
4. **Tiện ích:**
    - **Xuất/Nhập Excel:** Hỗ trợ import dữ liệu thiết bị hàng loạt và xuất báo cáo kiểm kê.
5. **Bảo mật:**
    - Đăng nhập/Đăng ký.
    - Phân quyền dựa trên vai trò (Admin, Staff, User).

---

## 4. Kiến trúc hệ thống (Backend)

Backend được tổ chức theo mô hình phân lớp (Layered Architecture) trong thư mục `project_one`:

- **`controller`**: Tiếp nhận request (REST API), validate dữ liệu đầu vào.
- **`service`**: Chứa toàn bộ logic nghiệp vụ (tính toán khấu hao, xử lý đề xuất...).
- **`repository`**: Giao tiếp trực tiếp với Database (JPA).
- **`dto` (Data Transfer Object)**: Đối tượng chuyển đổi dữ liệu, ẩn giấu cấu trúc thực của Entity.
- **`exception`**: Xử lý lỗi tập trung (Global Exception Handling).
- **`iservice`**:Định nghĩa contract cho service. Giúp tách interface và implementation, dễ unit test và mở rộng.

---

## 5. Cấu trúc thư mục

```text
Equipment_Management/
├── project_one/          # Source code Backend (Spring Boot)
    ├── docker-compose.yml    # File cấu hình Docker toàn dự án
    ├── Dockerfile            # Cấu hình build image
├── my-react-app/         # Source code Frontend (ReactJS - AI Generated)
├── .env                  # Biến môi trường (Bạn cần tự tạo file này)
└── README.md             # Tài liệu hướng dẫn
```



---

## 6. Hướng dẫn cài đặt & cấu hình (Installation & Configuration)

### Yêu cầu tiên quyết (Prerequisites)
- **Cách 1 (Khuyên dùng):** Cài đặt Docker Desktop (bao gồm Docker Compose).
- **Cách 2 (Thủ công):** Cài Java JDK 21, Maven, Node.js và MySQL.

### Bước 1: Cấu hình biến môi trường (.env)
Tạo file `.env.docker` nếu dùng docker hoặc `env.local` nếu dùng local tại thư mục gốc `project_one/` và copy nội dung sau (ví dụ):

```.env.local
DB_URL=jdbc:mysql://localhost:3306/ten_database
DB_USERNAME=root
DB_PASSWORD=tự điền
JWT_SIGNER_KEY=tự điền
ADMIN_PASSWORD=admin
ADMIN_USERNAME=admin@gmail.com

```
Khi chạy Docker, host là tên service mysql
```.env.docker

DB_URL=jdbc:mysql://mysql:3306/ten_database
DB_USERNAME=root
DB_PASSWORD=tu dien
JWT_SIGNER_KEY=tự ghi
ADMIN_PASSWORD=admin
ADMIN_USERNAME=admin@gmail.com
MYSQL_ROOT_PASSWORD=tu dien
MYSQL_DATABASE=thiet_bi_truong_dh_utc
```

### Bước 2: Khởi động
Phương án A: Chạy bằng Docker (chạy BE)
```
docker-compose up -d --build
```

- Chờ vài phút để Docker build image và chạy container.
- Truy cập hệ thống:
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080

Phương án B: Cài đặt thủ công

- Chuẩn bị Database: Tạo database ten_database trong MySQL.
- Chạy Backend:
```
cd project_one
mvn spring-boot:run
```

- Chạy Frontend:
```
cd my-react-app
npm install
npm run dev
```
## 7. Thông tin đăng nhập mặc định (Default Accounts)
### Admin:
- Đảm bảo username/password trong .env khớp với MySQL của bạn

## 8. Lưu ý quan trọng
### Về Frontend: Mã nguồn Frontend được sinh bởi AI nên cấu trúc có thể chưa tối ưu hoàn toàn, chủ yếu phục vụ việc hiển thị và tương tác với các API của Backend.

### Lỗi kết nối: Nếu Frontend không gọi được API khi chạy Docker, hãy kiểm tra xem URL API trong code React (axios.js ) có đang trỏ đúng về http://localhost:8080 hay không.

### Cổng (Port): Đảm bảo cổng 8080 (Backend) và 5173 (Frontend) không bị chiếm dụng bởi ứng dụng khác trước khi chạy.

## 8. Một số hình ảnh demo
### Đăng nhập
<img width="957" height="437" alt="image" src="https://github.com/user-attachments/assets/6ac57d34-dc19-4383-8727-715360e70cac" />

### Tổng Quan
<img width="958" height="440" alt="image" src="https://github.com/user-attachments/assets/0068ff5b-8ed1-4d21-96fa-a85f36a80fbf" />

### Danh sách thiết bị
<img width="956" height="442" alt="image" src="https://github.com/user-attachments/assets/04c377ec-4c14-4e2e-8148-f6661d360b7f" />

### Thanh Lý
<img width="956" height="445" alt="image" src="https://github.com/user-attachments/assets/aa59c4da-c60c-480c-99da-3aa3daebedee" />

### Mua sắm
<img width="959" height="440" alt="image" src="https://github.com/user-attachments/assets/5ac69606-d679-4f03-ad7c-8d0d6384e761" />







