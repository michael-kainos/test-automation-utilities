package com.automation.utilities.browser;

import com.automation.utilities.readers.PropertiesFileReader;
import com.browserstack.local.Local;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class BrowserStackActions {
    private PropertiesFileReader propertiesFileReader;
    private static final Logger logger = LoggerFactory.getLogger(BrowserStackActions.class);

    public BrowserStackActions(){
        this.propertiesFileReader = new PropertiesFileReader(System.getProperty("user.dir") + "/src/test/resources/properties/url.properties");
    }

    private HashMap<String,String> localConnectionArguments(){
        String accessKey = propertiesFileReader.getProperty("browser.stack.access.key");
        HashMap<String, String> localConnectionArguments = new HashMap<>();
        localConnectionArguments.put("key",accessKey);
        localConnectionArguments.put("localIdentifier",System.getenv("BROWSER_STACK_LOCAL_IDENTIFIER"));
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

    public void startBrowserStackSession(boolean isApplicationPublic){
        if(!isApplicationPublic){
            startBrowserStackLocal();
        } else{
            DesiredCapabilities capabilities = new DesiredCapabilities();
            HashMap<String, Object> browserStackOptions = new HashMap<>();
            browserStackOptions.put("browserName",System.getenv("BROWSER_NAME"));
            browserStackOptions.put("seleniumVersion", System.getenv("SELENIUM_VERSION"));
            capabilities.setCapability("bstack:options",browserStackOptions);
        }
    }

}
