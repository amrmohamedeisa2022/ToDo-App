# ToDo-App 

 Project Overview
This project is a **Microservices-based Todo Application** built using **Spring Boot 4** and **Java 17**.

It consists of two main services:

- **User Service**: Responsible for authentication, user management, OTP activation, and password recovery.
- **Todo Service**: Responsible for managing Todo items (CRUD operations) and communicating with User Service to validate JWT tokens.

---

##  Technologies Used
- Java 17
- Spring Boot 4
- Spring Data JPA (Hibernate)
- Spring Security
- JWT Authentication (jjwt)
- MySQL Database
- JavaMailSender (Gmail SMTP)
- REST APIs
- Lombok
- Postman for API Testing

---

##  User Service Features
The User Service provides secure authentication and account management:

- User Registration
- OTP Email Verification (Account Activation)
- User Login (JWT Token Generation)
- Token Validation API
- Regenerate OTP
- Forget Password (OTP)
- Change Password using OTP
- Create User Profile (name + profile image stored in database as BLOB)
- Global Exception Handling

---

##  Todo Service Features
The Todo Service manages Todo items and supports full CRUD operations:

- Add Todo Item
- Update Todo Item
- Delete Todo Item
- Search Todo Item by ID
- Search Todo Items by Title
- Pagination Support (Get All Items)
- Inter-service communication with User Service using **RestTemplate** for JWT validation

---

##  Microservices Communication
The Todo Service verifies every request by sending the JWT token to the User Service using **RestTemplate**.

 If the token is valid → the request is processed  
 If the token is invalid → the request is rejected

---

##  Database Design
The project uses MySQL with the following main tables:

- User
- OTP
- JWT
- Todo
