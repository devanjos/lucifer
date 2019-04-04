package app.anjos.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import io.matob.database.Model;

@Entity
@Table(name = "image")
public class Image implements Model<Integer> {

	private static final long serialVersionUID = -62126165437408651L;

	@Id
	private Integer id;
	private String format; // svg, png, jpg
	private String data;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String getLabel() {
		return id.toString();
	}
}
