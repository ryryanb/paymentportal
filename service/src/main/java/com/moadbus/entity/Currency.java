package entity;

import lombok.Data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "CURRENCY")
public class Currency implements Serializable {

	// CURRENCYCODE, CURRENCYSYMBOL, BANK_IDENTIFIER, ISOCODE
	private static final long serialVersionUID = -7988859848027034600L;

	@Id
	@Column(name = "CURRENCYCODE")
	private String currencyCode;
	@Column(name = "CURRENCYSYMBOL")
	private String currencySymbol;
	@Column(name = "ISOCODE")
	private String isoCode;

}
