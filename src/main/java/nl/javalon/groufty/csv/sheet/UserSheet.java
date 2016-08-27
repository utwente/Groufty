package nl.javalon.groufty.csv.sheet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import nl.javalon.groufty.domain.user.UserAuthority;
import nl.javalon.groufty.domain.user.UserId;

/**
 * Annotation source for Jackson to be read on top of {@link nl.javalon.groufty.domain.user.User}
 * @author Lukas Miedema
 */
@JsonPropertyOrder({"userId", "fullName", "authority"})
@JsonIgnoreProperties({"authorId", "authorType", "enabled"})
public interface UserSheet {

	@JsonProperty("User id")
	UserId getUserId();

	@JsonProperty("Full name")
	String getFullName();

	@JsonProperty("Roles")
	UserAuthority getAuthority();
}
