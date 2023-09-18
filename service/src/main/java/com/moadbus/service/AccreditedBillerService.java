package service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import entity.AccreditedBiller;
import repository.AccreditedBillerRepository;

@Service
public class AccreditedBillerService {
	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private AccreditedBillerRepository accreditedBillerRepository;
	
	public AccreditedBiller getByIdAndToken(Integer id, String token) {
		logger.debug("getByIdAndToken: " + id);
		return accreditedBillerRepository.getByIdAndToken(id, token);
	}
}
