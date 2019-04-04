package app.anjos.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import io.matob.database.Model;

@Entity
@Table(name = "supplier")
public class Supplier implements Model<Integer> {

	private static final long serialVersionUID = -5232263253688595761L;

	@Id
	private Integer id;
	private String cnpj;
	private String name;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getLabel() {
		return name;
	}
}
