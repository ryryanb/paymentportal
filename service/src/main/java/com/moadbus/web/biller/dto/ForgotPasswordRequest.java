package com.moadbus.web.biller.dto;

import lombok.Data;

@Data
public class ForgotPasswordRequest {

	private String userName;
	private String email;
	
}
