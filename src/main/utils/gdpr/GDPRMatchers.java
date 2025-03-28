package uk.gov.dwp.utils.gdpr;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Description;
import uk.gov.dwp.utils.database.QueryResult;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static uk.gov.dwp.utils.LambdaUtil.toHandledLambda;
import static uk.gov.dwp.utils.database.QueryResult.Column;

/**
 * Contains harmcrest matchers for GDPR-related checks
 */
public class GDPRMatchers {

    /**
     * Returns a matcher for verifying that all data in a Column are equal to an expected value
     *
     * @param expected the expected value
     * @return an instance of the ListContainsOnlyMatcher matcher.
     */
    public static ListContainsOnlyMatcher containsOnly(Object expected) {
        return new ListContainsOnlyMatcher(expected);
    }

    /**
     * Returns a matcher for  verifying that all data in a Column or QueryResult are anonymised
     *
     * @return an instance of the ListContainsOnlyMatcher matcher.
     */
    public static CustomMatcher<Object> isAnonymised() {
        return new IsAnonymisedMatcher();
    }

    /**
     * Matcher that checks if all the data in a Column or QueryResult are anonymised
     */
    static class IsAnonymisedMatcher extends CustomMatcher<Object> {

        private String errorMessage = "";

        private IsAnonymisedMatcher() {
            super("all values in list to be anonymised");
        }

        @Override
        public boolean matches(Object actual) {
            if (actual == null) {
                errorMessage = "found null";
                return false;
            }

            if (actual instanceof Column) {
                return isListAnonymised(((Column) actual));
            } else if (actual instanceof QueryResult) {
                QueryResult actualResult = ((QueryResult) actual);
                return actualResult.getColumnNames().stream()
                        .map(toHandledLambda(actualResult::getColumn))
                        .allMatch(this::isListAnonymised);
            } else if (actual instanceof String) {
                return isStringAnonymised((String) actual);
            }

            errorMessage = "was unable to assert anonymity on objects of type: " + (actual.getClass().getName());
            return false;
        }

        private boolean isListAnonymised(Column<Object> values) {
            List<String> unmatched = values.toStringColumn().stream()
                    .filter(value -> !value.toUpperCase().replaceAll("X", "").trim().isEmpty())
                    .collect(Collectors.toList());

            if (!unmatched.isEmpty()) {
                errorMessage += "found the following values from column '" + values.getName() +
                        "' are not anonymised: " + String.join(", ", unmatched) + "\r\n";
                return false;
            }

            return true;
        }

        private boolean isStringAnonymised(String value) {
            String unmatched = value.toUpperCase().replaceAll("X", "").trim();
            if (!unmatched.isEmpty()) {
                errorMessage += "found the following values from column '" + value +
                        "' are not anonymised: " + String.join(", ", unmatched) + "\r\n";
                return false;
            }

            return true;
        }

        @Override
        public void describeMismatch(Object item, Description description) {
            description.appendText(errorMessage);
        }
    }

    /**
     * Matcher that checks if all the data in a Column are equal to an expected value.
     */
    static class ListContainsOnlyMatcher extends CustomMatcher<List<?>> {

        private Object expected;
        private String errorMessage;

        private ListContainsOnlyMatcher(Object expected) {
            super("all values in list to match '" + expected + "'");
            this.expected = expected;
        }

        @Override
        public boolean matches(Object actual) {
            if (actual == null) {
                errorMessage = "found null";
                return false;
            }

            List<String> unmatched = ((List<Object>) actual).stream()
                    .filter(value -> !Objects.equals(expected, value))
                    .map(Objects::toString)
                    .collect(Collectors.toList());

            if (!unmatched.isEmpty()) {
                errorMessage = "found the following values did not match: " + String.join(", ", unmatched);
                return false;
            }

            return true;
        }

        @Override
        public void describeMismatch(Object item, Description description) {
            description.appendText(errorMessage);
        }
    }

    public static CustomMatcher<Object> isLastCharactersAnonymised(String value, int numberOfLastChars) {
        return new isLastCharactersAnonymised(value, numberOfLastChars);
    }

    static class isLastCharactersAnonymised extends CustomMatcher<Object> {

        private int numberLastChars;
        private String value;
        private String errorMessage;

        private isLastCharactersAnonymised(String value, int numberLastChars) {
            super("Expected chars are to be anonymised");
            this.numberLastChars = numberLastChars;
            this.value = value;
        }

        @Override
        public boolean matches(Object actual) {
            if (actual == null) {
                errorMessage = "found null";
                return false;
            }
            String expectedValue = ((String) actual).toUpperCase().replaceAll("X", "");
            if (!expectedValue.equals(value.substring(0, numberLastChars-1))) {
                errorMessage = "found the following values did not match: " + String.join(", ", expectedValue);
                return false;
            }
            return true;

        }

        @Override
        public void describeMismatch(Object item, Description description) {
            description.appendText(errorMessage);
        }
    }
}
