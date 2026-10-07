# 🏪 Warung Management System

A desktop-based **Point of Sale (POS) and store management application** built with **Java Swing** and **MySQL**.

This application is designed to help small businesses manage products, sales transactions, cashier accounts, inventory, and sales reports in one integrated system.

The project follows the **MVC (Model-View-Controller)** architecture combined with the **DAO (Data Access Object)** pattern to separate the user interface, business logic, and database operations.

---

## ✨ Features

### 🔐 Authentication & Role Management

- Login using username and password
- Supports two user roles:
  - **Admin**
  - **Cashier**
- Different menus and access permissions based on the user's role
- Account validation before allowing access to the application

---

### 📊 Admin Dashboard

The Admin dashboard provides an overview of the store's current activity, including:

- Total active products
- Number of today's transactions
- Today's revenue
- Number of low-stock products
- Stock alerts for products that need restocking

---

### 📦 Product Management

Admins can manage product information through the application.

Available features include:

- Add new products
- Edit product information
- Delete or deactivate products
- Search products by name or product code
- Manage product categories
- Manage purchase prices
- Manage selling prices
- Manage inventory stock
- Manage product units
- Monitor low-stock and out-of-stock products

---

### 👨‍💼 Cashier Management

Admins can manage cashier accounts directly from the application.

Features include:

- Add cashier accounts
- Edit cashier information
- Assign cashier shifts:
  - Morning
  - Afternoon
  - Night
- Deactivate cashier accounts
- View cashier account status

---

### 🛒 Point of Sale

Cashiers can process customer purchases through the POS interface.

POS features include:

- Search products by name or product code
- Add products to the shopping cart
- Set product quantity
- Display product price and available stock
- Remove products from the cart
- Automatic subtotal calculation
- Automatic total calculation
- Enter customer payment amount
- Automatic change calculation
- Quick payment amount shortcuts
- Cancel transactions
- Start a new transaction
- Display receipt after successful payment

---

### 🧾 Transaction History

The system stores all completed sales transactions.

Transaction information includes:

- Transaction number
- Transaction date and time
- Cashier name
- Total items
- Transaction total
- Customer payment
- Change amount
- Transaction status

Users can also view the detailed contents of each transaction.

---

### 📈 Reports & Statistics

Admins can monitor business performance through several reports and statistics.

Available information includes:

- Today's total transactions
- Today's revenue
- Transactions from the last 7 days
- Revenue from the last 7 days
- Total transactions
- Total revenue
- Total active products
- Total cashiers
- Best-selling products
- Low-stock products

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java | Main programming language |
| Java Swing | Desktop graphical user interface |
| MySQL | Relational database |
| JDBC | Database connectivity |
| MySQL Connector/J | MySQL JDBC driver |
| Apache Ant | Build system |
| Apache NetBeans | IDE and project configuration |

---

## 🏗️ Architecture

This project follows the **MVC + DAO** architecture.

```text
              ┌───────────────┐
              │     View      │
              │  Java Swing   │
              └───────┬───────┘
                      │
                      ▼
              ┌───────────────┐
              │  Controller   │
              │ Business Logic│
              └───────┬───────┘
                      │
                      ▼
              ┌───────────────┐
              │      DAO      │
              │ Database Layer│
              └───────┬───────┘
                      │
                      ▼
              ┌───────────────┐
              │     MySQL     │
              └───────────────┘
```

### Model

The Model layer represents the application's main data entities.

Examples:

```text
User
Admin
Cashier
Product
Transaction
TransactionItem
```

---

### View

The View layer handles the application's graphical user interface using Java Swing.

Examples:

```text
LoginFrame
AdminFrame
CashierFrame
POSPanel
ProductPanel
CashierPanel
HistoryPanel
ReportPanel
```

---

### Controller

The Controller layer handles application logic and communication between the View and DAO layers.

Examples:

```text
AuthController
UserController
ProductController
TransactionController
```

---

### DAO

The **Data Access Object** layer is responsible for handling database operations.

This approach keeps SQL and database access logic separate from the application's business logic.

---

## 📁 Project Structure

```text
sistem-manajemen-warung/
│
├── sistem_manajemen_warung/
│   │
│   ├── src/
│   │   ├── controller/
│   │   ├── dao/
│   │   ├── database/
│   │   ├── model/
│   │   ├── util/
│   │   ├── view/
│   │   │
│   │   └── App.java
│   │
│   ├── nbproject/
│   └── build.xml
│
├── sql/
│   └── warung_db.sql
│
└── README.md
```

---

## 🗄️ Database

The application uses a MySQL database named:

```text
warung_db
```

The main tables include:

| Table | Description |
|---|---|
| `users` | Stores Admin and Cashier accounts |
| `produk` | Stores product and inventory information |
| `transaksi` | Stores sales transactions |
| `item_transaksi` | Stores items associated with each transaction |

The simplified relationship between tables is:

```text
users
  │
  └── transaksi
        │
        └── item_transaksi
                  │
                  └── produk
```

---

## 🚀 Getting Started

### Prerequisites

Before running the application, make sure you have installed:

- Java Development Kit
- Apache NetBeans
- MySQL Server
- MySQL Connector/J

---

### 1. Clone the Repository

```bash
git clone https://github.com/lollybrownn/sistem-manajemen-warung.git
```

Move into the project directory:

```bash
cd sistem-manajemen-warung
```

---

### 2. Set Up the Database

Make sure your MySQL server is running.

Import the SQL file located at:

```text
sql/warung_db.sql
```

Using MySQL CLI:

```bash
mysql -u root -p < sql/warung_db.sql
```

Alternatively, you can import the SQL file using:

- MySQL Workbench
- phpMyAdmin
- HeidiSQL
- Another MySQL database management tool

The SQL script will create the required database and tables.

---

### 3. Configure the Database Connection

Open:

```text
sistem_manajemen_warung/src/database/DBConnection.java
```

Update the database configuration according to your local MySQL environment.

Example:

```java
private static final String DB_HOST = "localhost";
private static final String DB_PORT = "3306";
private static final String DB_NAME = "warung_db";
private static final String DB_USER = "root";
private static final String DB_PASS = "";
```

Change `DB_USER` and `DB_PASS` if your MySQL credentials are different.

---

### 4. Add MySQL Connector/J

The application requires **MySQL Connector/J** to connect Java with the MySQL database.

If NetBeans displays a broken library reference:

```text
Project
→ Properties
→ Libraries
→ Add JAR/Folder
```

Then select your MySQL Connector/J `.jar` file.

Example:

```text
mysql-connector-j-9.x.x.jar
```

---

### 5. Open the Project

Open the following folder using Apache NetBeans:

```text
sistem_manajemen_warung/
```

Make sure your Java Development Kit is configured correctly.

---

### 6. Run the Application

Run:

```text
App.java
```

Or use:

```text
Run Project
```

from Apache NetBeans.

The application will check the MySQL connection before opening the login page.

---

## 🔑 Demo Accounts

The database includes several default accounts for testing and demonstration purposes.

### Admin

```text
Username : admin
Password : admin123
Role     : ADMIN
```

### Cashier 1

```text
Username : kasir1
Password : kasir123
Shift    : MORNING
```

### Cashier 2

```text
Username : kasir2
Password : kasir123
Shift    : AFTERNOON
```

> These accounts are intended for development and demonstration purposes only. Default credentials should not be used in production environments.

---

## 🔄 Application Flow

```text
                ┌───────────────┐
                │     Login     │
                └───────┬───────┘
                        │
                ┌───────▼───────┐
                │  Role Check   │
                └───────┬───────┘
                        │
              ┌─────────┴─────────┐
              │                   │
        ┌─────▼─────┐       ┌────▼──────┐
        │   ADMIN   │       │  CASHIER  │
        └─────┬─────┘       └────┬──────┘
              │                   │
     ┌────────┼────────┐     ┌────┼─────────┐
     │        │        │     │    │         │
 Dashboard Products Cashiers POS History Products
     │        │        │     │
     │      History    │  Transaction
     │        │        │     │
     └──── Reports ────┘   Payment
                              │
                           Receipt
```

---

## 🎯 Project Objectives

This project was developed as a learning project to practice and implement several software engineering concepts, including:

- Object-Oriented Programming
- Java Desktop Development
- Java Swing
- JDBC
- Relational Databases
- CRUD Operations
- Authentication
- Role-Based Authorization
- MVC Architecture
- DAO Pattern
- Transaction Management
- Inventory Management

---

## 🔮 Future Improvements

Potential improvements for future versions include:

- Password hashing
- Export reports to PDF or Excel
- Sales charts and visual analytics
- Date-based report filtering
- Thermal receipt printer integration
- Barcode scanner support
- Database backup and restore
- Advanced analytics dashboard
- External database configuration file
- Unit testing
- Better exception handling
- Input validation improvements

---

## 📸 Screenshots

Screenshots of the application will be added here.

Recommended screenshots:

```text
Login Page
Admin Dashboard
Product Management
Point of Sale
Transaction History
Sales Report
```

Example:

```markdown
### Login

![Login](screenshots/login.png)

### Admin Dashboard

![Admin Dashboard](screenshots/dashboard.png)

### Point of Sale

![POS](screenshots/pos.png)
```

---

## 👨‍💻 Author

**Albert**

Informatics Student  
Universitas Pembangunan Nasional "Veteran" Yogyakarta

GitHub: [@lollybrownn](https://github.com/lollybrownn)

---

## 📄 License

This project was created for **learning and portfolio purposes**.

Feel free to explore, fork, and improve the project.
