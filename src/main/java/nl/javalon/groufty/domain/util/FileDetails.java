package nl.javalon.groufty.domain.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Blob;

/**
 * Embeddable class embedded in an entity when a file blob + file name is needed.
 * @see nl.javalon.groufty.domain.task.Task
 * @see nl.javalon.groufty.domain.task.Submission
 */
@Embeddable
@Getter
@Setter
public class FileDetails {

	@NotNull
	@JsonIgnore
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "file", nullable = true)
	private Blob file;

	@NotNull
	@Column(name = "fileName", nullable = true)
	private String fileName;
}
