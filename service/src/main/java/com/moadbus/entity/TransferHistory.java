package entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "TRANSFER_HISTORY")
public class TransferHistory implements Serializable {

	//ID;FROM_ACCT;TO_ACCT;FROM_ACCT_NAME;TO_ACCT_NAME;FROM_ACCT_TYPE_ID;TO_ACCT_TYPE_ID;EMAIL_ID;PHONE_NO;
	//USER_ID;CURR_CODE;AMOUNT;CREATE_TIME;;FREQUENCY;TRANSFER_DATE;START_DATE;DURATION;
	//NUM_TRANSFER;CONTINUE_TILL;BANK_IDENTIFIER;FX_RATE;FX_AMOUNT;TRAN_CURR_CODE;ROUTING_NO;BANK_NAME;
	//PAYEE_NICKNAME;TRANSFER_TYPE;COMMISSION;TCA;STAMP;TAX_FEE;REF_NUM;CONF_ID;BILLER_ID
	private static final long serialVersionUID = -645968896179759963L;

	@Id
	@Column(name = "ID")
	private Long id;

	@Column(name = "FROM_ACCT")
	private String fromAccount;
	
	@Column(name = "TO_ACCT")
	private String toAccount;
	
	@Column(name = "AMOUNT")
	private double amount;
	
	@Column(name = "CREATE_TIME")
	private Date created;

	@Column(name = "COMMENT")
	private String comment;
	
	@Column(name = "CURR_CODE")
	private String currencyCode;
	
	@Column(name = "CONF_ID")
	private Long confirmationId;
	
	@Column(name = "BILLER_ID")
	private String billerId;
	
	@Column(name = "TO_ACCT_NAME")
	private String toAcctName;
	
	@Column(name = "REF_NUM")
	private String refNum;

}
