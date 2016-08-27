package nl.javalon.groufty.repository;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.transform.ResultTransformer;

import javax.inject.Inject;

/**
 * Common base-class for native query repositories - repositories that make use of queries defined in /named-query
 * @author Lukas Miedema
 */
public class AbstractNativeRepository {

	@Inject
	private HibernateEntityManagerFactory hibernateEntityManagerFactory;

	/**
	 * Returns the current session from the session factory.
	 * @return
	 */
	protected Session getSession() {
		return hibernateEntityManagerFactory.getSessionFactory().getCurrentSession();
	}

	/**
	 * Prepares a query and sets the {@link AliasToBeanConstructorResultTransformer}
	 * using the provided return type.
	 * @param namedQuery
	 * @param transformer
	 * @return
	 */
	protected Query prepareQuery(String namedQuery, ResultTransformer transformer) {
		Session session = getSession();
		Query query = getSession().getNamedQuery(namedQuery);
		return query.setResultTransformer(transformer);
	}
}
