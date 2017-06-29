package service.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DeliveryModel {

	private Integer deliveryId;
	private Integer vehicle;
	private List<PackageModel> packages;
	private boolean hasError;
	private String errorMessage;

	private Integer step;
	private StepListModel stepList;
	private boolean isRegister = false;
	private static final String TRUCK_ZONE = "Zona do Caminhão";
	private static final String TRANSFERING_ZONE = "Zona de Transferência";
	private static final String SUPPLY_ZONE = "Zona de Abastecimento";
	private static final String INSTRUCTIONS_PATH = "intructions";
	private static final String REGISTERS_PATH = "registers";
	private static final String PROCESSING_PATH = "processing";
	
	/**
	 * Construtor para o cadastro de uma nova entrega
	 * @param json json com todas as informacoes da entrega
	 */
	public DeliveryModel(JSONObject json) {
		isRegister = true;
		FileWriter w = null;
		try {
			if (fillInformation(json)) {
				File registerDir = new File(REGISTERS_PATH);
				if (!registerDir.exists()) {
					registerDir.mkdir();
				}
				File registerJson = new File(registerDir.getPath() + File.separatorChar + getDeliveryId()+"_"+getVehicle()+".json");
				if (registerJson.exists()) {
					errorMessage = "Já existe um cadastro com este veículo e entrega!";
					setHasError(true);
					return;
				} 
	
				registerJson.createNewFile();
			
				setHasError(false);
				w = new FileWriter(registerJson);
				w.append(new JSONObject(DeliveryModel.this).toString());
				w.flush();
				w.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			errorMessage = "Erro ao criar objeto \n" + e.getMessage();
			setHasError(true);
		} finally {
			if (w != null) {
				try {
					w.close();
				} catch (IOException e) {
					errorMessage = "Erro ao criar objeto \n" + e.getMessage();
					setHasError(true);
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Construtor para carregar informacoes ja salvas em arquivo json
	 * @param deliveryId
	 * @param vehicle
	 */
	public DeliveryModel(Integer deliveryId, Integer vehicle) {
		Scanner sc = null;
		StringBuilder sb = new StringBuilder();
		try {
			File processingDir = new File(PROCESSING_PATH);
			if (!processingDir.exists()) {
				processingDir.mkdir();
			}
			File processingFile = new File(processingDir.getPath() + File.separatorChar + deliveryId+"_"+vehicle+".dat");
			if (processingFile.exists()) {
				/* Ja esta sendo processado */
				errorMessage = "As instruções estão sendo processadas.\nTente novamente em alguns minutos.";
				setHasError(true);
				return;
			}
			File registerDir = new File(REGISTERS_PATH);
			if (!registerDir.exists()) {
				errorMessage = "Não existe nenhum cadastro com esta entrega e veículo!";
				setHasError(true);
				return;
			}
			File registerJson = new File(registerDir.getPath() + File.separatorChar + deliveryId+"_"+vehicle+".json");
			if (!registerJson.exists()) {
				errorMessage = "Não existe nenhum cadastro com esta entrega e veículo!";
				setHasError(true);
				return;
			}
		
			sc = new Scanner(registerJson);
			while (sc.hasNextLine()) {
				sb.append(sc.nextLine());
			}
			JSONObject json = new JSONObject(sb.toString());
			fillInformation(json);
		} catch (FileNotFoundException e) {
			errorMessage = "Erro ao criar objeto \n" + e.getMessage() + "\nJSON: " + sb.toString();
			setHasError(true);
			e.printStackTrace();
		} catch (JSONException e) {
			errorMessage = "Erro ao criar objeto \n" + e.getMessage() + "\nJSON: " + sb.toString();
			setHasError(true);
			e.printStackTrace();
		} finally {
			if (sc != null) {
				sc.close();
			}
		}
	}
	

	
	private boolean fillInformation(JSONObject json) {
		try {
			setDeliveryId(json.getInt("deliveryId"));
		
			if (getDeliveryId() == null) {
				errorMessage = "Parâmetro 'deliveryId' não encontrado! Por favor verifique.";
				setHasError(true);
				return false;
			}
			setVehicle(json.getInt("vehicle"));
			if (getVehicle() == null) {
				errorMessage = "Parâmetro 'vehicle' não encontrado! Por favor verifique.";
				setHasError(true);
				return false;
			}
			
			JSONArray arr = json.getJSONArray("packages");
			if (arr == null || arr.length() == 0) {
				/* Se nao ouver nenhum pacote cadastrado, gera um erro */
				errorMessage = "Nenhuma carga foi cadastrada! Por favor verifique.";
				setHasError(true);
				return false;
			} else {
				JSONObject j = null;
				PackageModel pkg = null;
				packages = new ArrayList<PackageModel>();
				for (int i = 0; i < arr.length(); i++) {
					j = (JSONObject) arr.get(i);
					pkg = new PackageModel();
					pkg.setId(j.getInt("id"));
					if (pkg.getId() == null) {
						errorMessage = "Parâmetro 'id' de 'packages' não encontrado! Por favor verifique.";
						setHasError(true);
						return false;
					}
					pkg.setWeight(j.getDouble("weight"));
	
					if (pkg.getWeight() == null) {
						errorMessage = "Parâmetro 'weight' de 'packages' não encontrado! Por favor verifique.";
						setHasError(true);
						return false;
					}
					packages.add(pkg);
				}
			}
		} catch (JSONException e) {
			setHasError(true);
			errorMessage = "Erro ao parsear json! Por favor verifique.\n" + e.getMessage();
			e.printStackTrace();
			if (e.getMessage().contains("JSONObject[\"deliveryId\"] not found")) {
				errorMessage = "Parâmetro 'deliveryId' não encontrado! Por favor verifique.";
			} else if (e.getMessage().contains("JSONObject[\"vehicle\"] not found")) {
				errorMessage = "Parâmetro 'vehicle' não encontrado! Por favor verifique.";
			} else if (e.getMessage().contains("JSONObject[\"packages\"] not found")) {
				errorMessage = "Parâmetro 'packages' não encontrado! Por favor verifique.";
			} else if (e.getMessage().contains("JSONObject[\"id\"] not found")) {
				errorMessage = "Parâmetro 'id' de 'packages' não encontrado! Por favor verifique.";
			} else if (e.getMessage().contains("JSONObject[\"weight\"] not found")) {
				errorMessage = "Parâmetro 'weight' de 'packages' não encontrado! Por favor verifique.";
			}
			return false;
		}
		return true;
	}

	public Integer getDeliveryId() {
		return deliveryId;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setDeliveryId(Integer deliveryId) {
		if (deliveryId == null) {
			errorMessage = "Parâmetro 'deliveryId' não encontrado! Por favor verifique.";
			setHasError(true);
		}
		this.deliveryId = deliveryId;
	}

	public Integer getVehicle() {
		return vehicle;
	}

	public void setVehicle(Integer vehicle) {
		if (vehicle == null) {
			errorMessage = "Parâmetro 'vehicle' não encontrado! Por favor verifique.";
			setHasError(true);
		}
		this.vehicle = vehicle;
	}

	public List<PackageModel> getPackages() {
		return packages;
	}

	public void setPackages(List<PackageModel> packages) {
		this.packages = packages;
	}

	public boolean isHasError() {
		return hasError;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}
	
	/**
	 * Verifica se ja existe um arquivo json com as
	 * instrucoes para esta entrega e veiculo
	 * caso o arquivo nao exita, executa o algoritmo para cria lo,
	 * caso ja exista, le o arquivo e retorna a informacao
	 * @return retorn o json com as intrucoes
	 */
	public String getDeliverySteps() {
		
		if (isRegister) {
			return null;
		}
		
		Scanner sc = null;
		try {
			File processingDir = new File(PROCESSING_PATH);
			if (!processingDir.exists()) {
				processingDir.mkdir();
			}
			File processingFile = new File(processingDir.getPath() + File.separatorChar + deliveryId+"_"+vehicle+".dat");
			if (processingFile.exists()) {
				/* Ja esta sendo processado */
				return "As instruções estão sendo processadas.\nTente novamente em alguns minutos.";
			}
			
			File instructionDir = new File(INSTRUCTIONS_PATH);
			File registerDir = new File(REGISTERS_PATH);
			if (!registerDir.exists()) {
				return "Não existe nenhum cadastro para essa entrega e veículo";
			}
			if (!instructionDir.exists()) {
				instructionDir.mkdir();
			}
			File instructionJson = new File(instructionDir.getPath() + File.separatorChar + deliveryId+"_"+vehicle+".json");
			if (!instructionJson.exists()) {
				makeStepsNow();
			}
			
			StringBuilder sb = new StringBuilder();
		
			sc = new Scanner(instructionJson);

			while (sc.hasNextLine()) {
				sb.append(sc.nextLine());
			}
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (sc != null) {
				sc.close();
			}
		}
		return null;
	}
	
	/**
	 * Este metodo cria a lista de instruções que sera enviada ao
	 * funcionario. Esta lista é salva no servidor em um arquivo json, para evitar o processamento 
	 * em futuras consultas.
	 * O metodo é rodado em uma thread separada quando há um novo cadastro.
	 * Deste modo, quando o funcionário for realizar a busca, a lista ja estará criada e salva,
	 * diminuindo o tempo de resposta.
	 */
	public void makeSteps() {
		new ProcessThread();
	}
	
	private void makeStepsNow() {
		File instructionDir = new File(INSTRUCTIONS_PATH);
		if (!instructionDir.exists()) {
			instructionDir.mkdir();
		}
		File instructionJson = new File(instructionDir.getPath() + File.separatorChar + deliveryId+"_"+vehicle+".json");
		if (instructionJson.exists() && instructionJson.length() > 0) {
			/* Ja existe o arquivo de instrucoes para esta entrega */
			return;
		}
		
		File processingDir = new File(PROCESSING_PATH);
		if (!processingDir.exists()) {
			processingDir.mkdir();
		}
		File processingFile = new File(processingDir.getPath() + File.separatorChar + deliveryId+"_"+vehicle+".dat");
		if (processingFile.exists()) {
			/* Ja esta sendo processado */
			return;
		}
		FileWriter w = null;
		try {
			processingFile.createNewFile();
			/* Primeiro ordena a lista de cargas em ordem crescente de pesos */
			Collections.sort(getPackages(), new Comparator<PackageModel>() {
	
				@Override
				public int compare(PackageModel o1, PackageModel o2) {
					return o1.getWeight().compareTo(o2.getWeight());
				}
			});
			System.out.println("VALOR INICIAL: " + getPackages().toString());
			
			step = 1;
			stepList = new StepListModel();
			makeSteps(getPackages().size(), SUPPLY_ZONE, TRANSFERING_ZONE, TRUCK_ZONE);
	
			JSONObject j = new JSONObject(stepList);
		
			instructionJson.createNewFile();
			w = new FileWriter(instructionJson);
			w.append(j.toString());
			w.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (w != null) {
				try {
					w.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (processingFile != null && processingFile.exists()) {
				processingFile.delete();
			}
		}
	
	}
	
	/**
	 * Metodo recursivo que gerar as instruçoes
	 * @param numOfPackages numero total de cargas desta entrega
	 * @param start A zona inicial (iniciada com zona de abastecimento)
	 * @param aux A zona auxiliar (iniciada com a zona de transferencia)
	 * @param end A zona final (iniciada com a zona do caminhao)
	 */
	private void makeSteps(Integer numOfPackages, String start, String aux, String end) {
		if (numOfPackages == 1) {
			stepList.add(new StepModel(step, getPackages().get(numOfPackages - 1).getId(), start, end));
			System.out.println("step: "+step+" - packageId: " +getPackages().get(numOfPackages - 1).getId()+ " - from " + start + " to " + end);
		} else {
			makeSteps(numOfPackages - 1, start, end, aux);
			step++;
			stepList.add(new StepModel(step, getPackages().get(numOfPackages - 1).getId(), start, end));
			System.out.println("step: "+step+" - packageId: " +getPackages().get(numOfPackages - 1).getId()+ " - from " + start + " to " + end);
			step++;
			makeSteps(numOfPackages - 1, aux, start, end);
		}
	}
	
	@Override
	public String toString() {
		return "vehicle: " + getVehicle() + " deliveryId: " + deliveryId + " packages: " + getPackages();
	}
	
	private class ProcessThread extends Thread {
		
		public ProcessThread() {
			this.start();
		}
		public void run() {
			makeStepsNow();
		}
	}
	
}
