package uk.gov.dwp.utils.casehistory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Replaces all placeholders with the appropriate values from a list of data providers that have been annotated.
 */
public class CaseHistoryResolver {

    /**
     * Replaces all placeholders with the appropriate values from a list of data providers that have been annotated.
     *
     * @param unresolvedCaseHistory the input case history with placeholders
     * @param dataProviders         a collection of objects that have their instance variables and methods annotated
     * @param saveToFile            a function for saving the case placeholders names and values to file.
     * @return a case history with all the placeholders replaced with the right values
     * @throws Throwable
     */
    public static List<String> resolve(List<String> unresolvedCaseHistory,
                                       List<Tuple<String, Object>> dataProviders,
                                       BiConsumer<List<String>, String> saveToFile) throws Throwable {
        List<PlaceHolder> placeHolders = resolvePlaceHolders(dataProviders);

        saveToFile.accept(placeHolders.stream().map(PlaceHolder::toString).collect(Collectors.toList()), "placeholders");
        checkForDuplicatePlaceHolders(placeHolders);

        List<String> resolvedCaseHistory = applyPlaceHolders(unresolvedCaseHistory, placeHolders);
        checkForUnresolvedPlaceholders(resolvedCaseHistory);

        return resolvedCaseHistory;
    }

    /**
     * Replaces all placeholders with the appropriate values from a list of data providers that have been annotated.
     *
     * @param unresolvedCaseHistory the input case history with placeholders
     * @param dataProviders         a collection of objects that have their instance variables and methods annotated
     * @return a case history with all the placeholders replaced with the right values
     * @throws Throwable
     */
    public static List<String> resolve(List<String> unresolvedCaseHistory,
                                       List<Tuple<String, Object>> dataProviders) throws Throwable {
        return resolve(unresolvedCaseHistory, dataProviders, CaseHistoryUtil::saveToFile);
    }

    private static void checkForUnresolvedPlaceholders(List<String> caseHistory) {
        String unresolvedEntries = caseHistory.stream()
                .filter((caseHistoryEntry) -> caseHistoryEntry.matches(".*\\{\\{[a-zA-Z_]+\\}\\}.*"))
                .collect(Collectors.joining("\r\n"));

        if (!unresolvedEntries.isEmpty()) {
            throw new RuntimeException("The case history template still contains unresolved placeholders {{[a-zA-Z]+}}: " +
                    "Affected lines:\r\n" + unresolvedEntries);
        }
    }

    private static List<String> applyPlaceHolders(List<String> unresolvedCaseHistory, List<PlaceHolder> placeHolders) {
        UnaryOperator<String> applyPlaceHolderToSingleEntry = (caseHistoryEntry) -> placeHolders.stream()
                .reduce(caseHistoryEntry,
                        (currentEntry, placeHolder) ->
                                currentEntry.replaceAll("\\{\\{" + placeHolder.getKey() + "\\}\\}", placeHolder.getValue()),
                        String::concat);

        return unresolvedCaseHistory.stream()
                .map(applyPlaceHolderToSingleEntry)
                .collect(Collectors.toList());
    }

    private static List<PlaceHolder> resolvePlaceHolders(List<Tuple<String, Object>> dataProviders) throws Throwable {
        List<PlaceHolder> placeHoldersAndValues = new ArrayList<>();

        for (Tuple<String, Object> dataProvider : dataProviders) {
            Class dataProviderClass = dataProvider.getValue().getClass();

            // if placeholder is a string, just
            if (dataProvider.getValue() instanceof String) {
                placeHoldersAndValues.add(new PlaceHolder(dataProviderClass.getSimpleName(), dataProvider.getKey(), dataProvider.getValue().toString()));
                continue;
            }

            // get all placeholders from data provider
            placeHoldersAndValues.addAll(getPlaceHolderValues(dataProvider.getValue(), dataProvider.getKey()));

            // get all placeholders from data provider's instance variables
            for (Field field : dataProviderClass.getDeclaredFields()) {
                field.setAccessible(true);
                String methodName = toSnakeCase(field.getName());
                String prefix = Optional.ofNullable(dataProvider.getKey())
                        .map(x -> x + "_" + methodName)
                        .orElse(methodName);
                placeHoldersAndValues.addAll(getPlaceHolderValues(field.get(dataProvider.getValue()), prefix));
            }
        }

        return placeHoldersAndValues;
    }

    public static List<PlaceHolder> getPlaceHolderValues(Object dataProvider, String prefix) throws Exception {
        List<PlaceHolder> placeHoldersAndValues = new ArrayList<>();

        if (dataProvider == null) {
            return placeHoldersAndValues;
        }

        Class targetClass = dataProvider.getClass();

        // parse annotated fields
        for (Field field : targetClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(CaseHistoryField.class)) {
                field.setAccessible(true);
                Object result = field.get(dataProvider);
                if (result != null) {
                    String annotationValue = field.getAnnotation(CaseHistoryField.class).value().trim();
                    String placeHolder = annotationValue.isEmpty() ? toSnakeCase(field.getName()) : annotationValue;
                    String completePlaceholder = Optional.ofNullable(prefix)
                            .map(x -> x + "_" + placeHolder)
                            .orElse(placeHolder);
                    String sourceName = targetClass.getSimpleName() + "." + field.getName();
                    placeHoldersAndValues.add(new PlaceHolder(sourceName, completePlaceholder, result.toString()));
                }
            }
        }

        // parse annotated methods
        for (Method method : targetClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(CaseHistoryField.class)) {
                method.setAccessible(true);
                Object result = method.invoke(dataProvider);
                String methodName = method.getName()
                        .replaceFirst("get", "")
                        .replaceFirst("set", "");
                String annotationValue = method.getAnnotation(CaseHistoryField.class).value().trim();
                String placeHolder = annotationValue.isEmpty() ? toSnakeCase(methodName) : annotationValue;
                String completePlaceholder = Optional.ofNullable(prefix)
                        .map(x -> x + "_" + placeHolder)
                        .orElse(placeHolder);
                String sourceName = targetClass.getSimpleName() + "." + methodName;
                placeHoldersAndValues.add(new PlaceHolder(sourceName, completePlaceholder, result.toString()));
            }
        }

        return placeHoldersAndValues;
    }

    private static void checkForDuplicatePlaceHolders(List<PlaceHolder> placeHoldersAndValues) {
        Map<String, String> uniques = new HashMap<>();
        Map<String, Set<String>> duplicates = new HashMap<>();

        for (PlaceHolder placeHolderValue : placeHoldersAndValues) {
            String key = placeHolderValue.getKey();
            String value = placeHolderValue.getValue();

            if (uniques.containsKey(key) && !uniques.get(key).equalsIgnoreCase(value)) {
                Set<String> duplicateValues = Optional.ofNullable(duplicates.get(key)).orElse(new HashSet<>());
                duplicateValues.add(value);
                duplicateValues.add(uniques.get(key));
                duplicates.put(key, duplicateValues);
            }

            uniques.put(key, value);
        }

        if (!duplicates.isEmpty()) {
            String duplicateReport = duplicates.entrySet().stream()
                    .map((entry) -> String.format("{{%s}} has values: %s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining("\r\n"));
            String errorMessage = "Could not resolve all placeholders in the case history template file: " +
                    "\r\nThe following placeholders have multiple values: \r\n" +
                    duplicateReport + "\r\nPlease inspect annotations and resolve duplicates.";
            throw new RuntimeException(errorMessage);
        }
    }

    private static String toSnakeCase(String camelCase) {
        return camelCase.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toUpperCase();
    }
}