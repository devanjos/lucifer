package app.anjos.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import app.anjos.model.Presentation;
import io.matob.database.exception.DatabaseException;
import io.matob.database.jpa.DAOJPA;
import io.matob.database.jpa.DAOJPAFactory;
import io.matob.database.jpa.EntityManagerController;
import io.matob.tools.string.StringUtils;

public class DisablePresentations {

	private static final String FILE_LABORATORIOS = "files/laboratorios.txt";
	private static final String DIR_PERMITIDOS = "files/produtos_permitidos";

	public static void main(String[] args) throws DatabaseException, IOException {
		EntityManagerController emc = new EntityManagerController();

		List<String[]> permitidos = readAllFiles(new File(DIR_PERMITIDOS));
		List<String> _permitidos = new LinkedList<>();
		permitidos.forEach((s) -> _permitidos.add("'" + s[0] + "'"));

		List<String[]> laboratorios = readFile(new File(FILE_LABORATORIOS));
		List<String> _laboratorios = new LinkedList<>();
		laboratorios.forEach((s) -> _laboratorios.add("'" + s[0] + "'"));

		try {
			emc.open();
			DAOJPA<Presentation> dao = DAOJPAFactory.createDAO(Presentation.class, emc);
			dao.executeNativeUpdate("UPDATE presentation pr SET pr.enabled = true;");
			dao.executeNativeUpdate("UPDATE presentation pr "
					+ "INNER JOIN product pd ON pd.id = pr.product_id "
					+ "INNER JOIN drug d ON d.id = pd.id "
					+ "INNER JOIN supplier s ON s.id = pd.supplier_id "
					+ "SET pr.enabled = false "
					+ "WHERE pr.price_anjos IS NULL "
					+ "OR pr.price_anjos <= 0 "
					+ "OR pr.code NOT IN(" + StringUtils.concat(",", _permitidos) + ") "
					+ "OR LOWER(s.name) NOT IN(" + StringUtils.concat(",", _laboratorios) + ");");
		} finally {
			emc.close();
		}
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
