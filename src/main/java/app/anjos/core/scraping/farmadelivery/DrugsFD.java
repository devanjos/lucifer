package app.anjos.core.scraping.farmadelivery;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.openqa.selenium.By;
import app.anjos.core.scraping.AbstractScraping;
import app.anjos.model.Presentation;

public class DrugsFD extends AbstractScraping<Presentation> {

	public static void main(String[] args) throws Exception {
		AbstractScraping.setChromeDriver("chromedriver_73.exe");
		try (AbstractScraping<Presentation> scraping = new DrugsFD()) {
			scraping.execute();
		}
	}

	private static String[] URLS = new String[] {
			"https://www.farmadelivery.com.br/remedios-e-medicamentos" //
			/*,
			"https://www.farmadelivery.com.br/saude-e-bem-estar",
			"https://www.farmadelivery.com.br/dermocosmeticos",
			"https://www.farmadelivery.com.br/dermocosmeticos"
			 */
	};

	private String url;

	// Etapa 2
	private Queue<String> productsUrl;
	private boolean endQueuing;

	public DrugsFD() {}

	private DrugsFD(String url) {
		this.url = url;

		productsUrl = new LinkedList<>();
		endQueuing = false;
	}

	@Override
	public void execute() throws Exception {
		if (url == null) {
			for (String url : URLS) {
				try (DrugsFD scraping = new DrugsFD(url)) {
					scraping.execute();
				}
			}
			return;
		}

		executeScraping();
	}

	private void executeScraping() throws Exception {
		visit(url);

		findD(By.id("narrow-by-list"));
		List<String> urls = findUrlsInElement(url.replaceAll("\\.", "\\\\.") + "/.+");
		urls.removeIf((u) -> url.equals(u));

		List<Thread> jobs = starJobs(urls);

		new Thread(() -> {
			Thread.currentThread().setName("Category " + url + "; Scraping Product");
			consumeList();
		}).start();

		Thread.currentThread().setName("Category " + url);
		listProductsURL();

		for (Thread j : jobs)
			j.join();
	}

	private List<Thread> starJobs(List<String> urls) throws Exception {
		Thread job;
		List<Thread> jobs = new LinkedList<>();
		for (String _url : urls) {
			job = new Thread(() -> {
				try (DrugsFD scraping = new DrugsFD(_url)) {
					scraping.execute();
				} catch (Exception e) {
					System.err.println(_url);
					e.printStackTrace();
				}
			});
			jobs.add(job);
			job.start();
		}
		return jobs;
	}

	private void listProductsURL() throws InterruptedException {
		int page = 1;
		visit(url + "?p=" + page);

		String[] pager;
		boolean lastPage = false;
		while (!lastPage) {
			Thread.sleep(100);
			findD(By.className("pager"));
			pager = getText().replaceAll("[^0-9\\s]", "").replaceAll("\\s+", " ").split(" ");
			lastPage = pager.length == 2 && pager[0].equals(pager[1]);

			findD(By.className("category-products"));
			findAllE(By.className("item"));
			forEach((e) -> productsUrl.add(e.findElement(By.className("product-image")).getAttribute("href")));
			visit(url + "?p=" + (++page));
		}
		endQueuing = true;
	}

	private void consumeList() {
		String url;

		while (!endQueuing || productsUrl == null || !productsUrl.isEmpty()) {
			while (productsUrl.isEmpty()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
			url = productsUrl.poll();
			try (SubDrugsFD subScraping = new SubDrugsFD(url)) {
				subScraping.execute();
				addAllData(subScraping.getData());
			} catch (Exception e) {
				System.err.println(url);
				e.printStackTrace();
			}
		}
	}
}