package app.anjos.core.scraping;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import app.anjos.core.scraping.consultaremedios.CategoriesCR;
import app.anjos.model.Category;
import io.matob.database.exception.DatabaseException;
import io.matob.database.jpa.DAOJPA;
import io.matob.database.jpa.DAOJPAFactory;
import io.matob.database.jpa.EntityManagerController;
import io.matob.tools.FileUtils;

public class ScrapingCategories {

	private static final boolean useFile = true;
	private static DAOJPA<Category> dao;
	private static List<Category> categories;

	public static void main(String[] args) throws Exception {
		loadData();

		EntityManagerController emc = new EntityManagerController();
		try {
			emc.begin();
			dao = DAOJPAFactory.createDAO(Category.class, emc);
			dao.setUseTransaction(false);

			for (Category c : categories) {
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
		}
	}

	private static void printAll() throws DatabaseException {
		dao.findAll(null).forEach((c) -> System.out.println(c));
	}

	private static void loadData() throws Exception {
		String file = "output/categories.obj";
		if (!useFile || !new File(file).exists()) {
			categories = new LinkedList<>();

			AbstractScraping.setChromeDriver("chromedriver.exe");
			try (AbstractScraping<Category> scraping = new CategoriesCR();) {
				scraping.execute();
				categories = scraping.getData();
				FileUtils.saveObject("", file, categories);
			}
		} else {
			categories = FileUtils.loadObject(file);
		}
	}
}
