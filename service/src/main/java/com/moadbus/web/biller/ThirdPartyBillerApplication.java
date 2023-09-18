package web.biller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan(basePackages = { "com.moadbus", "com.moadbus.web.biller.api" })
public class ThirdPartyBillerApplication extends SpringBootServletInitializer{

	private static final Logger log = LoggerFactory.getLogger(ThirdPartyBillerApplication.class);
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ThirdPartyBillerApplication.class);
	}
	
	public static void main(String[] args) {
//		SpringApplication.run(ThirdPartyBillerApplication.class, args);
		SpringApplication app = new SpringApplication(ThirdPartyBillerApplication.class);
		Environment env = app.run(args).getEnvironment();
		log.info(
				"\n\n------------------------------------------------------------------------------\n"
						+ "Application '{}' is running!\n"
						+ "------------------------------------------------------------------------------\n",
				env.getProperty("spring.application.name") + ":" + env.getProperty("app.version"));
	}

}
