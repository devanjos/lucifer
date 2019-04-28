package app.anjos.core.scraping.consultaremedios;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import org.openqa.selenium.By;
import app.anjos.core.scraping.AbstractScraping;
import app.anjos.model.Category;
import app.anjos.model.Drug;
import app.anjos.model.Image;
import app.anjos.model.Presentation;
import app.anjos.model.Speciality;
import app.anjos.model.Substance;
import app.anjos.model.Supplier;

public class SubDrugsCR extends AbstractScraping<Presentation> {

	private String url;

	public SubDrugsCR() {}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void execute() {
		visit(url);
		Drug drug = new Drug();

		findD(By.className("product-header__title"));
		String productName = getText().replaceAll("\"", "");
		drug.setName(productName); // Nome do Produto

		findAllD(By.className("js-offer-set"));
		forEach((e) -> {
			Presentation presentation = new Presentation();

			String ean = e.getAttribute("data-ean").replaceFirst("^0+", "");
			presentation.setCode(ean);

			findE(e, By.className("presentation-offer-info__ms"));
			findE(By.tagName("strong"));
			presentation.setMs(getText());

			findE(e, By.className("presentation-offer-info__description"));
			findE(By.tagName("a"));
			presentation.setName(getAttribute("title").replace(productName, "").trim()); // Nome da Apresentação

			findE(e, By.className("presentation-offer-info__img"));
			Image image = getImage(getAttribute("data-src"));
			if (image != null && image.getData() != null && !image.getData().isEmpty())
				presentation.setImage(image); // URL da imagem

			presentation.setProduct(drug);
			presentation.setDataSource("consultaremedios.com.br");
			addData(presentation);
		});

		boolean[] buscarEspecialidadesBulaPro = new boolean[] { false };
		findAllD(By.className("extra-infos-block"));
		forEach((e) -> {
			setElement(e);

			String block = findE(By.tagName("h3"), false).getText();
			String value = findE(By.tagName("p"), false).getText();

			if ("Fabricante".equals(block) || "Comercializado".equals(block))
				drug.setSupplier(new Supplier(value)); // Fabricante
			else if ("Tipo do Medicamento".equals(block))
				drug.setType(getDrugType(value)); // Tipo de Medicamento
			else if ("Necessita de Receita".equals(block))
				drug.setPrescription(value); // Necessita de Receita
			else if ("Princípio Ativo".equals(block)) {
				String[] substances = value.split("\\+");
				List<Substance> substancesList = new LinkedList<>();
				for (String s : substances)
					substancesList.add(new Substance(s.trim()));
				drug.setSubstances(substancesList);
			} else if ("Categoria do Medicamento".equals(block)) {
				drug.setCategory(new Category(value)); // Categoria do Medicamento
			} else if ("Especialidades".equals(block)) {
				if (!value.contains("…")) {
					String[] specialities = value.split(",");
					List<Speciality> specialitiesList = new LinkedList<>();
					for (String s : specialities)
						specialitiesList.add(new Speciality(s.trim()));
					drug.setSpecialities(specialitiesList); // Especialidades
				} else {
					buscarEspecialidadesBulaPro[0] = true;
				}
			}
		});

		visit(getCurrentUrl().substring(0, getCurrentUrl().length() - 1) + "bula");
		if (getPageSource().contains("Error 404"))
			return;

		findD(By.className("leaflet-content"));
		String funcionamento = findE(By.id("para-que-serve"), false).getText().replace("Para que serve o ", "");
		funcionamento = "Como o " + funcionamento + " funciona?";
		String indicacoes = findE(By.tagName("div"), false).getText();
		if (indicacoes.contains(funcionamento)) {
			drug.setIndications(indicacoes.substring(0, indicacoes.indexOf(funcionamento))); // Para que Serve?
			funcionamento = indicacoes.substring(indicacoes.indexOf(funcionamento) + funcionamento.length());
			drug.setHowWorks((indicacoes.equals(funcionamento) ? "" : funcionamento)); // Como Funciona?
		} else
			drug.setIndications(indicacoes);
		findD(By.className("leaflet-content"));
		String contraindicacoes = findE(By.id("contraindicacao")).getText();
		if (existsElement("temp")) {
			contraindicacoes = findAllE(By.tagName("div"), false).get(1).getText();
			drug.setContraindications(contraindicacoes);
		}

		findAllD(By.className("highlight-action"));
		if (existsElements() && getElements(0).getText().equals("Bula em PDF")) {
			String bulaUrl = getElements(0).getAttribute("href");
			bulaUrl = bulaUrl.replace("https://docs.google.com/gview?url=", "");
			bulaUrl = bulaUrl.substring(0, bulaUrl.indexOf("?"));
			drug.setBulaSource(bulaUrl);
		}

		if (buscarEspecialidadesBulaPro[0]) {
			visit(getCurrentUrl().replace("https://", "https://pro.").substring(0, getCurrentUrl().length() - 1));
			findD(By.id("product-information"));
			findAllE(By.tagName("ul"));
			forEach((e) -> {
				if (e.getText().startsWith("Especialidades")) {
					String[] specialities = e.findElement(By.className("list-info-prod-right")).getText().split(",");
					List<Speciality> specialitiesList = new LinkedList<>();
					for (String s : specialities)
						specialitiesList.add(new Speciality(s.trim()));
					drug.setSpecialities(specialitiesList); // Especialidades
				}
			});
		}
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

	public static void main(String[] args) {

	}

	private static Image getImage(String url) {
		try {
			if (url.toLowerCase().contains("product_images_configuration"))
				return null;

			String format = "jpg";
			if (url.matches("(.+)(\\?\\d+)"))
				url = url.substring(0, url.lastIndexOf('?'));
			if (url.matches("(.+)(\\.(jpg|png|jpeg)"))
				format = url.substring(url.lastIndexOf('.'));

			URL _url = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) _url
					.openConnection();
			connection.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
			BufferedImage image = ImageIO.read(connection.getInputStream());

			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, format, os);
			String data = Base64.getEncoder().encodeToString(os.toByteArray());

			return new Image(format, data);
		} catch (Exception e) {
			return null;
		}
	}
}