//package uk.gov.dwp.utils.field;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.Keys;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.FindBy;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import uk.gov.dwp.data.DateTimeConstants;
//import uk.gov.dwp.datatypes.EventDate;
//import uk.gov.dwp.datatypes.FieldType;
//import uk.gov.dwp.datatypes.Period;
//import uk.gov.dwp.pageobjects.PegaPage;
//import uk.gov.dwp.webdriver.configuration.TestConfigHelper;
//import uk.gov.dwp.webdriver.utils.SelectBoxUtils;
//import uk.gov.dwp.webdriver.utils.WindowUtils;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Field;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.stream.Collectors;
//
//import static uk.gov.dwp.webdriver.utils.ClearUtils.tolerantClear;
//import static uk.gov.dwp.webdriver.utils.ClickUtils.tolerantClick;
//import static uk.gov.dwp.webdriver.utils.FindByUtils.multipleLocatorMatchGetDisplayed;
//import static uk.gov.dwp.webdriver.utils.GetTextUtils.tolerantGetText;
//import static uk.gov.dwp.webdriver.utils.RadioButtonUtils.tolerantSelectByLabel;
//import static uk.gov.dwp.webdriver.utils.SelectBoxUtils.tolerantItemByIndex;
//import static uk.gov.dwp.webdriver.utils.SendKeysUtils.tolerantType;
//
//public class PageField extends PegaPage {
//
//    private static final String INPUT_TEXT = "Michael";
//    private List<String> errorMessages;
//    private List<Field> concernedFields = new ArrayList<>();
//    private Stack<Field> traversedFields = new Stack<>();
//    private Map<String, List<String>> pageErrorsForDynamicFields = new HashMap<>();
//    private List<String> errorMessagesForDynamicFields = new ArrayList<>();
//    @FindBy(xpath = "//*[@id='PegaRULESErrorFlag' or @style='color:#d4351c;']")
//    private List<WebElement> errorMessageText;
//
//
//    public PageField(WebDriver webDriver) {
//        super(webDriver);
//    }
//
//    public <T extends PegaPage> void tryToEvaluateFields(ErrorMessageStore errorMessageStore, T page) {
//        if (TestConfigHelper.get().getTestConfigItem("requiredPageValidations").equals("true")) {
//            errorMessageStore.setErrorMessagesForMandatoryFields(validateBlankEntries(page));
//            errorMessageStore.setErrorMessagesForDynamicFields(validateDynamicFields(page));
////            errorMessageStore.setErrorMessagesForDateTimeFields(validateDateTimeFields(page));
//            errorMessageStore.setErrorMessagesForDateTimeFields(validateFromToDateTimeFields(page));
//            errorMessageStore.setErrorMessagesForDateTimeFields(validateDateTimeFieldsForPastAndFutureDates(page));
//  //          errorMessageStore.setErrorMessagesForMonetaryFields(validateMonetaryFields(page));
//        }
//
//    }
//
//    private <T extends PegaPage> Optional<Map<String, List<String>>> validateDynamicFields(T page) {
//
//        concernedFields.clear();
//        traversedFields.clear();
//        pageErrorsForDynamicFields.clear();
//        errorMessagesForDynamicFields.clear();
//
//        List<Field> listOfAllDynamicFields = Arrays.stream(page.getClass().getDeclaredFields())
//                .filter(field -> field.isAnnotationPresent(EvaluateDynamicField.class))
//                .collect(Collectors.toList());
//        CopyOnWriteArrayList<Field> allDynamicFields = new CopyOnWriteArrayList<>(listOfAllDynamicFields);
//
//        if (!allDynamicFields.isEmpty()) {
//            traverseRecursively(page, allDynamicFields);
//            resetValuesInDynamicFields(page, FieldType.TRIGGER_DYNAMIC_FIELD);
//            pageErrorsForDynamicFields.put(page.getClass().getName(), errorMessagesForDynamicFields);
//        }
//        return Optional.ofNullable(pageErrorsForDynamicFields);
//
//    }
//
//    private <T extends PegaPage> Optional<Field> findTriggerDynamicFieldFromEvaluateField(T page) {
//
//        return List.of(page.getClass().getDeclaredFields())
//                .stream().filter(field -> !(List.of(field.getDeclaredAnnotations())
//                        .stream().filter(e -> e.annotationType().equals(uk.gov.dwp.utils.field.EvaluateField.class))
//                        .collect(Collectors.toList()).isEmpty()))
//                .filter(field -> !(List.of(field.getAnnotationsByType(EvaluateField.class))
//                        .stream()
//                        .filter(e -> e.ofType().equals(FieldType.TRIGGER_DYNAMIC_FIELD))
//                        .collect(Collectors.toList()).isEmpty())
//                ).findFirst();
//
//    }
//
//    private <T extends PegaPage> Optional<Field> findTriggerDynamicFieldFromEvaluateFields(T page) {
//
//        return List.of(page.getClass().getDeclaredFields())
//                .stream().filter(field -> !(List.of(field.getDeclaredAnnotations())
//                        .stream().filter(e -> e.annotationType().equals(uk.gov.dwp.utils.field.EvaluateFields.class))
//                        .collect(Collectors.toList()).isEmpty()))
//                .filter(field -> !(List.of(field.getAnnotationsByType(EvaluateFields.class))
//                        .stream()
//                        .filter(e -> !(List.of(e.value()).stream().filter(a -> a.ofType().equals(FieldType.TRIGGER_DYNAMIC_FIELD))
//                                .collect(Collectors.toList()).isEmpty()))
//                        .collect(Collectors.toList()).isEmpty())
//                ).findFirst();
//
//    }
//
//    private <T extends PegaPage> void traverseRecursively(T page, CopyOnWriteArrayList<Field> fieldsToTraverse) {
//        Field currentRoot;
//        for (Field field : fieldsToTraverse) {
//            field.setAccessible(true);
//            currentRoot = getTriggerField(page, field.getAnnotation(EvaluateDynamicField.class).triggeredBy());
//            currentRoot.setAccessible(true);
//            takeActionOnField(page, currentRoot, field);
//            concernedFields.add(field);
//            errorMessagesForDynamicFields.addAll(submitFormAndGetRelativeErrorMessages(page).orElseThrow());
//            concernedFields.clear();
//        }
//    }
//
//    private <T extends PegaPage> void takeActionOnField(T page, Field currentRootNode, Field currentChildNode) {
//
//        if (currentRootNode.getType().getTypeName().equals("java.util.List")) {
//            List<WebElement> dynamicField = getWebElements(page, currentRootNode);
//            if (currentRootNode.getAnnotation(FindBy.class).css().contains("select")) {
//                selectBoxByVisibleText(dynamicField.get(0), currentChildNode.getAnnotation(EvaluateDynamicField.class).withValue());
//            } else {
//                WindowUtils.scrollIntoView(webDriver, dynamicField.get(0));
//                selectRadioBtByLabel(dynamicField, currentChildNode.getAnnotation(EvaluateDynamicField.class).withValue());
//            }
//        } else if (currentRootNode.getAnnotation(FindBy.class).css().contains("select")) {
//            WebElement dynamicField = getWebElement(page, currentRootNode);
//            selectBoxByVisibleText(dynamicField, currentChildNode.getAnnotation(EvaluateDynamicField.class).withValue());
//        } else if (currentRootNode.getAnnotation(FindBy.class).css().contains("label")) {
//            WebElement dynamicField = getWebElement(page, currentRootNode);
//            click(dynamicField);
//        } else if (currentRootNode.getAnnotation(FindBy.class).css().contains("input")) {
//            WebElement dynamicField = getWebElement(page, currentRootNode);
//            clear(dynamicField);
//            sendText(dynamicField, currentChildNode.getAnnotation(EvaluateDynamicField.class).withValue());
//        } else if (currentRootNode.getAnnotation(FindBy.class).xpath().contains("input")) {
//            WebElement dynamicField = getWebElement(page, currentRootNode);
//            clear(dynamicField);
//            clear(dynamicField);
//            sendText(dynamicField, currentChildNode.getAnnotation(EvaluateDynamicField.class).withValue());
//        }
//    }
//
//    private <T extends PegaPage> Optional<Map<String, List<String>>> validateBlankEntries(T page) {
//
//        Map<String, List<String>> pageErrors = new HashMap<>();
//        pageErrors.put(page.getClass().getName(), submitFormAndGetErrorMessages(page).orElseThrow());
//        return Optional.ofNullable(pageErrors);
//
//    }
//
//    private <T extends PegaPage> Optional<Map<String, List<String>>> validateMonetaryFields(T page) {
//
//        AtomicBoolean isTargetFieldFound = new AtomicBoolean(false);
//        Map<String, List<String>> pageErrors = new HashMap<>();
//        concernedFields.clear();
//
//        Arrays.stream(page.getClass().getDeclaredFields())
//                .filter(field -> field.isAnnotationPresent(EvaluateField.class))
//                .filter(field -> field.getAnnotation(EvaluateField.class).ofType()
//                        .equals(FieldType.MONETARY_FIELD))
//                .forEach(field -> {
//                    concernedFields.add(field);
//                    isTargetFieldFound.set(true);
//                    field.setAccessible(true);
//                    if (field.getType().getTypeName().equals("java.util.List")) {
//                        getWebElements(page, field)
//                                .stream()
//                                .forEach(monetaryField -> {
//                                    click(monetaryField);
//                                    sendText(monetaryField, INPUT_TEXT);
//                                });
//                    } else {
//                        WebElement monetaryField = getWebElement(page, field);
//                        click(monetaryField);
//                        sendText(monetaryField, INPUT_TEXT);
//                    }
//                });
//        if (isTargetFieldFound.get()) {
//            pageErrors.put(page.getClass().getName(), submitFormAndGetRelativeErrorMessages(page).orElseThrow());
//            clearValuesInFields(page, FieldType.MONETARY_FIELD);
//        }
//        return Optional.ofNullable(pageErrors);
//
//    }
//
//    private <T extends PegaPage> Optional<Map<String, List<String>>> validateDateTimeFields(T page) {
//
//        AtomicBoolean isTargetFieldFound = new AtomicBoolean(false);
//        Map<String, List<String>> pageErrors = new HashMap<>();
//        concernedFields.clear();
//
//        Arrays.stream(page.getClass().getDeclaredFields())
//                .filter(field -> field.isAnnotationPresent(EvaluateField.class))
//                .filter(field -> field.getAnnotation(EvaluateField.class).ofType()
//                        .equals(FieldType.DATE_TIME_FIELD))
//                .forEach(field -> {
//                    concernedFields.add(field);
//                    isTargetFieldFound.set(true);
//                    field.setAccessible(true);
//                    if (field.getType().getTypeName().equals("java.util.List")) {
//                        getWebElements(page, field)
//                                .stream()
//                                .forEach(dateTimeField -> {
//                                    click(dateTimeField);
//                                    sendText(dateTimeField, INPUT_TEXT);
//                                });
//                    } else {
//                        WebElement dateTimeField = getWebElement(page, field);
//                        click(dateTimeField);
//                        sendText(dateTimeField, INPUT_TEXT);
//                        click(dateTimeField);
//                    }
//                });
//        if (isTargetFieldFound.get()) {
//            pageErrors.put(page.getClass().getName(), submitFormAndGetRelativeErrorMessages(page).orElseThrow());
//            clearValuesInFields(page, FieldType.DATE_TIME_FIELD);
//        }
//        return Optional.ofNullable(pageErrors);
//
//    }
//
//    private <T extends PegaPage> Optional<Map<String, List<String>>> validateFromToDateTimeFields(T page) {
//
//        AtomicBoolean isTargetFieldFound = new AtomicBoolean(false);
//        Map<String, List<String>> pageErrors = new HashMap<>();
//        concernedFields.clear();
//        AtomicBoolean isFromDateFuture = new AtomicBoolean(false);
//
//        Arrays.stream(page.getClass().getDeclaredFields())
//                .filter(field -> field.isAnnotationPresent(EvaluateField.class))
//                .filter(field -> field.getAnnotation(EvaluateField.class).ofType()
//                        .equals(FieldType.DATE_TIME_FIELD) && field.getAnnotation(EvaluateField.class).ofPeriod()
//                        .equals(Period.FROM_DATE_FIELD) || field.getAnnotation(EvaluateField.class).ofType()
//                        .equals(FieldType.DATE_TIME_FIELD) && field.getAnnotation(EvaluateField.class).ofPeriod()
//                        .equals(Period.TO_DATE_FIELD))
//                .forEach(field -> {
//                    if (field.getAnnotation(EvaluateField.class).ofPeriod().equals(Period.FROM_DATE_FIELD)) {
//                        concernedFields.add(field);
//                        if (field.getAnnotation(EvaluateField.class).withValue().equals(EventDate.FUTURE_DATE)) {
//                            isFromDateFuture.set(true);
//                        }
//                    }
//                    isTargetFieldFound.set(true);
//                    field.setAccessible(true);
//                    if (field.getType().getTypeName().equals("java.util.List")) {
//                        getWebElements(page, field)
//                                .stream()
//                                .forEach(dateTimeField -> {
//                                    enterDateAndContinue(isFromDateFuture.get(), field, dateTimeField);
//                                });
//                    } else {
//                        WebElement dateTimeField = getWebElement(page, field);
//                        enterDateAndContinue(isFromDateFuture.get(), field, dateTimeField);
//                    }
//                });
//        if (isTargetFieldFound.get()) {
//            pageErrors.put(page.getClass().getName(), submitFormAndGetRelativeErrorMessages(page).orElseThrow());
//            clearValuesInFields(page, FieldType.DATE_TIME_FIELD);
//        }
//        return Optional.ofNullable(pageErrors);
//
//    }
//
//    private <T extends PegaPage> Optional<Map<String, List<String>>> validateDateTimeFieldsForPastAndFutureDates(T page) {
//
//        Map<String, List<String>> pageErrors = new HashMap<>();
//        concernedFields.clear();
//
//        Arrays.stream(page.getClass().getDeclaredFields())
//                .filter(field -> field.isAnnotationPresent(EvaluateField.class))
//                .filter(field -> field.getAnnotation(EvaluateField.class).ofType()
//                        .equals(FieldType.DATE_TIME_FIELD))
//                .filter(field -> field.getAnnotation(EvaluateField.class).ofPeriod()
//                        .equals(Period.FROM_DATE_FIELD) || field.getAnnotation(EvaluateField.class).ofPeriod()
//                        .equals(Period.TO_DATE_FIELD) || field.getAnnotation(EvaluateField.class).ofPeriod()
//                        .equals(Period.DEFAULT))
//                .forEach(field -> {
//                    field.setAccessible(true);
//                    if (field.getType().getTypeName().equals("java.util.List")) {
//                        getWebElements(page, field)
//                                .stream()
//                                .forEach(dateTimeField -> {
//                                    enterAppropriateDateAndSubmit(page, field, dateTimeField, pageErrors);
//                                });
//                    } else {
//                        WebElement dateTimeField = getWebElement(page, field);
//                        enterAppropriateDateAndSubmit(page, field, dateTimeField, pageErrors);
//                    }
//                });
//        return Optional.ofNullable(pageErrors);
//
//    }
//
//    private <T extends PegaPage> void enterAppropriateDateAndSubmit(T page, Field field, WebElement dateTimeField, Map<String, List<String>> pageErrors) {
//        concernedFields.add(field);
//        if (field.getAnnotation(EvaluateField.class).withValue().equals(EventDate.FUTURE_DATE)) {
//            String intendedDate = LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern(DateTimeConstants.ALTERNATIVE_DATE_FORMAT));
//            submitDateAndGetTheErrorMessage(page, dateTimeField, pageErrors, intendedDate);
//        } else if (field.getAnnotation(EvaluateField.class).withValue().equals(EventDate.PAST_DATE)) {
//            String intendedDate = LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern(DateTimeConstants.ALTERNATIVE_DATE_FORMAT));
//            submitDateAndGetTheErrorMessage(page, dateTimeField, pageErrors, intendedDate);
//        }
//    }
//
//    private <T extends PegaPage> void submitDateAndGetTheErrorMessage(T page, WebElement dateTimeField, Map<String, List<String>> pageErrors, String intendedDate) {
//        sendText(dateTimeField, intendedDate);
//        if (pageErrors.containsKey(page.getClass().getName()))
//            pageErrors.get(page.getClass().getName()).addAll(submitFormAndGetRelativeErrorMessages(page).orElseThrow());
//        else
//            pageErrors.put(page.getClass().getName(), submitFormAndGetRelativeErrorMessages(page).orElseThrow());
//        clearValuesInFields(page, FieldType.DATE_TIME_FIELD);
//        concernedFields.clear();
//
//    }
//
//
//    private <T extends PegaPage> Optional<List<String>> submitFormAndGetRelativeErrorMessages(T page) {
//
//        errorMessages = new ArrayList<>();
//        Arrays.stream(page.getClass().getDeclaredFields())
//                .filter(field -> field.isAnnotationPresent(EvaluateField.class))
//                .filter(field -> field.getAnnotation(EvaluateField.class).ofType()
//                        .equals(FieldType.SUBMIT_FIELD))
//                .forEach(field -> {
//                    field.setAccessible(true);
//                    if (field.getType().getName().equals("org.openqa.selenium.By")) {
//                        By submitButton = getBy(page, field);
//                        click(multipleLocatorMatchGetDisplayed(webDriver, submitButton));
//
//                    } else {
//                        WebElement submitButton = getWebElement(page, field);
//                        click(submitButton);
//                    }
//                });
//
//        concernedFields.
//                forEach(field -> {
//                    field.setAccessible(true);
//                    if (!field.getAnnotation(FindBy.class).xpath().contains("//label")) {
//                        if (field.getType().getTypeName().equals("java.util.List")) {
//                            getWebElements(page, field)
//                                    .stream()
//                                    .forEach(interestedField -> {
//                                        String dataTestId = List.of(interestedField.getAttribute("outerHTML")
//                                                .split(" ")).stream()
//                                                .filter(e -> e.contains("data-test-id")).findFirst().orElseThrow();
//                                        String errorMessagePattern = "//*[@".concat(dataTestId)
//                                                .concat("]").concat("/..//*[@id='PegaRULESErrorFlag']");
//                                        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(errorMessagePattern)))
//                                                .forEach((WebElement errorText) -> {
//                                                    if (errorText.isDisplayed()) {
//                                                        errorMessages.add(getText(errorText));
//                                                    }
//                                                });
//                                    });
//                        } else {
//                            WebElement dateTimeField = getWebElement(page, field);
//                            String dataTestId = List.of(dateTimeField.getAttribute("outerHTML")
//                                    .split(" ")).stream()
//                                    .filter(e -> e.contains("data-test-id")).findFirst().orElseThrow();
//                            String errorMessagePattern = "//*[@".concat(dataTestId)
//                                    .concat("]").concat("/..//*[@id='PegaRULESErrorFlag']").concat("|").concat("//*[@").concat(dataTestId)
//                                    .concat("]").concat("/../..//*[@id='PegaRULESErrorFlag']");
//
//                            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(errorMessagePattern)))
//                                    .forEach((WebElement errorText) -> {
//                                        if (errorText.isDisplayed()) {
//                                            errorMessages.add(getText(errorText));
//                                        }
//                                    });
//                        }
//                    } else {
//                        errorMessages.add(getText(webDriver
//                                .findElement(By.xpath(field.getAnnotation(FindBy.class).xpath().split("//label")[0]
//                                        .concat("//following-sibling::div/span[@id='PegaRULESErrorFlag']")))));
//                    }
//                });
//        return Optional.ofNullable(errorMessages);
//
//    }
//
//    private void enterDateAndContinue(boolean isFutureDate, Field field, WebElement dateTimeField) {
//        click(dateTimeField);
//        if (isFutureDate) {
//            if (field.getAnnotation(EvaluateField.class).ofPeriod().equals(Period.FROM_DATE_FIELD)) {
//                sendText(dateTimeField, LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern(DateTimeConstants.ALTERNATIVE_DATE_FORMAT)));
//            } else {
//                sendText(dateTimeField, LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern(DateTimeConstants.ALTERNATIVE_DATE_FORMAT)));
//            }
//        } else {
//            if (field.getAnnotation(EvaluateField.class).ofPeriod().equals(Period.FROM_DATE_FIELD)) {
//                sendText(dateTimeField, LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern(DateTimeConstants.ALTERNATIVE_DATE_FORMAT)));
//            } else {
//                sendText(dateTimeField, LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern(DateTimeConstants.ALTERNATIVE_DATE_FORMAT)));
//            }
//        }
//    }
//
//    private <T extends PegaPage> Optional<List<String>> submitFormAndGetErrorMessages(T page) {
//
//        errorMessages = new ArrayList<>();
//        Arrays.stream(page.getClass().getDeclaredFields())
//                .filter(field -> field.isAnnotationPresent(EvaluateField.class))
//                .filter(field -> field.getAnnotation(EvaluateField.class).ofType()
//                        .equals(FieldType.SUBMIT_FIELD))
//                .forEach(field -> {
//                    field.setAccessible(true);
//                    if (field.getType().getName().equals("org.openqa.selenium.By")) {
//                        By submitButton = getBy(page, field);
//                        click(multipleLocatorMatchGetDisplayed(webDriver, submitButton));
//
//                    } else {
//                        WebElement submitButton = getWebElement(page, field);
//                        click(submitButton);
//                    }
//                    errorMessageText.forEach((WebElement errorText) -> {
//                        if (errorText.isDisplayed()) {
//                            errorMessages.add(getText(errorText));
//                        }
//                    });
//                });
//        return Optional.ofNullable(errorMessages);
//
//    }
//
//    private <T extends PegaPage> void clearValuesInFields(T page, FieldType fieldType) {
//
//        Arrays.stream(page.getClass().getDeclaredFields())
//                .filter(field -> field.isAnnotationPresent(EvaluateField.class))
//                .filter(field -> field.getAnnotation(EvaluateField.class).ofType()
//                        .equals(fieldType))
//                .forEach(field -> {
//                    field.setAccessible(true);
//                    if (field.getType().getTypeName().equals("java.util.List")) {
//                        getWebElements(page, field)
//                                .stream()
//                                .forEach(eachField -> {
//                                    clear(wait.until(ExpectedConditions.visibilityOf(eachField)));
//                                });
//                    } else {
//                        clear(wait.until(ExpectedConditions.visibilityOf(getWebElement(page, field))));
//                    }
//                });
//    }
//
//    private <T extends PegaPage> boolean isAnnotatedFieldPresent(T page, Field field) {
//
//        boolean isPresent = false;
//        for (Annotation annotation : field.getDeclaredAnnotations()) {
//            if (annotation.annotationType().equals(uk.gov.dwp.utils.field.EvaluateField.class)) {
//                Optional<Field> triggerDynamicFieldUnAssigned = findTriggerDynamicFieldFromEvaluateField(page);
//                if (triggerDynamicFieldUnAssigned.isPresent())
//                    isPresent = true;
//            } else if (annotation.annotationType().equals(uk.gov.dwp.utils.field.EvaluateFields.class)) {
//                Optional<Field> triggerDynamicFieldUnAssigned = findTriggerDynamicFieldFromEvaluateFields(page);
//                if (triggerDynamicFieldUnAssigned.isPresent())
//                    isPresent = true;
//            }
//        }
//        return isPresent;
//
//    }
//
//    private <T extends PegaPage> void resetValuesInDynamicFields(T page, FieldType fieldType) {
//
//        Arrays.stream(page.getClass().getDeclaredFields())
//                .filter(field -> isAnnotatedFieldPresent(page, field))
//                .forEach(field -> {
//                    field.setAccessible(true);
//                    if (!field.getType().getTypeName().equals("java.util.List")) {
//                        if (!field.getType().getTypeName().equals("org.openqa.selenium.By")) {
//                            if (field.getAnnotation(FindBy.class).css().contains("select"))
//                                selectByIndex(getWebElement(page, field), 1);
//                            else if (field.getAnnotation(FindBy.class).css().contains("input") ||
//                                    field.getAnnotation(FindBy.class).xpath().contains("input"))
//                                clear(getWebElement(page, field));
//                            else if (field.getAnnotation(FindBy.class).css().contains("label") &&
//                                    !field.getType().getTypeName().equals("java.util.List")) {
//                                WebElement webElement = getWebElement(page, field);
//                                if (webElement.isSelected())
//                                    click(webElement);
//                            }
//                        }
//                    }
//                });
//    }
//
//    private String getText(WebElement errorText) {
//
//        String text = null;
//        try {
//            text = tolerantGetText(errorText);
//        } catch (Throwable throwable) {
//            throw new RuntimeException(throwable);
//        }
//        return text;
//
//    }
//
//    private void sendText(WebElement webElement, String inputText) {
//
//        try {
//            tolerantType(webElement, inputText);
//            webElement.sendKeys(Keys.TAB);
//        } catch (Throwable throwable) {
//            throw new RuntimeException(throwable);
//        }
//
//    }
//
//    private <T> WebElement getWebElement(T page, Field field) {
//
//        WebElement webElement = null;
//        try {
//            webElement = (WebElement) field.get(page);
//        } catch (IllegalAccessException illegalAccessException) {
//            throw new RuntimeException(illegalAccessException);
//        }
//        return webElement;
//
//    }
//
//    private <T> By getBy(T page, Field field) {
//
//        By by = null;
//        try {
//            by = (By) field.get(page);
//        } catch (IllegalAccessException illegalAccessException) {
//            throw new RuntimeException(illegalAccessException);
//        }
//        return by;
//
//    }
//
//    private <T> List<WebElement> getWebElements(T page, Field field) {
//
//        List<WebElement> webElements = null;
//        try {
//            webElements = (List<WebElement>) field.get(page);
//        } catch (IllegalAccessException illegalAccessException) {
//            throw new RuntimeException(illegalAccessException);
//        }
//        return webElements;
//
//    }
//
//    private void clear(WebElement webElement) {
//
//        try {
//            tolerantClear(webElement);
//        } catch (Throwable throwable) {
//            throw new RuntimeException(throwable);
//        }
//
//    }
//
//    private void click(WebElement webElement) {
//
//        try {
//            WindowUtils.scrollIntoView(webDriver, webElement);
//            tolerantClick(webElement);
//        } catch (Throwable throwable) {
//            throw new RuntimeException(throwable);
//        }
//
//    }
//
//    private void selectBoxByVisibleText(WebElement webElement, String text) {
//
//        try {
//            SelectBoxUtils.tolerantItemByVisibleText(webElement, text);
//        } catch (Throwable throwable) {
//            throw new RuntimeException(throwable);
//        }
//
//    }
//
//    private void selectRadioBtByLabel(List<WebElement> webElement, String text) {
//
//        try {
//            tolerantSelectByLabel(webElement, text);
//        } catch (Throwable throwable) {
//            throw new RuntimeException(throwable);
//        }
//
//    }
//
//    private void selectByIndex(WebElement webElement, int index) {
//
//        try {
//            tolerantItemByIndex(webElement, index);
//        } catch (Throwable throwable) {
//            throw new RuntimeException(throwable);
//        }
//
//    }
//
//    private <T extends PegaPage> Field getTriggerField(T page, String fieldName) {
//        for (Field field : page.getClass().getDeclaredFields()) {
//            if (field.getName().equals(fieldName)) {
//                return field;
//            }
//        }
//        return null;
//    }
//
//}
