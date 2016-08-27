package nl.javalon.groufty.resource.util;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.csv.*;
import nl.javalon.groufty.domain.review.ReviewTemplateMismatchException;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Resource for importing and exporting data en-masse.
 * @author Lukas Miedema
 */
@Transactional
@PreAuthorize("hasRole('ROLE_EDITOR')")
@RestController
@RequestMapping(value = RestPrefixConfiguration.PREFIX + "inout")
@Api("Import and export data en-masse")
public class InOutResource {

	@Inject private CsvService csvService;
	private final DateFormat dateFormat = new SimpleDateFormat(CsvService.DATE_FORMAT);

	@ApiOperation("Import CSV files. Returns the number of imported entities.")
	@RequestMapping(method = RequestMethod.POST,
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public CsvImportResult importData(
			@RequestPart(value = "users", required = false) MultipartFile users,
			@RequestPart(value = "groupings", required = false) MultipartFile groupings,
			@RequestPart(value = "groups", required = false) MultipartFile groups,
			@RequestPart(value = "taskLists",required = false) MultipartFile taskLists,
			@RequestPart(value = "tasks", required = false) MultipartFile tasks,
			@RequestPart(value = "submissions", required = false) MultipartFile submissions,
			@RequestPart(value = "submissionLists", required = false) MultipartFile submissionLists,
			@RequestPart(value = "submissionFiles", required = false) List<MultipartFile> submissionFiles,
			@RequestPart(value = "taskFiles", required = false) List<MultipartFile> taskFiles,
			@RequestPart(value = "reviews", required = false) List<MultipartFile> reviews,
			@RequestPart(value = "reviewTemplates", required = false) List<MultipartFile> reviewTemplates
			) throws IOException, CsvException, SQLException, ReviewTemplateMismatchException {
		CsvDataSet dataSet = new CsvDataSet(users, groupings, groups, taskLists, tasks, submissions, submissionLists,
				submissionFiles, taskFiles, reviews, reviewTemplates);
		return csvService.getReader().read(dataSet);
	}

	@ApiOperation("Export database as CSV files in a zip.")
	@RequestMapping(method = RequestMethod.GET)
	public void exportData(CsvExportRequest exportRequest, HttpServletResponse response) throws IOException, SQLException {

		String fileName = "Groufty Data Takeout " + dateFormat.format(new Date()) + ".zip";
		response.setHeader("content-disposition", "attachment; filename=" + fileName);
		response.setContentType("application/zip");

		// Write directly to the output stream
		// This means the file size is unknown, but also that no caching needs to be done as data retrieved from the
		// database will be zipped and sent immediately.
		csvService.getWriter().write(exportRequest, response.getOutputStream());
	}

}
