package nl.javalon.groufty.build;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Wraps around {@link BuildInfo} to provide the profile and the full build string.
 * @author Lukas Miedema
 */
@Component
@Data
public class ReleaseInfo {

	@Inject @JsonUnwrapped
	private BuildInfo buildInfo;
	private String[] profiles;

	@Inject
	public void setEnvironment(Environment environment) {
		this.profiles = environment.getActiveProfiles();
	}

	@JsonGetter
	public String getBuildString() {
		String buildName;
		if (buildInfo.getCommitTags() == null || buildInfo.getCommitTags().length <= 1) {
			buildName = buildInfo.getBranchName();
		} else {
			buildName = Arrays.stream(buildInfo.getCommitTags()).collect(Collectors.joining("."));
		}
		String profiles = Arrays.stream(this.profiles).collect(Collectors.joining(", "));
		return buildInfo.getCommitCount() + "." + profiles + "/" + buildName + "." + buildInfo.getCommitHash();
	}

}
