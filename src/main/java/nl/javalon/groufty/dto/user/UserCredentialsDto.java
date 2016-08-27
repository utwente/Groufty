package nl.javalon.groufty.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.Size;

/**
 * @author Lukas Miedema
 */
@Data
@NoArgsConstructor
public class UserCredentialsDto {

	@ApiModelProperty(value = "i.e. s1245 or m1245")
	@NonNull
	@Size(min = 1)
	private String userId;

	@ApiModelProperty(value = "Password in plain text")
	@NonNull
	@Size(min = 1)
	private String password;
}
