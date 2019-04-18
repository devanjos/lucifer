package app.anjos.core.scraping;

import app.anjos.core.scraping.farmadelivery.CategoriesFD;
import app.anjos.model.Category;
import io.matob.database.jpa.DAOJPA;
import io.matob.database.jpa.DAOJPAFactory;
import io.matob.database.jpa.EntityManagerController;

public class ScrapingCategories {

	private static DAOJPA<Category> dao;

	public static void main(String[] args) throws Exception {
		AbstractScraping.setChromeDriver("chromedriver_73.exe");
		AbstractScraping<Category> scraping = new CategoriesFD();

		EntityManagerController emc = new EntityManagerController();
		try {
			emc.begin();
			dao = DAOJPAFactory.createDAO(Category.class, emc);
			dao.setUseTransaction(false);

			for (Category c : scraping.execute())
				dao.save(c);

			emc.commit();
		} catch (Exception ex) {
			emc.rollback();
			throw ex;
		} finally {
			emc.close();
			scraping.close();
		}
	}
}
