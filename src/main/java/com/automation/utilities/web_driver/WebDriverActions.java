package com.automation.utilities.web_driver;

import com.automation.utilities.browser.BrowserStackActions;
import com.automation.utilities.readers.PropertiesFileReader;


import io.cucumber.java.Scenario;
import org.apache.commons.lang3.BooleanUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class WebDriverActions {
    private static final Logger logger = LoggerFactory.getLogger(WebDriverActions.class);
    private WebDriver webDriver;

    public void startWebDriver(Scenario scenario) {
        if(this.webDriver == null) {
            PropertiesFileReader automationProperties = new PropertiesFileReader("/src/test/resources/properties/automation.properties");
            if (BooleanUtils.toBoolean(automationProperties.getProperty("browser.stack.testing"))) {
                BrowserStackActions browserStackActions = new BrowserStackActions();
                try {
                    this.webDriver = (new RemoteWebDriver(new URL(browserStackActions.getBrowserStackUrl()), browserStackActions.browserStackSessionCapabilities(scenario.getName())));
                } catch (MalformedURLException e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                //TODO implement class LocalBrowserActions
            }
        }
    }

    public void stopWebDriver(){
        if(this.webDriver !=null){
            this.webDriver.quit();
        }
    }

    public WebDriver getWebDriver(){
        return this.webDriver;
    }

}
