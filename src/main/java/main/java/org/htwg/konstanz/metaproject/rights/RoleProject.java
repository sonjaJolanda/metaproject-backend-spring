package main.java.org.htwg.konstanz.metaproject.rights;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * A role element model, used to persist a role in database.
 * 
 * @author SiKelle
 * @version 2.0
 */
@Entity
@DiscriminatorValue("PROJECT")
public class RoleProject extends RoleAbstract {
	private static final long serialVersionUID = 1L;

	@Override
	public RoleTypes getRoleType() {
		return RoleTypes.PROJECT;
	}

	@Override
	public Boolean validateRights() {
		for (Rights right : this.getRoleRights()) {
			if (!right.getLinked().contains(this.getRoleType())) {
				return false;
			}
		}
		return true;
	}

}
