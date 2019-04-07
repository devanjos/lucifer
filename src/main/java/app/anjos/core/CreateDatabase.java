package app.anjos.core;

import io.matob.database.jpa.EntityManagerController;

public class CreateDatabase {

	public static void main(String[] args) {
		try (EntityManagerController emc = new EntityManagerController()) {
			emc.open();
		}
	}
}
