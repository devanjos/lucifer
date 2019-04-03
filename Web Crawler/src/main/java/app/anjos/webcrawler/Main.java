package app.anjos.webcrawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Main {

	private static final String INPUT = "files/input.txt";
	private static final String SEARCH_URL = "https://consultaremedios.com.br/busca?termo=";

	private static ChromeOptions OPTIONS;

	private static final boolean SINGLE_THREAD = false;
	private static int LOCK = 0;

	public static void main(String[] args) throws Exception {
		System.setProperty("webdriver.chrome.driver", "chromedriver_73.exe");
		OPTIONS = new ChromeOptions();
		OPTIONS.addArguments("--headless");

		try (BufferedReader reader = new BufferedReader(new FileReader(INPUT))) {
			String line[];
			while (reader.ready()) {
				line = reader.readLine().split(";");

				LOCK++;
				while (!SINGLE_THREAD && LOCK > 100) {
					Thread.sleep(500);
				}

				Thread.sleep(2000);
				if (line.length > 1)
					start(line[0], line[1]);
				else
					start(line[0], "");
			}
		}
	}

	private static void start(String ean, String ms) throws Exception {
		if (SINGLE_THREAD)
			System.out.println(Thread.currentThread().getId() + " - " + run(ean, ms));
		else {
			new Thread(() -> {
				try {
					System.out.println(Thread.currentThread().getId() + " - " + run(ean, ms));
				} catch (Exception e) {
					System.err.print(Thread.currentThread().getId() + " - Erro: " + ean + "\t");
					e.printStackTrace();
				} finally {
					LOCK--;
				}
			}).start();
		}
	}

	private static Product run(String ean, String ms) throws Exception {
		List<WebElement> elements;
		WebElement element;
		String redirectUrl;
		Product p = new Product();
		p.ean = ean;
		p.ms = ms;

		WebDriver driver = new ChromeDriver(OPTIONS);

		try {
			System.out.println(Thread.currentThread().getId() + " - EAN: " + ean);
			driver.get(SEARCH_URL + ean);

			elements = driver.findElements(By.id("result-subtitle"));
			if (!elements.isEmpty() && elements.get(0).getText().equals("Desculpe, mas não encontramos nenhum resultado para sua busca"))
				throw new Exception("404: Not Result " + ean);

			redirectUrl = driver.getCurrentUrl();
			if (!redirectUrl.matches("https://consultaremedios\\.com\\.br/.+/p"))
				throw new Exception("412: Url não tratada: " + redirectUrl);

			element = driver.findElement(By.className("product-header__title"));
			String productName = element.getText().replaceAll("\"", "");
			p.nome = productName; // Nome do Produto

			elements = driver.findElements(By.className("js-offer-set"));
			boolean exists = false;
			for (WebElement e : elements) {
				if (ean.equals(e.getAttribute("data-ean"))) {
					exists = true;
					element = e.findElement(By.className("presentation-offer-info__description"));
					p.apresentacao = element.findElement(By.tagName("a")).getAttribute("title").replace(productName, "").trim(); // Nome da Apresentação

					element = e.findElement(By.className("presentation-offer-info__img"));
					p.img = element.getAttribute("src"); // URL da imagem
				}
			}

			if (!exists)
				throw new Exception("404: EAN: " + ean + "\tMS: " + ms);

			elements = driver.findElements(By.className("extra-infos-block"));
			String block;
			String value;
			for (WebElement e : elements) {
				block = e.findElement(By.tagName("h3")).getText();
				value = e.findElement(By.tagName("p")).getText();

				if ("Fabricante".equals(block))
					p.fabricante = value; // Fabricante
				else if ("Tipo do Medicamento".equals(block))
					p.tipoMedicamento = value; // Tipo de Medicamento
				else if ("Necessita de Receita".equals(block))
					p.receita = value.toLowerCase().startsWith("sim"); // Necessita de Receita
				else if ("Princípio Ativo".equals(block))
					p.principioAtivo = value; // Princípio Ativo
				else if ("Categoria do Medicamento".equals(block))
					p.categoria = value; // Categoria do Medicamento
				else if ("Especialidades".equals(block))
					p.especialidades = value; // Especialidades
			}

			/*
			element = driver.findElement(By.id("indication-collapse"));
			String indication = element.getText();
			p.indicacoes = indication.replaceFirst("(.*)(\\\nComo o .+ funciona\\?)(.+)", "$1"); // Para que Serve?
			
			String funcionamento = indication.replaceFirst("(.*)(\\\nComo o .+ funciona\\?)(.+)", "$3");
			p.funcionamento = (indication.equals(funcionamento) ? "" : funcionamento); // Como Funciona?
			 */

			driver.get(redirectUrl.substring(0, redirectUrl.length() - 1) + "bula");

			element = driver.findElement(By.className("leaflet-content"));
			String funcionamento = element.findElement(By.id("para-que-serve")).getText().replace("Para que serve o ", "");
			funcionamento = "Como o " + funcionamento + " funciona?";
			String indicacoes = element.findElement(By.tagName("div")).getText();
			if (indicacoes.contains(funcionamento))
				p.indicacoes = indicacoes.substring(0, indicacoes.indexOf(funcionamento)); // Para que Serve?
			else
				p.indicacoes = indicacoes;

			funcionamento = indicacoes.substring(indicacoes.indexOf(funcionamento) + funcionamento.length());
			p.funcionamento = (indicacoes.equals(funcionamento) ? "" : funcionamento); // Como Funciona?

			elements = driver.findElements(By.className("highlight-action"));
			if (!elements.isEmpty()) {
				element = elements.get(0);
				String bulaUrl = element.getAttribute("href");
				bulaUrl = bulaUrl.replace("https://docs.google.com/gview?url=", "");
				bulaUrl = bulaUrl.substring(0, bulaUrl.indexOf("?"));
				p.bula = bulaUrl;
			}

			return p;
		} finally {
			driver.close();
			driver.quit();
		}
	}

	private static class Product {

		String ean;
		String ms;
		String nome;
		String apresentacao;
		String tipoMedicamento;
		String img;
		String fabricante;
		boolean receita;
		String principioAtivo;
		String categoria;
		String especialidades;
		String indicacoes;
		String funcionamento;
		String bula;

		@Override
		public String toString() {
			return "\n\t" + ean
					+ "\n\t" + ms
					+ "\n\t" + nome
					+ "\n\t" + apresentacao
					+ "\n\t" + tipoMedicamento
					+ "\n\t" + img
					+ "\n\t" + fabricante
					+ "\n\t" + receita
					+ "\n\t" + principioAtivo
					+ "\n\t" + categoria
					+ "\n\t" + especialidades
					+ "\n\t" + indicacoes
					+ "\n\t" + funcionamento
					+ "\n\t" + bula;
		}
	}
}
