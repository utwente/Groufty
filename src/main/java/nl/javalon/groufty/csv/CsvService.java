package nl.javalon.groufty.csv;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import lombok.Getter;
import nl.javalon.groufty.csv.deserialize.BooleanDeserializer;
import nl.javalon.groufty.csv.sheet.UserSheet;
import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.repository.crud.*;
import nl.javalon.groufty.util.FileService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.SimpleDateFormat;

/**
 * Utility for importing and exporting {@link CsvDataSet} to the database.
 * @author Lukas Miedema
 */
@Service
public class CsvService {

	public static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

	@Inject UserRepository userRepository;
	@Inject GroupingRepository groupingRepository;
	@Inject GroupRepository groupRepository;
	@Inject TaskListRepository taskListRepository;
	@Inject TaskRepository taskRepository;
	@Inject ReviewTemplateRepository reviewTemplateRepository;
	@Inject SubmissionListRepository submissionListRepository;
	@Inject SubmissionRepository submissionRepository;
	@Inject ReviewRepository reviewRepository;
	@Inject FileService fileService;

	// Mappers
	final CsvMapper csvMapper;
	final ObjectMapper jsonMapper;

	// IO delegates
	@Getter	private final CsvWriter writer;
	@Getter	private final CsvReader reader;

	public CsvService() {
		this.csvMapper = new CsvMapper();
		this.csvMapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
		this.csvMapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
		SimpleModule module = new SimpleModule();
		BooleanDeserializer booleanDeserializer = new BooleanDeserializer();
		module.addDeserializer(Boolean.class, booleanDeserializer);
		module.addDeserializer(boolean.class, booleanDeserializer);
		this.csvMapper.registerModule(module);

		// Set annotation sources where the domain object can be used directly
		this.csvMapper.addMixIn(User.class, UserSheet.class);

		// Json mapper for things that can't be stored as CSV (Review templates)
		this.jsonMapper = new ObjectMapper();
		this.jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
		this.jsonMapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);

		this.writer = new CsvWriter(this);
		this.reader = new CsvReader(this);
	}
}
