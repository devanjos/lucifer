package app.anjos.core.scraping.farmadelivery;

import java.util.LinkedList;
import java.util.List;
import org.openqa.selenium.By;
import app.anjos.core.scraping.AbstractScraping;
import app.anjos.model.Category;

public class CategoriesFD extends AbstractScraping<Category> {

	private String[] urls;
	private List<Category> categories;

	public CategoriesFD() {
		urls = new String[] {
				"https://www.farmadelivery.com.br/remedios-e-medicamentos" //
				/*,
				"https://www.farmadelivery.com.br/saude-e-bem-estar",
				"https://www.farmadelivery.com.br/dermocosmeticos",
				"https://www.farmadelivery.com.br/dermocosmeticos"
				 */
		};

		categories = new LinkedList<>();
	}

	@Override
	public List<Category> execute() throws Exception {
		for (String c : urls)
			execute(c, null);
		return categories;
	}

	private void execute(String url, Category parent) throws Exception {
		visit(url);

		find(By.className("category-title"));
		String name = getCategoryName(getText());
		Category category = new Category(name);
		category.setParent(parent);
		categories.add(category);

		find(By.id("narrow-by-list"));
		List<String> urls = findUrlsInElement(url.replaceAll("\\.", "\\\\.") + "/.+");
		urls.removeIf((u) -> url.equals(u));
		for (String _url : urls)
			execute(_url, category);
	}

	public static String getCategoryName(String name) {
		if ("REMÉDIOS E MEDICAMENTOS".equals(name))
			return "Medicamentos";
		if ("Remédio e Colírio para Olhos".equals(name))
			return "Doenças dos Olhos";
		if ("Remédio e Colírio para Glaucoma".equals(name))
			return "Glaucoma";
		if ("Remédio e Xarope para Tosse".equals(name))
			return "Tosse";

		return name.replaceFirst("Remédios? ((p/)|([pP]ara)|(de)) (.+)", "$5").trim();
	}
}