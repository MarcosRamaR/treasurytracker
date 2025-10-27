
## Index

* [Description](#description)
* [Funcionalities](#functionalities)
* [Acceso al proyecto](#access-to-the-project)
* [Used Technologies](#used-technologies)
* [API Endpoints](#api-endpoints)
* [Developers](#developers)


## Description
A web application for personal finance management and cash flow tracking. Developed with a React frontend and a Spring Boot backend, it allows users to monitor expenses, filter transactions, and gain insights into their finances.

## Functionalities
- `Expense Management`: Add, edit, and delete expenses with detailed information
- `Income Management`: Similar functionality for tracking income sources
- `Expenses Categorization`: Organize expenses into categories (Food, Transport, Entertainment, Others)
- `Advanced Filtering`: Multi-criteria filtering (Dates, Category and imports)
- `Real-time Summary`: View total and average expenses in the summary section

### Planned Features
- `Advanced Analytics`: Interactive charts and graphs for financial insights
- `Future Projections`: Forecast upcoming expenses and income


## Access to the project


## Used Technologies
  * Java</br>
  * Spring Boot</br>
  * Spring Data JPA</br>
  * JavaScript</br>
  * React</br>
  * Vite</br>
  * HTML</br>
  * CSS</br>

## React and Vite
This application uses **React** together with **Vite** for bundling and Hot Module Replacement (HMR).

## API Endpoints

### Base URL: `http://localhost:8080/api/expenses`

### Recipe Operations

| Method | Endpoint              | Description                          | Example Request Body |
|--------|-----------------------|--------------------------------------|----------------------|
| `GET`  | ``            | Get all expenses                      | -                    |
| `GET`  | `/{id}`       | Get expense by ID                     | -                    |
| `GET`  | `/category/{category}`       | Get expenses from one category                     | -                    |
| `GET`  | `/between-date`       | Get expenses by a date range                   | -                    |
| `GET`  | `/total`       | Get the sum of all expenses                   | -                    |
| `POST` | ``            | Create new expense                    |  [See below](#-sample-requests)   |
| `PUT`  | `/{id}`       | Update the expense of this id              |-                    |
| `DELETE` | `/{id}`     | Delete expense by id                       | -                    |

###  Sample Requests

**Create expense** (`POST`)
```json:
{
  "description": "Groceries",
  "amount": 45.50,
  "category": "Food",
  "date": "2024-01-15"
}
```

## Developers

<div align= "center">Marcos Rama </div>
<div align= "center">Email: marcos.rama.1994@gmail.com</div>

## License

<div align="left">
  
**Current Status:** Open Source - Free to use, modify and distribute

**Future Considerations:** Licensing terms may evolve as the project matures. 
Users are advised to check this section periodically for updates.

For commercial use inquiries, please contact the developer.
</div>
