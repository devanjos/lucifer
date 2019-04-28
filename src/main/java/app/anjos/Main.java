package app.anjos;

import app.anjos.core.ConfigurePrice;
import app.anjos.core.DisablePresentations;
import app.anjos.core.DownloadBulas;
import app.anjos.core.RemoveImages;
import app.anjos.core.RunScripts;

public class Main {

	public static void main(String[] args) throws Exception {
		//crapingCategories.main(args);
		//ScrapingDrugs.main(args);

		RemoveImages.main(args);
		ConfigurePrice.main(args);
		// ChangeEncartelados.main(args);
		DisablePresentations.main(args);
		RunScripts.main(args);
		DownloadBulas.main(args);
	}
}
