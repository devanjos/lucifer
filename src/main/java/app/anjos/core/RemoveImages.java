package app.anjos.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import app.anjos.model.Drug;
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

	private static final String DIR_IMAGES = "files/imagens";

	public static void main(String[] args) throws Exception {
		removeNull();
		removeDuplicates();
		setImages();
	}

	private static void setImages() throws Exception {
		String code, format, data;
		JPQLBuilder jpql, subquery;
		Drug d;

		EntityManagerController emc = new EntityManagerController();
		DAOJPA<Drug> dao;
		try {
			emc.begin();
			dao = DAOJPAFactory.createDAO(Drug.class, emc);
			dao.setUseTransaction(false);
			for (File f : new File(DIR_IMAGES).listFiles()) {
				code = f.getName().substring(0, f.getName().lastIndexOf('.'));
				format = f.getName().substring(f.getName().lastIndexOf('.') + 1);
				data = encodeFileToBase64Binary(f);

				subquery = new JPQLBuilder(Presentation.class)
						.setAlias("pr")
						.where(new Clause("pr.code = :code"),
								new Clause("pr.product.id = d.id"))
						.addParameter("code", code);

				jpql = new JPQLBuilder().where(new Clause("EXISTS(" + subquery.build() + ")"));

				d = dao.executeSingleQuery(jpql);
				if (d == null) {
					System.out.println("[Set Image] Produto não encontrado, EAN: " + code);
					continue;
				}

				if (d.getImage() == null)
					d.setImage(new Image());
				d.getImage().setFormat(format);
				d.getImage().setData(data);
			}

			emc.commit();
		} catch (Exception ex) {
			emc.rollback();
			throw ex;
		} finally {
			emc.close();
		}
	}

	private static String encodeFileToBase64Binary(File file) throws IOException {
		byte[] bytes = loadFile(file);
		byte[] encoded = Base64.getEncoder().encode(bytes);
		String encodedString = new String(encoded);

		return encodedString;
	}

	private static byte[] loadFile(File file) throws IOException {
		byte[] bytes;
		try (InputStream is = new FileInputStream(file)) {
			long length = file.length();
			if (length > Integer.MAX_VALUE) {
				throw new IOException("File to large " + file.getName());
			}
			bytes = new byte[(int) length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file " + file.getName());
			}
		}
		return bytes;
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
