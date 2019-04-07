package app.anjos.core;

import java.io.BufferedReader;
import java.io.FileReader;
import app.anjos.model.Drug;
import app.anjos.model.Presentation;
import io.matob.database.jpa.DAOJPA;
import io.matob.database.jpa.DAOJPAFactory;
import io.matob.database.jpa.EntityManagerController;
import io.matob.database.jpa.JPQLBuilder;
import io.matob.database.util.sql.clause.Clause;
import io.matob.tools.MathUtils;

public class UpdatePrice {

	private static final String INPUT_1 = "files/1. precos.txt";
	private static final String INPUT_2 = "files/2. precos_manuais.txt";

	public static void main(String[] args) throws Exception {
		aplicarFormulaPreco();
		verificarPrecoManual();
	}

	private static void aplicarFormulaPreco() throws Exception {
		EntityManagerController emc = new EntityManagerController();
		try (BufferedReader reader = new BufferedReader(new FileReader(INPUT_1))) {
			DAOJPA<Presentation> dao = DAOJPAFactory.createDAO(Presentation.class, emc);
			dao.setUseTransaction(false);
			emc.begin();

			String line[];
			String code;
			String ms;
			double priceSupplier;
			double princeMax;
			Presentation p;
			Drug drug;
			while (reader.ready()) {
				line = reader.readLine().split(";");
				code = line[0];
				ms = (line[1].isEmpty()) ? null : line[1];
				priceSupplier = Double.parseDouble(line[2]);
				priceSupplier = (priceSupplier > 0) ? priceSupplier : null;
				princeMax = Double.parseDouble(line[3]);

				JPQLBuilder jpql = new JPQLBuilder()
						.where(new Clause("m.code = :code"))
						.addParameter("code", code)
						.addParameter("ms", ms);
				if (line[1] != null)
					jpql.where(new Clause("m.ms = :ms"));

				p = dao.executeSingleQuery(jpql);

				if (p == null) {
					System.out.println("Produto não encontrado, EAN: " + code + "; MS: " + ms);
					continue;
				}

				if (!(p.getProduct() instanceof Drug) || (p.getManualPrice() && p.getPriceAnjos() != null && p.getPriceAnjos() > 0))
					continue;

				drug = (Drug) p.getProduct();
				p.setPriceSupplier(priceSupplier);
				p.setPriceMax(princeMax);
				if (drug.getType() == 'G' || drug.getType() == 'S' || drug.getType() == 'I') {
					p.setPricePharmacy(MathUtils.round(((priceSupplier * 0.30) * 1.45), 2));
					p.setPriceAnjos(MathUtils.round((p.getPricePharmacy() * 1.20), 2));
				} else {
					p.setPricePharmacy(MathUtils.round(((priceSupplier * 0.97) * 1.20), 2));
					p.setPriceAnjos(MathUtils.round((p.getPricePharmacy() * 1.12), 2));
				}

				dao.save(p);
			}

			emc.commit();
		} catch (Exception ex) {
			emc.rollback();
			throw ex;
		} finally {
			emc.close();
		}
	}

	private static void verificarPrecoManual() throws Exception {
		EntityManagerController emc = new EntityManagerController();
		try (BufferedReader reader = new BufferedReader(new FileReader(INPUT_2))) {
			DAOJPA<Presentation> dao = DAOJPAFactory.createDAO(Presentation.class, emc);
			dao.setUseTransaction(false);
			emc.begin();

			String line;
			Presentation p;
			while (reader.ready()) {
				line = reader.readLine().trim();
				if (line.isEmpty())
					continue;

				JPQLBuilder jpql = new JPQLBuilder()
						.where(new Clause("m.code = :code"))
						.addParameter("code", line);
				p = dao.executeSingleQuery(jpql);

				if (p == null) {
					System.out.println("Produto não encontrado, EAN: " + line);
					continue;
				}

				p.setManualPrice(true);
				dao.save(p);
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
