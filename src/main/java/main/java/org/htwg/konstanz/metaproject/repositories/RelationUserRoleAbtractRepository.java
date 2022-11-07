package main.java.org.htwg.konstanz.metaproject.repositories;

import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.rights.RelationUserRoleAbstract;
import main.java.org.htwg.konstanz.metaproject.rights.RoleAbstract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.management.relation.Role;
import java.util.List;

@Repository
public interface RelationUserRoleAbtractRepository extends JpaRepository<RelationUserRoleAbstract, Long> {

    List<RelationUserRoleAbstract> findRelationUserRoleAbstractByUser(User user);

}
