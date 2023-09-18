package com.moadbus.web.biller.security;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import entity.User;
import entity.User.USER_STATUS;

public class SecurityUser implements UserDetails {

	private static final long serialVersionUID = 3208888935944605903L;

	private User user;

	private Locale locale;

	private GrantedAuthority[] authorities = null;

	public SecurityUser(User user, GrantedAuthority[] authorities) {

		this.authorities = authorities;
		this.user = user;
		this.locale = StringUtils.parseLocaleString("fr");

	}

	public User getUser() {
		return user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
		Collections.addAll(auth, authorities);
		return auth;
	}

	@Override
	public String getPassword() {
		try {
			return StringUtils.hasText(user.getEncryptedPassword()) ? user.getEncryptedPassword()
					: MD5EncryptionUtil.encryptMD5("");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getUsername() {
		return user.getUserName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		if (USER_STATUS.Y.equals(user.getStatus())) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		if (USER_STATUS.Y.equals(user.getStatus())) {
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		return "SecurityUser:" + user.getUserName();
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
