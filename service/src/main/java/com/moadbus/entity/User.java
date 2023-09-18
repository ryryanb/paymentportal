package entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ACCREDITED_BILLER_USER")
public class User implements Serializable {

	private static final long serialVersionUID = 3508617275079720960L;

	public enum USER_STATUS {
		Y, N, P, L
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "LOGIN", unique = true)
	private String login;

	@Column(name = "USERNAME", unique = true)
	private String userName;

	@Column(name = "ENCRYPTED_PASSWORD")
	private String encryptedPassword;

	@Column(name = "STATUS", length = 1)
	@Enumerated(EnumType.STRING)
	private USER_STATUS status;

	@Column(name = "CREATED")
	private Date created;

	@Column(name = "MODIFIED")
	private Date modified;

	@Column(name = "RESET_PASSWORD_TOKEN")
	private String resetPasswordToken;

	@Column(name = "FORCE_CHANGE_PASSWORD")
	private boolean forceChangePassowrd;

	@Column(name = "BILLER_ID")
	private String billerId;

	@Column(name = "FIRST_LOGIN")
	private boolean firstLogin;

	@Column(name = "COMPANY_NAME")
	private String companyName;

	@Column(name = "CONTACT_NAME")
	private String contactName;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "PHONE")
	private String phone;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "BANK_IDENTIFIER")
	private String bankIdentifier;

	@Column(name = "LOGIN_ATTEMPTS")
	private Integer loginAttempts = 0;
	
	@Column(name = "OTP_REQUESTED_TIME")
    private Date otpRequestedTime;
	
	@Column(name = "OTP")
    private String otp;
	
	@Column(name = "OTP_ATTEMPTS")
	private Integer otpAttempts = 0;

}
