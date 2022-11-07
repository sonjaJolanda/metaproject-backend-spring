package main.java.org.htwg.konstanz.metaproject.entities;

import main.java.org.htwg.konstanz.metaproject.enums.TeamMemberStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Stefano, FaAmbros
 */

@Entity
@Table(name = "RelationTeamUser", uniqueConstraints = @UniqueConstraint(columnNames = {"teamId", "userId"}))
public class RelationTeamUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "relationTeamUserId")
    @GeneratedValue
    private Long relationTeamUserId;

    @OneToOne
    @NotNull
    @JoinColumn(name = "teamId", insertable = true, updatable = true)
    private Team teamId;

    @OneToOne
    @JoinColumn(name = "userId", insertable = true, updatable = true)
    private User userId;

    @NotNull(message = "Status is null")
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "teamMemberStatus", nullable = false)
    private TeamMemberStatus teamMemberStatus;

    @Column(name = "inviteDate", nullable = true)
    private String inviteDate;

    public void setInviteDate(String date) {
        this.inviteDate = date;
    }

    public String getInviteDate() {
        return inviteDate;
    }

    public TeamMemberStatus getTeamMemberStatus() {
        return teamMemberStatus;
    }

    public void setTeamMemberStatus(TeamMemberStatus teamMemberStatus) {
        this.teamMemberStatus = teamMemberStatus;
    }

    public Long getRelationTeamUserId() {
        return relationTeamUserId;
    }

    public void setRelationTeamUserId(Long relationTeamUserId) {
        this.relationTeamUserId = relationTeamUserId;
    }

    public Team getTeamId() {
        return teamId;
    }

    public void setTeamId(Team teamId) {
        this.teamId = teamId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

}
