package uk.gov.dwp.utils.assertions;

import io.qameta.allure.Step;
import uk.gov.dwp.data.ExpectedErrorMessages;
import uk.gov.dwp.utils.field.ErrorMessageStore;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

public class PageFieldAssertions {

    @Step("Assert each type validation messages")
    private static void assertValidationMessages(String messageType,
                                                 Map<String, List<String>> actualPageValidationMessages,
                                                 Map<String, List<String>> expectedPageValidationMessages) {
        actualPageValidationMessages.entrySet().forEach(entry -> {
             assertThat(messageType.concat(" This Page: ").concat(entry.getKey()).concat(" is missing in expected file."),
                            expectedPageValidationMessages.containsKey(entry.getKey()));
                    assertThat(messageType.concat(" On Page: ").concat(entry.getKey()),
                            entry.getValue(), containsInAnyOrder(expectedPageValidationMessages.get(entry.getKey()).toArray()));
                }
        );
    }

    @Step("Assert all page fields validation messages")
    public static void assertPageValidationMessages(ErrorMessageStore errorMessageStore,
                                                    ExpectedErrorMessages expectedErrorMessages) throws IOException {

        assertValidationMessages("Mandatory Field validation messages are not displayed As Expected",
                errorMessageStore.getErrorMessagesForMandatoryFields(),
                expectedErrorMessages.mandatoryFields(errorMessageStore));

        assertValidationMessages("Monetary Field validation messages are not displayed As Expected",
                errorMessageStore.getErrorMessagesForMonetaryFields(),
                expectedErrorMessages.monetaryFields(errorMessageStore));

        assertValidationMessages("DateTime Field validation messages are not displayed As Expected",
                errorMessageStore.getErrorMessagesForDateTimeFields(),
                expectedErrorMessages.dateTimeFields(errorMessageStore));

        assertValidationMessages("Dynamic Field validation messages are not displayed As Expected",
                errorMessageStore.getErrorMessagesForDynamicFields(),
                expectedErrorMessages.dynamicFields(errorMessageStore));

    }
}