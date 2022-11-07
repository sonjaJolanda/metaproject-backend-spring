package main.java.org.htwg.konstanz.metaproject.repositories;

import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.ProjectStatusChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectStatusChangeRepository extends JpaRepository<ProjectStatusChange, Long> {

    List<ProjectStatusChange> findByProject(Project project);

}
