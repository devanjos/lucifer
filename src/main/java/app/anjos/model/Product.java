package app.anjos.model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import io.matob.database.Model;

@Entity
@Table(name = "product")
@Inheritance(strategy = InheritanceType.JOINED)
public class Product implements Model<Integer> {

	private static final long serialVersionUID = -6071652540687665820L;

	@Id
	private Integer id;
	private String name;
	private Category category;
	private Supplier supplier;
	private Image image;
	private List<Presentation> presentations;

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

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public List<Presentation> getPresentations() {
		return presentations;
	}

	public void setPresentations(List<Presentation> presentations) {
		this.presentations = presentations;
	}

	@Override
	public String getLabel() {
		return name;
	}
}