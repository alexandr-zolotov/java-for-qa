package com.thomascook.jfq.cucumber.binding;

import cucumber.api.java.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaliciousHooks {

    private static final Logger LOG = LoggerFactory.getLogger(MaliciousHooks.class);
    /*
      This hook will be executed for all scenarios
     */
    @Before(order = 1)
    public void alwaysTheFirst(){
        LOG.info("Your cat is watching you");
    }
}
