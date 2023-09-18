package com.moadbus.web.biller.conf;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import service.PropertiesService;

@Configuration
public class MailConfig {
	@Autowired
	private PropertiesService propertiesService;

	private final Log logger = LogFactory.getLog(getClass());
	@Bean
	public JavaMailSender getJavaMailSender() {
		Map<String, String> mailConfigs = propertiesService.getEmailConfig();
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(mailConfigs.get("email.mail.smtp.host"));
		mailSender.setPort(Integer.parseInt(mailConfigs.get("email.mail.smtp.port")));

		mailSender.setUsername(mailConfigs.get("email.username"));
		mailSender.setPassword(mailConfigs.get("email.password"));


		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", mailConfigs.get("email.mail.smtp.auth").toLowerCase());
		props.put("mail.smtp.starttls.enable", mailConfigs.get("email.mail.smtp.starttls.enable").toLowerCase());
		props.put("mail.debug", "true");
	
		logger.debug("HOST:" + mailSender.getHost());
		logger.debug("POST:" + mailSender.getPort());
		logger.debug("getUsername:" + mailSender.getUsername());
		logger.debug("getPassword:" + mailSender.getPassword());
		
		logger.debug("mail.transport.protocol:" + props.getProperty("mail.transport.protocol"));
		logger.debug("mail.smtp.auth:" + props.getProperty("mail.smtp.auth"));
		logger.debug("mail.smtp.starttls.enable:" + props.getProperty("mail.smtp.starttls.enable"));
		
	    
		return mailSender;
	}

}
