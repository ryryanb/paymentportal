package com.moadbus.web.biller.utils;

public class Constants {
	
	public static final String usernameParams = "j_username";
	public static final String passwordParams = "j_password";
	public static final Integer LOGIN_ATTEMPTS = 3;
	
	public static final long OTP_VALID_DURATION = 5 * 60 * 1000;
	public static final String ATTR_OTP_ATTEMPTS = "OTP_ATTEMPTS";
	public static final Integer OTP_ATTEMPTS = 3;
	public static final String ATTR_LOGIN_OTP = "LOGIN_OTP";
	
	/**
	 * Prefix to fetch all the message related to Biller portal 
	 * messages from Label Property Table(Language Pack)
	 */
	public static final String PREFIX_BILLER_PORTAL = "biller.portal.";
	

}
