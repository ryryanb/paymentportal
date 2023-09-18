package com.moadbus.web.biller.conf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.moadbus.web.biller.security.AjaxAuthenticationFailureHandler;
import com.moadbus.web.biller.security.JWTAuthenticationProcessingFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	RestAuthenticationEntryPoint restAuthenticationEntryPoint;
	
	@Autowired
	SavedRequestAwareAuthenticationSuccessHandler successHandler;
	
	@Autowired
	Environment enviroment;
	
	@Autowired
	AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;
	
//	SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
	
	@Override
	protected void configure(HttpSecurity http) throws Exception { 
	    http.cors().and()
	    .csrf().disable()
	    .exceptionHandling()
	    .authenticationEntryPoint(restAuthenticationEntryPoint)
	    .and()
	    .authorizeRequests()
	    .antMatchers(HttpMethod.POST, "/api/authenticate").permitAll()
	    .antMatchers(HttpMethod.POST, "/api/user/enroll").permitAll()
	    .antMatchers(HttpMethod.POST, "/api/signup").permitAll()
	    .antMatchers(HttpMethod.POST, "/api/forgotPassword").permitAll()
	    .antMatchers(HttpMethod.POST, "/api/resetPassword").permitAll()
	    .antMatchers(HttpMethod.POST, "/api/verifyotp").permitAll()
	    .antMatchers(HttpMethod.POST, "/api/generateotp").permitAll()
	    //.antMatchers(HttpMethod.POST, "/api/changePassword").permitAll()
	    //.antMatchers(HttpMethod.POST, "/api/payment/history").permitAll()
	    
	    .antMatchers("/api/v2/api-docs").permitAll()
	    .antMatchers("/api/swagger-ui/").permitAll()
	    
	    .regexMatchers("\\/api\\/[a-zA-Z0-9/?=&-_]+").authenticated()
	    //.antMatchers("/api/admin/**").hasRole("ADMIN")
	    .and()
	    .formLogin()
	    .successHandler(successHandler)
	    .failureHandler(ajaxAuthenticationFailureHandler)
	    .usernameParameter("j_username")
        .passwordParameter("j_password")
	    .and()
	    .logout()
	    .and().addFilterBefore(new JWTAuthenticationProcessingFilter(userDetailsService), BasicAuthenticationFilter.class);
	}
	
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
    	
    	String stringDomains = enviroment.getProperty("trusted.domains");
    	List<String> trustedDomains = new ArrayList<String>();
    	if (StringUtils.isNotBlank(stringDomains) && !stringDomains.contains("@trusted.domains@") ){
	    	String domains[] = StringUtils.splitByWholeSeparatorPreserveAllTokens(stringDomains, ",");
	    	for (String d : domains){
	    		d = StringUtils.trim(d);
	    		if (StringUtils.isNotBlank(d)){
	    			trustedDomains.add(d);
	    		}
	    	}
    	}
    	
    	
        CorsConfiguration configuration = new CorsConfiguration();
        if (trustedDomains == null || trustedDomains.size() ==0){
        	 configuration.setAllowedOrigins(Arrays.asList("*"));
        }else {
        	 configuration.setAllowedOrigins(trustedDomains);
        }

        configuration.setAllowedHeaders(Arrays.asList("origin", "content-type", "accept", "authorization", "x-requested-with"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/*", configuration);
        return source;
    }
}
