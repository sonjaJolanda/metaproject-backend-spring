package main.java.org.htwg.konstanz.metaproject.repositories;

import main.java.org.htwg.konstanz.metaproject.entities.RelationTeamUser;
import main.java.org.htwg.konstanz.metaproject.entities.Team;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.enums.TeamMemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationTeamUserRepository extends JpaRepository<RelationTeamUser, Long> {

    List<RelationTeamUser> findByTeamIdAndUserId(Team team, User user);

    List<RelationTeamUser> findByTeamIdAndTeamMemberStatus(Team team, TeamMemberStatus teamMemberStatus);

    List<RelationTeamUser> findByTeamId(Team teamId);

    List<RelationTeamUser> findByUserId(User userId);

}
