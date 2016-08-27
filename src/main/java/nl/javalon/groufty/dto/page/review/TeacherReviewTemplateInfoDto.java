package nl.javalon.groufty.dto.page.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import nl.javalon.groufty.util.convert.SqlToBeanConstructor;

/**
 * @author Lukas Miedema
 */
@Data
@AllArgsConstructor(onConstructor = @__(@SqlToBeanConstructor))
public class TeacherReviewTemplateInfoDto {

	private final long reviewTemplateId;
	private final String name;
	private final long propertyCount;
	private final long taskCount;
	private final long reviewCount;
}
