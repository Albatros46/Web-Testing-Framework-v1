# Web Testing Framework v1 · Selenium 4 + TestNG

[![UI Tests](https://github.com/Albatros46/Web-Testing-Framework-v1/actions/workflows/ui-tests.yml/badge.svg)](https://github.com/Albatros46/Web-Testing-Framework-v1/actions/workflows/ui-tests.yml)
![Java](https://img.shields.io/badge/Java-21-orange) ![Selenium](https://img.shields.io/badge/Selenium-4.51-43B02A) ![TestNG](https://img.shields.io/badge/TestNG-7.12-red) ![Grid](https://img.shields.io/badge/Selenium%20Grid-Docker-2496ED) ![Allure](https://img.shields.io/badge/report-Allure%202-orange) ![Extent](https://img.shields.io/badge/report-ExtentReports%205-blue) ![Tests](https://img.shields.io/badge/tests-39%20passing-brightgreen)

A reusable UI test framework: the generic part (`com.servet.framework`) knows nothing about the application under test; the application part (`com.servet.saucedemo`) contains only page objects and tests.
The same tests run on a developer machine, in GitHub Actions and on a Selenium Grid in Docker — selected by one switch: `-Denv=local | ci | grid`.

Part of my 52-week QA Automation roadmap — **Week 12**, the end of phase 2 (web UI automation). It brings together weeks 6–11, built step by step in [saucedemo-selenium-pom](https://github.com/Albatros46/saucedemo-selenium-pom), and adds what a team framework needs: environment profiles, Selenium Grid, logging and CI.

## Architecture

```mermaid
flowchart LR
    subgraph APP["com.servet.saucedemo  (application)"]
        T["Tests<br/>Login · Inventory · Checkout<br/>Excel · JSON · DB"] --> P["Page objects<br/>LoginPage → InventoryPage → …"]
        T --> BT["BaseTest<br/>openLoginPage()"]
    end
    subgraph FW["com.servet.framework  (reusable)"]
        BWT["BaseWebTest<br/>browser per test"] --> DM["DriverManager<br/>ThreadLocal"]
        DM --> DF["DriverFactory<br/>local or Grid"]
        BP["BasePage<br/>explicit waits"]
        C["Config<br/>3 layers"]
        L["Listeners<br/>log · screenshot · retry"]
        R["Reporting<br/>Allure · Extent · scrubber"]
        D["Data readers<br/>Excel · JSON"]
    end
    BT --> BWT
    P --> BP
    DF --> B1["Chrome / Edge / Firefox"]
    DF --> G["Selenium Grid<br/>(Docker)"]
```

```
src/main/java/com/servet/framework/      FRAMEWORK — reusable, no app knowledge
├── config/Config                        config.properties → config-{env}.properties → -Dkey=value
├── driver/DriverFactory                 Chrome / Edge / Firefox, local or RemoteWebDriver, start retry
├── driver/DriverManager                 ThreadLocal<WebDriver>: one browser per thread
├── base/BaseWebTest                     fresh browser per test (@BeforeMethod / @AfterMethod)
├── pages/BasePage                       wait → click / type / read
├── listeners/                           TestListener (log + screenshot) · RetryAnalyzer · RetryTransformer
│                                        ExtentReportListener · AllureEnvironmentListener · MaskedParameterScrubber
├── reporting/ReportParameters           hide a parameter in Allure
└── data/                                ExcelReader (Apache POI) · JsonReader (Jackson)

src/main/java/com/servet/saucedemo/pages/   APPLICATION — page objects only
src/test/java/com/servet/saucedemo/         APPLICATION — BaseTest, tests, test data, DataProviders
src/test/java/com/servet/framework/         framework unit tests (no browser)
```

## Run

| Command | What runs |
|---|---|
| `mvn test` | 14 UI tests in Chrome, window visible (`env=local`) |
| `mvn test -Denv=ci` | same, headless, longer timeouts — what GitHub Actions runs |
| `docker compose up -d` then `mvn test -Denv=grid` | browsers run in a Selenium Grid container |
| `mvn test -Dsuite=framework` | 9 framework unit tests, no browser, a few seconds |
| `mvn test -Dsuite=smoke` | 4 critical flows |
| `mvn test -Dsuite=datadriven` | 20 tests driven by Excel, JSON and an H2 database |
| `mvn test -Dsuite=parallel` | 14 tests in 4 threads |
| `mvn test -Dsuite=cross-browser` | smoke tests in Chrome, Edge and Firefox at the same time |
| `mvn test -Dsuite=demo` | listener demo: a real bug with screenshot + a flaky test passing on retry (red on purpose) |
| `mvn test -Dbrowser=firefox -DretryCount=0` | override any single setting |
| `mvn test -DlogLevel=DEBUG` | more log detail |
| `mvn allure:serve` | build the Allure report and open it |

In PowerShell, quote parameters that contain dots or `#`: `mvn test "-Dtest=LoginTest#validLoginOpensProductsPage"`.

## Configuration: three layers

```
config.properties          defaults for every environment
  └─ config-{env}.properties   environment profile, chosen with -Denv (default: local)
       └─ -Dkey=value              one setting, from the command line
```

| Setting | `local` | `ci` | `grid` |
|---|---|---|---|
| `headless` | false | true | true |
| `explicitWaitSeconds` | 10 | 15 | 15 |
| `pageLoadTimeoutSeconds` | 30 | 60 | 30 |
| `gridUrl` | — (local browser) | — | `http://localhost:4444` |

- A typo in the profile name fails fast: `-Denv=prdo` → `config-prdo.properties bulunamadı`, no test runs against the wrong defaults.
- A blank value means "not set", so `-DgridUrl=` sends a `grid` run back to a local browser.
- The active profile and Grid URL are written into both reports (Allure *Environment*, Extent *System info*).

## Selenium Grid in Docker

```bash
docker compose up -d        # Grid + Chrome 155 in one container, http://localhost:4444/ui
mvn test -Denv=grid -Dsuite=parallel
docker compose down
```

`DriverFactory` builds the same `ChromeOptions` either way and passes them to `ChromeDriver` or to `RemoteWebDriver`. Tests, pages and listeners do not change — screenshots on failure work on the Grid too. Watch the browser live at `http://localhost:7900` (noVNC, password `secret`).

Verified in CI, where the Grid runs as a service container — every browser was opened on the Grid, not on the runner's own Chrome:

```
DriverFactory - Starting chrome on Grid http://localhost:4444 (headless=true)
TestListener  - PASS   CheckoutTest.completePurchase (1543 ms)
TestListener  - DONE   <Smoke>: 4 passed, 0 failed, 0 skipped
```

Locally the Grid could not be started yet: Docker Desktop on my machine failed with an `input/output error` in its image store while pulling the image. The CI run above is the proof that the Grid setup works.

## Continuous integration (GitHub Actions)

`.github/workflows/ui-tests.yml` runs on every push and pull request:

```mermaid
flowchart LR
    U["unit<br/>framework suite<br/>no browser"] --> C["UI · chrome"]
    U --> F["UI · firefox"]
    U --> DD["data-driven<br/>Excel · JSON · DB"]
    U --> GR["Selenium Grid<br/>service container"]
```

- The cheap job runs first; if the framework itself is broken, no browser job starts.
- The Grid job starts `selenium/standalone-chrome` as a **service container**, waits until `/status` reports ready, then runs with `-Denv=grid`.
- Every job uploads Allure results, the Extent report, screenshots, the log file and Surefire reports as an artifact (14 days), also when tests fail.
- `SAUCE_PASSWORD` comes from a repository secret when set; otherwise the public demo password is used.

First green run ([#2](https://github.com/Albatros46/Web-Testing-Framework-v1/actions/runs/40506583902)):

| Job | Tests | Test step | Job total |
|---|---|---|---|
| Framework unit tests | 9 | 22 s | 28 s |
| UI · chrome | 14 | 52 s | 63 s |
| UI · firefox | 14 | 104 s | 116 s |
| Data-driven | 20 | 44 s | 55 s |
| Selenium Grid | 4 | 21 s | 58 s (incl. 26 s container start) |

Run #1 never started: a one-line `run:` contained `"ready": ?true`, and YAML read `: ` as a new key. Fix: a block scalar (`run: |`). Since then the workflow file is validated locally with PyYAML before pushing.

## Logging

The framework uses only the SLF4J API; the test side chooses Logback (`src/test/resources/logback-test.xml`). Every line carries the thread name, so parallel runs stay readable:

```
13:44:37.751 INFO  [TestNG-test-Parallel UI Tests-1] DriverFactory - Starting chrome locally (headless=true)
13:44:37.751 INFO  [TestNG-test-Parallel UI Tests-2] DriverFactory - Starting chrome locally (headless=true)
13:44:37.751 INFO  [TestNG-test-Parallel UI Tests-3] DriverFactory - Starting chrome locally (headless=true)
13:44:37.751 INFO  [TestNG-test-Parallel UI Tests-4] DriverFactory - Starting chrome locally (headless=true)
13:44:45.212 INFO  [TestNG-test-Parallel UI Tests-2] TestListener - START  LoginTest.lockedOutUserSeesError
13:44:45.212 INFO  [TestNG-test-Parallel UI Tests-3] TestListener - START  LoginTest.logoutReturnsToLoginPage
```

Output goes to the console and to `target/logs/test-run.log`. Third-party libraries log at `WARN` only; Apache POI's Log4j output is routed into the same place by `log4j-to-slf4j`.
Log messages are plain ASCII on purpose: the Windows console turned `▶ ✔` and Turkish characters into `?`.

## Adding a page and a test

1. **Page** in `com.servet.saucedemo.pages`, extending `BasePage`. Locators are private constants; each action returns the next page:

   ```java
   public class ProductPage extends BasePage {
       private static final By ADD_TO_CART = By.cssSelector("[data-test^='add-to-cart']");

       public ProductPage(WebDriver driver) { super(driver); }

       @Step("Add the product to the cart")
       public ProductPage addToCart() { click(ADD_TO_CART); return this; }
   }
   ```

2. **Test** extending `BaseTest` — a fresh browser is already there:

   ```java
   public class ProductTest extends BaseTest {
       @Test(groups = "regression")
       public void productCanBeAddedFromDetailPage() {
           openLoginPage().loginAs(STANDARD, PASSWORD) /* … */;
       }
   }
   ```

3. Add the class to a suite in `src/test/resources/suites/`. Logging, screenshots, retry and both reports come from the listeners — no extra code.

To test a **different application**, keep `com.servet.framework` and write a new `pages` + `BaseTest` package.

## Design rules

| Rule | Why |
|---|---|
| Framework code never imports application code | The framework can be reused; the dependency goes one way only. |
| Locators live only in page classes | When the UI changes, one place is updated. |
| Explicit waits only, implicit wait = 0 | Mixing both makes wait times unpredictable. No `Thread.sleep()`. |
| One browser per test, kept in a `ThreadLocal` | Tests can run in parallel without sharing a browser. |
| Retry only browser start-up automatically; test retry is visible | A retried test shows up as *skipped + passed* and must be investigated. |
| No secrets in test data or reports | `{{password}}` placeholder + `MaskedParameterScrubber` (see [Week 11](https://github.com/Albatros46/saucedemo-selenium-pom#keeping-the-password-out-of-every-report)). |

## Tests (39)

| Suite | Tests | Browser |
|---|---|---|
| `framework` | 9 — `ConfigTest` (5) · `DataReadersTest` (4) | none |
| `testng` | 14 — `LoginTest` · `InventoryTest` · `CheckoutTest` | yes |
| `datadriven` | 20 — Excel (7) · JSON (3) · H2 database (6) · `DataReadersTest` (4) | yes |

`DataReadersTest` runs in both `framework` and `datadriven`; counted once, that is **39 tests**.

## What I learned
- _TODO: 2–3 sentences in your own words_
