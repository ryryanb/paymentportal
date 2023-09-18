package com.moadbus.web.biller.conf;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jndi.JndiTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
@EnableJpaRepositories(basePackages = "repository")
@EntityScan(basePackages = { "entity" })
public class JNDIConfig {

	private final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private Environment env;
	//@Autowired
	//private PropertiesService propertiesService;

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws NamingException {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setPackagesToScan("entity");
		em.setPersistenceUnitName("biller-persistence-unit");
		em.setDataSource(dataSource());

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabasePlatform("org.hibernate.dialect.DB2Dialect");
		em.setJpaVendorAdapter(vendorAdapter);
		// rest of entity manager configuration
		return em;
	}
	
    @Bean
    public DataSource dataSource() throws NamingException {
		logger.info(">>>>>>>>>>>>>>>>>>>>> JNDI Lookup >>>>>>>>>>>>>>>>>>>>> " + env.getProperty("jdbc.url"));
        return (DataSource) new JndiTemplate().lookup(env.getProperty("jdbc.url"));
    }
    
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
    
    /*@Bean
	public JavaMailSender getJavaMailSender() {
		Map<String, String> mailConfigs = propertiesService.getEmailConfig();
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(mailConfigs.get("email.mail.smtp.host"));
		mailSender.setPort(Integer.parseInt(mailConfigs.get("email.mail.smtp.port")));

		mailSender.setUsername(mailConfigs.get("email.username"));
		mailSender.setPassword(mailConfigs.get("email.password"));

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", mailConfigs.get("email.mail.smtp.auth"));
		props.put("mail.smtp.starttls.enable", mailConfigs.get("email.mail.smtp.starttls.enable"));
		return mailSender;
	}*/
}
