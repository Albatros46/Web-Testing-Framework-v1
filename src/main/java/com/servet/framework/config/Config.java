package com.servet.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.Properties;

/**
 * Ayarları üç katmandan okur; sonraki katman öncekini ezer:
 * <ol>
 *   <li>config.properties — tüm ortamlar için varsayılanlar</li>
 *   <li>config-{env}.properties — ortam profili, -Denv=ci ile seçilir (varsayılan: local)</li>
 *   <li>System property — tek bir ayarı komut satırından ezer: -Dbrowser=firefox</li>
 * </ol>
 * Böylece aynı testler kod değiştirmeden geliştirici makinesinde, CI'da veya Selenium Grid'de koşar.
 */
public final class Config {

    private static final String ENV = System.getProperty("env", "local");
    private static final Properties PROPERTIES = load();

    private Config() {
    }

    /** Zorunlu ayar: yoksa test hiç başlamadan anlaşılır bir hatayla durur. */
    public static String get(String key) {
        return optional(key).orElseThrow(
                () -> new IllegalStateException("Ayar bulunamadı: " + key + " (env=" + ENV + ")"));
    }

    /** İsteğe bağlı ayar: boş değer "yok" sayılır, böylece bir profil varsayılanı boşaltabilir. */
    public static Optional<String> optional(String key) {
        String value = System.getProperty(key, PROPERTIES.getProperty(key));
        return value == null || value.isBlank() ? Optional.empty() : Optional.of(value.trim());
    }

    public static String env() {
        return ENV;
    }

    public static String baseUrl() {
        return get("baseUrl");
    }

    public static String browser() {
        return get("browser");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static int explicitWaitSeconds() {
        return Integer.parseInt(get("explicitWaitSeconds"));
    }

    public static int pageLoadTimeoutSeconds() {
        return Integer.parseInt(get("pageLoadTimeoutSeconds"));
    }

    public static int retryCount() {
        return Integer.parseInt(get("retryCount"));
    }

    /** Doluysa tarayıcı yerelde değil, bu adresteki Selenium Grid'de açılır (ör. http://localhost:4444). */
    public static Optional<String> gridUrl() {
        return optional("gridUrl");
    }

    private static Properties load() {
        Properties properties = new Properties();
        loadInto(properties, "/config.properties");
        // Bilinmeyen bir profil (-Denv=prdo gibi bir yazım hatası) sessizce varsayılanlarla koşmasın
        loadInto(properties, "/config-" + ENV + ".properties");
        return properties;
    }

    private static void loadInto(Properties properties, String resource) {
        try (InputStream in = Config.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException(resource.substring(1) + " bulunamadı");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
