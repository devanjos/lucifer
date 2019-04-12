package app.anjos.model;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "drug")
@DiscriminatorValue(value = "D")
public class Drug extends Product {

	private static final long serialVersionUID = -4799581132394919157L;

	private Character type; // R - Referência, G - Genérico, S - Similar

	private String prescription;

	@Column(columnDefinition = "TEXT")
	private String indications;

	@Column(name = "how_works", columnDefinition = "TEXT")
	private String howWorks;

	private String bula;

	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(name = "drug_speciality", //
			joinColumns = { @JoinColumn(name = "drug_id") }, //
			inverseJoinColumns = { @JoinColumn(name = "speciality_id") })
	private List<Speciality> specialities = new LinkedList<>();

	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(name = "drug_substance", //
			joinColumns = { @JoinColumn(name = "drug_id") }, //
			inverseJoinColumns = { @JoinColumn(name = "substance_id") })
	private List<Substance> substances = new LinkedList<>();

	@CreationTimestamp
	private LocalDate createdAt;

	@UpdateTimestamp
	private LocalDate updatedAt;

	public Drug() {}

	public Character getType() {
		return type;
	}

	public void setType(Character type) {
		this.type = type;
	}

	public String getPrescription() {
		return prescription;
	}

	public void setPrescription(String prescription) {
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

	@Override
	public LocalDate getCreatedAt() {
		return createdAt;
	}

	@Override
	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public LocalDate getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public void setUpdatedAt(LocalDate updatedAt) {
		this.updatedAt = updatedAt;
	}
}