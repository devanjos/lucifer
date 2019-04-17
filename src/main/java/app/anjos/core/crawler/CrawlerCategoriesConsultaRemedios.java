package app.anjos.core.crawler;

import java.util.LinkedList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import app.anjos.model.Category;
import io.matob.database.jpa.DAOJPA;
import io.matob.database.jpa.DAOJPAFactory;
import io.matob.database.jpa.EntityManagerController;

public class CrawlerCategoriesConsultaRemedios {

	private static ChromeOptions options = new ChromeOptions();
	private static WebDriver driver;
	private static DAOJPA<Category> dao;

	public static void main(String[] args) throws Exception {
		System.setProperty("webdriver.chrome.driver", "chromedriver_73.exe");
		options.addArguments("--headless");
		driver = new ChromeDriver(options);

		EntityManagerController emc = new EntityManagerController();
		try {
			emc.begin();
			dao = DAOJPAFactory.createDAO(Category.class, emc);
			dao.setUseTransaction(false);
			execute("https://consultaremedios.com.br/categorias", null);
			emc.commit();
		} catch (Exception ex) {
			emc.rollback();
			throw ex;
		} finally {
			emc.close();
			driver.close();
			driver.quit();
		}
	}

	private static void execute(String url, Category parent) throws Exception {
		System.out.println("GET: " + url);
		Category category = null;
		driver.get(url);
		Thread.sleep(200);

		List<WebElement> elements = driver.findElements(By.className("category-header__title"));
		if (!elements.isEmpty() && !"Medicamentos".equals(elements.get(0).getText())) {
			String name = elements.get(0).getText();
			category = new Category(name);
			category.setParent(parent);
			category = dao.save(category);
		}

		List<String> urls = new LinkedList<>();
		for (WebElement e : driver.findElements(By.className("category-link__item")))
			urls.add(e.findElement(By.tagName("a")).getAttribute("href"));

		for (String _url : urls)
			execute(_url, category);
	}
}