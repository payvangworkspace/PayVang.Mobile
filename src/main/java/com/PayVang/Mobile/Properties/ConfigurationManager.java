package com.PayVang.Mobile.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationManager {

    private static Environment env;

    @Autowired
    public ConfigurationManager(Environment env) {
        ConfigurationManager.env = env;
    }

    public static String getProperty(String propertyName) {
        return env.getProperty(propertyName, "");
    }
}


