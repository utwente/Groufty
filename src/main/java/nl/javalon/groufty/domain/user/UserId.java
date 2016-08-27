package nl.javalon.groufty.domain.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Contains a {@link UserType} enum and a long indicating the user id.
 * @author Lukas Miedema
 */
@Data
@NoArgsConstructor
@Embeddable
public class UserId {

	@Column(name = "user_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private UserType type;

	@Column(name = "user_number", nullable = false)
	private long number;

	public UserId(@NonNull UserType type, long number) {
		this.type = type;
		this.number = number;
	}

	/**
	 * Parses a string to a userId
	 * @param userId for instance s12345 or m12345
	 * @return the user id, or null if the input was null or empty
	 */
	@JsonCreator
	public static UserId fromString(String userId) {
		if (userId == null || userId.isEmpty()) {
			return null;
		}
		char typeChar = userId.charAt(0);
		UserType type = null;
		if (typeChar == 's' || typeChar == 'S') {
			type = UserType.STUDENT;
		} else if (typeChar == 'm' || typeChar == 'M') {
			type = UserType.EMPLOYEE;
		} else {
			throw new IllegalArgumentException("'" + typeChar + "' is no valid user type indicator");
		}
		return new UserId(type, Long.parseLong(userId.substring(1)));
	}

	@Override
	@JsonValue
	public String toString() {
		return (this.type == UserType.STUDENT ? 's' : 'm') + Long.toString(number);
	}
}
