package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.PrioTeamProject;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.Team;
import main.java.org.htwg.konstanz.metaproject.repositories.PrioTeamProjectRepository;
import main.java.org.htwg.konstanz.metaproject.repositories.ProjectRepository;
import main.java.org.htwg.konstanz.metaproject.repositories.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Implementation of data access object for prios.
 *
 * @author SiKelle
 */
@Service
public class PrioTeamProjectDAOImpl implements PrioTeamProjectDAO {

    private static final Logger log = LoggerFactory.getLogger(ProjectDAOImpl.class);

    private final PrioTeamProjectRepository prioTeamProjectRepo;

    private final TeamRepository teamRepo;

    private final ProjectRepository projectRepo;

    public PrioTeamProjectDAOImpl(PrioTeamProjectRepository prioTeamProjectRepo, TeamRepository teamRepo, ProjectRepository projectRepo) {
        this.prioTeamProjectRepo = prioTeamProjectRepo;
        this.teamRepo = teamRepo;
        this.projectRepo = projectRepo;
    }

    @Override
    public Collection<PrioTeamProject> findByProject(Long projectId) {
        Project project = projectRepo.findById(projectId).orElse(null);
        return prioTeamProjectRepo.findByProjectId(project);
    }

    @Override
    public Collection<PrioTeamProject> deleteByProject(Long projectId) {
        Collection<PrioTeamProject> prios = findByProject(projectId);
        prioTeamProjectRepo.deleteAll(prios);
        return prios;
    }

    @Override
    public Collection<PrioTeamProject> deleteByTeam(Long teamId) {
        Collection<PrioTeamProject> prios = findByTeam(teamId);
        prioTeamProjectRepo.deleteAll(prios);
        return prios;
    }

    @Override
    public PrioTeamProject delete(Long prioId){
        PrioTeamProject prio = findById(prioId);
        prioTeamProjectRepo.delete(prio);
        return prio;
    }

    @Override
    public Collection<PrioTeamProject> findByTeam(Long teamId) {
        Team team = teamRepo.findById(teamId).orElse(null);
        return prioTeamProjectRepo.findByTeamId(team);
    }

    @Override
    public Collection<PrioTeamProject> findByTeamAndProject(Long teamId, Long projectId) {
        Team team = teamRepo.findById(teamId).orElse(null);
        Project project = projectRepo.findById(projectId).orElse(null);
        return prioTeamProjectRepo.findByTeamIdAndProjectId(team, project);
    }

    @Override
    public Collection<PrioTeamProject> findByMetaAndTeam(Long metaId, Long teamId) {
        return prioTeamProjectRepo.findByMetaAndTeam(metaId, teamId);
    }

    @Override
    public Collection<PrioTeamProject> findByMeta(Long metaId) {
        return prioTeamProjectRepo.findByMeta(metaId);
    }

    @Override
    public PrioTeamProject save(PrioTeamProject prio) {
        return prioTeamProjectRepo.save(prio);
    }

    @Override
    public PrioTeamProject saveWithFlush(PrioTeamProject prio) {
        prio = prioTeamProjectRepo.save(prio);
        prioTeamProjectRepo.flush();
        return prio;
    }

    @Override
    public PrioTeamProject findById(Long id) {
        return prioTeamProjectRepo.findById(id).orElse(null);
    }

    @Override
    public PrioTeamProject update(Long id, PrioTeamProject prio) {
        PrioTeamProject priorisation = findById(id);
        if (priorisation == null)
            return null;

        prio.setPrioTeamProject(priorisation.getPrioTeamProject());
        prioTeamProjectRepo.save(prio);
        return prio;
    }

}
