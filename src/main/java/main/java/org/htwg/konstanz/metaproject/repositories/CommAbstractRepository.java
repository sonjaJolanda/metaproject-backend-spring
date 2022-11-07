package main.java.org.htwg.konstanz.metaproject.repositories;

import main.java.org.htwg.konstanz.metaproject.communication.CommAbstract;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommAbstractRepository extends JpaRepository<CommAbstract, Long> {

}
