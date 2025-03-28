package uk.gov.dwp.utils.casehistory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class CaseHistoryUtil {
    /**
     * Saves the a list of strings to a file.
     * It uses the stack trace to find the test class that triggers this call ans uses the test class name and method name to save the file.
     * We do not throw an exception if this fails as it is not critical to the success if the test.
     *
     * @param content to be saved
     * @param suffix  a suffix to be attached to the end of the filename.
     */
    public static void saveToFile(List<String> content, String suffix) {
        String caseHistoryFolderPath = "target/case-history/";
        File caseHistoryFolder = new File(caseHistoryFolderPath);
        if (!caseHistoryFolder.exists()) {
            caseHistoryFolder.mkdir();
        }

        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.toString().startsWith("uk.gov.dwp.endtoendtests")) {
                String[] classNameChunks = element.getClassName().split("\\.");
                String className = classNameChunks[classNameChunks.length - 1];
                String caseHistoryFileName = caseHistoryFolderPath + className + "." + element.getMethodName() + "." + suffix;

                List<String> cleanedCaseHistory = content.stream()
                        .map((entry) -> entry.replaceAll("[\\t\\n\\r]+", " "))
                        .collect(Collectors.toList());
                try {
                    Files.write(new File(caseHistoryFileName).toPath(), cleanedCaseHistory);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * Extracts the entries from a list of case history records.
     *
     * @param caseHistoryRecords
     * @return a list of strings containing the entries of the input case history records
     */
    public static List<String> getCaseHistoryEntries(List<CaseHistoryRecord> caseHistoryRecords) {
        return caseHistoryRecords.stream()
                .map(CaseHistoryRecord::getEntry)
                .collect(Collectors.toList());
    }
}
