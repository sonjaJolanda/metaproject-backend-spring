package main.java.org.htwg.konstanz.metaproject.repositories;

import main.java.org.htwg.konstanz.metaproject.rights.DefaultRoles;
import main.java.org.htwg.konstanz.metaproject.rights.RoleAbstract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleAbstractRepository extends JpaRepository<RoleAbstract, Long> {

    Optional<RoleAbstract> findRoleAbstractByDefaultRoleKey(DefaultRoles defaultRole);

}
