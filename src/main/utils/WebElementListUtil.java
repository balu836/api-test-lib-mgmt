package uk.gov.dwp.utils;

import org.openqa.selenium.WebElement;
import uk.gov.dwp.webdriver.utils.GetAttributeUtils;
import uk.gov.dwp.webdriver.utils.GetTextUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WebElementListUtil {

    public static WebElement getWebElementFromList(List<WebElement> elementList, String elementText) {
        return elementList.stream()
                .filter(webElement -> getText(webElement).equals(elementText))
                .findFirst()
                .orElseThrow(getNoSuchElementExceptionSupplier(elementText));
    }

    public static WebElement getActiveWebElementFromList(List<WebElement> elementList, String elementText) {
        return elementList.stream()
                .filter(webElement -> getText(webElement).equals(elementText) && getAttribute(webElement) == null)
                .findFirst()
                .orElseThrow(getNoSuchElementExceptionSupplier(elementText));
    }

    public static int getElementPositionFromList(List<WebElement> elementList, String elementText) {
        return IntStream.range(0, elementList.size())
                .filter(i -> getText(elementList.get(i)).trim().equals(elementText))
                .findFirst()
                .orElseThrow(getNoSuchElementExceptionSupplier(elementText));
    }

    public static int getElementPositionFromListByElementTextContains(List<WebElement> elementList, String elementText) {
        return IntStream.range(0, elementList.size())
                .filter(i -> getText(elementList.get(i)).trim().contains(elementText))
                .findFirst()
                .orElseThrow(getNoSuchElementExceptionSupplier(elementText));
    }

    public static boolean isElementPresentInListByElementTextContains(List<WebElement> elementList, String elementText) {
        return elementList.stream()
                .anyMatch(webElement -> getText(webElement).contains(elementText));
    }

    public static WebElement getElementFromListByElementTextContains(List<WebElement> elementList, String elementText) {
        return elementList.stream()
                .filter(webElement -> getText(webElement).contains(elementText))
                .findFirst()
                .orElseThrow(getNoSuchElementExceptionSupplier(elementText));
    }

    public static boolean isElementPresentFromListByElementText(List<WebElement> elementList, String elementText) {
        return elementList.stream()
                .anyMatch(webElement -> getText(webElement).contains(elementText));
    }

    public static List<String> getElementTexts(List<WebElement> elementList) {
        return elementList.stream()
                .filter(WebElement::isDisplayed)
                .map(WebElementListUtil::getText)
                .collect(Collectors.toList());
    }

    public static List<String> getAttributeName(List<WebElement> elementList) {
        return elementList.stream()
                .filter(WebElement::isDisplayed)
                .map(WebElementListUtil::getAttribute)
                .collect(Collectors.toList());
    }


    public static String getVisibleElementText(List<WebElement> elementList) throws Throwable {
        return getText(elementList.stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(NoSuchElementException::new));
    }

    public static WebElement getRequiredWeElement(List<WebElement> webElementList, String text) {
        return webElementList.stream()
                .filter(webElement -> webElement.isDisplayed() && getText(webElement).equals(text))
                .findFirst()
                .orElseThrow(getNoSuchElementExceptionSupplier(text));
    }

    public static WebElement getVisibleElement(List<WebElement> webElementList) {
        return webElementList.stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public static List<WebElement> getVisibleElements(List<WebElement> webElementList) {
        return webElementList.stream()
                .filter(WebElement::isDisplayed)
                .collect(Collectors.toList());
    }

    private static String getText(WebElement webElement) {
        try {
            return GetTextUtils.tolerantGetText(webElement);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable.getMessage());
        }
    }

    private static String getAttribute(WebElement webElement) {
        try {
            return GetAttributeUtils.tolerantGetAttribute(webElement, "disabled");
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable.getMessage());
        }
    }

    private static Supplier<NoSuchElementException> getNoSuchElementExceptionSupplier(String elementText) {
        return () -> new NoSuchElementException("Expected '" + elementText + "' element/Link is not present");
    }
}
