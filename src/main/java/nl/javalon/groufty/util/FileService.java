package nl.javalon.groufty.util;

import nl.javalon.groufty.domain.util.FileDetails;
import nl.javalon.groufty.resource.NotAcceptableException;
import org.hibernate.LobHelper;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

import static nl.javalon.groufty.resource.RestResourceHelper.checkFound;

/**
 * Utility for serving files
 */
@Service
public class FileService {

	@Inject
	private HibernateEntityManagerFactory hibernateEntityManagerFactory;

	private final static String ANONYMOUS_FILE_NAME_SALT = "zYieXwBDWvAzG3Fc3W0LHWhUGteJ8TG9";
	private final static String ANONYMOUS_FILE_NAME_SUFFIX = " (Anonymous)";

	/**
	 * Utility method for returning a file from FileDetails
	 * @param details the file details
	 * @param response the response to write to
	 * @param anonymousFileName when set to true, the file name will be hashed so the receiver cannot see it.
	 */
	public void findFile(FileDetails details, HttpServletResponse response, boolean anonymousFileName) throws SQLException, IOException {
		try {
			checkFound(details);

			// Create headers
			String fileName = anonymousFileName ? anonymizeFileName(details.getFileName()) : details.getFileName();
			response.setHeader("content-disposition", "attachment; filename=" + fileName);
			response.setContentLength((int) details.getFile().length());
			response.setStatus(HttpStatus.OK.value());

			// Copy
			StreamUtils.copy(details.getFile().getBinaryStream(), response.getOutputStream());
		} catch (RuntimeException e) {
			throw e;
		}
	}

	/**
	 * Hashes the file name and suffixes it with the anonymous file name suffix. Extension will be retained.
	 * Note that this isn't necessarily uncrackable, it's just meant to make it sufficiently hard to decipher that
	 * nobody will bother.
	 * @param fileName
	 * @return
	 */
	public String anonymizeFileName(String fileName) {
		int index = fileName.lastIndexOf('.');
		String extension = index == -1 ? "" : fileName.substring(index);
		String digest = Sha512DigestUtils.shaHex(ANONYMOUS_FILE_NAME_SALT + fileName).substring(0, 8);
		return digest + ANONYMOUS_FILE_NAME_SUFFIX + extension;
	}

	/**
	 * Utility method for storing a MultipartFile as blob in file details.
	 * @param multipartFile the file as submitted by POST
	 * @return the file details containing the file as blob
	 */
	public FileDetails convertFile(MultipartFile multipartFile) throws IOException, SQLException, NotAcceptableException {
		byte[] bytes = multipartFile.getBytes();
		PdfDetector pdfDetector = new PdfDetector(bytes);
		pdfDetector.validate();

		// Create file details
		// Use a blob instead of a byte array to prevent having to store the whole file in Java memory
		FileDetails details = new FileDetails();
		LobHelper lobCreator = hibernateEntityManagerFactory.getSessionFactory().getCurrentSession().getLobHelper();
		Blob blob = lobCreator.createBlob(bytes);
		details.setFileName(multipartFile.getOriginalFilename());
		details.setFile(blob);
		return details;
	}
}
