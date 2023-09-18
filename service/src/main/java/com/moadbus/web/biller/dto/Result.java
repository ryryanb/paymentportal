package com.moadbus.web.biller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result {

	private int code;
	private String message;
	private Object details;
	
	public Result() {
		this.code = 200;
	}

}
