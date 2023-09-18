package com.moadbus.web.biller.security;

public class SecurityUtil {
	public static final String JWT_SECRET = "fwio432360583j5l63jj5345805353805lrtwe";
	
	public static final long   JWT_EXPIRATIONTIME = 3600000; // 3600000 = 60 minute 
	
	public static final String JWT_TOKEN_PARAM = "token";
	
	public static final String TOKEN_PREFIX = "Bearer";
	
	public static final String HEADER_STRING = "Authorization";
	
}
