package main.java.org.htwg.konstanz.metaproject.repositories;

import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaprojectRepository extends JpaRepository<Metaproject, Long>, PagingAndSortingRepository<Metaproject, Long> {
    @NonNull
    Page<Metaproject> findAll(Pageable pageable);

    @Query("SELECT m FROM Metaproject m WHERE m.visible = true")
    Page<Metaproject> findAllVisible(Pageable pageable);
}
