# MoneyTransfer


This is a basic MoneyTransfer REST API that lets you :
- Create an Account with initial funds, a 4 digits ID will be automatically generated
- Transfer money between an account and another
- Retrieve an account info
- Remove an account
- Show all accounts

An embedded RESTful server (Jetty+Jersey) was needed to start from a standard Java main method. Using Maven we can package the entire application in a single executable jar file, which can be started from any machine with a Java environment. H2 Database has been used an a lightweight internal Java database to store the accounts.
I chose some features from REST-Assured for testing.

A "mvn test" from "MoneyTransfer-master" will compile and run all the tests that ensure the API is performing the corresponding action depending on specific money transfer scenarios.

Another option is starting the embedded Jetty server and throw tools like POSTMAN fire the corresponding URIs to consume the REST Api :

java -jar RevoMoney-0.0.1-SNAPSHOT.jar

Examples:

- http://localhost:8080/entry-point/showAllAccounts
- http://localhost:8080/entry-point/createAccount?funds=20
- http://localhost:8080/entry-point/removeAccount/6795
- http://localhost:8080/entry-point/getAccountInfo/9915
- http://localhost:8080/entry-point/transferMoney?account1=5884&account2=9915&funds=5
