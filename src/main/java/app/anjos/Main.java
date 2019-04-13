package app.anjos;

import app.anjos.core.ConfigurePrice;
import app.anjos.core.CreateDatabase;
import app.anjos.core.DisablePresentations;
import app.anjos.core.DownloadBulas;
import app.anjos.core.RemoveImages;
import app.anjos.core.Scripts;
import app.anjos.core.WebCrawler;
import app.anjos.core.crawler.CrawlerCategories;

public class Main {

	public static void main(String[] args) throws Exception {
		CreateDatabase.main(args);
		CrawlerCategories.main(args);
		WebCrawler.main(args);

		RemoveImages.main(args);
		ConfigurePrice.main(args);
		DisablePresentations.main(args);
		Scripts.main(args);
		DownloadBulas.main(args);
	}
}
