
## Index

* [Description](#description)
* [Funcionalities](#functionalities)
* [Acceso al proyecto](#access-to-the-project)
* [Used Technologies](#used-technologies)
* [API Endpoints](#api-endpoints)
* [Developers](#developers)


## Description
A web application for personal finance management and cash flow tracking. Developed with a React frontend and the backend is structured into two Spring Boot services: an authentication-service and a transaction-service. Although they currently share a database, they are organized as independent modules. It allows users to monitor expenses, filter transactions, and gain insights into their finances.The backend is tested with unit tests using JUnit and Mockito, ensuring that core functionalities such as creating, updating, retrieving, and deleting correctly.

## Functionalities
- `User Authentication`: Register, log in, and manage individual user accounts. Each user's data is displayed only to the logged-in user.
- `Expense Management`: Add, edit, and delete expenses with detailed information. Each user only can manage their own personal expense records.
- `Income Management`: Similar functionality for tracking income sources, tracked separately for each user.
- `Expenses Categorization`: Organize expenses into categories (Food, Transport, Entertainment, Others).
- `Advanced Filtering`: Multi-criteria filtering (Dates, Category and Imports).
- `Real-time Summary`: Displays your current bank balance, projected balance in 30 days, and year-end balance. Alerts you if your balance is expected to go negative within the next 7 days.
- `Advanced Analytics`: Charts and graphs for financial insights.
- `Export`: Export expenses and incomes separately or together into a single CSV file. Includes the option to export filtered results.


## Access to the project


## Used Technologies
  * Java</br>
  * Spring Boot</br>
  * Spring Data JPA</br>
  * Junit</br>
  * Mockito</br>
  * JavaScript</br>
  * React</br>
  * Vite</br>
  * HTML</br>
  * CSS</br>
  * Postman</br>
  * DBeaver</br>
  * Docker Desktop</br>

## React and Vite
This application uses **React** together with **Vite** for bundling and Hot Module Replacement (HMR).



## Developers

<div align= "center">Marcos Rama </div>
<div align= "center">Email: marcos.rama.1994@gmail.com</div>

## License

<div align="left">
  
**Current Status:** Open Source - Free to use, modify and free distribute

**Future Considerations:** Licensing terms may evolve as the project matures. 
Users are advised to check this section periodically for updates.

For commercial use inquiries, please contact the developer.
</div>
