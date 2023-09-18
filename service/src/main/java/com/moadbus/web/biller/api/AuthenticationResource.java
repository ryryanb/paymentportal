package com.moadbus.web.biller.api;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import entity.Audit.AuditAction;
import entity.Biller;
import entity.Properties;
import entity.User;
import entity.User.USER_STATUS;
import repository.BillerRepository;
import repository.PropertiesRepository;
import repository.UserRepository;
import service.AuditService;
import service.BillerService;
import service.EmailService;
import service.UserService;
import tool.SMSSender;
import com.moadbus.web.biller.InvalidDataException;
import com.moadbus.web.biller.dto.AccreditedBillerUser;
import com.moadbus.web.biller.dto.ChangePasswordRequest;
import com.moadbus.web.biller.dto.ForgotPasswordRequest;
import com.moadbus.web.biller.dto.GenerateOtpRequest;
import com.moadbus.web.biller.dto.ResetPasswordRequest;
import com.moadbus.web.biller.dto.Result;
import com.moadbus.web.biller.dto.Signup;
import com.moadbus.web.biller.dto.UserCredential;
import com.moadbus.web.biller.dto.UserInformation;
import com.moadbus.web.biller.dto.VerifyOtpRequest;
import com.moadbus.web.biller.security.MD5EncryptionUtil;
import com.moadbus.web.biller.security.SecurityUtil;
import com.moadbus.web.biller.utils.Constants;
import com.moadbus.web.biller.utils.OtpUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
public class AuthenticationResource {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;
	
	@Autowired 
	private AuditService auditService;
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private BillerRepository billerRepository;

	@Autowired
    private BillerService billerService;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PropertiesRepository propertiesRepository;

	@Value("${authentication.masterotp}")
	private String masterOtp;

	@PostMapping(path = "/api/authenticate")
	public ResponseEntity<?> authenticate(@RequestBody UserCredential usersCredentials, HttpServletRequest request,
			HttpServletResponse response) {
		if (usersCredentials == null) {
			Result result = new Result();
			result.setCode(400);
			result.setMessage("Missing user credentials");
			return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
		}

		if (!StringUtils.hasText(usersCredentials.getUsername())
				|| !StringUtils.hasText(usersCredentials.getPassword())) {
			Result result = new Result();
			result.setCode(400);
			result.setMessage("username or password are empty !");
			return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
		}

		User user = userService.getUserByLogin(usersCredentials.getUsername());
		if (user == null) {
			Result result = new Result();
			result.setCode(401);
			result.setMessage("User doesn't exist");
			return new ResponseEntity<Result>(result, HttpStatus.UNAUTHORIZED);
		}else {
			logger.debug("Found user:" + user.getUserName());
		}

		if (!USER_STATUS.Y.equals(user.getStatus())) {
			Result result = new Result();
			result.setCode(401);
			result.setMessage("This user has been deactivated");
			return new ResponseEntity<Result>(result, HttpStatus.UNAUTHORIZED);
		}

		try {
			if (user.getEncryptedPassword().equals(MD5EncryptionUtil.encryptMD5(usersCredentials.getPassword()))) {
		        UserInformation userInfo = new UserInformation(user);				
				Result result = new Result();	
				
				//Generate OTP
				String otp = String.valueOf(OtpUtils.getRandomNumberSixDigit());
				request.getSession(true).setAttribute(Constants.ATTR_LOGIN_OTP, otp);
				user.setOtp(otp);
				user.setOtpRequestedTime(new Date());
				sendOtp(user, otp);
				Calendar calendar = Calendar.getInstance();
				int expiryTime = 5; // to be used from property table.
				calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + expiryTime);
				request.getSession(true).setAttribute("OTP_TIME_STAMP", calendar.getTime());
				
				userService.save(user);
				result.setMessage("Authentication is successful. OTP sent.");	
				result.setDetails(userInfo);
				//auditService.audit(user, AuditAction.LOGIN, null);
				return new ResponseEntity<Result>(result, HttpStatus.OK);
			} else {
				Result result = new Result();
				result.setCode(401);
				result.setMessage("Invalid Password!");
				return new ResponseEntity<Result>(result, HttpStatus.UNAUTHORIZED);
			}
		} catch (NoSuchAlgorithmException e) {
			logger.error("Error on login", e);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error on login", ex);
		}
		Result result = new Result();
		result.setCode(500);
		result.setMessage("Error in authenticate");
		return new ResponseEntity<Result>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@PostMapping(path = "/api/generateotp")
	public ResponseEntity<?> generateOtp(@RequestBody GenerateOtpRequest generateOtpRequest, HttpServletRequest request,
			HttpServletResponse response) {
		
		User user = userService.getUserByLogin(generateOtpRequest.getUserName());
		logger.info("user found" + user.getLogin());
		Result result = new Result();
		 
		try {
			   
				String otp = String.valueOf(OtpUtils.getRandomNumberSixDigit());
				logger.info("otp generated:" + otp);
				request.getSession(true).setAttribute(Constants.ATTR_LOGIN_OTP, otp);
				user.setOtp(otp);
				user.setOtpRequestedTime(new Date());
				sendOtp(user, otp);
				Calendar calendar = Calendar.getInstance();
				int expiryTime = 5; // to be used from property table.
				calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + expiryTime);			
				request.getSession(true).setAttribute("OTP_TIME_STAMP", calendar.getTime());
				
				userService.save(user);
				//auditService.audit(user, AuditAction.LOGIN, null);
				result.setMessage("Successful OTP regeneration. ");
				result.setCode(200);
				return new ResponseEntity<Result>(result, HttpStatus.OK);

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error in generating OTP", ex);
		}
		result.setCode(500);
		result.setMessage("Error in generating OTP");
		return new ResponseEntity<Result>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@PostMapping(path = "/api/verifyotp")
	public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest, HttpServletRequest request,
			HttpServletResponse response) {
		//String masterOtp = propertiesRepository.findByPropertyName("masterOtp").getPropertyValue();
		logger.debug("start method: verifyOtp");

		User user = userService.getUserByLogin(verifyOtpRequest.getUserName());

		String sessionOtp = user.getOtp();
		logger.info("otp from request" + verifyOtpRequest.getOtp());
		logger.info("sessionOtp" + sessionOtp);		

		Result result = new Result();		

		if( verifyOtpRequest.getOtp().equals(sessionOtp) || (StringUtils.hasText(masterOtp)&& verifyOtpRequest.getOtp().equals(masterOtp))) {

			UserInformation userInfo = new UserInformation(user);
			userInfo.setBillerId(user.getBillerId());
			String device = "BROWSER";
			if (request.getHeader("User-Agent").indexOf("Mobile") != -1) {
				device = "MOBILE";
			}
			Date currentDate = new Date();
			Date expiryDate = new Date(System.currentTimeMillis() + SecurityUtil.JWT_EXPIRATIONTIME);
			String deviceIp = request.getRemoteAddr();
			String token = Jwts.builder().setSubject(verifyOtpRequest.getUserName()).claim("device", device)
					.claim("IP", deviceIp).setNotBefore(currentDate).setExpiration(expiryDate)
					.signWith(SignatureAlgorithm.HS256, SecurityUtil.JWT_SECRET).compact();

			response.addHeader(SecurityUtil.HEADER_STRING, SecurityUtil.TOKEN_PREFIX + " " + token);			
			userInfo.setToken(token); 			

			result.setMessage("Login OTP validated successfully.");
			result.setCode(200);
			result.setDetails(userInfo);
			request.getSession().removeAttribute(Constants.ATTR_OTP_ATTEMPTS);
			request.getSession().removeAttribute(Constants.ATTR_LOGIN_OTP);
			return new ResponseEntity<Result>(result, HttpStatus.OK);

		}

		else  {    		
			logger.debug("Invalid Attempt" );
			result.setMessage("Invalid Attempt ");
			result.setCode(400);
			return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);

		}

	}

	@PostMapping(path = "/api/changePassword")
	public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePassRequest, HttpServletRequest request,
			HttpServletResponse response) {
		if (changePassRequest == null) {
			Result result = new Result();
			result.setMessage("Missing parameters.");
    		result.setCode(400);
			return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
		}

		if (!StringUtils.hasText(changePassRequest.getOldPassword())
				|| !StringUtils.hasText(changePassRequest.getNewPassword())) {
			Result result = new Result();
			result.setMessage("Old and new password are required!");
    		result.setCode(400);
			return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
		}

		User user = userService.getCurrentUser();
		Result result = new Result();

		try {
			if (user.getEncryptedPassword().equals(MD5EncryptionUtil.encryptMD5(changePassRequest.getOldPassword().trim()))) {
				userService.updatePassword(user.getUserName(), changePassRequest.getNewPassword());
				result.setMessage("Password has been successfully change.");
				auditService.audit(user, AuditAction.LOGIN, "Password has been successfully change.");
				return new ResponseEntity<Result>(result, HttpStatus.OK);
			} else {
				result.setMessage("Invalid Password!");
	    		result.setCode(400);
				return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
			}
		} catch (NoSuchAlgorithmException e) {
			logger.error("Error on login", e);
		}

		result.setMessage("Couldn't create token");
		result.setCode(500);
		return new ResponseEntity<Result>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@PostMapping(path = "/api/forgotPassword")
	public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPassRequest,
			HttpServletRequest request, HttpServletResponse response) {
		if (forgotPassRequest == null) {
			Result result = new Result();
			result.setMessage("Missing paramenters");
    		result.setCode(400);
			return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
		}
		Result result = new Result();
		try {
			if (!StringUtils.hasText(forgotPassRequest.getUserName())) {
				throw new InvalidDataException("User name is required");
			}
			
			if (!StringUtils.hasText(forgotPassRequest.getEmail())) {
				throw new InvalidDataException("Email is required");
			}
			User user = userService.getUserByLoginAndEmail(forgotPassRequest.getUserName(),
					forgotPassRequest.getEmail());
			if (user == null) {
				result.setMessage("User is not existed!");
	    		result.setCode(401);
				return new ResponseEntity<Result>(result, HttpStatus.UNAUTHORIZED);
			} if (!USER_STATUS.Y.equals(user.getStatus())) {
				result.setMessage("This user has been deactivated");
	    		result.setCode(401);
				return new ResponseEntity<Result>(result, HttpStatus.UNAUTHORIZED);
				
			} else {
				logger.debug("Found user:" + user.getUserName());
			}
			String token = UUID.randomUUID().toString();
			logger.debug("Reset password token:" + token);
			user.setResetPasswordToken(token);
			userService.save(user);
			//send email
			String emailMsg = getForgotPasswordEmailContent(token, user.getUserName());
			//TODO: pankaj | get email from biller table
	        emailService.sendEmail(user.getEmail(), "Forgot Password", emailMsg);
			result.setMessage("Reset password link has been sent to your registered email.");

			return new ResponseEntity<Result>(result, HttpStatus.OK);

		} catch (InvalidDataException e) {
			result.setMessage(e.getMessage());
			result.setCode(HttpStatus.BAD_REQUEST.ordinal());
			return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
		}
	}
	
	private String getForgotPasswordEmailContent(String token, String username) {
		String resetUrl = "https://core1.moadbusglobal.com" + servletContext.getContextPath() + "/#/reset-password/"
				+ token;
		StringBuilder emailMsg = new StringBuilder();
		emailMsg.append("<p> Dear ").append(username).append(",</p>")
				.append("<p>You've requested a password reset: ").append("<a href=\"").append(resetUrl)
				.append("\">" + resetUrl + "</a></p>").append("<p>Thanks!</p>");
		return emailMsg.toString();
	}
	
	@PostMapping(path = "/api/resetPassword")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest,
			HttpServletRequest request, HttpServletResponse response) {
		if (resetPasswordRequest == null) {
			Result result = new Result();
			result.setMessage("Missing parameters");
    		result.setCode(400);
			return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
		}
		Result result = new Result();
		try {
			User user = userService.getUserByResetPasswordToken(resetPasswordRequest.getToken());
			if (user == null) {
				result.setMessage("Token is invalid!");
	    		result.setCode(401);
				return new ResponseEntity<Result>(result, HttpStatus.UNAUTHORIZED);
			}

			if (!StringUtils.hasText(resetPasswordRequest.getNewPassword())) {
				throw new InvalidDataException("New password is required");
			}
			 userService.updatePassword(user.getUserName(),
			 resetPasswordRequest.getNewPassword(), true);
			result.setMessage("Password has been successfully reset.");
			return new ResponseEntity<Result>(result, HttpStatus.OK);
		} catch (InvalidDataException e) {
			result.setMessage(e.getMessage());
			result.setCode(HttpStatus.BAD_REQUEST.ordinal());
			return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * #3561 | Pankaj | Register Biller portal user
	 * @param signup
	 * @return Response object
	 */
	@PostMapping(path = "/api/signup")
	public ResponseEntity<Result> signup(@RequestBody Signup signup) {
		logger.info("Start --> Portal User Signup");
		Result result = new Result();
		try {
			if (!StringUtils.hasText(signup.getBillerId())) {
				result.setMessage("Biller Id is empty.");
				result.setCode(HttpStatus.BAD_REQUEST.ordinal());
				return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
			}

            Biller biller = billerService.getByBillerId(Long.parseLong(signup.getBillerId()));
			if(biller == null) {
				result.setMessage("Invalid Biller Id : " + signup.getBillerId());
				result.setCode(HttpStatus.BAD_REQUEST.ordinal());
				return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
			}			
			
			User user = userRepository.findByUserNameIgnoreCase(signup.getUserName());
			if(user != null) {
				result.setMessage("Username already exists. Please try a different username.");
				result.setCode(HttpStatus.BAD_REQUEST.ordinal());
				return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
			}
			
			validateSignup(signup, signup.getBillerId());
			User newUser = userService.signup(signup, signup.getBillerId());
			AccreditedBillerUser billerUser = null;
			if(newUser != null) {
				billerUser = new AccreditedBillerUser();
				billerUser.setId(newUser.getId());
				billerUser.setUserName(newUser.getUserName());
				result.setDetails(billerUser);
				result.setMessage("User Signup is successful.");
				return new ResponseEntity<Result>(result, HttpStatus.OK);	
			} else {
				result.setDetails(null);
				result.setMessage("User Signup not successful.");
				result.setCode(HttpStatus.NOT_FOUND.ordinal());
				return new ResponseEntity<Result>(result, HttpStatus.NOT_FOUND);
			}
		} catch (InvalidDataException e) {
			e.printStackTrace();
			logger.error("InvalidDataException --> Signup", e);
			result.setMessage(e.getMessage());
			result.setCode(HttpStatus.BAD_REQUEST.ordinal());
			return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception --> Signup", e);
			if (e.getMessage().contains("ConstraintViolationException")) {
				result.setMessage("Username already exists");
				result.setCode(HttpStatus.BAD_REQUEST.ordinal());
				return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
			} else {
				result.setMessage(e.getMessage());
				result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.ordinal());
				return new ResponseEntity<Result>(result, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}finally {
			logger.info("End --> Portal User Signup");
		}
	}
	
	/**
	 * Validate user signup details
	 * @param signup
	 * @param billerId
	 * @throws InvalidDataException
	 */
	private void validateSignup(Signup signup, String billerId) throws InvalidDataException {

		//Currently one user per Biller
		if (userService.findByBillerIdAndStatus(billerId, USER_STATUS.Y) != null) {
			throw new InvalidDataException("Only one user allowed to register for Accredited Biller.");
		}

		if (!StringUtils.hasText(signup.getUserName())) {
			throw new InvalidDataException("Please enter Username.");
		} else if (userService.getUserByLogin(signup.getUserName()) != null) {
			throw new InvalidDataException("Username already exists");
		}
		if (!StringUtils.hasText(signup.getPassword())) {
			throw new InvalidDataException("Please enter password.");
		}
		if (!StringUtils.hasText(signup.getConfirmPassword())) {
			throw new InvalidDataException("Please enter confirm password.");
		}

		if (StringUtils.hasText(signup.getPassword()) && StringUtils.hasText(signup.getConfirmPassword())) {
			if (!signup.getPassword().equalsIgnoreCase(signup.getConfirmPassword())) {
				throw new InvalidDataException("Password should match. Please try again.");
			}
		}

	}
	
	/**
	 * Send Login OTP
	 * @param user
	 */
	private void sendOtp(User user, String otp) throws Exception{
		try {
			logger.info("METHOD sendOtp");
			String billerId = user.getBillerId();
			String email = "";
			String phoneNo = null;
			Biller biller = billerRepository.findBillerByBillerId(Long.parseLong(billerId));
			logger.info("biller set" + biller.getBillerName());
			if(biller != null) {
				email = biller.getEmailAddress();

				logger.info("email" + email);
				
				phoneNo = biller.getPhoneNumber();
				Properties otpConfigProperty = null;
				try {
					otpConfigProperty = propertiesRepository.findByPropertyName("otpSendMedium");
				logger.info("otpConfigProperty" + otpConfigProperty);
				} catch (Exception ex) {
					logger.info("otpConfigProperty set to default bothe email and phone");
				}
				if(otpConfigProperty != null) {
					String otpMode = otpConfigProperty.getPropertyValue() != null ? otpConfigProperty.getPropertyValue() : "2";
					// 0 -> SMS | 1 -> Email | 2 -> Both
					logger.info("otpMode" + otpMode);
					
					if(phoneNo != null && (otpMode.equals("0") || otpMode.equals("2"))) { 
						    sendSmsOtp(phoneNo, biller.getBillerShortName(), otp );
 						logger.info("SMS OTP sent successfully.");
					} 
					if( (otpMode.equals("1") || otpMode.equals("2"))) {
						    sendEmailOtp(email, biller.getBillerShortName(), otp);
						//TODO: Audit
						logger.info("Email OTP sent successfully.");
					} 
				} else {
					throw new Exception("OTP sending medium(sms/email) is not configured.");
				}
			} 
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in sendOtp: ", e);
		}
	}
	
	/**
	 * Send OTP by Email
	 * @param email
	 * @param billerName
	 */
	private void sendEmailOtp(String email, String billerName, String otp) {
		String emailMsg = getOtpEmailContent(billerName, String.valueOf(otp));
		emailService.sendEmail(email, "Login - One Time Password (OTP)", emailMsg);
	}
	
	private String getOtpEmailContent(String billerName, String otp) {
		StringBuilder emailMsg = new StringBuilder();
		emailMsg.append("<p> Dear ").append(billerName).append(",</p>")
				.append("<p>Please use the following One Time Password(OTP) to Login </p> ")
				.append("<p><b>").append(otp).append("</b></p><br/>")
				.append("<p> Note: This Otp is valid only for 5 minutes. </p>")
				.append("<p>Thanks!</p>");
		return emailMsg.toString();
	}
	
	private void sendSmsOtp(String phoneNo, String billerName, String otp) {
		String message = "Your one time OTP for online transaction " + otp;

		final SMSSender smsSender = new SMSSender();
		try {
			smsSender.sendSMS(phoneNo, message);
		} catch (Exception ex) {
			logger.error("Error in sendSmsOTP");
			ex.printStackTrace();
		}
	}

}
