package app.anjos.core.scraping;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import app.anjos.core.PresentationPersister;
import app.anjos.core.scraping.consultaremedios.DrugsCR;
import app.anjos.model.Drug;
import app.anjos.model.Presentation;
import io.matob.tools.FileUtils;

public class ScrapingDrugs {

	private static final boolean useFile = true;

	private static Map<String, Presentation> presentationsMapCR;
	private static Map<String, Presentation> presentationsMapFD;

	private static boolean singleThread = false;

	public static void main(String[] args) throws Exception {
		AbstractScraping.setChromeDriver("chromedriver.exe");
		loadData();

		List<Presentation> presentations = new LinkedList<>();

		Drug d1, d2;
		Presentation p2;
		boolean merged;
		for (Presentation p1 : presentationsMapCR.values()) {
			if (presentationsMapFD.containsKey(p1.getCode())) {
				p2 = presentationsMapFD.get(p1.getCode());
				d1 = (Drug) p1.getProduct();
				d2 = (Drug) p2.getProduct();
				merged = false;

				if (p1.getImage() == null) {
					p1.setImage(p2.getImage());
					merged = true;
				}

				if (d1.getIndications() == null || d1.getIndications().isEmpty()) {
					d1.setIndications(d2.getIndications());
					merged = true;
				}

				if (d1.getHowWorks() == null || d1.getHowWorks().isEmpty()) {
					d1.setHowWorks(d2.getHowWorks());
					merged = true;
				}

				if (d1.getContraindications() == null || d1.getContraindications().isEmpty()) {
					d1.setContraindications(d2.getContraindications());
					merged = true;
				}

				if (merged)
					p1.setDataSource(p1.getDataSource() + "+" + p2.getDataSource());
			}

			presentations.add(p1);
		}

		new PresentationPersister(presentations).execute();
	}

	private static void loadData() throws Exception {
		String fileCR = "output/presentationsCR.obj";
		if (!useFile || !new File(fileCR).exists()) {
			presentationsMapCR = new HashMap<>();
			try (DrugsCR scrapingCR = new DrugsCR()) {
				scrapingCR.setSingleThread(singleThread);
				scrapingCR.execute();
				List<Presentation> presentationsCR = scrapingCR.getData();
				presentationsCR.forEach((p) -> presentationsMapCR.put(p.getCode(), p));
				FileUtils.saveObject("", fileCR, presentationsMapCR);
			}
		} else {
			presentationsMapCR = FileUtils.loadObject(fileCR);
		}

		presentationsMapFD = new HashMap<>();
		/*String fileFD = "output/presentationsFD.obj";
		if (!useFile || !new File(fileFD).exists()) {
			presentationsMapFD = new HashMap<>();
			try (AbstractScraping<Presentation> scrapingFD = new DrugsFD()) {
				scrapingFD.execute();
				List<Presentation> presentationsFD = scrapingFD.getData();
				presentationsFD.forEach((p) -> presentationsMapFD.put(p.getCode(), p));
				FileUtils.saveObject("", fileFD, presentationsMapFD);
			}
		} else {
			presentationsMapFD = FileUtils.loadObject(fileFD);
		}*/
	}
}
