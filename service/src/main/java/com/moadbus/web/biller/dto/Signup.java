package com.moadbus.web.biller.dto;

import lombok.Data;

@Data
public class Signup {

	private String userName;
	private String password;
	private String confirmPassword;
	private String billerId;

}
