### Library management API Test
In this project have used Library management API URL(**https://librarymanagementapisystem.onrender.com/**) to automate the functionalities,
have automated the API tests using **Cucumber** with **TestNG** framework and **RestAssured** Library

#### Requirements
- Java 21 
- Maven 3.9.8
  
#### Available Options
- To configure the application properties use **app.properties** file under resource folder

#### Run the tests
Use **mvn clean test** command to run the tests

### Reporting
Used extent report library to generate test execution summary report.
Once tests has been finished the extent report can be generated under **target/extent-reports**/ which can be viwable in any of the browser.

### Functionalities covered
Added test to verify the Book availability, Book borrow and book return
