# Payanam Bus Tracking System 🚌

Payanam is a robust, console-based Java application designed to manage and track bus routes, ticket bookings, and live bus locations. Built with a strict **Model-View-Presenter (MVP)** architecture, the application ensures a clean separation of concerns, making it highly maintainable and scalable.

## 🌟 Features

The application supports three distinct user roles:

### 1. Passenger
* **Secure Authentication:** Sign up and log in using phone numbers and securely hashed passwords.
* **Bus Search:** Search for buses by bus number or destination stop.
* **Ticket Booking:** Book digital tickets from a specific source to a destination. Prices are calculated dynamically based on distance.
* **View Tickets:** View currently booked tickets along with their validity and expiration times.
* **Live Tracking:** View the real-time location of buses along their routes.

### 2. Ticket Collector
* **Validate Tickets:** Check passenger tickets using their unique IDs. The system ensures tickets are valid, haven't expired, and haven't been used before. Once validated, tickets are marked as "used".
* **Update Bus Location:** Start a journey, advance the bus to the next stop, and calculate Estimated Times of Arrival (ETA) for subsequent stops.
* **Route Reversal:** Once a bus reaches its final destination, the collector can instantly reverse the route for the return journey.

### 3. Administrator
* **Fleet Management:** Add new buses or delete existing ones.
* **Route Management:** Define and replace the sequence of stops for any bus.
* **User Management:** Remove users or register new Ticket Collectors into the system.

---

## 🏗 Architecture

The project strictly follows the **MVP (Model-View-Presenter)** design pattern:
* **Model:** Contains all business logic (price calculations, ETA generation) and communicates with the `PayanamDB` repository. It is completely isolated from the UI.
* **View:** Responsible *only* for displaying output to the console and capturing user input via `Scanner`. It contains zero business logic.
* **Presenter:** Acts as the middleman. It receives input from the View, requests data or operations from the Model, and dictates what the View should display next.

---

## 💻 Tech Stack

* **Language:** Java 8+
* **Database:** MySQL
* **Data Access:** JDBC (Java Database Connectivity)
* **Security:** BCrypt (for secure password hashing)

---

## 🛠 Prerequisites

Before running the application, ensure you have the following installed:
1. **Java Development Kit (JDK):** Version 8 or higher.
2. **MySQL Server:** Running locally or remotely.
3. **JDBC Driver:** MySQL Connector/J must be included in your project libraries.
4. **BCrypt:** `jbcrypt-0.4.jar` must be included in your `libs/` folder.

---

## 🚀 Setup and Installation

1. **Configure the Database:**
   Ensure MySQL is running. Open `src/com/sunilskyros/payanam/util/DBConnection.java` and update your database credentials:
   ```java
   String url = "jdbc:mysql://localhost:3306/payanam_db"; // Ensure this DB exists
   String user = "root";
   String password = "your_password";
   ```
   *Note: The application automatically handles table creation (`passengers`, `buses`, `stops`, `tickets`) on the first boot.*

2. **Compile the Project:**
   Ensure your IDE (IntelliJ, Eclipse, etc.) has the `libs/` folder added to its build path.

3. **Run the Application:**
   Execute the `main` method located in `src/com/sunilskyros/payanam/Payanam.java`.

---

## 🛡 Code Quality & Standards

* **Clean Code:** The codebase enforces strict separation of concerns. Direct SQL queries are entirely restricted to the Data Access Layer (`PayanamDB.java`).
* **Safe Deletions:** Rather than destructively deleting tickets upon validation, the system uses "soft deletes" (`is_valid = false`) to maintain historical auditing.
* **Extensively Documented:** All core features, models, presenters, and database operations feature comprehensive Javadoc comments.
