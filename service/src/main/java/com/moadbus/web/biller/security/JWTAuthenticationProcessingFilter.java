package com.moadbus.web.biller.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JWTAuthenticationProcessingFilter extends GenericFilterBean {

	protected final Log logger = LogFactory.getLog(getClass());



	private UserDetailsService userDetailsService;

	public JWTAuthenticationProcessingFilter(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String token = ((HttpServletRequest) request).getHeader(SecurityUtil.HEADER_STRING);
		logger.debug("token:" + token);
		if (token != null && !token.startsWith("Basic")) {
			try{
				if (!authenticate((HttpServletResponse)response,token)){
					return;
				}

			}catch(Exception e) {
				
				logger.error(e.getMessage(),e);
				((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			} 
		}else if (token ==null){
			//for image, document links
			token = ((HttpServletRequest) request).getParameter(SecurityUtil.JWT_TOKEN_PARAM);
			if (token !=null){
				if (!authenticate((HttpServletResponse)response,token)){
					return;
				}
			}
		}
		
		chain.doFilter(request, response);

	}
	
	private boolean authenticate(HttpServletResponse response, String token) throws IOException{
		Claims claims = Jwts.parser().setSigningKey(SecurityUtil.JWT_SECRET).parseClaimsJws(token.replace(SecurityUtil.TOKEN_PREFIX, "")).getBody();
		if (claims == null || claims.getExpiration().before(new Date())) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}

		String username = claims.getSubject();	
		if (!StringUtils.hasText(username)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
		
		UserDetails user = userDetailsService.loadUserByUsername(username);
		if (user == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
		
		Authentication authentication = new AuthenticationImpl((SecurityUser) user);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return true;
	}

}
