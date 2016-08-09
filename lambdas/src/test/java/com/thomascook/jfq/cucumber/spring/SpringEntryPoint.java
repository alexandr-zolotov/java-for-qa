package com.thomascook.jfq.cucumber.spring;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:cucumber/spring",
        glue = "com.thomascook.jfq.cucumber.spring"
)
public class SpringEntryPoint {
}
