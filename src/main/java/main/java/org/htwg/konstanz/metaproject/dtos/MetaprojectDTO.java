package main.java.org.htwg.konstanz.metaproject.dtos;

/**
 * A metaproject data transfer object to send only a short version of this
 * metaproject to client and avoid to much traffic. To transform a team into
 * this dto use a xmlAdapter class.
 * 
 * @author SiKelle
 *
 */
public class MetaprojectDTO {

	private Long metaprojectId;

	private String metaprojectTitle;

	public Long getMetaprojectId() {
		return metaprojectId;
	}

	public void setMetaprojectId(Long metaprojectId) {
		this.metaprojectId = metaprojectId;
	}

	public String getMetaprojectTitle() {
		return metaprojectTitle;
	}

	public void setMetaprojectTitle(String metaprojectName) {
		this.metaprojectTitle = metaprojectName;
	}

}
