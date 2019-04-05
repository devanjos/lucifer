package app.anjos;

import javax.persistence.EntityManager;
import io.matob.database.jpa.DAOFactory;
import io.matob.database.jpa.EMFFactory;
import io.matob.database.jpa.PersistenceHibernateFactory;

public class CreateDatabase {

	public static void main(String[] args) {
		EMFFactory emfFactory = new PersistenceHibernateFactory();
		EntityManager em = emfFactory.createEntityManager();
		DAOFactory df = new DAOFactory(emfFactory);
	}
}
