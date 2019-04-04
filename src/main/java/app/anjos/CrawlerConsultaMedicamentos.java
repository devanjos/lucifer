package app.anjos;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import org.openqa.selenium.chrome.ChromeOptions;
import app.anjos.model.Product;

public class CrawlerConsultaMedicamentos implements Runnable {

	private static final String URL = "https://consultaremedios.com.br";
	private static final String START_POINT = "https://consultaremedios.com.br/medicamentos/";
	//?pagina=1
	private static final String[] SUB_POINTS = new String[] { "0-9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	//private static final String[] SUB_POINTS = new String[] { "0-9" };

	private static List<Product> products = new LinkedList<>();

	public static void main(String[] args) throws Exception {
		System.setProperty("webdriver.chrome.driver", "chromedriver_73.exe");
		List<Thread> jobs = new LinkedList<>();
		Thread j;
		for (String s : SUB_POINTS) {
			j = new Thread(new CrawlerConsultaMedicamentos(START_POINT + s));
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

	private String startPoint;
	private ChromeOptions options;

	private List<String> toCrawler;
	private boolean listFinish;

	private CrawlerConsultaMedicamentos(String startPoint) {
		this.startPoint = startPoint;
		options = new ChromeOptions();
		options.addArguments("--headless");

		toCrawler = new LinkedList<>();
		listFinish = false;
	}

	@Override
	public void run() {
		createListThread().start();

		while (!listFinish || !toCrawler.isEmpty()) {
			while (toCrawler.isEmpty()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			System.out.println(toCrawler.remove(toCrawler.size() - 1)); //TODO
		}
	}

	private Thread createListThread() {
		return new Thread(() -> {
			for (int x = 0; x < 10; x++) {
				toCrawler.add(0, "teste" + x); //TODO
			}

			listFinish = true;
		});
	}
}