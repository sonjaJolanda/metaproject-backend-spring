package main.java.org.htwg.konstanz.metaproject.dtos;

/**
 * A team data transfer object to send only a short version of this team to
 * client and avoid to much traffic. To transform a team into this dto use a
 * xmlAdapter class.
 * 
 * @author SiKelle
 *
 */
public class TeamDTO {

	private Long teamId;

	private String teamName;

	private Long metaprojectId;

	private String metaprojectTitle;

	public Long getTeamId() {
		return teamId;
	}

	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

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
