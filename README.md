# Justlife Booking System

A Spring Boot-based booking and availability management system for assigning cleaning professionals to vehicles and appointments.

---

## 📦 Features

- Book professionals for a specific time and duration
- Update existing bookings with conflict checks
- Check professional availability for a given day or time range
- Swagger UI integration for API exploration
- JUnit 5 based unit and integration tests

---

## 🚀 Technologies Used

- Spring Boot 3.4.5
- Spring Data JPA (MySQL)
- Springdoc OpenAPI (Swagger)
- JUnit 5, Mockito
- Lombok

---

## 🧱 Project Structure

- `controller/` – REST endpoints
- `service/` – Business logic
- `dto/` – Request/response objects and TimeRange logic
- `entity/` – JPA entities: `Booking`, `Professional`, `Vehicle`
- `repository/` – Spring Data JPA interfaces
- `test/` – Unit and integration test coverage

---

## 🛠️ Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/justlife-booking.git
cd justlife-booking
```

### 2. Database Configuration

Create a MySQL database named:

```sql
CREATE DATABASE justlife_booking;
```

Update your `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/justlife_booking
spring.datasource.username=your_mysql_user
spring.datasource.password=your_mysql_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

### 3. Build & Run

```bash
./mvnw clean install
./mvnw spring-boot:run
```

---

## 📖 API Documentation

Once running, access Swagger UI at:

```
http://localhost:8080/swagger-ui/index.html
```

---

## ✅ Testing

Run all unit and integration tests:

```bash
./mvnw test
```

Test classes include:

- `BookingServiceTest`
- `BookingControllerTest`
- `AvailabilityServiceTest`

---

## 🧪 Example APIs

### Create Booking

```http
POST /api/bookings
Content-Type: application/json

{
  "startTime": "2025-05-10T10:00:00",
  "durationHour": 2,
  "professionalCount": 3
}
```

### Check Availability (by day)

```http
GET /api/bookings?date=2025-05-10
```

### Check Availability (by time range)

```http
GET /api/bookings?date=2025-05-10&startTime=10:00&durationInHour=2
```

---

## 🙏 Acknowledgements

This system is built as a part of a case study for efficient scheduling, conflict resolution, and availability tracking.