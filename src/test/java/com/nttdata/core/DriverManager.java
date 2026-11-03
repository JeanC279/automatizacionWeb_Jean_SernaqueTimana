package com.nttdata.core;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriverManager {
    private static WebDriver driver;
    private static Scenario scenario;

    public static WebDriver getDriver() {
        return driver;
    }

    @Before(order = 0)
    public void setUp() {
        // Detectar SO
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            // Windows OS
            System.setProperty("webdriver.chrome.driver", "drivers\\chromedriver.exe");
        } else if (os.contains("mac")) {
            // Mac OS
            System.setProperty("webdriver.chrome.driver", "drivers/chromedriver");
        } else {
            throw new IllegalStateException("Unsupported operating system: " + os);
        }

        // HTTP Factory
        System.setProperty("webdriver.http.factory", "jdk-http-client");

        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Before(order = 1)
    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    @After(order = 100)
    public void guardarEvidenciaDeFallo(Scenario escenario) {
        if (escenario.isFailed() && driver != null) {
            try {
                byte[] imagen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                escenario.attach(imagen, "image/png", "Captura al fallar");
            } catch (Exception ex) {
                System.err.println("No fue posible capturar la pantalla: " + ex.getMessage());
            }
        }
    }

    @After(order = 0)
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    public static void screenShot() {
        byte[] evidencia = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        scenario.attach(evidencia, "image/png", "evidencias");
    }

    public static void scrollDown(){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // This  will scroll down the page by  1000 pixel vertical
        js.executeScript("window.scrollBy(0,1000)");
    }
    public static void esperaImplicita(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
