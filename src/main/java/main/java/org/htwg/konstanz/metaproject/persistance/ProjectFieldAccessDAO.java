package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.ProjectFieldAccess;

import java.util.Collection;

public interface ProjectFieldAccessDAO {

    Collection<ProjectFieldAccess> findByProjectId(long projectId);

    ProjectFieldAccess save(ProjectFieldAccess metaproject);

    Collection<ProjectFieldAccess> update(ProjectFieldAccess transientMetaproject);

    Collection<ProjectFieldAccess> delete(ProjectFieldAccess projectFieldAccess);

    Collection<ProjectFieldAccess> findAll();

    ProjectFieldAccess findByIdAndField(long projectId, String field);

}
