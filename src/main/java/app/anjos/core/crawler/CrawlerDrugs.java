package app.anjos.core.crawler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import app.anjos.model.Category;
import app.anjos.model.Drug;
import app.anjos.model.Image;
import app.anjos.model.Presentation;
import app.anjos.model.Speciality;
import app.anjos.model.Substance;
import app.anjos.model.Supplier;

public class CrawlerDrugs {

	private WebDriver driver;

	public CrawlerDrugs(WebDriver driver) {
		this.driver = driver;
	}

	public List<Presentation> execute() throws IOException {
		List<WebElement> elements;
		WebElement element;
		String redirectUrl;
		Drug p = new Drug();

		redirectUrl = driver.getCurrentUrl();

		element = driver.findElement(By.className("product-header__title"));
		String productName = element.getText().replaceAll("\"", "");
		p.setName(productName); // Nome do Produto

		elements = driver.findElements(By.className("js-offer-set"));
		List<Presentation> presentations = new LinkedList<>();
		Presentation presentation;

		for (WebElement e : elements) {
			element = e.findElement(By.className("presentation-offer-info__description"));
			presentation = new Presentation();
			presentation.setCode(e.getAttribute("data-ean"));
			presentation.setMs(e.findElement(By.className("presentation-offer-info__ms")).findElement(By.tagName("strong")).getText());
			presentation.setName(element.findElement(By.tagName("a")).getAttribute("title").replace(productName, "").trim()); // Nome da Apresentação

			element = e.findElement(By.className("presentation-offer-info__img"));
			Image image = new Image("jpg", getByteArrayFromImageURL(element.getAttribute("data-src")));
			if (image.getData() != null && !image.getData().isEmpty())
				presentation.setImage(image); // URL da imagem

			presentation.setProduct(p);
			presentation.setDataSource("consultaremedios.com.br");
			presentations.add(presentation);
		}

		elements = driver.findElements(By.className("extra-infos-block"));
		String block;
		String value;
		for (WebElement e : elements) {
			block = e.findElement(By.tagName("h3")).getText();
			value = e.findElement(By.tagName("p")).getText();

			if ("Fabricante".equals(block) || "Comercializado".equals(block))
				p.setSupplier(new Supplier(value)); // Fabricante
			else if ("Tipo do Medicamento".equals(block))
				p.setType(getDrugType(value)); // Tipo de Medicamento
			else if ("Necessita de Receita".equals(block))
				p.setPrescription(value); // Necessita de Receita
			else if ("Princípio Ativo".equals(block)) {
				String[] substances = value.split("\\+");
				List<Substance> substancesList = new LinkedList<>();
				for (String s : substances)
					substancesList.add(new Substance(s.trim()));
				p.setSubstances(substancesList);
			} else if ("Categoria do Medicamento".equals(block)) {
				p.setCategory(new Category(value)); // Categoria do Medicamento
			} else if ("Especialidades".equals(block)) {
				String[] specialities = value.split(",");
				List<Speciality> specialitiesList = new LinkedList<>();
				for (String s : specialities)
					specialitiesList.add(new Speciality(s.trim()));
				p.setSpecialities(specialitiesList); // Especialidades
			}
		}

		driver.get(redirectUrl.substring(0, redirectUrl.length() - 1) + "bula");
		if (!driver.getPageSource().contains("Error 404")) {
			element = driver.findElement(By.className("leaflet-content"));
			String funcionamento = element.findElement(By.id("para-que-serve")).getText().replace("Para que serve o ", "");
			funcionamento = "Como o " + funcionamento + " funciona?";
			String indicacoes = element.findElement(By.tagName("div")).getText();
			if (indicacoes.contains(funcionamento)) {
				p.setIndications(indicacoes.substring(0, indicacoes.indexOf(funcionamento))); // Para que Serve?
				funcionamento = indicacoes.substring(indicacoes.indexOf(funcionamento) + funcionamento.length());
				p.setHowWorks((indicacoes.equals(funcionamento) ? "" : funcionamento)); // Como Funciona?
			} else
				p.setIndications(indicacoes);

			elements = driver.findElements(By.className("highlight-action"));
			if (!elements.isEmpty() && elements.get(0).getText().equals("Bula em PDF")) {
				element = elements.get(0);
				String bulaUrl = element.getAttribute("href");
				bulaUrl = bulaUrl.replace("https://docs.google.com/gview?url=", "");
				bulaUrl = bulaUrl.substring(0, bulaUrl.indexOf("?"));

				p.setBula(bulaUrl);
			}
		}

		return presentations;
	}

	private Character getDrugType(String value) {
		if (value.contains("Referência"))
			return 'R';
		if (value.contains("Genérico"))
			return 'G';
		if (value.contains("Intercambiável"))
			return 'I';
		if (value.contains("Similar"))
			return 'S';
		if (value.contains("Específico"))
			return 'E';
		if (value.contains("Biológico"))
			return 'B';
		if (value.contains("Novo"))
			return 'N';
		if (value.contains("Radio"))
			return 'D';
		else
			return 'O';
	}

	private static String getByteArrayFromImageURL(String urlStr) {
		try {
			if (urlStr.toLowerCase().contains("product_images_configuration"))
				return null;

			URL url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
			BufferedImage image = ImageIO.read(connection.getInputStream());

			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, "png", os);
			return Base64.getEncoder().encodeToString(os.toByteArray());
		} catch (Exception e) {
			return null;
		}
	}

	private static void downloadBula(String ean, String urlStr) throws IOException {
		/*URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url
				.openConnection();
		connection.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
		
		String fileName = ean + ".pdf";
		Path targetPath = new File("output/bulas/" + fileName).toPath();
		Files.copy(connection.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);*/
	}
}