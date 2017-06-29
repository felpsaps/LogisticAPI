package service.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe modelo da lista de instucoes.
 * Classe criada para facilitar o manejo do arquivo json.
 * @author Felps
 *
 */
public class StepListModel {

	private List<StepModel> steps;
	
	public StepListModel() {
		steps = new ArrayList<StepModel>();
	}

	public List<StepModel> getSteps() {
		return steps;
	}

	public void setSteps(List<StepModel> steps) {
		this.steps = steps;
	}
	
	public void add(StepModel step) {
		steps.add(step);
	}
	
}
