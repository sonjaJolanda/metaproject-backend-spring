package main.java.org.htwg.konstanz.metaproject.repositories;

import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByMetaproject(Metaproject metaproject);

    List<Project> findByMetaprojectAndProjectTitle(Metaproject metaproject, String projectTitle);

}
