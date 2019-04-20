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

public class ChangeEncartelados {

	private static final String FILE_ENCARTELADOS = "files/encartelados.txt";

	public static void main(String[] args) throws DatabaseException, IOException {
		EntityManagerController emc = new EntityManagerController();

		List<String[]> encartelados = readFile(new File(FILE_ENCARTELADOS));

		try {
			emc.open();
			DAOJPA<Presentation> dao = DAOJPAFactory.createDAO(Presentation.class, emc);
		} finally {
			emc.close();
		}
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
