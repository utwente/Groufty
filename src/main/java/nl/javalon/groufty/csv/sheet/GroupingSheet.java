package nl.javalon.groufty.csv.sheet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.javalon.groufty.domain.user.UserId;

/**
 * Entry in a "flattend" Grouping CSV sheet. It's flattend in the sense that every user in the grouping has a
 * separate entry.
 * @author Lukas Miedema
 */
@JsonPropertyOrder({"name", "userId"})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupingSheet {

	@JsonProperty("Name")
	private String name;

	@JsonProperty("User id")
	private UserId userId;
}
