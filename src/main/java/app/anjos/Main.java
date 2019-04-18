package app.anjos;

import app.anjos.core.ConfigurePrice;
import app.anjos.core.DisablePresentations;
import app.anjos.core.RemoveImages;
import app.anjos.core.Scripts;
import app.anjos.core.old.ScrapingCR;
import app.anjos.core.scraping.ScrapingBulas;
import app.anjos.core.scraping.ScrapingCategories;

public class Main {

	public static void main(String[] args) throws Exception {
		ScrapingCategories.main(args);
		ScrapingCR.main(args);

		RemoveImages.main(args);
		ConfigurePrice.main(args);
		DisablePresentations.main(args);
		Scripts.main(args);
		ScrapingBulas.main(args);
	}
}
