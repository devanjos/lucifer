package app.anjos.core;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import app.anjos.model.Drug;
import io.matob.database.jpa.DAOJPA;
import io.matob.database.jpa.DAOJPAFactory;
import io.matob.database.jpa.EntityManagerController;
import io.matob.database.jpa.JPQLBuilder;
import io.matob.database.util.sql.clause.Clause;

public class DownloadBulas {

	public static void main(String[] args) throws Exception {
		EntityManagerController emc = new EntityManagerController();

		try {
			emc.open();
			DAOJPA<Drug> dao = DAOJPAFactory.createDAO(Drug.class, emc);
			dao.setUseTransaction(false);

			emc.begin();

			List<Drug> drugs = dao.executeQuery(new JPQLBuilder()
					.where(new Clause("m.bula IS NOT NULL")));

			for (Drug d : drugs) {
				d.setBula(downloadFile(d.getBula()));
				dao.save(d);
			}

			//emc.commit();
		} catch (Exception ex) {
			emc.rollback();
			throw ex;
		} finally {
			emc.close();
		}
	}

	private static String downloadFile(String url) throws MalformedURLException, IOException, FileNotFoundException {
		URL _url = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) _url
				.openConnection();
		connection.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

		String fileName = System.currentTimeMillis() + ".pdf";
		System.out.println("Downloading Bula: " + url);
		System.out.println("\t\t: " + fileName);
		try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
				FileOutputStream fileOutputStream = new FileOutputStream("output/bulas/" + fileName)) {
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);
			}
		}

		return fileName;
	}
}
