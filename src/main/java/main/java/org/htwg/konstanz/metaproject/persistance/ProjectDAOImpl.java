package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.repositories.MetaprojectRepository;
import main.java.org.htwg.konstanz.metaproject.repositories.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Implementation for project data access object.
 *
 * @author SiKelle
 */
@Service
public class ProjectDAOImpl implements ProjectDAO {

    private static final Logger log = LoggerFactory.getLogger(ProjectDAOImpl.class);

    private final ProjectRepository projectRepo;

    private final MetaprojectRepository metaprojectRepo;

    public ProjectDAOImpl(ProjectRepository projectRepo, MetaprojectRepository metaprojectRepo) {
        this.projectRepo = projectRepo;
        this.metaprojectRepo = metaprojectRepo;
    }

    @Override
    public Project findById(Long id) {
        return projectRepo.findById(id).orElse(null);
    }

    @Override
    public Project save(Project project) {
        project.setProjectTitle(trim(project.getProjectTitle()));
        return projectRepo.save(project);
    }

    @Override
    public Project update(Long id, Project transientProject) {
        Project project = findById(id);
        if (project == null)
            return null;

        transientProject.setProjectId(project.getProjectId());
        save(transientProject);
        return transientProject;
    }

    @Override
    public Project delete(Long id) {
        Project project = findById(id);
        if (project != null)
            projectRepo.delete(project);
        return project;
    }

    @Override
    public Collection<Project> findByMetaproject(Long metaprojectId) {
        Metaproject metaproject = metaprojectRepo.findById(metaprojectId).orElse(null);
        return projectRepo.findByMetaproject(metaproject);
    }

    @Override
    public Project findByMetaprojectAndTitle(Long metaprojectId, String title) {
        Metaproject metaproject = metaprojectRepo.findById(metaprojectId).orElse(null);
        List<Project> result = projectRepo.findByMetaprojectAndProjectTitle(metaproject, title);
        if (result.isEmpty())
            return null;
        else
            return result.get(0);
    }

    @Override
    public List<Project> findAll() {
        return projectRepo.findAll();
    }

}
