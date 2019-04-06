package app.anjos;

import io.matob.database.jpa.EntityManagerController;

public class CreateDatabase {

	public static void main(String[] args) {
		EntityManagerController emc = new EntityManagerController();
		emc.open();
	}
}
