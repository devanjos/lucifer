package app.anjos.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "idPessoa")
public class Drug extends Product {

	private static final long serialVersionUID = -4799581132394919157L;

	private Character type; // R - Referência, G - Genérico, S - Similar
	private Boolean retencaoReceita;
	private String principioAtivo;
	private String especialidades;
	private String indicacoes;
	private String funcionamento;
	private String bula;

	public Character getType() {
		return type;
	}

	public void setType(Character type) {
		this.type = type;
	}

	public Boolean getRetencaoReceita() {
		return retencaoReceita;
	}

	public void setRetencaoReceita(Boolean retencaoReceita) {
		this.retencaoReceita = retencaoReceita;
	}

	public String getPrincipioAtivo() {
		return principioAtivo;
	}

	public void setPrincipioAtivo(String principioAtivo) {
		this.principioAtivo = principioAtivo;
	}

	public String getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(String especialidades) {
		this.especialidades = especialidades;
	}

	public String getIndicacoes() {
		return indicacoes;
	}

	public void setIndicacoes(String indicacoes) {
		this.indicacoes = indicacoes;
	}

	public String getFuncionamento() {
		return funcionamento;
	}

	public void setFuncionamento(String funcionamento) {
		this.funcionamento = funcionamento;
	}

	public String getBula() {
		return bula;
	}

	public void setBula(String bula) {
		this.bula = bula;
	}
}