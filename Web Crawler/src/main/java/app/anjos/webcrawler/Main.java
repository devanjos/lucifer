package app.anjos.webcrawler;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Main {

	private static final String SEARCH_URL = "https://consultaremedios.com.br/busca?termo=";

	public static void main(String[] args) {
		// 7848995329460
		// 7848
		String ean = "7848";

		run(ean);
	}

	private static void run(String ean) {
		System.setProperty("webdriver.chrome.driver", "chromedriver_73.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		WebDriver driver = new ChromeDriver(options);

		List<WebElement> elements;
		WebElement element;

		try {
			driver.get(SEARCH_URL + ean);

			elements = driver.findElements(By.id("result-subtitle"));
			if (!elements.isEmpty() && elements.get(0).getText().equals("Desculpe, mas não encontramos nenhum resultado para sua busca"))
				System.out.println("404: " + ean);
		} finally {
			driver.close();
		}
	}
}
