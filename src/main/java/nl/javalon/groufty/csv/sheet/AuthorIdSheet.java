package nl.javalon.groufty.csv.sheet;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.javalon.groufty.domain.user.Author;
import nl.javalon.groufty.domain.user.Group;
import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.domain.user.UserId;

/**
 * Embeddable class for author identification (which can be either a user or a group)
 * @author Lukas Miedema
 */
@Data
@NoArgsConstructor
public class AuthorIdSheet {

	private String groupName;
	private String groupGroupingName;
	private UserId userId;

	/**
	 * Constructs sheet from author
	 * @param author
	 */
	public AuthorIdSheet(Author author) {
		if (author instanceof Group) {
			Group g = (Group) author;
			groupName = g.getGroupName();
			groupGroupingName = g.getGrouping().getGroupingName();
		} else if (author instanceof User) {
			userId = ((User) author).getUserId();
		}
	}
}
