package service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private PropertiesService propertiesService;
	
	private String fromAddress ="";

    @Retryable(value = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 100))
	public void sendEmail(String toEmail, String subject, String message) {
		try {
			// #2972 Vu 20210416
			//display logo
			Resource resource = new DefaultResourceLoader().getResource("images/logo.png");
			StringBuilder emailMsg = new StringBuilder();
			emailMsg.append(message).append("<div><img src='cid:logo' style='float:left;padding-top:15px'/></div>");
			
			fromAddress = propertiesService.getEmailConfig().get("email.username");
        	MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        	MimeMessageHelper helper = 
        		    new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, "UTF-8");
    		helper.setText(emailMsg.toString(), true);
    		helper.setTo(toEmail);
    		helper.setFrom(fromAddress);
    		helper.setSubject(subject);
    		helper.addInline("logo", resource);
    		// End #2972
    		javaMailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            logger.error("sendMail " + subject, ex);
        }
	}

}
