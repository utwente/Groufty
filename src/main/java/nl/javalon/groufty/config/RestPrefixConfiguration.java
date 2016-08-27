package nl.javalon.groufty.config;

public class RestPrefixConfiguration {
	public static final String PREFIX = "api/v1/";
	public static final String PAGE_PREFIX = PREFIX + "page/";

	public static final String FULL_URL = "http://localhost:8080/" + PREFIX;
	public static final String FULL_URL_WITHOUT_PREFIX = "http://localhost:8080/";

}
