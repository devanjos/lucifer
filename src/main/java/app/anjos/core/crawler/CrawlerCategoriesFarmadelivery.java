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

public class CrawlerCategoriesFarmadelivery {

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
			execute("https://www.farmadelivery.com.br/remedios-e-medicamentos", null);
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
		WebElement element;
		driver.get(url);

		element = driver.findElement(By.className("category-title"));

		String name = getCategoryName(element.getText());
		category = new Category(name);
		category.setParent(parent);
		category = dao.save(category);

		element = driver.findElement(By.id("narrow-by-list"));
		List<WebElement> elements = element.findElements(By.tagName("dt"));
		List<String> urls = new LinkedList<>();
		if (!elements.isEmpty() && "Categoria".equals(elements.get(0).getText())) {
			element = element.findElement(By.tagName("dd"));
			for (WebElement e : element.findElements(By.xpath(".//ol"))) {
				System.out.println(e.getTagName());
				urls.add(e.findElement(By.tagName("a")).getAttribute("href"));
			}
		}
		for (String _url : urls)
			execute(_url, category);
	}

	private static String getCategoryName(String name) {
		if ("REMÉDIOS E MEDICAMENTOS".equals(name))
			return "Medicamentos";
		if ("Remédio para Circulação".equals(name))
			return "Circulação";
		if ("Remédio para Colesterol e Trigliceres".equals(name))
			return "Colesterol e Trigliceres";
		if ("Remédio para Diabetes".equals(name))
			return "Diabetes";
		if ("Doenças dos Ossos".equals(name))
			return "Doenças dos Ossos";
		if ("Dor e Contusão".equals(name))
			return "Dor e Contusão";
		if ("Remédio e Colírio para Olhos".equals(name))
			return "Doenças dos Olhos";
		if ("Obesidade".equals(name))
			return "Obesidade";
		if ("Remédio para Pele e Mucosa".equals(name))
			return "Pele e Mucosa";
		if ("Problemas Estomacais".equals(name))
			return "Problemas Estomacais";
		if ("Remédio p/ Problemas Intestinais".equals(name))
			return "Problemas Intestinais";
		if ("Remédios p/ Saúde da Mulher".equals(name))
			return "Saúde da Mulher";
		if ("Remédios p/ Saúde do Homem".equals(name))
			return "Saúde do Homem";
		if ("Suplementos e Vitaminas".equals(name))
			return "Suplementos e Vitaminas";
		if ("Remédio para Tireóide".equals(name))
			return "Tireóide";
		if ("Remédio p/ Varizes e Hemorróidas".equals(name))
			return "Varizes e Hemorróidas";
		return name;
	}
}