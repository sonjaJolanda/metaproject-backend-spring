package main.java.org.htwg.konstanz.metaproject.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author kagamito
 */

@Entity
@Table(name = "RelationMetaprojectUser", uniqueConstraints = {@UniqueConstraint(columnNames = { "metaProjectId", "userId" })})
public class RelationMetaprojectUser implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "relationMetaprojectUserId")
	@GeneratedValue
	private Long relationMetaprojectUserId;

	@NotNull
	@OneToOne
	@JoinColumn(name="metaProjectId", insertable = true, updatable = true)
	private Metaproject metaProjectId;

	@NotNull
	@OneToOne
	@JoinColumn(name="userId", insertable = true, updatable = true)
	private User userId;

	public Long getRelationMetaprojectUserId() {
		return relationMetaprojectUserId;
	}

	public void setRelationMetaprojectUserId(Long relationMetaprojectUserId) {
		this.relationMetaprojectUserId = relationMetaprojectUserId;
	}

	public Metaproject getMetaprojectId() {
		return metaProjectId;
	}

	public void setMetaprojectId(Metaproject metaProjectId) {
		this.metaProjectId = metaProjectId;
	}

	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}

}
