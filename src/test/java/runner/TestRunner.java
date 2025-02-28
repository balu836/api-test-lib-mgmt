package runner;


import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Test runner class to initiate
 */
@CucumberOptions(
        features = "src/test/java/features",
        glue = {"org.example.stepdefinations"},
        plugin = {"testng", "html:target/cucumber-report", "json:target/cucumber.json"},
        monochrome = true,
        publish = true
)
public class TestRunner extends AbstractTestNGCucumberTests {
}
