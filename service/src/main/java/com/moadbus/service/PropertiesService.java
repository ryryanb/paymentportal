package service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import entity.Properties;
import repository.PropertiesRepository;

@Service
public class PropertiesService {
	@Autowired
	private PropertiesRepository repository;

	public List<Properties> list(List<String> names) {
		return repository.findByPropertyNameIn(names);
	}

	public Map<String, String> getEmailConfig() {
		String[] propertiesNames = { "email.mail.smtp.auth", "email.mail.smtp.starttls.enable", "email.mail.smtp.host",
				"email.mail.smtp.port", "email.username", "email.password"};
		List<Properties> properties = list(Arrays.asList(propertiesNames));
		if (properties == null || properties.isEmpty()) {
			return null;
		}
		Map<String, String> mailConfigs = new HashMap<String, String>();
		for (Properties property : properties) {
			mailConfigs.put(property.getPropertyName(), property.getPropertyValue());
		}
		return mailConfigs;
	}
}