package com.servet.saucedemo.base;

import com.servet.framework.base.BaseWebTest;
import com.servet.saucedemo.pages.LoginPage;

/**
 * SauceDemo testlerinin atası. Tarayıcı yaşam döngüsü framework'ten (BaseWebTest) gelir;
 * burada sadece uygulamanın giriş noktası var.
 */
public abstract class BaseTest extends BaseWebTest {

    protected LoginPage openLoginPage() {
        return new LoginPage(driver()).open();
    }
}
