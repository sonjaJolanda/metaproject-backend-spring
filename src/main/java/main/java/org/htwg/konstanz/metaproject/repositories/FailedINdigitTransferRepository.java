package main.java.org.htwg.konstanz.metaproject.repositories;

import main.java.org.htwg.konstanz.metaproject.entities.FailedINdigitTransfer;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FailedINdigitTransferRepository extends JpaRepository<FailedINdigitTransfer, Long> {

    Optional<FailedINdigitTransfer> findByProject(Project project);

}
