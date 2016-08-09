package com.thomascook.jfq.cucumber.spring;

import cucumber.api.java.en.Given;
import org.apache.commons.lang3.Validate;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.util.Map;

@ContextConfiguration(classes = AppTestConfig.class)
public class AutowiredStepDefs {

    @Resource(name = "dictionary")
    Map<String, String> dictionary;

    @Given("test start")
    public void start(){
        Validate.notNull(dictionary);
    }
}
