//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebDriverException;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
//import org.openqa.selenium.support.ui.ExpectedCondition;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import uk.gov.dwp.webdriver.configuration.TestConfigHelper;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//public class PegaUIEventListener extends AbstractWebDriverEventListener {
//
//    private static PegaUIEventListener uiEventListener;
//    private final ExpectedCondition<WebElement> pegaPageIsReady = ExpectedConditions.presenceOfElementLocated(
//            By.xpath("//div[@data-state-busy-status='none']"));
//
//    private PegaUIEventListener() {
//    }
//
//    public static PegaUIEventListener get() {
//        if (uiEventListener == null) {
//            uiEventListener = new PegaUIEventListener();
//        }
//        return uiEventListener;
//    }
//
//    @Override
//    public void beforeFindBy(By by, WebElement element, WebDriver driver) {
//
//        waitUntil(driver, ExpectedConditions.and(pegaPageIsReady));
//    }
//
//    @Override
//    public void beforeClickOn(WebElement element, WebDriver driver) {
//        ExpectedCondition<Boolean> elementNotStale = ExpectedConditions.not(ExpectedConditions.stalenessOf(element));
//        ExpectedCondition<WebElement> elementClickable = ExpectedConditions.elementToBeClickable(element);
//        waitUntil(driver, ExpectedConditions.and(pegaPageIsReady, elementNotStale, elementClickable));
//    }
//
//    @Override
//    public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
//        ExpectedCondition<Boolean> elementNotStale = ExpectedConditions.not(ExpectedConditions.stalenessOf(element));
//        waitUntil(driver, ExpectedConditions.and(pegaPageIsReady, elementNotStale));
//    }
//
//    @Override
//    public void beforeGetText(WebElement element, WebDriver driver) {
//        ExpectedCondition<Boolean> elementNotStale = ExpectedConditions.not(ExpectedConditions.stalenessOf(element));
//        waitUntil(driver, ExpectedConditions.and(pegaPageIsReady, elementNotStale));
//    }
//
//    private Collection<Class<? extends Throwable>> getExceptionsToIgnore() {
//        List<Class<? extends Throwable>> exceptionsToHandle = new ArrayList<>();
//        for (String exceptionName : TestConfigHelper.get().getTolerantActionExceptions().getExceptionsToHandle()) {
//            try {
//                exceptionsToHandle.add((Class<? extends Throwable>) Class.forName(exceptionName));
//            } catch (ClassNotFoundException | ClassCastException ex) {
//                // no need to throw this exception. Lets just skip that entry and pretend nothing happened :-)
//            }
//        }
//        return exceptionsToHandle;
//    }
//
//    private void waitUntil(WebDriver driver, ExpectedCondition<?> condition) {
//        long timeout = TestConfigHelper.get().getWebDriverWaitTimeout();
//        String errorMessage = "Some preconditions have not been met. Even after waiting for " + timeout + "s.";
//        try {
//            waitForCondition(driver, condition, timeout, errorMessage);
//        } catch (WebDriverException e) {
//            driver.navigate().refresh();
//            waitForCondition(driver, condition, timeout, errorMessage);
//        }
//    }
//
//    private void waitForCondition(WebDriver driver, ExpectedCondition<?> condition, long timeout, String errorMessage) {
//        new WebDriverWait(driver, timeout)
//                .ignoreAll(getExceptionsToIgnore())
//                .withMessage(errorMessage)
//                .until(condition);
//    }
//}
