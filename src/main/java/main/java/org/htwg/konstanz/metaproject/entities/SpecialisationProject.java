package main.java.org.htwg.konstanz.metaproject.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * This is a proportion for a metaproject specialisation.
 *
 * @author Fahocur, AlVeliu, JoFesenm, SiKelle
 */

@Entity
@Table(name = "SpecialisationProject")
public class SpecialisationProject implements Cloneable{

	@Id
	@GeneratedValue
	@Column(name = "specialisationId")
	@JsonIgnore
	private Long specialisationId;
	
	@NotNull(message= "specialisationName is null")
	@ManyToOne
	private Specialisation specialisation;
	
	@Column(name = "specialisationProportion")
	// @JsonIgnore -> Has been removed to fix the create method for projects
	private int specialisationProportion;

	@Override
	protected Object clone()  {
		SpecialisationProject clone = new SpecialisationProject();
		clone.setSpecialisation((Specialisation) specialisation.clone());
		clone.setSpecialisationProportion(specialisationProportion);
		return clone;
	}

	public Long getSpecialisationId() {
		return specialisationId;
	}

	public void setSpecialisationId(Long specialisationId) {
		this.specialisationId = specialisationId;
	}

	public Specialisation getSpecialisation() {
		return specialisation;
	}

	public void setSpecialisation(Specialisation specialisation) {
		this.specialisation = specialisation;
	}

	public int getSpecialisationProportion() {
		return specialisationProportion;
	}

	public void setSpecialisationProportion(int specialisationProportion) {
		this.specialisationProportion = specialisationProportion;
	}
	
}
