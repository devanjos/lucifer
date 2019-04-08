package app.anjos.core;

import java.util.List;
import app.anjos.model.Image;
import app.anjos.model.Presentation;
import app.anjos.model.Product;
import io.matob.database.jpa.DAOJPA;
import io.matob.database.jpa.DAOJPAFactory;
import io.matob.database.jpa.EntityManagerController;
import io.matob.database.jpa.JPQLBuilder;
import io.matob.database.util.sql.SField;
import io.matob.database.util.sql.clause.Clause;
import io.matob.database.util.sql.clause.Operator;
import io.matob.database.util.sql.join.InnerJoin;
import io.matob.database.util.sql.join.LeftJoin;

public class RemoveImages {

	public static void main(String[] args) throws Exception {
		removeNull();
		removeDuplicates();
	}

	private static void removeNull() throws Exception {
		EntityManagerController emc = new EntityManagerController();
		try {
			DAOJPA<Presentation> presDao = DAOJPAFactory.createDAO(Presentation.class, emc);
			presDao.setUseTransaction(false);
			emc.begin();

			List<Presentation> presentations = presDao.executeQuery(new JPQLBuilder(Presentation.class)
					.join(new InnerJoin("m.image img"))
					.where("img.data IS NULL"));
			for (Presentation p : presentations) {
				p.setImage(null);
				presDao.save(p);
			}

			emc.commit();
		} catch (Exception ex) {
			emc.rollback();
			throw ex;
		} finally {
			emc.close();
		}
	}

	private static void removeDuplicates() throws Exception {
		EntityManagerController emc = new EntityManagerController();
		try {
			DAOJPA<Product> prodDao = DAOJPAFactory.createDAO(Product.class, emc);
			prodDao.setUseTransaction(false);
			DAOJPA<Presentation> presDao = DAOJPAFactory.createDAO(Presentation.class, emc);
			presDao.setUseTransaction(false);
			emc.begin();

			JPQLBuilder subQuery = new JPQLBuilder(Presentation.class)
					.setAlias("_ap")
					.join(new InnerJoin("_ap.image _img"))
					.where("ap.id <> _ap.id",
							"ap.product.id = _ap.product.id",
							"img.data = _img.data");
			JPQLBuilder subQuery2 = new JPQLBuilder(Presentation.class)
					.select(new SField("ap.id"))
					.setAlias("ap")
					.join(new InnerJoin("ap.image img"))
					.where("ap.product.id = m.id", "EXISTS(" + subQuery + ")");
			List<Product> products = prodDao.executeQuery(new JPQLBuilder()
					.join(new LeftJoin("m.image img"))
					.where("img.id IS NULL", "EXISTS(" + subQuery2 + ")"));

			List<Presentation> presentations;
			for (Product p : products) {
				presentations = presDao.executeQuery(new JPQLBuilder()
						.setAlias("ap")
						.join(new InnerJoin("ap.image img"))
						.where(new Clause(Operator.EQ, "ap.product.id", p.getId()),
								new Clause("EXISTS(" + subQuery + ")")));
				p.setImage(new Image(presentations.get(0).getImage().getFormat(), presentations.get(0).getImage().getData()));
				prodDao.save(p);

				for (Presentation ap : presentations) {
					ap.setImage(null);
					presDao.save(ap);
				}
			}

			emc.commit();
		} catch (Exception ex) {
			emc.rollback();
			throw ex;
		} finally {
			emc.close();
		}
	}
}
