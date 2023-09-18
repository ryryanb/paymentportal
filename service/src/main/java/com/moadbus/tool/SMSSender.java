package tool;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SMSSender  {
	
	protected final Log logger = LogFactory.getLog(getClass());

	public void sendSMS(final String toNumber, String message) {

		final String phoneNumber = toNumber.trim();
		if (StringUtils.isNotEmpty(phoneNumber)) {
			try {
				message = message.trim();
			
				final CCClient client = new CCClient();
				final SMSInterface iface = client.getSMSInterface();

				logger.info("Sending SMS ....");
				logger.info("Mobile number: " + phoneNumber);
				logger.info("SMS message: " + message);
				final String serviceResponse = iface.sendMessage(phoneNumber, message);
				logger.info("SMS Service Response : " + serviceResponse);
				if (serviceResponse != null && StringUtils.equalsIgnoreCase("000", StringUtils.trim(serviceResponse))) {
					logger.info("Success sending SMS");
				} else {
					logger.info("Cannot send otp to the given phone number - It may be invalid Number: " + phoneNumber);
				}

			} catch (Exception e) {
				logger.error("Exception occured in sendSMS", e);
			}
		} else {
			logger.info("Phone Number is null...");
		}
	}
	
	
}