package nl.javalon.groufty.csv.sheet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.javalon.groufty.domain.user.UserId;

/**
 * Entry of group sheet.
 * @author Lukas Miedema
 */
@JsonPropertyOrder({"groupName", "groupingName", "userId"})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupSheet {

	@JsonProperty("Group name")
	private String groupName;

	@JsonProperty("Grouping name")
	private String groupingName;

	@JsonProperty("User id")
	private UserId userId;
}
