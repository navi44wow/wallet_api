# Wallet API

## Overview

The Wallet API is a RESTful web service that allows users to manage their wallets and entries. It provides endpoints for creating, updating, and retrieving users, wallets, and entries. Additionally, it supports exporting entries to a CSV file for a specified period.

## Features

- Create, update, and retrieve users and wallets
- Create and retrieve entries for wallets
- Export entries to a CSV file format for a specified period

## Technologies

- Java
- Spring Boot
- Spring Data JPA
- MySQL
- OpenCSV

## Prerequisites

- Java 11 or higher
- Maven
- MySQL and MySQL Workbench

## Getting Started

### Clone the repository

```sh
git clone https://github.com/navi44wow/wallet_api.git
```

### Configure the database

Update the `application.properties` file with your MySQL database configuration:

```properties
spring.datasource.username=yourusername
spring.datasource.password=yourpassword
```

### Build and run the application

```sh
mvn clean install
mvn spring-boot:run
```

### Test the application

```sh
mvn test
```

also there is a Postman collection attached in the files, 
you can run it in your postman account to test the requests and to see the test results.
This is the location of the postman collection:
src/main/resources/walletAPI.postman_collection.json

## API Endpoints

### User Endpoints

- **Get User by ID**
  - `GET /api/users/{id}`
  - Response: `200 OK` with user details or `404 Not Found` if the user does not exist

- **Get All Users**
  - `GET /api/users`
  - Response: `200 OK` with a list of users

- **Create User**
  - `POST /api/users`
  - Request Body: JSON representation of the user
  - Response: `201 Created` with the created user

### Wallet Endpoints

- **Get Wallets by User ID**
  - `GET /api/users/{userId}/wallets`
  - Response: `200 OK` with a list of wallets or `404 Not Found` if the user does not exist

- **Get Wallet by User ID and Wallet ID**
  - `GET /api/users/{userId}/wallets/{walletId}`
  - Response: `200 OK` with wallet details or `404 Not Found` if the wallet does not exist

- **Add Wallet to User**
  - `POST /api/users/{userId}/wallets`
  - Request Body: JSON representation of the wallet
  - Response: `201 Created` with the created wallet or `404 Not Found` if the user does not exist

### Entry Endpoints

- **Get Entries by User ID and Wallet ID**
  - `GET /api/users/{userId}/wallets/{walletId}/entries`
  - Response: `200 OK` with a list of entries or `404 Not Found` if the wallet does not exist

- **Get Entries Summary by User ID and Wallet ID**
  - `GET /api/users/{userId}/wallets/{walletId}/entries-summary`
  - Query Parameters: `startDate`, `endDate`
  - Response: `200 OK` with the entry summary or `404 Not Found` if the wallet does not exist

- **Export Entries to CSV**
  - `GET /api/users/entries/csv`
  - Query Parameters: `userId`, `walletId`, `startDate`, `endDate`
  - Response: `200 OK` with a CSV file containing the entries for the specified period

- **Transfer Amount between Wallets**
  - `POST /api/users/transfer`
  - Request Body: JSON representation of the transfer details
  - Response: `200 OK` with a success message or `400 Bad Request` if the transfer data is invalid

- **Deposit or Withdraw from Wallet**
  - `POST /api/users/entry`
  - Request Body: JSON representation of the deposit or withdrawal details
  - Response: `200 OK` with a success message or `400 Bad Request` if the data is invalid


