package app.anjos.core.crawler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import app.anjos.model.Category;
import io.matob.database.jpa.DAOJPA;
import io.matob.database.jpa.DAOJPAFactory;
import io.matob.database.jpa.EntityManagerController;

public class CrawlerCategory {

	private static ChromeOptions options = new ChromeOptions();
	private static WebDriver driver;
	private static DAOJPA<Category> dao;
	private static Map<String, Category> cache;

	public static void main(String[] args) throws Exception {
		System.setProperty("webdriver.chrome.driver", "chromedriver_73.exe");
		options.addArguments("--headless");
		driver = new ChromeDriver(options);

		cache = new HashMap<>();

		EntityManagerController emc = new EntityManagerController();
		try {
			emc.begin();
			dao = DAOJPAFactory.createDAO(Category.class, emc);
			dao.setUseTransaction(false);
			execute("https://consultaremedios.com.br/categorias", true);
			emc.commit();
		} catch (Exception ex) {
			emc.rollback();
			ex.printStackTrace();
		} finally {
			emc.close();
			driver.close();
			driver.quit();
		}
	}

	private static Category execute(String url, boolean featured) throws Exception {
		System.out.println("GET: " + url);
		Category category = null;
		driver.get(url);
		Thread.sleep(200);

		List<WebElement> elements = driver.findElements(By.className("category-header__title"));
		if (!elements.isEmpty() && !"Medicamentos".equals(elements.get(0).getText())) {
			String name = elements.get(0).getText();
			category = (cache.containsKey(name)) ? cache.get(name) : dao.executeSingleQuery("m.name", "'" + name + "'");
			if (category != null)
				return category;

			category = new Category(name);
			category.setFeatured(featured);
		}

		List<String> urls = new LinkedList<>();
		for (WebElement e : driver.findElements(By.className("category-link__item")))
			urls.add(e.findElement(By.tagName("a")).getAttribute("href"));

		Category c;
		for (String u : urls) {
			c = execute(u, category == null);
			if (category != null)
				category.getSubcategories().add(c);
		}

		if (category != null)
			category = dao.save(category);

		return category;
	}
}