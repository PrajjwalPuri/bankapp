# 🏦 Bank Management System

A web-based Bank Management Application developed using Spring Boot that enables users to securely perform basic banking operations such as registration, login, deposit, withdrawal, fund transfer, and viewing transaction history.

---

## 🚀 Features
- User Registration
- Secure Login & Authentication
- Deposit Money
- Withdraw Money
- Fund Transfer Between Accounts
- View Transaction History

---

## 🛠️ Technologies Used

### Backend
- Java  
- Spring Boot  
- Spring Security  
- Spring Data JPA  

### Frontend
- HTML  
- CSS  
- Thymeleaf  

### Database
- MySQL  

---

## 🗂️ Database Schema

### Account Table
- id (Primary Key)  
- username  
- password  
- balance  

### Transaction Table
- id (Primary Key)  
- amount  
- timestamp  
- type  
- account_id (Foreign Key → Account.id)  

*Relationship:*  
One Account → Many Transactions

---

## ⚙️ Application Modules
- Authentication Module  
- Account Management Module  
- Transaction Module  
- Transaction History Module  

---

## ▶️ How to Run the Project
- Clone the repository  
- Configure MySQL database in application.properties  
- Run the Spring Boot application  
- Access the application at http://localhost:8080

---

## 👨‍💻 Author
Prajjwal Puri
