package uk.gov.dwp.utils.field;

import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@ToString
public class ErrorMessageStore {

    private final Map<String, List<String>> errorMessagesForMandatoryFields = new HashMap<>();
    private final Map<String, List<String>> errorMessagesForMonetaryFields = new HashMap<>();
    private final Map<String, List<String>> errorMessagesForDateTimeFields = new HashMap<>();
    private final Map<String, List<String>> errorMessagesForDynamicFields = new HashMap<>();

    public Map<String, List<String>> getErrorMessagesForDateTimeFields() {
        return errorMessagesForDateTimeFields;
    }

    public void setErrorMessagesForDateTimeFields(Optional<Map<String, List<String>>> errorMessagesForDateTimeFields) {
        setErrorMessages(errorMessagesForDateTimeFields, getErrorMessagesForDateTimeFields());
    }

    public Map getErrorMessagesForMandatoryFields() {
        return errorMessagesForMandatoryFields;
    }

    public void setErrorMessagesForMandatoryFields(Optional<Map<String, List<String>>> errorMessagesForMandatoryFields) {
        setErrorMessages(errorMessagesForMandatoryFields, getErrorMessagesForMandatoryFields());
    }

    public Map getErrorMessagesForDynamicFields() {
        return errorMessagesForDynamicFields;
    }

    public void setErrorMessagesForDynamicFields(Optional<Map<String, List<String>>> errorMessagesForDynamicFields) {
        setErrorMessages(errorMessagesForDynamicFields, getErrorMessagesForDynamicFields());
    }

    public Map getErrorMessagesForMonetaryFields() {
        return errorMessagesForMonetaryFields;
    }

    public void setErrorMessagesForMonetaryFields(Optional<Map<String, List<String>>> errorMessagesForMonetaryFields) {
        setErrorMessages(errorMessagesForMonetaryFields, getErrorMessagesForMonetaryFields());
    }

    private void setErrorMessages(Optional<Map<String, List<String>>> errorMessages, Map<String, List<String>> errorMessageStore) {
        errorMessages.ifPresent(pageErrors -> pageErrors
                .entrySet().stream()
                .forEach(entry -> {
                    if ((errorMessageStore.containsKey(entry.getKey()))) {
                        errorMessageStore
                                .get(entry.getKey()).addAll(errorMessages.get().get(entry.getKey()));
                    } else {
                        errorMessageStore.put(entry.getKey(), entry.getValue());
                    }
                }));
    }

}
