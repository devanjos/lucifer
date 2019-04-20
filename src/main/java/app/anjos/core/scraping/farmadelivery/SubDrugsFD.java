package app.anjos.core.scraping.farmadelivery;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.openqa.selenium.By;
import app.anjos.core.scraping.AbstractScraping;
import app.anjos.model.Drug;
import app.anjos.model.Image;
import app.anjos.model.Presentation;

public class SubDrugsFD extends AbstractScraping<Presentation> {

	private String url;

	public SubDrugsFD(String url) {
		this.url = url;
	}

	@Override
	public void execute() {
		visit(url);
		Drug drug = new Drug();

		findD(By.className("product-name"));
		findE(By.tagName("h1"));
		drug.setName(getText());

		Presentation presentation = new Presentation();
		presentation.setName(getText());

		findD(By.className("codigo_barras"));
		findE(By.className("data"));
		presentation.setCode((getText() != null) ? getText().replaceFirst("^0+", "") : null);

		findD(By.className("nr_ministerio_da_saude"));
		findE(By.className("data"));
		presentation.setMs((getText() != null) ? getText().replaceFirst("^0+", "") : null);

		findD(By.className("tx_principal_indicacao"));
		findE(By.className("data"));
		drug.setIndications((getText() != null) ? getText().replaceFirst("^0+", "") : null);

		findD(By.className("tx_contra_indicacao"));
		findE(By.className("data"));
		drug.setContraindications((getText() != null) ? getText().replaceFirst("^0+", "") : null);

		findD(By.className("zoomWindow"));
		if (existsElement()) {
			String img = getAttribute("style");
			img = img.replaceFirst("(.+)(background-image: url\\()([^)]+)(.+)", "$3").replace("&quot;", "").replace("\"", "");
			Image image = new Image("jpg", getByteArrayFromImageURL(getAttribute("data-src")));
			if (image.getData() != null && !image.getData().isEmpty())
				presentation.setImage(image); // URL da imagem
		}

		presentation.setProduct(drug);
		presentation.setDataSource("farmadelivery.com.br");
		addData(presentation);
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
}