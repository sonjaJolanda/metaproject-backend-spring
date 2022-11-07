package main.java.org.htwg.konstanz.metaproject.services;

import main.java.org.htwg.konstanz.metaproject.dtos.ProjectMembersINdigitDTO;

import java.io.IOException;
import java.util.List;

public interface INdigitApiMetaproject {

    /**
     * Search for a user by username.
     *
     * @param username the username to search for
     * @return list of search results
     * @throws IOException                              is thrown if http request failed
     * @throws INdigitApiService.HttpStatusCodeException is thrown if a bad status code was returned
     */
    List<User> searchUser(String username) throws IOException, INdigitApiService.HttpStatusCodeException;

    class User {
        private final int userid;
        private final String username;

        public int getUserid() {
            return userid;
        }

        public String getUsername() {
            return username;
        }

        public User(int userid, String username) {
            this.userid = userid;
            this.username = username;
        }
    }

    /**
     * List all project status types to get their unique identifier to use in other requests.
     *
     * @return list of all available project status
     * @throws IOException                              is thrown if http request failed
     * @throws INdigitApiService.HttpStatusCodeException is thrown if a bad status code was returned
     */
    List<Status> listStatus() throws IOException, INdigitApiService.HttpStatusCodeException;

    class Status {
        private final int statusid;
        private final String name;
        private final String color;

        public Status(int statusid, String name, String color) {
            this.statusid = statusid;
            this.name = name;
            this.color = color;
        }

        public String getColor() {
            return color;
        }

        public String getName() {
            return name;
        }

        public int getStatusid() {
            return statusid;
        }
    }

    /**
     * List all study programs to get their unique identifier to use in other requests.
     *
     * @return list of all available study programs
     * @throws IOException                              is thrown if http request failed
     * @throws INdigitApiService.HttpStatusCodeException is thrown if a bad status code was returned
     */
    List<StudyProgram> listStudyPrograms() throws IOException, INdigitApiService.HttpStatusCodeException;

    class StudyProgram {
        private final int spid;
        private final String abbreviation;
        private final String name;

        public StudyProgram(int spid, String abbreviation, String name) {
            this.spid = spid;
            this.abbreviation = abbreviation;
            this.name = name;
        }

        public int getSpid() {
            return spid;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Transfer a status change for a project to INdigit.
     *
     * @param project project model to be transferred
     * @throws IOException                              is thrown if http request failed
     * @throws INdigitApiService.HttpStatusCodeException is thrown if a bad status code was returned
     */
    int transferProjectStatusChange(IndigitProject project) throws IOException, INdigitApiService.HttpStatusCodeException;

    /**
     * Transfer a change of members of a project to INdigit.
     *
     * @param members members model to be transferred
     * @throws IOException                              is thrown if http request failed
     * @throws INdigitApiService.HttpStatusCodeException is thrown if a bad status code was returned
     */
    int transferProjectMembers(List<ProjectMembersINdigitDTO> members) throws IOException, INdigitApiService.HttpStatusCodeException;

    class IndigitProject {
        private final String transferuser;
        private final long projectid;
        private final String projecttitle;
        private final String projectleader;
        private final int projectmembers;
        private final int projectstatus;
        private final String updatetimestamp;
        private final int projectstudyprogram;

        public IndigitProject(String transferuser, long projectid, String projecttitle, String projectleader, int projectmembers, int projectstatus, String updatetimestamp, int projectstudyprogram) {
            this.transferuser = transferuser;
            this.projectid = projectid;
            this.projecttitle = projecttitle;
            this.projectleader = projectleader;
            this.projectmembers = projectmembers;
            this.projectstatus = projectstatus;
            this.updatetimestamp = updatetimestamp;
            this.projectstudyprogram = projectstudyprogram;
        }

        public String getTransferuser() {
            return transferuser;
        }

        public long getProjectid() {
            return projectid;
        }

        public String getProjecttitle() {
            return projecttitle;
        }

        public String getProjectleader() {
            return projectleader;
        }

        public int getProjectmembers() {
            return projectmembers;
        }

        public int getProjectstatus() {
            return projectstatus;
        }

        public String getUpdatetimestamp() {
            return updatetimestamp;
        }

        public int getProjectstudyprogram() {
            return projectstudyprogram;
        }
    }
}
