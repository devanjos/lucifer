package app.anjos;

import app.anjos.core.CreateDatabase;
import app.anjos.core.DisableProducts;
import app.anjos.core.UpdatePrice;
import app.anjos.core.WebCrawler;
import app.anjos.core.crawler.CrawlerCategory;

public class Main {

	public static void main(String[] args) throws Exception {
		CreateDatabase.main(args);
		CrawlerCategory.main(args);
		WebCrawler.main(args);
		UpdatePrice.main(args);
		DisableProducts.main(args);
	}
}
