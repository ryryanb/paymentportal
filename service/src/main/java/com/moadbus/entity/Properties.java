package entity;

import lombok.Data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "PROPERTIES")
public class Properties implements Serializable {
	// PROPERTYNAME, PROPERTYVALUE, CREATEDDATE, MODIFIEDDATE, DESCRIPTION,
	// BANK_IDENTIFIER, PROPTYPE, STATUS
	private static final long serialVersionUID = -5266985080897018497L;

	@Id
	@Column(name = "PROPERTYNAME")
	private String propertyName;
	
	@Column(name = "PROPERTYVALUE")
	private String propertyValue;

}
