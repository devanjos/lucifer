package app.anjos.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import app.anjos.model.Drug;
import app.anjos.model.Presentation;
import io.matob.database.exception.DatabaseException;
import io.matob.database.jpa.DAOJPA;
import io.matob.database.jpa.DAOJPAFactory;
import io.matob.database.jpa.EntityManagerController;
import io.matob.database.jpa.JPQLBuilder;
import io.matob.database.util.sql.clause.Clause;
import io.matob.tools.MathUtils;

public class ConfigurePrice {

	private static final String DIR_FORMULA = "files/preco/formula";
	private static final String DIR_MANUAL = "files/preco/manual";

	private static EntityManagerController emc;
	private static DAOJPA<Presentation> dao;

	public static void main(String[] args) throws Exception {
		emc = new EntityManagerController();
		try {
			emc.begin();
			dao = DAOJPAFactory.createDAO(Presentation.class, emc);
			dao.setUseTransaction(false);

			System.out.println("Aplicando preços através da fórmula");
			aplicarPrecoFormula(readAllFiles(new File(DIR_FORMULA)));
			System.out.println("----------------------------------------------------------------------");

			System.out.println("Aplicando preços manuais");
			aplicarPrecoManual(readAllFiles(new File(DIR_MANUAL)));
			System.out.println("----------------------------------------------------------------------");

			emc.commit();
		} catch (Exception ex) {
			emc.rollback();
			throw ex;
		} finally {
			emc.close();
		}
	}

	private static void aplicarPrecoFormula(List<String[]> list) throws Exception {
		String priceSource, code, ms;
		double priceSupplier, princeMax;
		Presentation pr;
		Drug drug;

		for (String[] line : list) {
			code = line[0];
			ms = line[1];
			priceSupplier = Double.parseDouble(line[2]);
			princeMax = Double.parseDouble(line[3]);
			priceSource = DIR_FORMULA + "/" + line[4];

			pr = searchPresentation(code, ms);
			if (pr == null || !(pr.getProduct() instanceof Drug) || (pr.getManualPrice() && pr.getPriceAnjos() != null && pr.getPriceAnjos() > 0))
				continue;

			drug = (Drug) pr.getProduct();
			pr.setPriceSource(priceSource);
			pr.setPriceSupplier(priceSupplier);
			pr.setPriceMax(princeMax);

			if (drug.getType() == 'G' || drug.getType() == 'S' || drug.getType() == 'I') {
				pr.setPricePharmacy(MathUtils.round(((priceSupplier * 0.30) * 1.45), 2));
				pr.setPriceAnjos(MathUtils.round((pr.getPricePharmacy() * 1.20), 2));
			} else {
				pr.setPricePharmacy(MathUtils.round(((priceSupplier * 0.97) * 1.20), 2));
				pr.setPriceAnjos(MathUtils.round((pr.getPricePharmacy() * 1.12), 2));
			}

			dao.save(pr);
		}
	}

	private static void aplicarPrecoManual(List<String[]> list) throws Exception {
		String priceSource, code, ms;
		double priceAnjos, pricePharmacy;
		Presentation pr;

		for (String[] line : list) {
			code = line[0];
			ms = line[1];
			pricePharmacy = MathUtils.round(Double.parseDouble(line[2]), 2);
			priceAnjos = MathUtils.round(Double.parseDouble(line[3]), 2);
			priceSource = DIR_MANUAL + "/" + line[4];

			pr = searchPresentation(code, ms);
			if (pr == null)
				continue;

			pr.setPriceSource(priceSource);
			pr.setManualPrice(true);
			if (priceAnjos > 0)
				pr.setPriceAnjos(priceAnjos);
			if (pricePharmacy > 0)
				pr.setPricePharmacy(pricePharmacy);

			dao.save(pr);
		}
	}

	private static Presentation searchPresentation(String code, String ms) throws DatabaseException {
		JPQLBuilder jpql = new JPQLBuilder().where(new Clause("m.code = :code"))
				.addParameter("code", code);
		if (ms != null && !ms.isEmpty())
			jpql.where(new Clause("m.ms = :ms"))
					.addParameter("ms", ms);

		Presentation pr = dao.executeSingleQuery(jpql);
		if (pr == null)
			System.out.println("Produto não encontrado, EAN: " + code + "; MS: " + ms);
		return pr;
	}

	private static List<String[]> readAllFiles(File directory) throws IOException {
		List<String[]> data = new LinkedList<>();
		for (File file : directory.listFiles())
			data.addAll(readFile(file));
		return data;

	}

	private static List<String[]> readFile(File file) throws IOException {
		List<String[]> data = new LinkedList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String[] split;
			while (reader.ready()) {
				split = reader.readLine().split(";");
				split = Arrays.copyOf(split, split.length + 1);
				split[split.length - 1] = file.getName();
				data.add(split);
			}
		}
		return data;
	}
}
