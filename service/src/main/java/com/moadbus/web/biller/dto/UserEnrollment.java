package com.moadbus.web.biller.dto;

import lombok.Data;

@Data
public class UserEnrollment {

	private String login;
	private String userName;
	private String companyName;
	private String address;
	private String contactName;
	private String phone;
	private String password;
	private String email;
	private String bankId;
	private boolean mustChangePassword;
	private boolean sendAccountInfo;
	private boolean generatePassword;

}
