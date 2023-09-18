package com.moadbus.web.biller.api;

import entity.Biller;
import entity.Properties;
import entity.User;
import entity.User.USER_STATUS;
import repository.PropertiesRepository;
import repository.UserRepository;
import service.BillerService;
import service.EmailService;
import service.UserService;
import com.moadbus.web.biller.InvalidDataException;
import com.moadbus.web.biller.dto.*;
import com.moadbus.web.biller.utils.Constants;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

@CrossOrigin
@RestController
public class UserResource {
	private final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private UserService userService;

	@Autowired
	private BillerService billerService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private Environment env;

	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PropertiesRepository propertiesRepository;

	@PostMapping(path = "/api/user/enroll")
	public ResponseEntity<Result> enroll(@RequestBody UserEnrollment userEnrollment, HttpServletRequest request,
			HttpServletResponse response) {
		Result result = new Result();
		String bankId = env.getProperty("application.bank.id");
		userEnrollment.setBankId(bankId);
		try {
			validate(userEnrollment);
			// generate password
			String randomPassword = null;
			if (userEnrollment.isGeneratePassword()) {
				randomPassword = RandomStringUtils.randomAlphanumeric(8);
				userEnrollment.setPassword(randomPassword);
			}
			User user = userService.enroll(userEnrollment);
			// send password to email
			if (randomPassword != null) {
				String emailMsg = getPasswordEmailContent(user.getUserName(), user.getEmail(), randomPassword);
				emailService.sendEmail(user.getEmail(), "User Registration", emailMsg);
				logger.debug("sent password to user");
			}
			// send user information to email
			if (userEnrollment.isSendAccountInfo()) {
				String emailMsg = getUserInformationEmailContent(user);
				emailService.sendEmail(user.getEmail(), "User Registration", emailMsg);
				logger.debug("sent information to user");
			}
			result.setDetails(user);
			result.setMessage("User Enrollment is successful.");
			return new ResponseEntity<Result>(result, HttpStatus.OK);
		} catch (InvalidDataException e) {
			result.setMessage(e.getMessage());
			result.setCode(HttpStatus.BAD_REQUEST.ordinal());
			return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("Enroll user", e);
			if (e.getMessage().contains("ConstraintViolationException")) {
				result.setMessage("The login is existed. Please try another");
				result.setCode(HttpStatus.BAD_REQUEST.ordinal());
				return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
			} else {
				result.setMessage(e.getMessage());
				result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.ordinal());
				return new ResponseEntity<Result>(result, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostMapping(path = "/api/user/biller-email-notifier")
	public ResponseEntity<Result> billerEmailNotifier(@RequestBody BillerEmailNotifier billerEmailNotifier, HttpServletRequest request,
													  HttpServletResponse response) {
		Result result = new Result();
		try {
			Biller biller = billerService.getByBillerId(Long.parseLong(billerEmailNotifier.getBillerId().toString()));
			validateBiller(biller, billerEmailNotifier);
			User user = userService.getCurrentUser();
			// send email to accredited biller user
			String emailMsg = getBillerUserInformationEmailContent(user);
			emailService.sendEmail(biller.getEmailAddress(), "Biller User Registration", emailMsg);
			logger.debug("Sent information to user " + biller.getEmailAddress());
			AccreditedBillerUser accreditedBillerUser = new AccreditedBillerUser();
			accreditedBillerUser.setId(biller.getBillerId());
			accreditedBillerUser.setBillerShortName(biller.getBillerShortName());
			result.setDetails(accreditedBillerUser);
			result.setMessage("Biller user successfully registered.");
			return new ResponseEntity<Result>(result, HttpStatus.OK);
		} catch (InvalidDataException e) {
			result.setMessage(e.getMessage());
			result.setCode(HttpStatus.BAD_REQUEST.ordinal());
			return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("Enroll user", e);
			if (e.getMessage().contains("ConstraintViolationException")) {
				result.setMessage("The login is existed. Please try another");
				result.setCode(HttpStatus.BAD_REQUEST.ordinal());
				return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
			} else {
				result.setMessage(e.getMessage());
				result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.ordinal());
				return new ResponseEntity<Result>(result, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	private void validate(UserEnrollment userEnrollment) throws InvalidDataException {
		if (StringUtils.isBlank(userEnrollment.getLogin())) {
			throw new InvalidDataException("Login is required");
		} else if (userService.getUserByLogin(userEnrollment.getLogin()) != null) {
			throw new InvalidDataException("The login is existed. Please try another");
		}

		if (userEnrollment.isGeneratePassword() == false && StringUtils.isBlank(userEnrollment.getPassword())) {
			throw new InvalidDataException("Password is required");
		}

		if (StringUtils.isBlank(userEnrollment.getCompanyName())) {
			throw new InvalidDataException("Company Name is required");
		}

		if (StringUtils.isBlank(userEnrollment.getPhone())) {
			throw new InvalidDataException("Phone is required");
		}

		if (StringUtils.isBlank(userEnrollment.getEmail())) {
			throw new InvalidDataException("Email is required");
		} 
//		else if (userService.getUserByEmail(userEnrollment.getEmail()) != null) {
//			throw new InvalidDataException("The email is existed. Please try another");
//		}

		if (StringUtils.isBlank(userEnrollment.getAddress())) {
			throw new InvalidDataException("Address is required");
		}

		if (StringUtils.isBlank(userEnrollment.getContactName())) {
			throw new InvalidDataException("Contact Name is required");
		}
	}

	private void validateBiller(Biller biller , BillerEmailNotifier billerEmailNotifier) throws InvalidDataException {
		if (biller == null ||
				(biller != null && Long.parseLong(billerEmailNotifier.getBillerId().toString()) != biller.getBillerId())) {
			throw new InvalidDataException("Invalid Biller Id " + billerEmailNotifier.getBillerId());
		}
	}

	private String getPasswordEmailContent(String login, String email, String password) {
		String appUrl = servletContext.getContextPath().concat(servletContext.getContextPath());
		StringBuilder emailMsg = new StringBuilder();
		emailMsg.append("<p> Dear ").append(login).append(",</p>").append("<p>Here is your login details on ")
				.append("<a href=\"").append(appUrl)
//				.append("\">" + appUrl + "</a></p>")
				.append("\"> Login Link </a></p>").append("<br>email: <b><a href=\"mailto:").append(email)
				.append("\" target=\"_blank\">").append(email).append("</a></b><br><span>password</span>: <b>")
				.append(password).append("</b><br><br>").append("<p>Thanks!</p>");
		return emailMsg.toString();
	}

	private String getUserInformationEmailContent(User user) {
		String appUrl = env.getProperty("application.domain.url").concat(servletContext.getContextPath());
		StringBuilder emailMsg = new StringBuilder();
		String labelStyle = "style='padding-top:10px; padding-right: 10px;'";
		String dataStyle = "style='padding-top:10px; padding-left: 10px;'";
		emailMsg.append("<p> Dear ").append(user.getUserName()).append(",</p>")
				.append("<p>You have successfully completed user registration on ").append("<a href=\"").append(appUrl)
//				.append("\">" + appUrl + "</a></p>")
				.append("\"> Login Link </a></p>").append("<p>Your information: </p>")
				.append("<table border='1' style='border-collapse:collapse;'>").append("<tr>").append("<td ")
				.append(labelStyle).append(">").append("Login").append("</td>").append("<td ").append(dataStyle)
				.append(">").append(user.getUserName()).append("</td>").append("</tr>").append("<tr>").append("<td ")
				.append(labelStyle).append(">").append("Company Name").append("</td>").append("<td ").append(dataStyle)
				.append(">").append(user.getCompanyName()).append("</td>").append("</tr>").append("<tr>").append("<td ")
				.append(labelStyle).append(">").append("Address").append("</td>").append("<td ").append(dataStyle)
				.append(">").append(user.getAddress()).append("</td>").append("</tr>").append("<tr>").append("<td ")
				.append(labelStyle).append(">").append("Contact Name").append("</td>").append("<td ").append(dataStyle)
				.append(">").append(user.getContactName()).append("</td>").append("</tr>").append("<tr>").append("<td ")
				.append(labelStyle).append(">").append("Email").append("</td>").append("<td ").append(dataStyle)
				.append(">").append(user.getEmail()).append("</td>").append("</tr>").append("<tr>").append("<td ")
				.append(labelStyle).append(">").append("Phone").append("</td>").append("<td ").append(dataStyle)
				.append(">").append(user.getPhone()).append("</td>").append("</tr>").append("</table>")
				.append("<p>Thanks!</p>");
		return emailMsg.toString();
	}

	private String getBillerUserInformationEmailContent(User user) {
		String appUrl = env.getProperty("application.domain.url").concat("/billerportal/index.html?b=1&billerId=" + user.getBillerId());
		logger.info("Biller Portal URL >>> " + appUrl);
		StringBuilder emailMsg = new StringBuilder();
		emailMsg.append("<p> Dear ").append(user.getUserName()).append(",</p>")
				.append("<p>You may complete user registration as accredited biller user on ")
				.append("<a href=\"").append(appUrl).append("\"> Register Link </a></p>")
				.append("<p>Thank you!</p>");
		return emailMsg.toString();
	}

	@PostMapping(path = "/api/user/update-status")
	public ResponseEntity<?> updateStatus(@RequestBody UserInformation userInformation,
			HttpServletRequest request, HttpServletResponse response) {
		if (userInformation == null) {
			return new ResponseEntity<String>("Missing paramenters", HttpStatus.BAD_REQUEST);
		}
		Result result = new Result();
		try {
			User user = userService.getUserByLogin(userInformation.getLogin());
			if (user == null) {
				return new ResponseEntity<String>("User not found : " + userInformation.getLogin(), HttpStatus.NOT_FOUND);
			}
			if(StringUtils.isBlank(user.getBillerId())) {
				return new ResponseEntity<String>("Biller Details missing : " + userInformation.getLogin(), HttpStatus.BAD_REQUEST);
			}
			Biller biller = billerService.getByBillerId(Long.parseLong(user.getBillerId()));
			if (biller != null) {
				if(biller.getStatus().equals("P")) {
					result.setMessage("Cannot update Status as Biller Status is Pending.");
					return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
				} else if(biller.getStatus().equals("N")) {
					result.setMessage("Cannot update status as Biller is Inactive.");
					return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
				} else {
					user.setStatus(USER_STATUS.Y);
					user.setModified(new Date());
					User updatedUser = userRepository.save(user);
					result.setDetails(updatedUser);
					result.setMessage("User status updated successfully");
					return new ResponseEntity<Result>(result, HttpStatus.OK);						
				}
			} else {
				result.setMessage("Biller not found for user : " + user.getUserName());
				result.setCode(HttpStatus.NOT_FOUND.ordinal());
				return new ResponseEntity<Result>(result, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setCode(HttpStatus.BAD_REQUEST.ordinal());
			return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(path = "/api/user/validate-otp")
	public ResponseEntity<?> validateOtp(@RequestBody UserInformation userInformation, 
			HttpServletRequest request, HttpServletResponse response, HttpSession session){
		Result result = new Result();
		try {
			if (userInformation == null) {
				return new ResponseEntity<String>("Missing paramenters", HttpStatus.BAD_REQUEST);
			}
			
			User user = userService.getUserByLogin(userInformation.getLogin());
			if (user == null) {
				return new ResponseEntity<String>("User not found : " + userInformation.getLogin(), HttpStatus.UNAUTHORIZED);
			}
			
			long currentTimeInMillis = System.currentTimeMillis();
	        long otpRequestedTimeInMillis = user.getOtpRequestedTime().getTime();
	         
	        if (otpRequestedTimeInMillis + Constants.OTP_VALID_DURATION < currentTimeInMillis) {
	        	return new ResponseEntity<String>("OTP expired, please try Login again", HttpStatus.BAD_REQUEST);
	        }
	        
	        Properties properties = propertiesRepository.findByPropertyName("max.otpfailure");
	        if(properties != null) {
	        	String otpAttempts = (String) request.getSession(true).getAttribute(Constants.ATTR_OTP_ATTEMPTS);
	        	String loginOtp = (String) request.getSession(true).getAttribute(Constants.ATTR_LOGIN_OTP);
	        	
	        	if(!StringUtils.isBlank(otpAttempts) && (Integer.parseInt(otpAttempts) > Constants.OTP_ATTEMPTS)) {
	        		request.getSession().removeAttribute(Constants.ATTR_OTP_ATTEMPTS);
	        		request.getSession().removeAttribute(Constants.ATTR_LOGIN_OTP);
	        		result.setMessage(properties.getPropertyValue());
	        		result.setCode(HttpStatus.BAD_REQUEST.ordinal());
	        		return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
	        	} else if(!StringUtils.isBlank(userInformation.getOtp()) && !StringUtils.isBlank(loginOtp) && !userInformation.getOtp().equals(loginOtp)) {
	        		int otpAttemptCount = Integer.parseInt(otpAttempts) + 1;
	        		request.getSession(true).setAttribute(Constants.ATTR_OTP_ATTEMPTS, String.valueOf(otpAttemptCount));
	        		logger.debug("Invalid Attempt : " + otpAttempts);
	        		result.setMessage("Invalid Attempt : " + otpAttempts);
	        		result.setCode(HttpStatus.BAD_REQUEST.ordinal());
	        		return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
	        	} else {
	        		result.setMessage("Login OTP validated successfully.");
	        		result.setCode(HttpStatus.OK.ordinal());
	        		request.getSession().removeAttribute(Constants.ATTR_OTP_ATTEMPTS);
	        		request.getSession().removeAttribute(Constants.ATTR_LOGIN_OTP);
	    			return new ResponseEntity<Result>(result, HttpStatus.OK);
	        	}
	        }else {
	        	logger.error("Max failed attempts for OTP is not configured.");
	        	return new ResponseEntity<String>("OTP expired, please try again", HttpStatus.BAD_REQUEST);
	        }
		}catch (Exception e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setCode(HttpStatus.BAD_REQUEST.ordinal());
			return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	//String otp = String.valueOf(OtpUtils.getRandomNumberSixDigit());
}
