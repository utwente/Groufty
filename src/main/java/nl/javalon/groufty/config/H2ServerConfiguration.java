package nl.javalon.groufty.config;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

/**
 * Configures H2 server so external tools can connect to it.
 * @author Lukas Miedema
 */
@Configuration
@Profile("dev")
public class H2ServerConfiguration {

	@Bean
	public Server h2TcpServer() throws SQLException {
		return Server.createTcpServer("-tcp", "-tcpAllowOthers").start();
	}
}
