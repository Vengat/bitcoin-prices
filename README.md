# Bitcoin Service
# Building a Bitcoin Service

This project aims to build a service that maintains historic bitcoin prices using a BTree data structure in memory. Additionally, it explores the possibility of using an LMS tree.

## Table of Contents

- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)
- [BitcoinBtree](#bitcoinbtree)
- [Contributing](#contributing)
- [License](#license)

## Getting Started

Follow these instructions to set up and run the project on your local machine for development and testing purposes.

### Prerequisites

Make sure you have the following software installed:

- Java 11 or higher
- Maven 3.6.0 or higher
- Spring Boot 2.5.4 or higher

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/yourusername/bitcoin_service.git
    cd bitcoin_service
    ```

2. Build the project using Maven:
    ```sh
    mvn clean install
    ```

## Running the Application

To run the application, use the following command:
```sh
mvn spring-boot:run
```

The application will start on http://localhost:8080.

## API Endpoints

### Get Historical Bitcoin Prices

URL: /api/bitcoin/historical-prices

Method: GET

Query Parameters:

- startDate (required): The start date for the price data (format: yyyy-MM-dd).
- endDate (required): The end date for the price data (format: yyyy-MM-dd).
- currency (required): The currency symbol (e.g., USD, INR).

Response:

```json
[
    {
        "date": "2023-10-05T14:48:00.000+00:00",
        "price": 50000.0,
        "max": false,
        "min": false,
        "currency": "INR"
    },
    {
        "date": "2023-10-05T14:48:00.000+00:00",
        "price": 51000.0,
        "max": false,
        "min": false,
        "currency": "INR"
    }
]
```

## Project Structure
## Project Structure

The project structure of the Bitcoin Service is as follows:

- `BitcoinController.java`: Handles HTTP requests and returns responses.
- `BitcoinService.java`: Contains business logic for fetching Bitcoin prices.
- `BitcoinPriceDTO.java`: Data Transfer Object for Bitcoin prices.
- `BitcoinPrice.java`: Model class representing Bitcoin price data.
- `BitcoinPriceConverter.java`: Utility class for converting model objects to DTOs.
- `BitcoinBtree.java`: Custom B-tree data structure for efficient storage and retrieval of Bitcoin prices.

Each component plays a specific role in the application:

- `BitcoinController.java` handles incoming HTTP requests related to Bitcoin prices and interacts with the `BitcoinService` to retrieve the data.

- `BitcoinService.java` contains the business logic for fetching Bitcoin prices. It communicates with the `BitcoinBtree` to retrieve the data and performs any necessary transformations before returning the response.

- `BitcoinPriceDTO.java` is a Data Transfer Object that represents the Bitcoin price data in a format suitable for API responses. It is used to serialize and deserialize the data between the client and server.

- `BitcoinPrice.java` is a model class that represents a single Bitcoin price entry. It contains properties such as the date, price, currency, and flags for maximum and minimum values.

- `BitcoinPriceConverter.java` is a utility class that provides methods for converting `BitcoinPrice` objects to `BitcoinPriceDTO` objects and vice versa.

- `BitcoinBtree.java` is a custom B-tree data structure implementation designed specifically for efficient storage and retrieval of Bitcoin prices. It supports operations such as insertion, search of Bitcoin price entries.

This project structure helps organize the codebase and separates concerns, making it easier to maintain and extend the Bitcoin Service.
