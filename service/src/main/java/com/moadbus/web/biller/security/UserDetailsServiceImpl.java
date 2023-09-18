package com.moadbus.web.biller.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import entity.User;
import entity.User.USER_STATUS;
import service.UserService;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		logger.info("Loading user: " + username);
		User user = userService.getUserByLogin(username);
		if (user == null) {
			logger.warn("Username not found: " + username);
			throw new UsernameNotFoundException("Username not found: " + username);
		}

		SecurityUser loggedUser = new SecurityUser(user,
				new GrantedAuthority[] { new GrantedAuthorityImpl(getRole(user)) });
		return loggedUser;
	}

	private String getRole(User user) {
		String userRole = "";
		if (USER_STATUS.Y.equals(user.getStatus())) {
			userRole = "ROLE_" ;//+ user.getRole();
		}
		logger.info("User role:" + userRole);
		return userRole;
	}
}
