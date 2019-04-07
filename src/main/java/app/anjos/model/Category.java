package app.anjos.model;

import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import io.matob.database.Model;

@Entity
@Table(name = "category")
public class Category implements Model<Integer> {

	private static final long serialVersionUID = 4543192925422553524L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 50)
	private String name;

	private boolean featured = false;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "image_id")
	private Image image;

	@ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinTable(name = "category_subcategory", //
			joinColumns = { @JoinColumn(name = "category_id") }, //
			inverseJoinColumns = { @JoinColumn(name = "subcategory_id") })
	private List<Category> subcategories = new LinkedList<>();

	public Category() {}

	public Category(String name) {
		super();
		this.name = name;
	}

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

	public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public List<Category> getSubcategories() {
		return subcategories;
	}

	public void setSubcategories(List<Category> subcategories) {
		this.subcategories = subcategories;
	}

	@Override
	public String getLabel() {
		return name;
	}
}