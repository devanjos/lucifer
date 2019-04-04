package app.anjos.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import io.matob.database.Model;

@Entity
@Table(name = "presentation")
public class Presentation implements Model<Integer> {

	private static final long serialVersionUID = -5527256281141892273L;

	@Id
	private Integer id;
	private String code;
	private String ms;
	private String name;
	private String fabricante;
	private Double priceSupplier;
	private Double priceMax;
	private Double priceAnjos;
	private Image image;

	private Product product;

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

	public String getFabricante() {
		return fabricante;
	}

	public void setFabricante(String fabricante) {
		this.fabricante = fabricante;
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

	public Double getPriceAnjos() {
		return priceAnjos;
	}

	public void setPriceAnjos(Double priceAnjos) {
		this.priceAnjos = priceAnjos;
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

	@Override
	public String getLabel() {
		return name;
	}
}