package service.model;
/**
 * Classe modelo de uma enrega, contendo seu id e seu peso
 * @author Felps
 *
 */
public class PackageModel {

	private Integer id;
	private Double weight;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
	@Override
	public String toString() {
		return "{ id: " + getId() + " weight: " + getWeight() + "}";
	}
}
