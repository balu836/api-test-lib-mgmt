//package uk.gov.dwp.utils.database;
//
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//import java.util.*;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static uk.gov.dwp.utils.LambdaUtil.toHandledLambda;
//
///**
// * Holds the results obtained from executing queries against a database
// */
//public class QueryResult {
//    private Map<String, List<Object>> data;
//
//    public QueryResult(Map<String, List<Object>> data) {
//        this.data = data;
//    }
//
//    /**
//     * Creates an instance of QueryResult from a java.sql.ResultSet instance
//     *
//     * @param resultSet the ResultSet instance obtained from executing a query against a database
//     * @return a QueryResult instance corresponding to the input ResultSet
//     * @throws SQLException
//     */
//    static QueryResult fromResultSet(ResultSet resultSet) throws SQLException {
//        ResultSetMetaData metadata = resultSet.getMetaData();
//        int columnCount = metadata.getColumnCount();
//        Map<String, List<Object>> resultData = new HashMap<>();
//
//        while (resultSet.next()) {
//            for (int i = 1; i <= columnCount; i++) {
//                String columnName = metadata.getColumnName(i);
//                List<Object> columnData = resultData.getOrDefault(columnName, new ArrayList<>());
//                columnData.add(resultSet.getObject(columnName));
//                resultData.put(columnName, columnData);
//            }
//        }
//
//        return new QueryResult(resultData);
//    }
//
//    /**
//     * Indicates if the QueryResult is empty
//     *
//     * @return boolean indicating if the QueryResult is empty
//     */
//    public boolean isEmpty() {
//        return data.keySet().stream()
//                .findFirst()
//                .map(data::get)
//                .map(List::isEmpty)
//                .orElse(true);
//    }
//
//    /**
//     * Returns a single column from the QueryResult based on it's name.
//     *
//     * @param columnName the name of the column to retrieve
//     * @param <T>        The type of data stored in the column
//     * @return A Column instance corresponding to the requested column name.
//     * @throws Exception if the column name required does not exist.
//     */
//    public <T> Column<T> getColumn(String columnName) throws Exception {
//        return Optional.ofNullable(data.get(columnName))
//                .map(column -> Column.<T>fromList(columnName, column))
//                .orElseThrow(() -> new Exception("Column '" + columnName + "' not found in the resultset."));
//    }
//
//    /**
//     * Returns the first element of the first row of the QueryResult
//     *
//     * @param <T> the type of the data held in the first column of the QueryResult
//     * @return the first element of the first row of the QueryResult
//     * @throws Exception if the QueryResult is empty
//     */
//    public <T> T getFirst() throws Exception {
//        return data.keySet().stream()
//                .findFirst()
//                .map(toHandledLambda(this::<T>getColumn))
//                .map(List::stream)
//                .flatMap(Stream::findFirst)
//                .orElseThrow(() -> new Exception("Expected at least one record to be returned but found none."));
//    }
//
//    /**
//     * returns a list of all the column names of the QueryResult
//     *
//     * @return a list of strings corresponding to the column names in the QueryResult
//     */
//    public List<String> getColumnNames() {
//        return new ArrayList<>(data.keySet());
//    }
//
//    /**
//     * Applies a transformer function to all values in the QueryResult that satisfy a conditional function
//     *
//     * @param conditional the conditional function used to determine if the transformer is applied
//     * @param mapFunction the transformer function
//     * @return a new QueryResult with the values of the transformer function applied.
//     */
//    public QueryResult mapIf(Function<Object, Boolean> conditional, Function<Object, Object> mapFunction) {
//        data.keySet().stream().forEach(columnName ->
//                data.put(columnName, data.get(columnName).stream().map(value -> {
//                    if (conditional.apply(value)) {
//                        return mapFunction.apply(value);
//                    } else {
//                        return value;
//                    }
//                }).collect(Collectors.toList())));
//        return this;
//    }
//
//    @Override
//    public String toString() {
//        return data.keySet().stream()
//                .map((columnName) -> Column.fromList(columnName, data.get(columnName)).toString())
//                .collect(Collectors.joining("\r\n"));
//    }
//
//    /**
//     * A sub-class of List that also holds the name of the result column
//     *
//     * @param <T>
//     */
//    public static class Column<T> extends ArrayList<T> {
//        private String name;
//        private List<?> data;
//
//        private Column(String name, List<T> list) {
//            super(list);
//            this.name = name;
//            this.data = list;
//        }
//
//        /**
//         * Generates a Column instance from a list of objects and a column name
//         *
//         * @param name - the column name
//         * @param list - a list of objects corresponding to the column values
//         * @param <T>  - the type of objects the column is holding
//         * @return a Column instance
//         */
//        public static <T> Column<T> fromList(String name, List<Object> list) {
//            List<T> myList = list.stream()
//                    .map((value) -> (T) value)
//                    .collect(Collectors.toList());
//            return new Column<>(name, myList);
//        }
//
//        @Override
//        public String toString() {
//            return "Column: " + name + " -> " + String.join(", ", toStringColumn());
//        }
//
//        /**
//         * Converts a column of any type into a column of strings by applying the toString function on all values
//         *
//         * @return A Column of String values.
//         */
//        public Column<String> toStringColumn() {
//            return new Column<>(name, data.stream().map(Objects::toString).collect(Collectors.toList()));
//        }
//
//        /**
//         * Returns the name of the Column
//         *
//         * @return a string corresponding to the name of the Column
//         */
//        public String getName() {
//            return name;
//        }
//    }
//
//    public String getColumnValue(String columnName) throws Exception {
//        return getColumn(columnName).get(0).toString();
//    }
//}
