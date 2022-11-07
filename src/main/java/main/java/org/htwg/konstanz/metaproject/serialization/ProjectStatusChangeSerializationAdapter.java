package main.java.org.htwg.konstanz.metaproject.serialization;

import main.java.org.htwg.konstanz.metaproject.dtos.ProjectStatusChangeDTO;
import main.java.org.htwg.konstanz.metaproject.entities.ProjectStatusChange;
import org.springframework.stereotype.Service;

import javax.xml.bind.annotation.adapters.XmlAdapter;

@Service
public class ProjectStatusChangeSerializationAdapter extends XmlAdapter<ProjectStatusChangeDTO, ProjectStatusChange> {

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     *
     * This method enables deserialization to a project object with only an id.
     */

    @Override
    public ProjectStatusChange unmarshal(ProjectStatusChangeDTO projectStatusChangeDTO) throws Exception {
        ProjectStatusChange t = new ProjectStatusChange();
        t.setStatusChangeId(projectStatusChangeDTO.getStatusChangeId());
        return t;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     *
     * This method serializes a project object to only its id.
     */


    @Override
    public ProjectStatusChangeDTO marshal(ProjectStatusChange statusChange) throws Exception {
        ProjectStatusChangeDTO projectStatusChangeDto = new ProjectStatusChangeDTO();
        projectStatusChangeDto.setStatusChangeId(projectStatusChangeDto.getStatusChangeId());
        if (projectStatusChangeDto.getProjectId() != null) {
            projectStatusChangeDto.setProjectId(projectStatusChangeDto.getProjectId());
        }
        return projectStatusChangeDto;
    }
}
