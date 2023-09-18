package entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "b_audit")
public class Audit implements Serializable{

	private static final long serialVersionUID = 547403540710688785L;
	
	public enum AuditAction{
		LOGIN, PAYMENT_QUERY, CHANGE_PASSWORD, LOGOUT 
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
 
    //@JoinColumn(name = "user_id", referencedColumnName = "id")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
    @Column(name = "created")
    private Date created;
    
    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    private AuditAction action;
    
    @Column(name = "details")
    private String details;

}
