package nl.javalon.groufty.csv;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Set of importable CSV files as directly retrieved from the client.
 * @author Lukas Miedema
 */
@Data
public class CsvDataSet {
	private final MultipartFile users;
	private final MultipartFile groupings;
	private final MultipartFile groups;
	private final MultipartFile taskLists;
	private final MultipartFile tasks;
	private final MultipartFile submissions;
	private final MultipartFile submissionLists;
	private final List<MultipartFile> submissionFiles;
	private final List<MultipartFile> taskFiles;
	private final List<MultipartFile> reviews;
	private final List<MultipartFile> reviewTemplates;
}
