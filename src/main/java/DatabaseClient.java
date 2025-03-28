//package uk.gov.dwp.utils.database;
//
//import uk.gov.dwp.webdriver.configuration.TestConfigHelper;
//
//import java.sql.*;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * A gateway class  responsible for executing SQL queries against an external database.
// */
//public class DatabaseClient {
//    private static DatabaseClient databaseClient;
//    private String url;
//    private String user;
//    private String password;
//
//    private DatabaseClient() {
//        url = getPegaDBUrl();
//        user = TestConfigHelper.get().getTestConfigItem("pegaDBUserID");
//        password = TestConfigHelper.get().getTestConfigItem("pegaDBPassword");
//    }
//
//    /**
//     * Generates a singleton instance of the DatabaseClient class.
//     *
//     * @return a singleton instance of DatabaseClient
//     */
//    public static DatabaseClient get() {
//        if (databaseClient == null) {
//            databaseClient = new DatabaseClient();
//        }
//
//        return databaseClient;
//    }
//
//    /**
//     * Executes an SQL command that does not require an output. e.g. UPDATE, CREATE, DELETE, etc
//     *
//     * @param queryTemplate The SQL statement with placeholders for parameters
//     * @param parameters    A lit of parameters to be used with the SQL statement to create PreparedStatement instance
//     * @return boolean indicating if the execution was successful
//     * @throws SQLException
//     */
//    public boolean execute(String queryTemplate, Object... parameters) throws SQLException {
//        try (Connection connection = DriverManager.getConnection(url, user, password)) {
//            return getPreparedStatement(queryTemplate, connection, Arrays.asList(parameters)).execute();
//        }
//    }
//
//    /**
//     * Executes an SQL queries and returns a set of results
//     *
//     * @param queryTemplate The SQL statement with placeholders for parameters
//     * @param parameters    A lit of parameters to be used with the SQL statement to create PreparedStatement instance
//     * @return QueryResult - containing all the results from the query.
//     * @throws SQLException
//     */
//    public QueryResult executeQuery(String queryTemplate, Object... parameters) throws SQLException {
//        try (Connection connection = DriverManager.getConnection(url, user, password);
//             PreparedStatement statement = getPreparedStatement(queryTemplate, connection, Arrays.asList(parameters));
//             ResultSet resultSet = statement.executeQuery()) {
//            return QueryResult.fromResultSet(resultSet);
//        }
//    }
//
//    private PreparedStatement getPreparedStatement(String queryTemplate, Connection connection, List<Object> parameters)
//            throws SQLException {
//        PreparedStatement statement = connection.prepareStatement(queryTemplate);
//        for (int index = 0; index < parameters.size(); index++) {
//            Object parameterValue = parameters.get(index);
//            statement.setObject(index + 1, parameterValue);
//        }
//        return statement;
//    }
//
//    public static String getPegaDBUrl() {
//        String environment = System.getProperty("environment");
//        String pegaDBURL = TestConfigHelper.get().getTestConfigItem("pegaDBURL");
//        String dbServiceName;
//        switch (environment) {
//            case "aqa":
//                dbServiceName = TestConfigHelper.get().getTestConfigItem("aqaDbServiceName");
//                break;
//            case "qa":
//                dbServiceName = TestConfigHelper.get().getTestConfigItem("qaDbServiceName");
//                break;
//            case "qa1":
//                dbServiceName = TestConfigHelper.get().getTestConfigItem("qa1DbServiceName");
//                break;
//            case "test":
//                dbServiceName = TestConfigHelper.get().getTestConfigItem("testDBServiceName");
//                break;
//            default:
//                throw new UnsupportedOperationException("No db service name present on the config file for this "
//                        + environment + "environment");
//        }
//        return pegaDBURL.replace("{{DB_SERVICE_NAME}}", dbServiceName);
//
//    }
//
//    public void waitUntilRefferalRecordExistsInDB(String queryTemplate, Object... parameters) throws SQLException {
//        try
//        {
//            Connection connection = DriverManager.getConnection(url, user, password);
//             PreparedStatement statement = getPreparedStatement(queryTemplate, connection, Arrays.asList(parameters));
//             ResultSet resultSet = statement.executeQuery();
//             boolean recordFound = false;
//             int i= 0;
//             while(!resultSet.next())
//             {
//                 Thread.sleep(60000);
//                 resultSet = statement.executeQuery();
//                 if(i>10)
//                 {
//                     throw new RuntimeException("Refferel has not reached CFEMS After 10 mins as well");
//                 }
//             }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//}
