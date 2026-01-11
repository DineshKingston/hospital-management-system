# 🏥 Hospital Management System (Spring Boot + MongoDB)

A simple **Hospital Management System** built using **Spring Boot, MongoDB, Thymeleaf**, and **HTML/CSS**.  
This project supports **Admin, Doctor, and Patient roles** with secure login and appointment management.

---

## 🚀 Features

### 👨‍💼 Admin
- Admin login & logout
- Add doctors
- View doctors list
- Delete doctor
- Reset doctor password

### 👨‍⚕️ Doctor
- Doctor login & logout
- View appointment requests
- Accept / Reject appointments
- View patient profile
- View appointment history
- Change own password
- Notification count for pending requests

### 🧑‍🤝‍🧑 Patient
- Patient signup & login
- View available doctors
- View doctor profile
- Apply for appointment (date & time)
- View appointment status
- Cancel pending appointments
- View own profile

---

## 🛠️ Technologies Used

- **Backend**: Spring Boot (Java)
- **Frontend**: HTML, Thymeleaf, CSS
- **Database**: MongoDB
- **Security**: BCrypt Password Encryption
- **Version Control**: Git & GitHub

---

## 📂 Project Structure

src/main/java/com/example/hospital
├── controller
│ ├── AdminController.java
│ ├── DoctorController.java
│ └── PatientController.java
│
├── model
│ ├── Admin.java
│ ├── Doctor.java
│ ├── Patient.java
│ └── Appointment.java
│
├── repository
│ ├── AdminRepository.java
│ ├── DoctorRepository.java
│ ├── PatientRepository.java
│ └── AppointmentRepository.java
│
src/main/resources
├── templates
│ ├── admin-.html
│ ├── doctor-.html
│ └── patient-*.html
│
├── static
│ ├── style.css
│ └── theme-toggle.js


---

## 🔐 Authentication & Security

- Passwords are stored using **BCrypt encryption**
- Session-based authentication
- Role-based access (Admin / Doctor / Patient)
- Only logged-in users can access protected pages

---

## 🧪 How to Run the Project

1. Clone the repository
   ```bash
   git clone https://github.com/USERNAME/hospital-management-system.git
2. Open the project in Eclipse / IntelliJ

3. Configure MongoDB connection in application.properties

4. Run the Spring Boot application

5. Open browser:
    http://localhost:8080


Default Admin Credentials:
  (Admin created manually in MongoDB)

Email    : username@gmail.com
Password : bcryptpassword
