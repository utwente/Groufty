package nl.javalon.groufty.config;

import lombok.extern.slf4j.Slf4j;
import nl.javalon.groufty.domain.user.UserAuthority;
import nl.javalon.groufty.security.DevAuthenticationProvider;
import nl.javalon.groufty.security.UtwenteUserContextMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * @author Lukas Miedema
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Inject private AuthenticationManagerBuilder auth;
	@Inject private UtwenteUserContextMapper userDetailsMapper;
	@Inject private Environment environment;
	@Autowired(required = false) private DevAuthenticationProvider devProvider;
	@Value("${groufty.authentication.enable-ldap-auth}") private boolean enableLdap;
	@Value("${groufty.authentication.enable-dev-auth}") private boolean enableDevAuth;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// For now use form login. This should be migrated to SAML in the future
		http
				.authorizeRequests()
					// every user can access the session endpoint (necessary to login)
					.antMatchers("/" + RestPrefixConfiguration.PREFIX + "users/session").permitAll()

					// also the release info is not a secret
					.antMatchers("/" + RestPrefixConfiguration.PREFIX + "release").permitAll()

					// page endpoints are easier to secure here than by method security
					.antMatchers("/" + RestPrefixConfiguration.PAGE_PREFIX + "student/**")
						.hasAuthority(UserAuthority.ROLE_PARTICIPANT.getAuthority())
					.antMatchers("/" + RestPrefixConfiguration.PAGE_PREFIX + "teacher/**")
						.hasAuthority(UserAuthority.ROLE_EDITOR.getAuthority())

					// All API requests require PARTICIPANT or EDITOR. Fine-grained is done via method security
					// This includes nameless page repositories
					.antMatchers("/" + RestPrefixConfiguration.PREFIX + "**")
						.hasAnyAuthority(UserAuthority.ROLE_EDITOR.getAuthority(), UserAuthority.ROLE_PARTICIPANT.getAuthority())

					// everything else requires no authentication (static resources)
					.anyRequest().permitAll()
				.and().headers()
					.frameOptions().sameOrigin()
				.and().csrf()
					.disable()
				.exceptionHandling()
					.authenticationEntryPoint(new Http403ForbiddenEntryPoint());
	}

	@PostConstruct
	public void registerGlobal() throws Exception {
		if (enableLdap) {
			auth.ldapAuthentication()
					.userDnPatterns("uid={0},cn=Users,dc=utwente,dc=nl")
					.userSearchBase("cn=Users,dc=utwente,dc=nl")
					.userSearchFilter("(uid={0})")
					.userDetailsContextMapper(userDetailsMapper)
					.contextSource().url("ldaps://ldapauth.utwente.nl:6360");
		}

		if (enableDevAuth) {
			log.warn("Enabling highly insecure DevAuthenticationProvider! Do not use this instance in production!");

			// This will blow up in production + enable dev auth
			// This is a feature - dev auth should never be enabled in production
			auth.authenticationProvider(devProvider);
		}
	}

}
