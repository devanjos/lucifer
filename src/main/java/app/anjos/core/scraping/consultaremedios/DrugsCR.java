package app.anjos.core.scraping.consultaremedios;

import java.util.LinkedList;
import java.util.List;
import org.openqa.selenium.By;
import app.anjos.core.scraping.AbstractScraping;
import app.anjos.model.Category;
import app.anjos.model.Presentation;

public class DrugsCR extends AbstractScraping<Presentation> {

	private String[] urls;
	private List<Presentation> presentations;

	public DrugsCR() {
		urls = new String[] {
				"https://consultaremedios.com.br/categorias" //
				/*,
				"https://www.farmadelivery.com.br/saude-e-bem-estar",
				"https://www.farmadelivery.com.br/dermocosmeticos",
				"https://www.farmadelivery.com.br/dermocosmeticos"
				 */
		};

		presentations = new LinkedList<>();
	}

	@Override
	public List<Presentation> execute() throws Exception {
		for (String c : urls)
			execute(c, null);
		return presentations;
	}

	private void execute(String url, Category parent) throws Exception {
		visit(url);

		find(By.className("category-header__title"));
		String name = getText();
		Category category = new Category(name);
		category.setParent(parent);
		//presentations.add(category);

		find(By.className("category-link__list"));
		List<String> urls = findUrlsInElement(url.replace("/categorias", "").replaceAll("\\.", "\\\\.") + "/.+");
		urls.removeIf((u) -> url.equals(u));
		for (String _url : urls)
			execute(_url, category);
	}
}