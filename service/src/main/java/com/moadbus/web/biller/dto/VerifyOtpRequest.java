package com.moadbus.web.biller.dto;

import lombok.Data;

@Data
public class VerifyOtpRequest {

	private String otp;
	private String roi;
	private String userName;

}
