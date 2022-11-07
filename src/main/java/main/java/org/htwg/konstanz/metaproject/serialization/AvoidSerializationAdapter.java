package main.java.org.htwg.konstanz.metaproject.serialization;

import org.springframework.stereotype.Service;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This adapter makes a custom serialization and deserialization. It can be used
 * for a string attribute in a class by annotation.
 * 
 * Example:
 * 
 * <pre>
 * &#64;XmlJavaTypeAdapter(AvoidSerializationAdapter.class)
 * private String userPassword;
 * </pre>
 * 
 * This adapter avoid the serialization of this attribute but enables the
 * deserialization of this attribute. It's useful for password fields or other
 * secret keys, which should never appear in frontend.
 * 
 * Be careful, any changes at this class could cause a possible security issue!
 * 
 * @author SiKelle
 *
 */
@Service
public class AvoidSerializationAdapter extends XmlAdapter<String, String> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 * 
	 * This method enables deserialization with all information.
	 */
	@Override
	public String unmarshal(String v) throws Exception {
		return v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 * 
	 * This method disables serialization and replaces the given attribute with
	 * null.
	 */
	@Override
	public String marshal(String v) throws Exception {
		return null;
	}

}
