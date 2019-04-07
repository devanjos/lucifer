package app.anjos.core.crawler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import app.anjos.model.Category;
import app.anjos.model.Drug;
import app.anjos.model.Presentation;
import app.anjos.model.Product;
import app.anjos.model.Speciality;
import app.anjos.model.Substance;
import app.anjos.model.Supplier;
import io.matob.database.exception.DatabaseException;
import io.matob.database.jpa.DAOJPA;
import io.matob.database.jpa.DAOJPAFactory;
import io.matob.database.jpa.EntityManagerController;
import io.matob.database.jpa.JPQLBuilder;
import io.matob.database.util.sql.SField;
import io.matob.database.util.sql.clause.Clause;

public class PresentationPersister {

	private EntityManagerController emc;
	private Map<String, Supplier> supplierCache;
	private Map<String, Speciality> specialityCache;
	private Map<String, Substance> substanceCache;
	private Map<String, Category> categoryCache;
	private Map<String, Product> productCache;
	private List<Presentation> presentations;

	public PresentationPersister(List<Presentation> presentations) {
		this.presentations = presentations;

		emc = new EntityManagerController();
		supplierCache = new HashMap<>();
		specialityCache = new HashMap<>();
		substanceCache = new HashMap<>();
		categoryCache = new HashMap<>();
		productCache = new HashMap<>();
	}

	public void execute() throws Exception {
		try {
			emc.open();
			loadCache();

			DAOJPA<Presentation> dao = DAOJPAFactory.createDAO(Presentation.class, emc);
			dao.setUseTransaction(false);

			emc.begin();
			Product product;
			for (Presentation presentation : presentations) {
				log("SAVE: " + presentation.getCode() + "\t" + presentation.getProduct().getName() + presentation.getName());
				product = presentation.getProduct();
				if (!productCache.containsKey(product.getName()))
					persistProduct(product);
				presentation.setProduct(productCache.get(product.getName()));
				Integer id = dao.executeSingleQuery(new JPQLBuilder()
						.select(new SField("m.id"))
						.where(new Clause("m.code = :code"),
								new Clause("m.ms = :ms"))
						.addParameter("code", presentation.getCode())
						.addParameter("ms", presentation.getMs()));
				presentation.setId(id);
				dao.save(presentation);
			} ;
			emc.commit();
		} catch (Exception ex) {
			emc.rollback();
			throw ex;
		} finally {
			emc.close();
		}
	}

	private void loadCache() throws DatabaseException {
		DAOJPAFactory.createDAO(Supplier.class, emc)
				.findAll(null)
				.forEach((s) -> supplierCache.put(s.getName(), s));
		DAOJPAFactory.createDAO(Category.class, emc)
				.findAll(null)
				.forEach((s) -> categoryCache.put(s.getName(), s));
		DAOJPAFactory.createDAO(Speciality.class, emc)
				.findAll(null)
				.forEach((s) -> specialityCache.put(s.getName(), s));
		DAOJPAFactory.createDAO(Substance.class, emc)
				.findAll(null)
				.forEach((s) -> substanceCache.put(s.getName(), s));
		DAOJPAFactory.createDAO(Product.class, emc)
				.findAll(null)
				.forEach((s) -> productCache.put(s.getName(), s));
	}

	private void persistProduct(Product value) throws DatabaseException {
		Supplier supplier = value.getSupplier();
		if (!categoryCache.containsKey(supplier.getName()))
			persistSupplier(supplier);
		value.setSupplier(supplierCache.get(supplier.getName()));

		List<Category> categories = new LinkedList<>();
		for (Category category : value.getCategories()) {
			if (!categoryCache.containsKey(category.getName()))
				persistCategory(category);
			categories.add(categoryCache.get(category.getName()));
		}
		value.setCategories(categories);

		if (value instanceof Drug) {
			Drug drug = (Drug) value;

			List<Speciality> specialities = new LinkedList<>();
			for (Speciality speciality : drug.getSpecialities()) {
				if (!specialityCache.containsKey(speciality.getName()))
					persistSpeciality(speciality);
				specialities.add(specialityCache.get(speciality.getName()));
			}
			drug.setSpecialities(specialities);

			List<Substance> substances = new LinkedList<>();
			for (Substance substance : drug.getSubstances()) {
				if (!substanceCache.containsKey(substance.getName()))
					persistSubstance(substance);
				substances.add(substanceCache.get(substance.getName()));
			}
			drug.setSubstances(substances);
		}

		DAOJPA<Product> dao = DAOJPAFactory.createDAO(Product.class, emc);
		dao.setUseTransaction(false);
		value = dao.save(value);
		productCache.put(value.getName(), value);
	}

	private void persistSupplier(Supplier value) throws DatabaseException {
		DAOJPA<Supplier> dao = DAOJPAFactory.createDAO(Supplier.class, emc);
		dao.setUseTransaction(false);
		value = dao.save(value);
		supplierCache.put(value.getName(), value);
	}

	private void persistCategory(Category value) throws DatabaseException {
		DAOJPA<Category> dao = DAOJPAFactory.createDAO(Category.class, emc);
		dao.setUseTransaction(false);
		value = dao.save(value);
		categoryCache.put(value.getName(), value);
	}

	private void persistSpeciality(Speciality value) throws DatabaseException {
		DAOJPA<Speciality> dao = DAOJPAFactory.createDAO(Speciality.class, emc);
		dao.setUseTransaction(false);
		value = dao.save(value);
		specialityCache.put(value.getName(), value);
	}

	private void persistSubstance(Substance value) throws DatabaseException {
		DAOJPA<Substance> dao = DAOJPAFactory.createDAO(Substance.class, emc);
		dao.setUseTransaction(false);
		value = dao.save(value);
		substanceCache.put(value.getName(), value);
	}

	private static synchronized void log(String msg) {
		System.out.println("-> " + Thread.currentThread().getName() + ": " + msg);
	}
}
