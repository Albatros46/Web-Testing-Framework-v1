package com.servet.framework.listeners;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servet.framework.driver.DriverManager;

/** O anki thread'in tarayıcısından ekran görüntüsü alır. Listener'lar asla exception fırlatmamalı. */
final class Screenshots {

    private static final Logger LOG = LoggerFactory.getLogger(Screenshots.class);

    private Screenshots() {
    }

    /** @return PNG baytları; tarayıcı yoksa veya alınamazsa null */
    static byte[] capture() {
        if (!DriverManager.isStarted()) {
            return null;
        }
        try {
            return ((TakesScreenshot) DriverManager.get()).getScreenshotAs(OutputType.BYTES);
        } catch (RuntimeException e) {
            LOG.warn("Screenshot failed: {}", e.getMessage());
            return null;
        }
    }
}
