package app.anjos.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import io.matob.database.Model;

@Entity
@Table(name = "presentation")
public class Presentation implements Model<Integer> {

	private static final long serialVersionUID = -5527256281141892273L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 30)
	private String code;

	@Column(length = 30)
	private String ms;

	@Column(length = 200)
	private String name;

	@Column(name = "manual_price")
	private Boolean manualPrice = false;

	@Column(name = "price_supplier", precision = 10, scale = 3)
	private Double priceSupplier;

	@Column(name = "price_max", precision = 10, scale = 3)
	private Double priceMax;

	@Column(name = "price_anjos", precision = 10, scale = 3)
	private Double priceAnjos;

	@Column(name = "price_pharmacy", precision = 10, scale = 3)
	private Double pricePharmacy;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "image_id")
	private Image image;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "product_id")
	private Product product;

	private Boolean enabled = true;

	public Presentation() {}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMs() {
		return ms;
	}

	public void setMs(String ms) {
		this.ms = ms;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPriceSupplier() {
		return priceSupplier;
	}

	public void setPriceSupplier(Double priceSupplier) {
		this.priceSupplier = priceSupplier;
	}

	public Double getPriceMax() {
		return priceMax;
	}

	public void setPriceMax(Double priceMax) {
		this.priceMax = priceMax;
	}

	public Boolean getManualPrice() {
		return manualPrice;
	}

	public void setManualPrice(Boolean manualPrice) {
		this.manualPrice = manualPrice;
	}

	public Double getPriceAnjos() {
		return priceAnjos;
	}

	public void setPriceAnjos(Double priceAnjos) {
		this.priceAnjos = priceAnjos;
	}

	public Double getPricePharmacy() {
		return pricePharmacy;
	}

	public void setPricePharmacy(Double pricePharmacy) {
		this.pricePharmacy = pricePharmacy;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String getLabel() {
		return name;
	}
}