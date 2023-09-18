package com.moadbus.web.biller.dto;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import entity.TransferHistory;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Payment {
	
	private Long id;
	
	private String billerId;
	
	private String fromAccount;
	
	private String toAccount;
	
	private String amount;
	
	private String date;

	private String comment;
	
	private String currency;
	
	private Long transactionId;
	
	private String amountText;
	
	private String toAcctName;
	
	private String refNum;

	public Payment(TransferHistory history) {
		this.id = history.getId();
		//mask the From Account number, show only the last 4 digits
		this.fromAccount = history.getFromAccount().replaceAll(".(?=.{4})", "X");
		this.toAccount = history.getToAccount();
		this.amount = new DecimalFormat("#,##0.00").format(history.getAmount());
		this.comment = history.getComment().replace("BILL_PAY:", "");
		DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		this.date = sdf.format(history.getCreated());
		this.setTransactionId(history.getConfirmationId());
		this.setBillerId(history.getBillerId());
		this.setToAcctName(history.getToAcctName());
		this.setRefNum(history.getRefNum());
	}

}
