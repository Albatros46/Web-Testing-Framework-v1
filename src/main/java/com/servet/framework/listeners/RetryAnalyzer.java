package com.servet.framework.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servet.framework.config.Config;

/**
 * Başarısız bir testi en fazla 'retryCount' kez tekrar dener (config.properties, varsayılan 1).
 *
 * DİKKAT: Retry, geçici sorunlar (ağ yavaşlığı, yavaş yüklenen sayfa) için bir emniyet kemeridir.
 * Gerçek bir hatayı gizlememeli: tekrar denemede geçen her test raporda "skipped + passed" olarak
 * görünür ve araştırılmalıdır.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LoggerFactory.getLogger(RetryAnalyzer.class);

    private int attempts;

    @Override
    public boolean retry(ITestResult result) {
        int maxRetries = Config.retryCount();
        if (attempts < maxRetries) {
            attempts++;
            LOG.warn("RETRY  {}.{} ({}/{})",
                    result.getTestClass().getRealClass().getSimpleName(),
                    result.getMethod().getMethodName(), attempts, maxRetries);
            return true;
        }
        return false;
    }
}
