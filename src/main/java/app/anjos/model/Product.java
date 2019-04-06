package app.anjos.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import io.matob.database.Model;

@Entity
@Table(name = "product")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", length = 1, discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("P")
public class Product implements Model<Integer> {

	private static final long serialVersionUID = -6071652540687665820L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 50)
	private String name;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "supplier_id")
	private Supplier supplier;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "image_id")
	private Image image;

	private boolean featured;

	@Column(name = "featured_priority")
	private Integer featuredPriority;

	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(name = "product_category", //
			joinColumns = { @JoinColumn(name = "product_id") }, //
			inverseJoinColumns = { @JoinColumn(name = "category_id") })
	private List<Category> categories;

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

	public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	public Integer getFeaturedPriority() {
		return featuredPriority;
	}

	public void setFeaturedPriority(Integer featuredPriority) {
		this.featuredPriority = featuredPriority;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getLabel() {
		return name;
	}
}