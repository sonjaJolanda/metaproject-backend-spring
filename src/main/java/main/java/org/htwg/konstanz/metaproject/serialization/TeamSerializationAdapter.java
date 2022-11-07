package main.java.org.htwg.konstanz.metaproject.serialization;

import main.java.org.htwg.konstanz.metaproject.dtos.TeamDTO;
import main.java.org.htwg.konstanz.metaproject.entities.Team;
import org.springframework.stereotype.Service;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This adapter makes a custom serialization and deserialization. It can be used
 * for a team attribute in a class by annotation.
 * 
 * Example:
 * 
 * <pre>
 * &#64;XmlJavaTypeAdapter(TeamSerializationAdapter.class)
 * private Team teamId;
 * </pre>
 * 
 * This adapter only serializes a team in a teamDto and deserializes a teamDto
 * in a new <b>empty (!)</b> team with id.
 * 
 * @author SiKelle
 *
 */
@Service
public class TeamSerializationAdapter extends XmlAdapter<TeamDTO, Team> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 * 
	 * This method enables deserialization to a team object with only an id.
	 */
	@Override
	public Team unmarshal(TeamDTO teamDto) throws Exception {
		Team t = new Team();
		t.setTeamId(teamDto.getTeamId());
		return t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 * 
	 * This method serializes a team object to only its id.
	 */
	@Override
	public TeamDTO marshal(Team team) throws Exception {
		TeamDTO teamDto = new TeamDTO();
		teamDto.setTeamId(team.getTeamId());
		teamDto.setTeamName(team.getTeamName());
		if (team.getMetaProjectId() != null) {
			teamDto.setMetaprojectId(team.getMetaProjectId().getMetaprojectId());
			teamDto.setMetaprojectTitle(team.getMetaProjectId().getMetaprojectTitle());
		}
		return teamDto;
	}

}
