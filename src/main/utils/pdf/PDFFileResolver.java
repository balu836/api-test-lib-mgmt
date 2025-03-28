package uk.gov.dwp.utils.pdf;

import uk.gov.dwp.utils.casehistory.PlaceHolder;
import uk.gov.dwp.utils.casehistory.Tuple;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PDFFileResolver {

    public static String resolve(String pdfFileTemplate, List<Tuple<String, Object>> dataProviders) throws Throwable {
        List<PlaceHolder> placeHolders = resolvePlaceHolders(dataProviders);
        return applyPlaceHolders(pdfFileTemplate, placeHolders);
    }

    private static List<PlaceHolder> resolvePlaceHolders(List<Tuple<String, Object>> dataProviders) throws Throwable {
        List<PlaceHolder> placeHoldersAndValues = new ArrayList<>();

        for (Tuple<String, Object> dataProvider : dataProviders) {
            Class dataProviderClass = dataProvider.getValue().getClass();
            if (dataProvider.getValue() instanceof String) {
                placeHoldersAndValues.add(new PlaceHolder(dataProviderClass.getSimpleName(),
                        "\\{\\{" + dataProvider.getKey() + "\\}\\}", dataProvider.getValue().toString()));
                continue;
            }
            placeHoldersAndValues.addAll(getPlaceHolderValues(dataProvider.getValue(), dataProvider.getKey()));
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

    private static List<PlaceHolder> getPlaceHolderValues(Object dataProvider, String prefix) throws Exception {
        List<PlaceHolder> placeHoldersAndValues = new ArrayList<>();

        if (dataProvider == null) {
            return placeHoldersAndValues;
        }
        Class<?> targetClass = dataProvider.getClass();
        for (Field field : targetClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(PDFDocField.class)) {
                field.setAccessible(true);
                Object result = field.get(dataProvider);
                if (result != null) {
                    String annotationValue = field.getAnnotation(PDFDocField.class).value().trim();
                    String placeHolder = annotationValue.isEmpty() ? toSnakeCase(field.getName()) : annotationValue;
                    String completePlaceholder = Optional.ofNullable(prefix)
                            .map(x -> x + "_" + placeHolder)
                            .orElse(placeHolder);
                    String sourceName = targetClass.getSimpleName() + "." + field.getName();
                    placeHoldersAndValues.add(new PlaceHolder(sourceName, "\\{\\{" + completePlaceholder + "\\}\\}",
                            result.toString()));
                }
            }
        }
        for (Method method : targetClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PDFDocField.class)) {
                method.setAccessible(true);
                Object result = method.invoke(dataProvider);
                String methodName = method.getName()
                        .replaceFirst("get", "")
                        .replaceFirst("set", "");
                String annotationValue = method.getAnnotation(PDFDocField.class).value().trim();
                String placeHolder = annotationValue.isEmpty() ? toSnakeCase(methodName) : annotationValue;
                String completePlaceholder = Optional.ofNullable(prefix)
                        .map(x -> x + "_" + placeHolder)
                        .orElse(placeHolder);
                String sourceName = targetClass.getSimpleName() + "." + methodName;
                placeHoldersAndValues.add(new PlaceHolder(sourceName, "\\{\\{" + completePlaceholder + "\\}\\}",
                        result.toString()));
            }
        }

        return placeHoldersAndValues;
    }

    public static String toSnakeCase(String camelCase) {
        return camelCase.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toUpperCase();
    }

    private static String applyPlaceHolders(String pdfTemplate, List<PlaceHolder> placeHolders) {
        for (PlaceHolder placeHolder : placeHolders) {
            pdfTemplate = pdfTemplate.replaceAll(placeHolder.getKey(), placeHolder.getValue());
        }
        return pdfTemplate;
    }
}
