package nl.javalon.groufty.config;

import org.springframework.boot.orm.jpa.hibernate.SpringNamingStrategy;

/**
 * All groufty tables are prefixed in the database. This is both to allow multiple applications running on the
 * same database and to avoid name clashes with keywords (like "USER", "GROUP") in certain databases (Postgres...)
 *
 * @author Lukas Miedema
 */
public class TablePrefixNamingStrategy extends SpringNamingStrategy {

	private static final long serialVersionUID = -3404923126424955081L;

	private final static String PREFIX = "groufty_";

	@Override
	public String classToTableName(String className) {
		return tableName(super.classToTableName(className));
	}

	@Override
	public String tableName(String tableName) {
		// Apply prefix and return
		return PREFIX + tableName;
	}
}
