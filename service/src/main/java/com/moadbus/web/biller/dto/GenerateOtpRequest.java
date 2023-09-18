package com.moadbus.web.biller.dto;

import lombok.Data;

@Data
public class GenerateOtpRequest {

	private String userName;
	private String token;

}
