package main.java.org.htwg.konstanz.metaproject.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Fahocur, AlVeliu, JoFesenm
 */

@Entity
@Table(name = "Specialisation")
public class Specialisation implements Cloneable {
	
	@Id
	@GeneratedValue
	@Column(name = "specialisationId")
	private Long specialisationId;
	
	@NotNull(message= "specialisationName is null")
	@Size(min = 1, max = 25, message = "1-25 letters and spaces")
	@Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
	@Column(name = "specialisationName")
	private String specialisationName;

	@Override
	protected Object clone() {
		Specialisation clone = new Specialisation();
		clone.setSpecialisationName(specialisationName);
		return clone;
	}

	public Long getSpecialisationId() {
		return specialisationId;
	}

	public void setSpecialisationId(Long specialisationId) {
		this.specialisationId = specialisationId;
	}

	public String getSpecialisationName() {
		return specialisationName;
	}

	public void setSpecialisationName(String text) {
		this.specialisationName = text;
	}

}
