package main.java.org.htwg.konstanz.metaproject.services;

import main.java.org.htwg.konstanz.metaproject.dtos.CopyMetaprojectDTO;
import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.User;

public interface CopyService {

    Long copyMetaproject(Metaproject oldMp, CopyMetaprojectDTO copyMp, User leader, User creator, boolean isTeamReg);

    Long copyProject(Project oldP, Metaproject mp, User projectLead, User currentUser, String newProjectName);

    Long moveProject(Project oldP, Metaproject oldMP, Metaproject newMP, User currentUser);
}
