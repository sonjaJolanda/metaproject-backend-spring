package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.ProjectStatusChange;
import main.java.org.htwg.konstanz.metaproject.repositories.ProjectRepository;
import main.java.org.htwg.konstanz.metaproject.repositories.ProjectStatusChangeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Implementation for projectstatuschange data access object.
 *
 * @author Sennur Kaya, Elisa-Lauren Bohnet
 */
@Service
public class ProjectStatusChangeDAOImpl implements ProjectStatusChangeDAO {

    private static final Logger log = LoggerFactory.getLogger(ProjectStatusChangeDAOImpl.class);

    private final ProjectStatusChangeRepository projectStatusChangeRepo;

    private final ProjectRepository projectRepo;

    public ProjectStatusChangeDAOImpl(ProjectStatusChangeRepository projectStatusChangeRepo, ProjectRepository projectRepo) {
        this.projectStatusChangeRepo = projectStatusChangeRepo;
        this.projectRepo = projectRepo;
    }

    @Override
    public ProjectStatusChange findById(Long id) {
        return projectStatusChangeRepo.findById(id).orElse(null);
    }


    @Override
    public ProjectStatusChange save(ProjectStatusChange statusChange) {
        return projectStatusChangeRepo.save(statusChange);
    }

    @Override
    public ProjectStatusChange update(Long id, ProjectStatusChange statusChange) {
        ProjectStatusChange status = findById(id);
        if (status == null)
            return null;

        statusChange.setStatusChangeId(status.getStatusChangeId());
        projectStatusChangeRepo.save(statusChange);
        return statusChange;
    }

    @Override
    public ProjectStatusChange delete(Long id) {
        ProjectStatusChange projectStatusChange = findById(id);
        if (projectStatusChange != null)
            projectStatusChangeRepo.delete(projectStatusChange);
        return projectStatusChange;
    }

    @Override
    public Collection<ProjectStatusChange> findByProject(Long projectId) {
        Project project = projectRepo.findById(projectId).orElse(null);
        return projectStatusChangeRepo.findByProject(project);
    }

}
