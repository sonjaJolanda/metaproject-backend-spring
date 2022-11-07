package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.ProjectFieldAccess;
import main.java.org.htwg.konstanz.metaproject.repositories.ProjectFieldAccessRepository;
import main.java.org.htwg.konstanz.metaproject.repositories.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class ProjectFieldAccessDAOImpl implements ProjectFieldAccessDAO {

    private static final Logger log = LoggerFactory.getLogger(ProjectFieldAccessDAOImpl.class);

    private final ProjectFieldAccessRepository projectFieldAccessRepo;

    private final ProjectRepository projectRepo;

    public ProjectFieldAccessDAOImpl(ProjectFieldAccessRepository projectFieldAccessRepo, ProjectRepository projectRepo) {
        this.projectFieldAccessRepo = projectFieldAccessRepo;
        this.projectRepo = projectRepo;
    }

    @Override
    public Collection<ProjectFieldAccess> findByProjectId(long projectId) {
        Project project = projectRepo.findById(projectId).orElse(null);
        return projectFieldAccessRepo.findByProjectId(project);
    }

    @Override
    public ProjectFieldAccess save(ProjectFieldAccess project) {
        return projectFieldAccessRepo.save(project);
    }

    @Override
    public Collection<ProjectFieldAccess> update(ProjectFieldAccess transientPfa) {
        ProjectFieldAccess pfa = this.findByIdAndField(transientPfa.getProjectId(), transientPfa.getField());
        save(pfa);
        return this.findAll();
    }

    @Override
    public Collection<ProjectFieldAccess> delete(ProjectFieldAccess projectFieldAccess) {
        ProjectFieldAccess pfa = this.findByIdAndField(projectFieldAccess.getProjectId(), projectFieldAccess.getField());
        if (pfa != null)
            projectFieldAccessRepo.delete(pfa);
        return this.findAll();
    }

    @Override
    public Collection<ProjectFieldAccess> findAll() {
        return projectFieldAccessRepo.findAll();
    }

    @Override
    public ProjectFieldAccess findByIdAndField(long projectId, String field) {
        Project project = projectRepo.findById(projectId).orElse(null);
        List<ProjectFieldAccess> result = projectFieldAccessRepo.findByProjectIdAndField(project, field);
        if (result.isEmpty())
            return null;
        else
            return result.get(0);
    }
}
