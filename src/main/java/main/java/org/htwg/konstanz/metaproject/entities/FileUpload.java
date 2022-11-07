package main.java.org.htwg.konstanz.metaproject.entities;

import main.java.org.htwg.konstanz.metaproject.enums.FileUploadType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author FaHocur, AlVeliu, JoFesenm, SiKelle, PaDrautz
 */

@Entity
@Table(name = "FileUpload")
public class FileUpload {

    @Id
    @GeneratedValue
    @Column(name = "uploadId")
    private long uploadId;

    @NotNull(message = "projectId is null")
    @Column(name = "projectId")
    private long projectId;

    @NotNull(message = "fileName is null")
    @Column(name = "fileName")
    private String fileName;

    @NotNull(message = "filePath is null")
    @Column(name = "filePath")
    @JsonIgnore
    private String filePath;

    @NotNull(message = "upload type is null")
    @Column(name = "type")
    @Enumerated(EnumType.ORDINAL)
    private FileUploadType type;

    public long getUploadId() {
        return uploadId;
    }

    public void setUploadId(long uploadId) {
        this.uploadId = uploadId;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public FileUploadType getType() {
        return type;
    }

    public void setType(FileUploadType type) {
        this.type = type;
    }

}
