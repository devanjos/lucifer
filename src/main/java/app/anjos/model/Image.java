package app.anjos.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.matob.database.Model;

@Entity
@Table(name = "image")
public class Image implements Model<Integer> {

	private static final long serialVersionUID = -62126165437408651L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 5)
	private String format; // svg, png, jpg

	@Column(columnDefinition = "TEXT")
	private String data;

	public Image() {}

	public Image(String format, String data) {
		this.format = format;
		this.data = data;
	}

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
