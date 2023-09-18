package com.moadbus.web.biller.security;

import org.springframework.security.core.GrantedAuthority;

public class GrantedAuthorityImpl implements GrantedAuthority {

	private static final long serialVersionUID = -9111965017651546875L;
	private String authority = null;

	public GrantedAuthorityImpl(String auth) {
		authority = auth;
	}

	@Override
	public String getAuthority() {
		return authority;
	}
}
