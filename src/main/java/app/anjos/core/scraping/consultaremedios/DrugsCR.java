package app.anjos.core.scraping.consultaremedios;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.openqa.selenium.By;
import app.anjos.core.scraping.AbstractScraping;
import app.anjos.model.Presentation;

public class DrugsCR extends AbstractScraping<Presentation> {

	private static final String BASE_URL = "https://consultaremedios.com.br/medicamentos/";
	private static final String[] SECTIONS = new String[] { "0-9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
			"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

	// Etapa 1
	private String section;

	// Etapa 2
	private Queue<String> productsUrl;
	private boolean endQueuing;

	private boolean singleThread = false;

	public DrugsCR() {}

	public DrugsCR(String section) {
		this.section = section;

		productsUrl = new LinkedList<>();
		endQueuing = false;
	}

	public void setSingleThread(boolean singleThread) {
		this.singleThread = singleThread;
	}

	@Override
	public void execute() throws Exception {
		if (section != null) {
			scrapingSection(section);
			return;
		}

		Thread job;
		List<Thread> jobs = new LinkedList<>();
		for (String s : SECTIONS) {
			job = new Thread(() -> {
				Thread.currentThread().setName("Section " + section);
				try (DrugsCR scraping = new DrugsCR(s)) {
					scraping.execute();
					addAllData(scraping.getData());
				} catch (Exception e) {
					System.err.println(section);
					e.printStackTrace();
				}
			});
			jobs.add(job);
			job.start();
			if (singleThread)
				job.join();
		}

		for (Thread j : jobs)
			j.join();
	}

	private void scrapingSection(String section) {
		new Thread(() -> {
			Thread.currentThread().setName("Section " + section + "; Create Queue");
			listProductsURL();
		}).start();

		Thread.currentThread().setName("Section " + section + "; Scraping Product");
		consumeList();
	}

	private void listProductsURL() {
		int page = 1;
		visit(BASE_URL + section + "?pagina=" + page);
		while (!findAllD(By.className("content-grid__item")).isEmpty()) {
			forEach((e) -> productsUrl.add(e.findElement(By.tagName("a")).getAttribute("href")));
			visit(BASE_URL + section + "?pagina=" + (++page));
		}
		endQueuing = true;
	}

	private void consumeList() {
		String url;

		try (SubDrugsCR subScraping = new SubDrugsCR()) {
			while (!endQueuing || productsUrl == null || !productsUrl.isEmpty()) {
				while (productsUrl.isEmpty()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				}

				url = productsUrl.poll();

				try {
					subScraping.clear();
					subScraping.setUrl(url);
					subScraping.execute();
					addAllData(subScraping.getData());
				} catch (Exception e) {
					System.err.println(url);
					e.printStackTrace();
				}
			}
		}
	}
}