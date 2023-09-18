package com.moadbus.web.biller.dto;

import lombok.Data;

@Data
public class BillSearch {
//Allow Third party provider to search on customer third party bill account number,
//and get back a list of payment history of the bank customer. 
	//Each element of the list will contain the Biller ID, Customer account number, 
	//Amount, Date-Time payment made and payment details.
	//4. Allow third party provider to search by Date (defaults to current) 
	//and get back a list of the transactions made (similar to point 3) , the total amount paid on that date etc
	//
	
	private String billAccountNumber;
	private String billerId;
	private String fromDate;
	private String toDate;
	private String sortBy;
	private String search;
	private int page;
	private String sortDirection;
	private int perPage;
	private String refNum;

}
