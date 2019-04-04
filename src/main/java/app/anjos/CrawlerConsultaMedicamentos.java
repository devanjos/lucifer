package app.anjos;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import app.anjos.model.Product;

public class CrawlerConsultaMedicamentos implements Runnable {

	private static final String URL = "https://consultaremedios.com.br";
	private static final String MED_URL = "https://consultaremedios.com.br/medicamentos";
	private static final String[] SECTIONS = new String[] { "0-9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
			"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	//private static final String[] SECTIONS = new String[] { "0-9" };

	private static List<Product> products = new LinkedList<>();

	public static void main(String[] args) throws Exception {
		System.setProperty("webdriver.chrome.driver", "chromedriver_73.exe");
		List<Thread> jobs = new LinkedList<>();
		Thread j;
		for (String s : SECTIONS) {
			j = new Thread(new CrawlerConsultaMedicamentos(s));
			jobs.add(j);
			j.start();
		}

		for (Thread job : jobs) {
			job.join();
		}

		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("result.obj"))) {
			out.writeObject(products);
		}
	}

	private static synchronized void addProduct(Product product) {
		synchronized (products) {
			products.add(product);
		}
	}

	// ##################################################################

	private String subPoint;
	private String url;
	private ChromeOptions options;

	private List<String> toCrawler;
	private boolean listFinish;

	private CrawlerConsultaMedicamentos(String section) {
		this.subPoint = section;
		this.url = MED_URL + "/" + section;
		options = new ChromeOptions();
		options.addArguments("--headless");

		toCrawler = new LinkedList<>();
		listFinish = false;
	}

	@Override
	public void run() {
		Thread.currentThread().setName("Create Prod (" + subPoint + ")");
		createListThread().start();

		WebDriver driver = new ChromeDriver(options);
		try {
			while (!listFinish || !toCrawler.isEmpty()) {
				while (toCrawler.isEmpty()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
				try {
					driver.get(toCrawler.remove(toCrawler.size() - 1));
					addProduct(crawlerProduct(driver));
				} catch (Exception ex) {
					System.err.println(driver.getCurrentUrl());
					ex.printStackTrace();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			driver.close();
			driver.quit();
		}
	}

	private Product crawlerProduct(WebDriver driver) {
		log(driver.getCurrentUrl());
		return new Product();
	}

	private Thread createListThread() {
		return new Thread(() -> {
			Thread.currentThread().setName("Create List (" + subPoint + ")");
			WebDriver driver = new ChromeDriver(options);
			int pag = 0;

			pag++;
			log("GET ?pagina=" + pag);
			driver.get(url + "?pagina=" + pag);
			while (driver.getPageSource().contains("content-grid__item")) {
				for (WebElement e : driver.findElements(By.className("content-grid__item")))
					toCrawler.add(0, e.findElement(By.tagName("a")).getAttribute("href"));
				pag++;
				log("GET ?pagina=" + pag);
				driver.get(url + "?pagina=" + pag);
			}

			log("END");
			listFinish = true;
		});
	}

	private static synchronized void log(String msg) {
		System.out.println("-> " + Thread.currentThread().getName() + ": " + msg);
	}
}