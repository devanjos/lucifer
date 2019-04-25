package app.anjos;

import app.anjos.core.ConfigurePrice;
import app.anjos.core.DisablePresentations;
import app.anjos.core.DownloadBulas;
import app.anjos.core.RemoveImages;
import app.anjos.core.RunScripts;
import app.anjos.core.scraping.ScrapingCategories;
import app.anjos.core.scraping.ScrapingDrugs;

public class Main {

	public static void main(String[] args) throws Exception {
		ScrapingCategories.main(args);
		ScrapingDrugs.main(args);

		RemoveImages.main(args);
		ConfigurePrice.main(args);
		// ChangeEncartelados.main(args);
		DisablePresentations.main(args);
		RunScripts.main(args);
		DownloadBulas.main(args);
	}
}
