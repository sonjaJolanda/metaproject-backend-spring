package main.java.org.htwg.konstanz.metaproject.repositories;

import main.java.org.htwg.konstanz.metaproject.entities.PrioTeamProject;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.Team;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrioTeamProjectRepository extends JpaRepository<PrioTeamProject, Long> {

    List<PrioTeamProject> findByTeamId(Team teamId);

    List<PrioTeamProject> findByTeamIdAndProjectId(Team teamId, Project projectId);

    List<PrioTeamProject> findByProjectId(Project projectId);

    @Query("SELECT prio FROM PrioTeamProject prio WHERE prio.projectId.metaproject.metaProjectId = :metaId")
    List<PrioTeamProject> findByMeta(Long metaId);

    @Query("SELECT prio FROM PrioTeamProject prio " +
            "WHERE prio.projectId.metaproject.metaProjectId = :metaId " +
            "AND prio.teamId.teamId = :teamId ")
    List<PrioTeamProject> findByMetaAndTeam(Long metaId, Long teamId);

}
