package app.anjos.core.old;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import app.anjos.core.PresentationPersister;
import app.anjos.model.Presentation;
import io.matob.tools.FileUtils;

public class ScrapingCR implements Runnable {

	private static final String[] SECTIONS = new String[] { "0-9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
			"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	private static List<Presentation> presentations = new LinkedList<>();

	private static final String FILE = "presentations.obj";
	private static final boolean useFile = true;

	private static final boolean useList = false;
	private static final List<String> URLs = new LinkedList<>(Arrays.asList(new String[] {
			"https://consultaremedios.com.br/acido-zoledronico-abl-brasil/bula",
			"https://consultaremedios.com.br/acido-zoledronico-cristalia/p"
	}));

	public static void main(String[] args) throws Exception {
		if (!useFile || !new File(FILE).exists()) {
			System.setProperty("webdriver.chrome.driver", "chromedriver_73.exe");

			List<Thread> jobs = new LinkedList<>();
			Thread j;
			for (String s : SECTIONS) {
				j = new Thread(new ScrapingCR(s));
				jobs.add(j);
				j.start();
			}

			for (Thread job : jobs) {
				job.join();
			}

			saveToFile();
		}
		new PresentationPersister(loadFromFile()).execute();
	}

	private static void saveToFile() throws Exception {
		FileUtils.saveObject("", FILE, presentations);
	}

	private static List<Presentation> loadFromFile() throws Exception {
		return FileUtils.loadObject(FILE);
	}

	private static synchronized void addPresentations(List<Presentation> p) {
		synchronized (presentations) {
			presentations.addAll(p);
		}
	}

	// ##################################################################

	private String subPoint;
	private String url;
	private ChromeOptions options;

	private List<String> toCrawler;
	private boolean listFinish;

	private ScrapingCR(String section) {
		subPoint = section;
		url = "https://consultaremedios.com.br/medicamentos/" + section;
		options = new ChromeOptions();
		options.addArguments("--headless");
		listFinish = false;
	}

	@Override
	public void run() {
		Thread.currentThread().setName("Create Prod (" + subPoint + ")");
		createList();

		WebDriver driver = new ChromeDriver(options);
		String url;
		try {
			while (!listFinish || toCrawler == null || !toCrawler.isEmpty()) {
				while (toCrawler.isEmpty()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				}
				try {
					url = toCrawler.remove(toCrawler.size() - 1);
					log("GET " + url);
					driver.get(url);
					addPresentations(new ScrapingDrugsCR(driver).execute());
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

	private void createList() {
		toCrawler = new LinkedList<>();

		if (useList) {
			toCrawler.addAll(URLs);
			listFinish = true;
			return;
		}

		new Thread(() -> {
			Thread.currentThread().setName("Create List (" + subPoint + ")");
			WebDriver driver = new ChromeDriver(options);
			int pag = 0;

			pag++;
			log("GET ?pagina=" + pag);
			driver.get(url + "?pagina=" + pag);
			int count = 0;
			while (driver.getPageSource().contains("content-grid__item")) {
				for (WebElement e : driver.findElements(By.className("content-grid__item"))) {
					toCrawler.add(0, e.findElement(By.tagName("a")).getAttribute("href"));
					count++;
				}
				pag++;
				log("GET ?pagina=" + pag);
				driver.get(url + "?pagina=" + pag);
			}

			log("END: " + count + " products");
			listFinish = true;
		}).start();
	}

	private static synchronized void log(String msg) {
		System.out.println("-> " + Thread.currentThread().getName() + ": " + msg);
	}
}