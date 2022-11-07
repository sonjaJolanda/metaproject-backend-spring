package main.java.org.htwg.konstanz.metaproject.repositories;

import main.java.org.htwg.konstanz.metaproject.entities.FileUpload;
import main.java.org.htwg.konstanz.metaproject.enums.FileUploadType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {

    @Query("SELECT fu FROM FileUpload fu WHERE fu.projectId = :projectId and fu.type = :type")
    Collection<FileUpload> findAllByProject(@Param("projectId") Long projectId, FileUploadType type);

}
