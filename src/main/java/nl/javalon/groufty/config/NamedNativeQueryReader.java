package nl.javalon.groufty.config;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileSystemUtils;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * @author Lukas Miedema
 */
@Configuration
@Slf4j
public class NamedNativeQueryReader {

	/**
	 * Location of all named native query files
	 */
	public static final String NAMED_NATIVE_QUERY_PATH = "classpath:named-query/*.sql";

	@Inject
	private void loadAndRegisterQueries(EntityManager entityManager) throws IOException {
		EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();

		for (Resource resource: new PathMatchingResourcePatternResolver().getResources(NAMED_NATIVE_QUERY_PATH)) {
			String filename = resource.getFilename();
			String queryName = filename.substring(0, filename.length() - 4); // remove the ".sql"
			String queryContent = readInputStream(resource.getInputStream());

			log.info("Creating native named query: " + queryName);

			// Create query object and register
			Query query = entityManager.createNativeQuery(queryContent);
			entityManagerFactory.addNamedQuery(queryName, query);
		}
	}

	private String readInputStream(InputStream inputStream) {
		Scanner s = new Scanner(inputStream).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}
