package main.java.org.htwg.konstanz.metaproject.rights;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * This class is used to persist right descriptions in database.
 * 
 * @author SiKelle
 * @version 1.0
 */
@Entity
@Table(name = "rightDetails")
public class RightDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "rightId")
	@NotNull(message = "right is null")
	@Enumerated(EnumType.STRING)
	private Rights right;

	@Column(name = "description")
	@NotNull(message = "description is null")
	private String description;

	public Rights getRight() {
		return right;
	}

	public void setRight(Rights right) {
		this.right = right;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


}