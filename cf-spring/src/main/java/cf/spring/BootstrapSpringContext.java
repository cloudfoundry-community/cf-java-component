package cf.spring;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Mike Heath <heathma@ldschurch.org>
 * @deprecated Use Spring Boot.
 */
@Deprecated
public class BootstrapSpringContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapSpringContext.class);

	public static ApplicationContext bootstrap(String programName, String[] args) {
		final JCommander commander = new JCommander();

		final Options options = new Options();
		options.configFile = "config/" + programName + ".yml";
		commander.addObject(options);

		commander.setProgramName(programName);

		try {
			commander.parse(args);

			System.setProperty("configFile", "file:" + options.configFile);

			final ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("classpath:context.xml");
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					LOGGER.info("Shutting down.");
					context.close();
				}
			});
			return context;
		} catch (ParameterException e) {
			System.err.println(e.getMessage());
			commander.usage();
			return null;
		}
	}

	static class Options {
		@Parameter(names = "-c", description = "Config file")
		String configFile;
	}


}
