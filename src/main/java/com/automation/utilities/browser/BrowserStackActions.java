package com.automation.utilities.browser;

import com.automation.utilities.date_time.DateTime;
import com.automation.utilities.readers.PropertiesFileReader;
import com.browserstack.local.Local;
import org.apache.commons.lang3.BooleanUtils;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class BrowserStackActions {
    private static final Logger logger = LoggerFactory.getLogger(BrowserStackActions.class);
    private static final PropertiesFileReader BROWSER_STACK_PROPERTIES = new PropertiesFileReader("/src/test/resources/properties/browserstack.properties");
    private static final PropertiesFileReader AUTOMATION_PROPERTIES = new PropertiesFileReader("/src/test/resources/properties/automation.properties");



    private HashMap<String,String> localConnectionArguments(){
        String accessKey = BROWSER_STACK_PROPERTIES.getProperty("browser.stack.access.key");
        HashMap<String, String> localConnectionArguments = new HashMap<>();
        localConnectionArguments.put("key",accessKey);
        String localIdentifier;
        if(BooleanUtils.toBoolean(AUTOMATION_PROPERTIES.getProperty("debug"))){
            localIdentifier = BROWSER_STACK_PROPERTIES.getProperty("browser.stack.local.identifier");
        } else {
            localIdentifier = System.getenv("BROWSER_STACK_LOCAL_IDENTIFIER");
        }
        localConnectionArguments.put("localIdentifier",localIdentifier);
        return localConnectionArguments;
    }

    private Local createBrowserStackConnection() {
        logger.info("Creating local connection");
        Local localConnection = new Local();
        try {
            localConnection.start(localConnectionArguments());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return localConnection;
    }

    private void startBrowserStackLocal(){
        while(true){
            try {
                if (createBrowserStackConnection().isRunning()) break;
            } catch (Exception e) {
                logger.error("Error with local connection");
                logger.error(e.getMessage(),e);
            }
            createBrowserStackConnection();
        }
    }

    private void stopBrowserStackLocal(){
        try {
            createBrowserStackConnection().stop();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    public DesiredCapabilities browserStackSessionCapabilities(String scenarioName){
        DesiredCapabilities capabilities = new DesiredCapabilities();
        PropertiesFileReader gradlePropertiesFileReader = new PropertiesFileReader("/gradle.properties");
        HashMap<String, Object> browserStackOptions = new HashMap<>();
        boolean isLocalTesting = BooleanUtils.toBoolean(System.getenv("TESTING_ON_PUBLIC_ENVIRONMENT"));
        String buildName = "";
        if(isLocalTesting){
            startBrowserStackLocal();
            browserStackOptions.put("local", System.getenv("true"));
        }
        String mobileTesting;
        if (BooleanUtils.toBoolean(AUTOMATION_PROPERTIES.getProperty("debug"))){
            mobileTesting = BROWSER_STACK_PROPERTIES.getProperty("mobile.testing");
        } else{
            mobileTesting = System.getenv("MOBILE_TESTING");
        }
        if(BooleanUtils.toBoolean(mobileTesting)){
            //TODO implement browserstack mobile device parameters
        } else {
            String browserName;
            String browserVersion;
            String operatingSystem;
            String operationSystemVersion;
            String publicEnvironmentTesting;
            if(BooleanUtils.toBoolean(AUTOMATION_PROPERTIES.getProperty("debug"))){
                buildName = BROWSER_STACK_PROPERTIES.getProperty("test.build");
                browserName = BROWSER_STACK_PROPERTIES.getProperty("browser.name");
                browserVersion = BROWSER_STACK_PROPERTIES.getProperty("browser.version");
                operatingSystem = BROWSER_STACK_PROPERTIES.getProperty("operation.system");
                operationSystemVersion = BROWSER_STACK_PROPERTIES.getProperty("operation.system.version");
                publicEnvironmentTesting = BROWSER_STACK_PROPERTIES.getProperty("public.environment.testing");
            } else {
                buildName = System.getenv("TEST_BUILD");
                browserName = System.getenv("BROWSER_NAME");
                browserVersion = System.getenv("BROWSER_VERSION");
                operatingSystem = System.getenv("OPERATING_SYSTEM");
                operationSystemVersion =  System.getenv("OS_VERSION");
                publicEnvironmentTesting = System.getenv("TESTING_ON_PUBLIC_ENVIRONMENT");
            }
            browserStackOptions.put("browserName", browserName);
            browserStackOptions.put("browserVersion", browserVersion);
            browserStackOptions.put("os", operatingSystem);
            browserStackOptions.put("osVersion", operationSystemVersion);
            browserStackOptions.put("local", publicEnvironmentTesting);
        }
        browserStackOptions.put("sessionName", scenarioName);
        DateTime dateTime = new DateTime();
        String testBuild = buildName + "-" + dateTime.getDateTimeOfChosenFormat("dd/MM/yyyy");
        browserStackOptions.put("buildName", testBuild);
        browserStackOptions.put("projectName", BROWSER_STACK_PROPERTIES.getProperty("browser.stack.project.name"));
        browserStackOptions.put("seleniumVersion", gradlePropertiesFileReader.getProperty("selenium.version"));
        capabilities.setCapability("bstack:options",browserStackOptions);
        return capabilities;
    }

    public String getBrowserStackUrl(){
        return  "https://" + BROWSER_STACK_PROPERTIES.getProperty("browser.stack.username") + ":" + BROWSER_STACK_PROPERTIES.getProperty("browser.stack.access.key") + BROWSER_STACK_PROPERTIES.getProperty("browser.stack.url.suffix");
    }

}
