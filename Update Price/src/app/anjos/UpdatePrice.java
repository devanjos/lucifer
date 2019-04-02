package app.anjos;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class UpdatePrice {

	private static final String INPUT = "files/input.txt";
	private static final String OUTPUT = "files/output.sql";

	private static final String HOST = "localhost";
	private static final String PORT = "3306";
	private static final String DATABASE = "anjos";
	private static final String USER = "root";
	private static final String PASSWORD = "admin";

	public static void main(String[] args) throws Exception {
		List<String> updates = new LinkedList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(INPUT));
				Connection conn = getConnection();
				PrintStream out = new PrintStream(new FileOutputStream(OUTPUT))) {

			String line[];
			PreparedStatement stmt;

			while (reader.ready()) {
				line = reader.readLine().split(";");

				stmt = conn.prepareStatement("SELECT p.id FROM presentations p WHERE p.barcode = '" + line[0] + "';");
				ResultSet rs = stmt.executeQuery();
				if (rs.next())
					updates.add("UPDATE presentations p SET p.pf = " + line[1] + ", p.pc = " + line[2] + " WHERE p.barcode = '" + line[0] + "';");
			}

			updates.forEach((c) -> out.println(c));
		}
	}

	private static Connection getConnection() {
		try {
			return DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useTimezone=true&serverTimezone=UTC", USER, PASSWORD);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
