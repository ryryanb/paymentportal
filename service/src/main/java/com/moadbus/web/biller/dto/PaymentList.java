package com.moadbus.web.biller.dto;

import lombok.Data;

import java.util.Map;

@Data
public class PaymentList extends BaseList<Payment>{

	private Map<String, String> totalAmount;

}
