package nl.javalon.groufty.resource.util;

import io.swagger.annotations.Api;
import nl.javalon.groufty.build.ReleaseInfo;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * @author Lukas Miedema
 */
@Api("Retrieve release information")
@RestController
@RequestMapping(value = RestPrefixConfiguration.PREFIX + "release", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ReleaseInfoResource {

	@Inject private ReleaseInfo releaseInfo;

	@RequestMapping(method = RequestMethod.GET)
	public ReleaseInfo getReleaseInfo() {
		return releaseInfo;
	}
}
