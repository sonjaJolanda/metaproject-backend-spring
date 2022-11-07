package main.java.org.htwg.konstanz.metaproject.repositories;

import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.ProjectFieldAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectFieldAccessRepository extends JpaRepository<ProjectFieldAccess, Long> {

    List<ProjectFieldAccess> findByProjectId(Project projectId);

    List<ProjectFieldAccess> findByProjectIdAndField(Project projectId, String field);

}
