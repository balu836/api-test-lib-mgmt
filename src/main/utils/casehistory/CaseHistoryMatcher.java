package uk.gov.dwp.utils.casehistory;

import io.qameta.allure.Step;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Custom hamcrest matcher for case histories.
 * It mainly ignore padding on the various entry values and provides better error description.
 * It also saves both the expected and actual case histories in the target folder.
 */
public class CaseHistoryMatcher extends CustomMatcher<List<CaseHistoryRecord>> {

    private String description;
    private List<CaseHistoryRecord> expected;
    private BiConsumer<List<String>, String> saveCaseHistory;
    private int toleranceInMins = 60;

    // ToDo: Some of the logic in this class should really be in other places - CaseHistoryUtil? CaseHistoryRecord? etc

    private CaseHistoryMatcher(List<CaseHistoryRecord> expected, BiConsumer<List<String>, String> saveCaseHistory) {
        super("both case histories to match...");
        this.expected = expected;
        this.saveCaseHistory = saveCaseHistory;
    }

    @Step("Validate the expected and actual case history")
    public static Matcher<List<CaseHistoryRecord>> isSameCaseHistoryAs(List<CaseHistoryRecord> expected) {
        return new CaseHistoryMatcher(expected, CaseHistoryUtil::saveToFile);
    }

    public static Matcher<List<CaseHistoryRecord>> isSameCaseHistoryAs(List<CaseHistoryRecord> expected, BiConsumer<List<String>,
            String> saveCaseHistory) {
        return new CaseHistoryMatcher(expected, saveCaseHistory);
    }

    @Override
    public boolean matches(Object actualObject) {
        List<CaseHistoryRecord> actual = (List<CaseHistoryRecord>) actualObject;

        List<String> actualEntries = CaseHistoryUtil.getCaseHistoryEntries(actual);
        List<String> expectedEntries = CaseHistoryUtil.getCaseHistoryEntries(expected);

        saveCaseHistory.accept(actualEntries, "actual");
        saveCaseHistory.accept(expectedEntries, "expected");

        if (actual.size() != expected.size()) {
            description = "Expected and actual case histories are of different size.";
            return false;
        }

        if (isCaseHistoryUnOrdered(expected, "Expected") ||
                isCaseHistoryUnOrdered(actual, "Actual")) {
            return false;
        }

        if (areDatesOutOfRange(actual)) {
            return false;
        }

        return doCaseHistoryEntriesMatch(expectedEntries, actualEntries);
    }

    private boolean doCaseHistoryEntriesMatch(List<String> expectedEntries, List<String> actualEntries) {
        Function<String, String> trimWords = (input) -> Arrays.stream(input.trim().split("\\|"))
                .map(String::trim)
                .collect(Collectors.joining(" | "));

        List<String> unMatchResults = IntStream.range(0, expectedEntries.size())
                .filter(index -> !caseHistoryEntryMatches(actualEntries.get(index), expectedEntries.get(index)))
                .mapToObj(index -> "Line " + (index + 1) + " from the expected and actual case histories did not match." +
                        "\r\nExpected: " + trimWords.apply(expectedEntries.get(index)) +
                        "\r\nActual:   " + trimWords.apply(actualEntries.get(index)))
                .collect(Collectors.toList());

        if (unMatchResults.size() > 0) {
            description = unMatchResults.size() + " lines did not match.\r\n" + String.join("\r\n\r\n", unMatchResults);
            return false;
        }

        return true;
    }

    private boolean isCaseHistoryUnOrdered(List<CaseHistoryRecord> caseHistory, String caseHistoryType) {
        boolean isUnOrdered = false;
        StringBuilder clashingDates = new StringBuilder();

        for (int index = 0; index < caseHistory.size() - 1; index++) {
            CaseHistoryRecord top = caseHistory.get(index);
            CaseHistoryRecord bottom = caseHistory.get(index + 1);

            if (top.getDate().isBefore(bottom.getDate())) {
                isUnOrdered = true;
                clashingDates.append("Lines " + index + " and " + (index + 1))
                        .append(" have their dates in the wrong order")
                        .append("\r\n")
                        .append("Line " + index + ": " + top)
                        .append("\r\n")
                        .append("Line " + (index + 1) + ": " + bottom)
                        .append("\r\n");
            }
        }

        if (isUnOrdered) {
            this.description = caseHistoryType + " case history dates are out of sync." + "\r\n" + clashingDates.toString();
        }

        return isUnOrdered;
    }

    private boolean caseHistoryEntryMatches(String expectedEntry, String actualEntry) {
        Function<String, List<String>> splitToWords = (input) -> Arrays.stream(input.trim().split("\\|"))
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        return expectedEntry.startsWith("//") ||
                actualEntry.startsWith("//") ||
                splitToWords.apply(expectedEntry).equals(splitToWords.apply(actualEntry));
    }

    private boolean areDatesOutOfRange(List<CaseHistoryRecord> actual) {
        List<String> clashingDates = IntStream.range(0, actual.size())
                .filter((index) -> !actual.get(index).getEntry().contains("48003654"))//skip the fraims user date entry validation
                .filter((index) -> {
                    CaseHistoryRecord expectedRecord = expected.get(index);
                    CaseHistoryRecord actualRecord = actual.get(index);
                    return actualRecord.getDate().isBefore(expectedRecord.getDate().minusMinutes(toleranceInMins)) ||
                            actualRecord.getDate().isAfter(expectedRecord.getDate().plusMinutes(toleranceInMins));
                })
                .mapToObj((index) -> "Expected: " + expected.get(index).toString() + "\r\nActual:   " + actual.get(index).toString() + "\r\n")
                .collect(Collectors.toList());

        if (clashingDates.size() > 0) {
            description = clashingDates.size() + " lines have dates that are not within " + toleranceInMins + " mins of expected .\r\n" +
                    String.join("\r\n", clashingDates);
            return true;
        }

        return false;
    }


    public void describeMismatch(Object item, Description description) {
        description.appendText(this.description);
    }

    public String getDescription() {
        return description;
    }
}