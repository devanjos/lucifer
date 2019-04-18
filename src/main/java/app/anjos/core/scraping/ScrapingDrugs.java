package app.anjos.core.scraping;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import app.anjos.core.scraping.consultaremedios.DrugsCR;
import app.anjos.core.scraping.farmadelivery.DrugsFD;
import app.anjos.model.Presentation;
import io.matob.database.jpa.DAOJPA;
import io.matob.database.jpa.DAOJPAFactory;
import io.matob.database.jpa.EntityManagerController;

public class ScrapingDrugs {

	private static DAOJPA<Presentation> dao;

	public static void main(String[] args) throws Exception {
		AbstractScraping.setChromeDriver("chromedriver_73.exe");
		AbstractScraping<Presentation> scrapingCR = new DrugsCR();
		AbstractScraping<Presentation> scrapingFD = new DrugsFD();

		EntityManagerController emc = new EntityManagerController();
		try {
			emc.begin();
			dao = DAOJPAFactory.createDAO(Presentation.class, emc);
			dao.setUseTransaction(false);

			List<Presentation> presentationsCR = scrapingCR.execute();
			Map<String, Presentation> presentationsMapCR = new HashMap<>();
			presentationsCR.forEach((p) -> presentationsMapCR.put(p.getCode(), p));
			List<Presentation> presentationsFD = scrapingFD.execute();
			Map<String, Presentation> presentationsMapFD = new HashMap<>();
			presentationsFD.forEach((p) -> presentationsMapFD.put(p.getCode(), p));

			List<Presentation> presentations = new LinkedList<>();

			// TODO - Merge

			for (Presentation p : presentations)
				dao.save(p);

			emc.commit();
		} catch (Exception ex) {
			emc.rollback();
			throw ex;
		} finally {
			emc.close();
			scrapingCR.close();
			scrapingFD.close();
		}
	}
}
