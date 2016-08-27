package nl.javalon.groufty.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import nl.javalon.groufty.domain.task.Task;
import nl.javalon.groufty.domain.task.TaskList;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Primitive user representation. Extended by more specific user objects.
 */
@Entity
@DiscriminatorValue("USER")
@SecondaryTable(
	name = "user",
	uniqueConstraints = @UniqueConstraint(columnNames = {"user_type", "user_number"}),
	indexes = @Index(name = "uid_index", columnList = "user_type,user_number", unique = true)
)
@Getter
@Setter
public class User extends Author implements UserDetails {

	@ApiModelProperty("The users id, i.e. s1234 or m1234, as received via the authentication mechanism.")
	@NotNull
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "type", column = @Column(name = "user_type", table = "user")),
			@AttributeOverride(name = "number", column = @Column(name = "user_number", table = "user")),
	})
	private UserId userId;

	@Column(name = "full_name", nullable = false, table = "user")
	@NotNull
	private String fullName;

	@ApiModelProperty("The role from which permissions should be derived, i.e. ROLE_PARTICIPANT")
	@Column(name = "authority", table = "user")
	@Enumerated(EnumType.STRING)
	private UserAuthority authority = UserAuthority.ROLE_NONE;

	@JsonIgnore
	@ManyToMany(mappedBy = "users")
	private Set<Group> groups;

	@JsonIgnore
	@ManyToMany(mappedBy = "users")
	private Set<Grouping> groupings;

	@JsonIgnore
	@OneToMany(mappedBy = "author", targetEntity = TaskList.class)
	private Set<TaskList> authoredTaskLists;

	@JsonIgnore
	@OneToMany(mappedBy = "author", targetEntity = Task.class)
	private Set<Task> authoredTasks;

	@Override
	public AuthorType getAuthorType() {
		return AuthorType.USER;
	}

	/**
	 * Alias for {@link #getUserId()}
	 * @return
	 */
	@Override
	public String getName() {
		return getUserId().toString();
	}

	// UserDetails methods
	@JsonIgnore
	public String getUsername() {
		return getFullName();
	}

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(this.authority);
	}

	@ApiModelProperty("Always true")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Override
	public boolean isEnabled() {
		return true;
	}

	@JsonIgnore
	@Override
	public String getPassword() {
		return null;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
}
