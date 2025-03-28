package uk.gov.dwp.utils;

import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.webdriver.configuration.RunType;
import uk.gov.dwp.webdriver.configuration.TestConfigHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class ScreenUtil {
    private static final File TEST_SCREENSHOT_DIRECTORY = new File(Paths.get("target").toString() +
            "/test-screenshots/");

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenUtil.class);

    public static void takeTestScreenshot(WebDriver webDriver, Class<?> testClass, String testName) {
        createTestScreenshotDirectoryIfNotExists();
        File screenshotFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        try {
            if (TestConfigHelper.get().getRunType().equals(RunType.LOCAL)) {
                File placeHolder = getTestScreenshotFile(testClass, testName);
                FileUtils.copyFile(screenshotFile, placeHolder);
            }
            Allure.addAttachment(testName, FileUtils.openInputStream(screenshotFile));
        } catch (IOException ioException) {
            LOGGER.error("Failed to save screenshot", ioException);
        }
    }

    private static void createTestScreenshotDirectoryIfNotExists() {
        if (!TEST_SCREENSHOT_DIRECTORY.exists()) {
            TEST_SCREENSHOT_DIRECTORY.mkdir();
        }
    }

    private static File getTestScreenshotFile(Class<?> testClass, String testName) {
        String sanitizedTestName = testName.replaceAll("\\(.*\\)", "");
        Long timeStamp = System.currentTimeMillis() / 1000L;
        String fullTestName = Optional.ofNullable(testClass)
                .map(Class::getSimpleName)
                .map((className) -> className + "." + sanitizedTestName + "." + timeStamp + ".jpg")
                .orElse(sanitizedTestName);
        return new File(TEST_SCREENSHOT_DIRECTORY.getPath() + "/" + fullTestName);
    }
}
