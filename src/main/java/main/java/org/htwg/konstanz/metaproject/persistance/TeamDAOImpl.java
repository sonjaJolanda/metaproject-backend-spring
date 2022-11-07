package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.Team;
import main.java.org.htwg.konstanz.metaproject.repositories.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Implementation of Team data access object.
 *
 * @author SiKelle
 */
@Service
public class TeamDAOImpl implements TeamDAO {

    private static final Logger log = LoggerFactory.getLogger(TeamDAOImpl.class);

    private final TeamRepository teamRepo;

    private final MetaprojectDAO metaprojectDAO;

    private final ProjectDAO projectDAO;

    public TeamDAOImpl(TeamRepository teamRepository, MetaprojectDAO metaprojectDAO, ProjectDAO projectDAO) {
        this.teamRepo = teamRepository;
        this.metaprojectDAO = metaprojectDAO;
        this.projectDAO = projectDAO;
    }

    @Override
    public Team findById(Long id) {
        return teamRepo.findById(id).orElse(null);
    }

    @Override
    public Team update(Team transientTeam, long id) {
        Team team = findById(id);
        if (team == null)
            return null;

        transientTeam.setTeamId(id);
        teamRepo.save(transientTeam);
        return transientTeam;
    }

    @Override
    public Team save(Team team) {
        return teamRepo.save(team);
    }

    @Override
    public Collection<Team> findByMetaprojectId(Long metaprojectId) {
        Metaproject metaproject = metaprojectDAO.findById(metaprojectId);
        return teamRepo.findByMetaProjectId(metaproject);
    }

    @Override
    public Team findByProjectId(Long projectId) {
        Project project = projectDAO.findById(projectId);
        List<Team> result =  teamRepo.findByProjectId(project);
        if (result.isEmpty())
            return null;
        else
            return result.get(0);
    }

    @Override
    public Team delete(Long teamId) {
        Team team = findById(teamId);
        if (team != null)
            teamRepo.delete(team);
        return team;
    }

    @Override
    public Collection<Team> deleteByMetaproject(Long metaprojectId) {
        Collection<Team> teams = findByMetaprojectId(metaprojectId);
        teamRepo.deleteAll(teams);
        return teams;
    }

    @Override
    public long getNumberOfTeams() {
        return teamRepo.findAll().size();
    }

    @Override
    public List<Team> findAll(){
        return teamRepo.findAll();
    }

}
