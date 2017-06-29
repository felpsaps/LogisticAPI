package service.model;

/**
 * Classe modelo de uma instrucao.
 * Contem o numero do step a ser executado, id da entrega, da onde para onde a entrega deve mudar
 * @author Felps
 *
 */
public class StepModel {

	private Integer step;
	private Integer deliveryId;
	private String from;
	private String to;
	
	public StepModel(Integer step, Integer deliveryId, String from, String to) {
		this.step = step;
		this.deliveryId = deliveryId;
		this.from = from;
		this.to= to;
	}
	
	public Integer getStep() {
		return step;
	}
	public void setStep(Integer step) {
		this.step = step;
	}
	public Integer getDeliveryId() {
		return deliveryId;
	}
	public void setDeliveryId(Integer deliveryId) {
		this.deliveryId = deliveryId;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	
	@Override
	public String toString() {
		return "step: " + step;
	}
}
