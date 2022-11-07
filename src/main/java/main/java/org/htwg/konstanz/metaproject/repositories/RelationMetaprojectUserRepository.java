package main.java.org.htwg.konstanz.metaproject.repositories;

import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.RelationMetaprojectUser;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationMetaprojectUserRepository extends JpaRepository<RelationMetaprojectUser, Long> {

    List<RelationMetaprojectUser> findByMetaProjectIdAndUserId(Metaproject metaProjectId, User userId);

    List<RelationMetaprojectUser> findByMetaProjectId(Metaproject metaProjectId);

    List<RelationMetaprojectUser> findByUserId(User userId);

}
