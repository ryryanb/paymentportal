package com.moadbus.web.biller.dto;

import lombok.Data;

@Data
public class SmsMessage {

	private String phone;
	private String message;

}
