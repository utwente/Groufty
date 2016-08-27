package nl.javalon.groufty.domain.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

/**
 * Represents a group of {@link User} objects in the system. 
 * A group has one or more users that belong to the group.
 */
@Entity
@SecondaryTable(name = "group",
		uniqueConstraints = @UniqueConstraint(columnNames = {"group_name", "grouping_id"}),
		indexes = @Index(name = "group_name_index", columnList = "group_name,grouping_id", unique = true)
)
@DiscriminatorValue("GROUP")
@Getter
@Setter
public class Group extends Author {

	@Column(name = "group_name", nullable = false, table = "group")
	private String groupName;

	@ManyToMany
	@JoinTable(name = "group_user")
	private Set<User> users;

	@ManyToOne
	@JoinColumn(name = "grouping_id", nullable = false, table = "group")
	private Grouping grouping;

	@Override
	public AuthorType getAuthorType() {
		return AuthorType.GROUP;
	}

	/**
	 * Alias for {@link #getGroupName()}
	 * @return
	 */
	@Override
	public String getName() {
		return getGroupName();
	}
}
