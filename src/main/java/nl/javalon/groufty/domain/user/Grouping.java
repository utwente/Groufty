package nl.javalon.groufty.domain.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author Lukas Miedema
 */
@Entity
@Table(
		name = "grouping",
		indexes = @Index(name = "grouping_name_index", columnList = "grouping_name", unique = true))
@Getter
@Setter
@EqualsAndHashCode(of = "groupingId")
public class Grouping {

	@Id
	@GeneratedValue
	@Column(name = "grouping_id")
	private long groupingId;

	@Column(name = "grouping_name", nullable = false, unique = true)
	@NotNull
	private String groupingName;

	@ManyToMany
	@JoinTable(name = "grouping_user")
	private Set<User> users;

	@OneToMany(mappedBy = "grouping")
	private Set<Group> groups;
}
