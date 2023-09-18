package service;

import java.util.Date;

import javax.transaction.Transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import entity.Audit;
import entity.Audit.AuditAction;
import entity.User;
import repository.AuditRepository;

@Service
public class AuditService {
	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private AuditRepository auditRepository;

	
	@Transactional
	public void audit(User user, AuditAction action,String actionData) {
		Audit audit = new Audit();
		audit.setUser(user);
		audit.setAction(action);
		audit.setDetails(actionData);
		audit.setCreated(new Date());
		auditRepository.save(audit);
	}
	

}
