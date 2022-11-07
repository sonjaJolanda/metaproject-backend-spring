package main.java.org.htwg.konstanz.metaproject.serialization;

import main.java.org.htwg.konstanz.metaproject.dtos.MetaprojectDTO;
import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import org.springframework.stereotype.Service;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This adapter makes a custom serialization and deserialization. It can be used
 * for a metaproject attribute in a class by annotation.
 * 
 * Example:
 * 
 * <pre>
 * &#64;XmlJavaTypeAdapter(MetaprojectSerializationAdapter.class)
 * private Metaproject metaprojectId;
 * </pre>
 * 
 * This adapter only serializes a metaproject in a metaprojectDto and
 * deserializes a metaprojectDto in a new <b>empty (!)</b> metaproject with id.
 * 
 * @author SiKelle
 *
 */
@Service
public class MetaprojectSerializationAdapter extends XmlAdapter<MetaprojectDTO, Metaproject> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 * 
	 * This method enables deserialization to a metaproject object with only an
	 * id.
	 */
	@Override
	public Metaproject unmarshal(MetaprojectDTO metaprojectDto) throws Exception {
		Metaproject m = new Metaproject();
		m.setMetaprojectId(metaprojectDto.getMetaprojectId());
		return m;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 * 
	 * This method serializes a metaproject object to only its id.
	 */
	@Override
	public MetaprojectDTO marshal(Metaproject metaproject) throws Exception {
		MetaprojectDTO metaprojectDto = new MetaprojectDTO();
		metaprojectDto.setMetaprojectId(metaproject.getMetaprojectId());
		metaprojectDto.setMetaprojectTitle(metaproject.getMetaprojectTitle());
		return metaprojectDto;
	}

}
