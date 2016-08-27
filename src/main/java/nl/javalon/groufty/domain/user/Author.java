package nl.javalon.groufty.domain.user;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;


/**
 * An author is a group or user who can be the author
 * of a Submission or a Review. This intermediary is used because for some
 * submissions a group is the author, whereas for others it's a single user.
 * @author Lukas Miedema
 */
@Entity
@Table(name = "author")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@EqualsAndHashCode(of = "authorId")
public abstract class Author {

	@Id
	@GeneratedValue
	@Column(name = "author_id", nullable = false)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private long authorId;

	@JsonGetter
	public abstract AuthorType getAuthorType();

	/**
	 * Returns the name of this author, either the group name or the user name.
	 * @return
	 */
	@JsonIgnore
	public abstract String getName();
}
