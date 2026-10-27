package com.servet.framework.config;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * Framework'ün kendi birim testleri: tarayıcı açmaz, saniyenin altında biter.
 * Ayar katmanları bozulursa bütün UI testleri yanlış ortamda koşar; bunu en başta yakalamak istiyoruz.
 *
 * Profil (-Denv) sınıf yüklenirken bir kez okunur. Bu testler aktif profilden bağımsız yazıldı,
 * böylece mvn test -Dsuite=framework -Denv=ci ile de geçerler.
 */
public class ConfigTest {

    private static final String KEY = "explicitWaitSeconds";

    @AfterMethod(alwaysRun = true)
    public void clearOverride() {
        System.clearProperty(KEY);
        System.clearProperty("gridUrl");
    }

    @Test(groups = "unit", description = "-Dkey=value dosyadaki değeri ezer")
    public void systemPropertyOverridesFileValue() {
        System.setProperty(KEY, "42");
        assertEquals(Config.explicitWaitSeconds(), 42);
    }

    @Test(groups = "unit", description = "Boş değer 'ayar yok' demektir: -DgridUrl= yerel tarayıcıya döner")
    public void blankValueIsTreatedAsMissing() {
        System.setProperty("gridUrl", "  ");
        assertTrue(Config.gridUrl().isEmpty());
    }

    @Test(groups = "unit", description = "Değerlerin başındaki/sonundaki boşluk kırpılır")
    public void valuesAreTrimmed() {
        System.setProperty("gridUrl", " http://localhost:4444 ");
        assertEquals(Config.gridUrl().orElseThrow(), "http://localhost:4444");
    }

    @Test(groups = "unit", description = "Eksik zorunlu ayar, hangi profilde arandığını söyleyerek patlar")
    public void missingRequiredKeyFailsWithEnvInMessage() {
        IllegalStateException e = expectThrows(IllegalStateException.class, () -> Config.get("noSuchKey"));
        assertEquals(e.getMessage(), "Ayar bulunamadı: noSuchKey (env=" + Config.env() + ")");
    }

    @Test(groups = "unit", description = "Her profil zorunlu ayarların hepsini sağlar")
    public void activeProfileProvidesAllRequiredSettings() {
        assertTrue(Config.baseUrl().startsWith("https://"));
        assertTrue(Config.explicitWaitSeconds() > 0);
        assertTrue(Config.pageLoadTimeoutSeconds() > 0);
        assertTrue(Config.retryCount() >= 0);
    }
}
