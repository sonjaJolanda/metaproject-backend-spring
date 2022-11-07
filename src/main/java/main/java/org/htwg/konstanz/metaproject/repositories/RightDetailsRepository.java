package main.java.org.htwg.konstanz.metaproject.repositories;

import main.java.org.htwg.konstanz.metaproject.rights.RightDetails;
import main.java.org.htwg.konstanz.metaproject.rights.Rights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RightDetailsRepository extends JpaRepository<RightDetails, Long> {

    Optional<RightDetails> findByRight(Rights right);

}
