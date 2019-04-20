package app.anjos.core.scraping.consultaremedios;

import java.util.List;
import org.openqa.selenium.By;
import app.anjos.core.scraping.AbstractScraping;
import app.anjos.model.Category;

public class CategoriesCR extends AbstractScraping<Category> {

	private static String[] URLS = new String[] {
			"https://consultaremedios.com.br/categorias" //
			/*,
			"https://www.farmadelivery.com.br/saude-e-bem-estar",
			"https://www.farmadelivery.com.br/dermocosmeticos",
			"https://www.farmadelivery.com.br/dermocosmeticos"
			 */
	};

	@Override
	public void execute() throws Exception {
		for (String c : URLS)
			execute(c, null);
	}

	private void execute(String url, Category parent) throws Exception {
		visit(url);

		findD(By.className("category-header__title"));
		String name = getText();
		Category category = new Category(name);
		category.setParent(parent);
		addData(category);

		findD(By.className("category-link__list"));
		List<String> urls = findUrlsInElement(url.replace("/categorias", "").replaceAll("\\.", "\\\\.").replaceFirst("/c$", "") + "/.+");
		urls.removeIf((u) -> url.equals(u));
		for (String _url : urls)
			execute(_url, category);
	}
}