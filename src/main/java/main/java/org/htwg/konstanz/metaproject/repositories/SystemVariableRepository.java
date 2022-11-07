package main.java.org.htwg.konstanz.metaproject.repositories;

import main.java.org.htwg.konstanz.metaproject.entities.SystemVariable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemVariableRepository extends JpaRepository<SystemVariable, Long> {

    Optional<SystemVariable> findByKey(String key);

}
