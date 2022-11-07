package main.java.org.htwg.konstanz.metaproject.rights;

import main.java.org.htwg.konstanz.metaproject.entities.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.annotation.ElementType;

/**
 * Abstract object for role, right and element mapping. Every
 * {@link ElementType} has its own {@link RoleAbstract} user element mapping in
 * combination with {@link ElementType} and id. Every mapping have to extend
 * this class.
 * 
 * @author SiKelle
 *
 */
@Entity
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "RelationUserRoleAbstract")
public abstract class RelationUserRoleAbstract implements Serializable {
	private static final long serialVersionUID = 5558898386086513824L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	protected Long id;

	@NotNull(message = "user is null")
	@ManyToOne
	private User user;

	/**
	 * Get role which is connected to this relation.
	 * 
	 * @return
	 */
	public abstract RoleAbstract getRole();

	/**
	 * Get id of connected element. If no element is connected, this id could be
	 * null.
	 * 
	 * @return
	 */
	public abstract Long getConnectedId();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
