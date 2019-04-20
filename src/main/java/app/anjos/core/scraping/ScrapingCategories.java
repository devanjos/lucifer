package app.anjos.core.scraping;

import app.anjos.core.scraping.consultaremedios.CategoriesCR;
import app.anjos.model.Category;
import io.matob.database.exception.DatabaseException;
import io.matob.database.jpa.DAOJPA;
import io.matob.database.jpa.DAOJPAFactory;
import io.matob.database.jpa.EntityManagerController;

public class ScrapingCategories {

	private static DAOJPA<Category> dao;

	public static void main(String[] args) throws Exception {
		AbstractScraping.setChromeDriver("chromedriver_73.exe");
		AbstractScraping<Category> scraping = new CategoriesCR();

		EntityManagerController emc = new EntityManagerController();
		try {
			emc.begin();
			dao = DAOJPAFactory.createDAO(Category.class, emc);
			dao.setUseTransaction(false);

			scraping.execute();
			for (Category c : scraping.getData()) {
				if (dao.executeSingleQuery("name", c.getName()) == null)
					dao.save(c);
			}

			emc.commit();

			printAll();
		} catch (Exception ex) {
			emc.rollback();
			throw ex;
		} finally {
			emc.close();
			scraping.close();
		}
	}

	private static void printAll() throws DatabaseException {
		dao.findAll(null).forEach((c) -> System.out.println(c));
	}
}
