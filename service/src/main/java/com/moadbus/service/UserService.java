package service;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.transaction.Transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import entity.User;
import entity.User.USER_STATUS;
import repository.UserRepository;
import com.moadbus.web.biller.dto.Signup;
import com.moadbus.web.biller.dto.UserEnrollment;
import com.moadbus.web.biller.security.MD5EncryptionUtil;
import com.moadbus.web.biller.security.SecurityUser;

@Service
public class UserService {
	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private UserRepository userRepository;
	
	public User getCurrentUser() {
		if (SecurityContextHolder.getContext().getAuthentication() == null
				|| SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
			return null;
		}

		SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return user.getUser();
	}

	public User getUserByLogin(String userName) {
		logger.debug("findByLogin:" + userName);
		return userRepository.findByUserNameIgnoreCase(userName);		
	}
	
	@Transactional
	public User enroll(UserEnrollment userEnrollment) {
		User user = new User();
		user.setCompanyName(userEnrollment.getCompanyName());
		user.setStatus(USER_STATUS.P);
		user.setContactName(userEnrollment.getContactName());
		user.setEmail(userEnrollment.getEmail());
		user.setLogin(userEnrollment.getLogin());
		user.setUserName(userEnrollment.getLogin());
		user.setPhone(userEnrollment.getPhone());
		user.setAddress(userEnrollment.getAddress());
		user.setBankIdentifier(userEnrollment.getBankId());
		if(userEnrollment.isGeneratePassword() || userEnrollment.isMustChangePassword()) {
			user.setForceChangePassowrd(true);
		}
		user.setCreated(new Date());
		try {
			user.setEncryptedPassword(MD5EncryptionUtil.encryptMD5(userEnrollment.getPassword().trim()));
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
		}
		return userRepository.save(user);
	}
	
	@Transactional
	public User signup(Signup signup, String billerId) {
		User user = new User();
		user.setUserName(signup.getUserName());
		user.setStatus(USER_STATUS.Y);
		user.setCreated(new Date());
		user.setModified(new Date());
		user.setBillerId(billerId);
		try {
			user.setEncryptedPassword(MD5EncryptionUtil.encryptMD5(signup.getPassword().trim()));
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
		}
		user.setForceChangePassowrd(true);
		user.setFirstLogin(true);
		return userRepository.save(user);
	}
	
	@Transactional
	public void updatePassword(String userName, String password, boolean resetToken) {
		User user = userRepository.findByUserNameIgnoreCase(userName);
		try {
			if(resetToken) {
				user.setResetPasswordToken(null);
			} else {
				user.setForceChangePassowrd(false);
			}
			user.setEncryptedPassword(MD5EncryptionUtil.encryptMD5(password.trim()));
			userRepository.save(user);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
		}
	}
	
	public void updatePassword(String userName, String password) {
		updatePassword(userName, password, false);
	}
	
	public User getUserByLoginAndEmail(String userName, String email) {
		logger.debug("findUserByLoginAndEmail: " + userName + " - " + email);
		return userRepository.findByUserNameIgnoreCaseAndEmailIgnoreCase(userName, email);
	}
	
	public void save (User user) {
		userRepository.save(user);
	}
	
	public User getUserByResetPasswordToken(String token) {
		logger.debug("findUserByResetPasswordToken: " + token);
		return userRepository.findByResetPasswordToken(token);		
	}

	public User getUserByEmail(String email) {
		logger.debug("findUserByEmail: " + email);
		return userRepository.findByEmailIgnoreCase(email);
	}
	
	public User findByBillerIdAndStatus(String billerId, USER_STATUS status) {
		return userRepository.findByBillerIdAndStatus(billerId, status);
	}
}
