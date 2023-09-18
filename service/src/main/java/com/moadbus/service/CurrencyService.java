package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import entity.Currency;
import repository.CurrencyRepository;

@Service
public class CurrencyService {
	protected final Log logger = LogFactory.getLog(getClass());
	@Autowired
	private CurrencyRepository repository;
	private Map<String, String> currencyMap;

	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}

	@PostConstruct
	public void loadCurrency() {
		List<Currency> list = repository.findAll();
		if (list != null && list.size() > 0) {
			currencyMap = new HashMap<String, String>();
			for (Currency currency : list) {
				currencyMap.put(currency.getCurrencyCode(), currency.getCurrencySymbol());
			}
			logger.debug("loadCurrency: " + currencyMap.size() + " rows");
		}
	}

	public String getCurrencySymbol(String currencyCode) {
		if (currencyMap == null || StringUtils.isBlank(currencyCode)) {
			return null;
		}
		return currencyMap.get(currencyCode);
	}
}