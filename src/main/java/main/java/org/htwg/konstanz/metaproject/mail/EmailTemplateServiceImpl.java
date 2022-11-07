package main.java.org.htwg.konstanz.metaproject.mail;

import main.java.org.htwg.konstanz.metaproject.communication.CommAbstract;
import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import org.apache.xmlbeans.impl.piccolo.io.IllegalCharException;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This service has methods to fill/create templates.
 * 
 * @author SiKelle
 *
 */
@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

	private static final String PARAM_SYSTEM_BASE_URL = "systemBaseUrl";

	private static final Logger log = LoggerFactory.getLogger(EmailCreatorServiceImpl.class);

	@Override
	public String fillPlaceholder(String template, Map<String, String> values) throws IllegalCharException {
		String placeholder = "";
		String text = "";
		int state = 0;
		for (int i = 0; i < template.length(); i++) {
			char c = template.charAt(i);
			switch (state) {
			// normal mode
			case 0:
				if (c == '{') {
					// maybe placeholder found
					state = 1;
				} else {
					// else add char
					text += c;
				}
				break;
			// maybe placeholder found mode
			case 1:
				if (c == '{') {
					state = 2;
				} else {
					state = 0;
				}
				break;
			// placeholder found mode
			case 2:
				if (c == '}') {
					// placeholder end
					state = 3;
				} else {
					// collect placeholder chars
					placeholder += c;
				}
				break;
			// placeholder ends
			case 3:
				if (c != '}') {
					throw new IllegalCharException("This is not a valid placeholder! Should end with two }}.");
				}
				//log.info("Replace \"{}\" with \"{}\"", placeholder, values.get(placeholder));
				// add placeholder value and reset all
				text += values.get(placeholder);
				placeholder = "";
				state = 0;
				break;
			}
		}
		return text;
	}

	/* (non-Javadoc)
	 * @see org.htwg.konstanz.metaproject.mail.EmailTemplateService#getParamMapOfCommunication(T)
	 */
	@Override
	public <T extends CommAbstract> Map<String, String> getParamMapOfCommunication(T object) {
		// find all methods that are named with a prefix "param"
		log.info("getParamMapOfCommunication: {}", object.getClass().getName());
		Set<Method> methods = ReflectionUtils.getAllMethods(object.getClass(),
				ReflectionUtils.withPrefix("param"), ReflectionUtils.withParametersCount(0),
				ReflectionUtils.withReturnType(String.class), ReflectionUtils.withModifier(Modifier.PUBLIC));
		Map<String, String> result = new HashMap<>();
		// find all param names and their value
		for (Method method : methods) {
			try {
				String name = method.getName();
				String field = name.substring(5, 6).toLowerCase() + name.substring(6);
				String value = (String) method.invoke(object);
				log.info("{}: {}", field, value);
				result.put(field, value);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.error(e.getMessage(), e);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		// add some custom global variables
		result.put(PARAM_SYSTEM_BASE_URL, Constants.METAPROJECT_ROOT_URL);
		return result;
	}

}
