package com.thomascook.jfq.cucumber.spring;

import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AppTestConfig {

    //just some arbitrary map to make sure that dependency injection works
    @Bean(name = "dictionary")
    public Map<String, String> getDictionary(){
        Map<String, String> dictionary = new HashMap<>();
        dictionary.put("key1", "value1");
        return Collections.unmodifiableMap(dictionary);
    }
}
