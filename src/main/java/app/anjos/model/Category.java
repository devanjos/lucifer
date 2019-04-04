package app.anjos.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import io.matob.database.Model;

@Entity
@Table(name = "category")
public class Category implements Model<Integer> {

	private static final long serialVersionUID = 4543192925422553524L;

	private Integer id;
	private String name;
	private Image image;
	private CategoryGroup categoryGroup;

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

	public CategoryGroup getCategoryGroup() {
		return categoryGroup;
	}

	public void setCategoryGroup(CategoryGroup categoryGroup) {
		this.categoryGroup = categoryGroup;
	}

	@Override
	public String getLabel() {
		return name;
	}
}