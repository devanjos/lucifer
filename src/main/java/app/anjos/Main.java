package app.anjos;

import app.anjos.core.CreateDatabase;
import app.anjos.core.Scripts;
import app.anjos.core.RemoveImages;
import app.anjos.core.UpdatePrice;
import app.anjos.core.WebCrawler;
import app.anjos.core.crawler.CrawlerCategory;

public class Main {

	public static void main(String[] args) throws Exception {
		CreateDatabase.main(args);
		CrawlerCategory.main(args);
		WebCrawler.main(args);
		UpdatePrice.main(args);
		Scripts.main(args);
		RemoveImages.main(args);
	}
}
