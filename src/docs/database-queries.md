## Making Database Queries

To make database queries, you can use the `execute()` and `executeQuery()` methods of the `DatabaseClient` class.

1. Check that the following database config values are present in the `config.json` file.

    ```json
    {
      "testConfig": {
         "pegaDBURL": "jdbc:postgresql://<db-host-name>:<db-port>/<db-name>",
         "pegaDBUserID": "<username>",
         "pegaDBPassword": "<password>"
       }
    }
    ```
   
1. Create an instance of the `DatabaseClient` class, 
build your SQL statement - replacing any parameters with placeholders and
execute the query.

    ```java
    import uk.gov.dwp.utils.database.DatabaseClient;
    DatabaseClient dbClient = DatabaseClient.get();
   
    String sqlTemplate = "SELECT * FROM mytable WHERE name=?"
    String name = "bob";
    QueryResult result = dbClient.executeQuery(sqlTemplate, bob);
    ```
   
1. A `QueryResult` object is returned.

    ```java
    QueryResult result = dbClient.executeQuery(sqlTemplate, bob);
   
    boolean isResultEmpty = result.isEmpty();
    String firstValue = result.getFirst();
    Column<String> allFirstNames = result.getColumn("firstname");
    ```
   
1. If the SQL is a command like CREATE/UPDATE/DELETE, then use the `execute()` method of `DatabaseClient`.

    ```java
    import uk.gov.dwp.utils.database.DatabaseClient;
    DatabaseClient dbClient = DatabaseClient.get();
   
    String sqlTemplate = "DELETE FROM mytable WHERE name=?"
    String name = "bob";
    boolean isDeleteSucessful = dbClient.execute(sqlTemplate, bob);
    ```
