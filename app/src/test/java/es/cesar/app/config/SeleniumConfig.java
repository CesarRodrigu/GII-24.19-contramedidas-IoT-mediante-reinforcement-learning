package es.cesar.app.config;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;

public class SeleniumConfig {
    public static String url = "http://localhost:8080/";

    public static WebDriver getWebDriver() {
        return getWebDriver("firefox");
    }

    public static WebDriver getWebDriver(String browser) {
        return switch (browser.toLowerCase()) {
            case "chrome" -> new ChromeDriver();
            case "firefox" -> new FirefoxDriver();
            case "edge" -> new EdgeDriver();
            case "ie" -> new InternetExplorerDriver();
            case "safari" -> new SafariDriver();
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }

}