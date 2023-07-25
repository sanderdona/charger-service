package nl.dimensiontech.domotics.chargerservice.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"nl.dimensiontech.domotics.chargerservice.cucumber"},
        plugin = {"pretty", "html:target/cucumber/report.html"}
)
public class CucumberIntegrationTest {


}
