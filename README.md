# Payanam Bus Tracking System 🚌

Payanam is a robust Bus Tracking and Ticket Booking system designed with a **decoupled, multi-port architecture**:
*   **Backend Server (`Port 8080`)**: Built on Spring Boot, JDBC, and MySQL. Implements core models, secure BCrypt password authentication, session management, and data endpoints.
*   **Frontend Server (`Port 3000`)**: A sleek, vanilla HTML/CSS/JavaScript static application served via a lightweight Node/Express dev proxy, enabling instant hot-reloading.

---

## 🌟 Features

The application supports three distinct user roles:

### 1. Passenger
*   **Secure Authentication:** Sign up and log in using phone numbers and securely hashed passwords.
*   **Bus Search:** Search for buses by bus number or destination stop.
*   **Ticket Booking:** Book digital tickets from a specific source to a destination. Prices are calculated dynamically based on distance.
*   **View Tickets:** View currently booked tickets along with their validity and expiration times.
*   **Live Tracking:** View the real-time location of buses along their routes with immediate spot refreshing.

### 2. Ticket Collector
*   **Validate Tickets:** Check passenger tickets using their unique IDs. Once validated, tickets are marked as "used".
*   **Update Bus Location:** Start a journey, advance the bus to the next stop sequence automatically, and calculate ETAs for subsequent stops.
*   **Route Reversal:** Once a bus reaches its final destination, the collector can instantly reverse the route for the return journey.
*   **Shift Finish:** Securely sign out or end shift to unlock assignment bounds.

### 3. Administrator
*   **Fleet Management:** Add new buses or delete existing ones.
*   **Route Management:** Define and replace the sequence of stops for any bus.
*   **User Management:** Remove users or register new Ticket Collectors into the system.

---

## 🏗 Architecture

The project strictly follows the **Model-View-Presenter (MVP)** design pattern at the business logic layer, split across two distinct layers:
*   **Backend (`backend/`):** Contains the MVP Models (business rules, price calculations, ETA generation) and central SQL Data Access Repository (`PayanamDB.java`). Exposes `@RestController` endpoints and manages secure cookies.
*   **Frontend (`frontend/`):** Dedicated HTML dashboards (`admin.html`, `collector.html`, `dashboard.html`) and styling assets. Served independently and connects securely through an Express reverse proxy to bypass CORS restrictions.

---

## 💻 Tech Stack

*   **Language:** Java 17+ (Backend) | Vanilla Javascript & CSS (Frontend) | Node.js (Dev server)
*   **Database:** MySQL
*   **Data Access:** JDBC (Java Database Connectivity)
*   **Security:** BCrypt (for secure password hashing)
*   **Web Framework:** Spring Boot 3.2.5

---

## 🚀 Setup and Installation

### 1. Database Configuration
Ensure MySQL is running. Open `backend/src/main/resources/application.properties` and update your database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/payanam?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password
```
*Note: The application automatically handles table creation and seeds a default admin account on the first boot.*

### 2. Run the Application (Dual-Port Mode)

To run the application, launch both the backend and frontend in separate terminals using the provided helper scripts:

#### Step A: Boot the Backend (Port 8080)
Open a terminal in the root directory and run:
```bash
./start-backend.sh
```
This compiles the Spring Boot server, connects to MySQL, and hosts the API server at `http://localhost:8080`.

#### Step B: Boot the Frontend (Port 3000)
Open a second terminal in the root directory and run:
```bash
./start-frontend.sh
```
This automatically installs the tiny dev-server dependencies (`express` and `http-proxy-middleware`) and launches the frontend server at `http://localhost:3000`.

---

## 🔑 Default Credentials

To explore the dashboards immediately, log in using the seeded profiles:

*   **Administrator:**
    *   **Phone Number:** `9999999999`
    *   **Password:** `admin123`
*   **Passenger:** Register a new account directly from the **Register** tab at `http://localhost:3000/`.

---

## 🛡 Code Quality & Benefits of decoupling

*   **Instant Frontend Updates:** You can edit HTML, CSS, or JS files inside the `frontend/` directory and see changes instantly by refreshing `http://localhost:3000/`. No backend server restarts needed!
*   **Clean Separation of Concerns:** Frontend engineers can work completely in `frontend/` using simple static tools, while backend engineers can work in `backend/` compiling and testing APIs.
*   **CORS Safeguarded:** Express proxy intercepts all cookie exchanges (`JSESSIONID`) and delivers them seamlessly, preventing browser blocks.
