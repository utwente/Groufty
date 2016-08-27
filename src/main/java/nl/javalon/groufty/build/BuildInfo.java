package nl.javalon.groufty.build;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Lukas Miedema
 */
@Component
@ConfigurationProperties(prefix = "groufty.build", locations = "classpath:build.properties")
@Data
public class BuildInfo {
	private long commitCount;
	private String branchName;
	private String commitHash;
	private String[] commitTags;
	private long timestamp;
}
