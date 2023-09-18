package service;

import entity.Biller;
import repository.BillerRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillerService {
	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private BillerRepository billerRepository;
	
	public Biller getByBillerId(Long id) {
		logger.debug("getByBillerId: " + id);
		return billerRepository.findByBillerId(id);
	}

	public List<Biller> getAllBillers() {
		logger.debug("getAllBillers");
		return billerRepository.findAll();
	}

}
