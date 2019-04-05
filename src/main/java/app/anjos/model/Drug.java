package app.anjos.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "drug")
@PrimaryKeyJoinColumn(name = "id")
public class Drug extends Product {

	private static final long serialVersionUID = -4799581132394919157L;

	private Character type; // R - Referência, G - Genérico, S - Similar

	private Boolean prescription;

	@Column(columnDefinition = "TEXT")
	private String indications;

	@Column(name = "how_works", columnDefinition = "TEXT")
	private String howWorks;

	private String bula;

	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(name = "drug_speciality", //
			joinColumns = { @JoinColumn(name = "drug_id") }, //
			inverseJoinColumns = { @JoinColumn(name = "speciality_id") })
	private List<Speciality> specialities;

	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(name = "drug_substance", //
			joinColumns = { @JoinColumn(name = "drug_id") }, //
			inverseJoinColumns = { @JoinColumn(name = "substance_id") })
	private List<Substance> substances;

	public Character getType() {
		return type;
	}

	public void setType(Character type) {
		this.type = type;
	}

	public Boolean getPrescription() {
		return prescription;
	}

	public void setPrescription(Boolean prescription) {
		this.prescription = prescription;
	}

	public String getIndications() {
		return indications;
	}

	public void setIndications(String indications) {
		this.indications = indications;
	}

	public String getHowWorks() {
		return howWorks;
	}

	public void setHowWorks(String howWorks) {
		this.howWorks = howWorks;
	}

	public String getBula() {
		return bula;
	}

	public void setBula(String bula) {
		this.bula = bula;
	}

	public List<Speciality> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<Speciality> specialities) {
		this.specialities = specialities;
	}

	public List<Substance> getSubstances() {
		return substances;
	}

	public void setSubstances(List<Substance> substances) {
		this.substances = substances;
	}
}