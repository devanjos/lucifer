package app.anjos.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import io.matob.database.Model;

@Entity
@Table(name = "categorygroup")
public class CategoryGroup implements Model<Integer> {

	private static final long serialVersionUID = -7255701090075528720L;

	private Integer id;
	private String name;
	private Image image;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	@Override
	public String getLabel() {
		return name;
	}
}