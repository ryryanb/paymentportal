package entity;

import lombok.Data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ACCREDITED_BILLER")
public class AccreditedBiller implements Serializable {

	private static final long serialVersionUID = -2937682679280608908L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "DESCRIPTION")
	private String billerName;

	@Column(name = "ACCT_NUM")
	private String accountNumber;

	@Column(name = "ACCOUNT_TYPE")
	private Integer accountType;

	@Column(name = "COST_CENTER")
	private String costCenter;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "BANK_IDENTIFIER")
	private String bankIdentifier;
	
	@Column(name = "CURRENCY")
	private String accountCurrency;
	
	@Column(name = "CONTACT_PERSON")
	private String contactPerson;

	@Column(name = "EMAIL_ADDRESS")
	private String emailAddress;
	
	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;

	@Column(name = "BANK_ROUTINGNO")
	private String bankRoutingNo;

	@Column(name = "BANK_NAME")
	private String bankName;

	@Column(name = "BILLER_SHORT_NAME")
	private String billerShortName;

	@Column(name = "USER_CIF")
	private String userCif;
	
	@Column(name = "BILLER_TYPE")
	private Integer billerType;

	@Column(name = "BILLER_ID")
	private Integer billerId;
	
	@Column(name = "TOKEN")
	private String token;

}
