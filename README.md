# Brokage Firm - Stock Order Management API

Api for a brokage firm so that their employees can send, list and delete stock orders for their customers. It also supports deposit and withdrawal operations for customer accounts. 

## Requirements

- Java 17 or higher
- Spring Boot 3.3.4
- Maven
- H2 Database

## Getting Started

### 1. Clone the Project

```bash
git clone https://github.com/osmannkocakk/brokage.git
cd brokagefirmchallange
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

### 4. User Credentials for API and H2 Database Console

- Login URL: `http://localhost:8080/login`

- Admin username: `admin`
- Admin password: `admin`

- Customer1 username: `customer1`
- Customer1 password: `Pass1`

- Customer2 username: `customer2`
- Customer2 password: `Pass2`

- Logout URL: `http://localhost:8080/logout`

The project uses an H2 in-memory database. You can access the H2 console via:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `admin`
- Password: `admin`

## API Endpoints

### ORDER:
- QUERY ALL ORDERS
- GET-> `localhost:8080/brokage/api/v1/order`
- QUERY ALL ORDERS OF A CUSTOMER:
- GET-> `localhost:8080/brokage/api/v1/order/{customerId}`  
- GET CUSTOMER ORDER 
- GET-> `localhost:8080/brokage/api/v1/order/{customerId}/{orderId}`
- CREATE ORDER FOR CUSTOMER
- POST-> `localhost:8080/brokage/api/v1/order/{customerId}`
- MATCH ORDER
- POST-> `localhost:8080/brokage/api/v1/order/match/{orderId}`
- CANCEL CUSTOMER ORDER :
- DELETE-> `localhost:8080/brokage/api/v1/order/{customerId}/{orderId}` 

### ASSET:
- QUERY ALL ASSETS
- GET-> `localhost:8080/brokage/api/v1/asset`   
- QUERY CUSTOMER ALL ASSETS:
- GET-> `localhost:8080/brokage/api/v1/asset/{customerId}`  
- DEPOSIT MONEY:
- POST-> `localhost:8080/brokage/api/v1/asset/deposit/{customerId}` 
- WITHDRAW MONEY:
- POST-> `localhost:8080/brokage/api/v1/asset/withdraw/{customerId}`


### CUSTOMER:
- QUERY ALL CUSTOMERS:
- GET-> `localhost:8080/brokage/api/v1/customer`
- QUERY CUSTOMER:
- GET-> `localhost:8080/brokage/api/v1/customer/{customerId}`
- CREATE CUSTOMER:
- POST-> `localhost:8080/brokage/api/v1/customer`

## API Sample ScreenShots and Results
![image](https://github.com/user-attachments/assets/c36ee514-9906-4761-aff6-1096adc37c29)
![image](https://github.com/user-attachments/assets/a0e58f62-1246-4480-9386-d940e39fc297)
![image](https://github.com/user-attachments/assets/5a67c0ee-45fe-40fc-b0f9-b12f03d27fb6)
![image](https://github.com/user-attachments/assets/754b449b-a48b-4550-a6f2-d502f711e9cf)
![image](https://github.com/user-attachments/assets/afcea79d-a2f6-4d2d-8688-897d0251d3ce)


## Testing Coverage
![image](https://github.com/user-attachments/assets/a3ba74bd-5c20-4f3a-b9d5-7fb13376c8bf)
![image](https://github.com/user-attachments/assets/a238bdea-f760-4c50-b394-de826a845229)


