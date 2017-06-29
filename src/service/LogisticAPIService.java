package service;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import service.model.DeliveryModel;

@Path("/")
public class LogisticAPIService {

	/**
	 * Request para a criacao das instrucoes
	 * Ex: 'http://localhost:8080/logisticAPI/api/step?deliveryId=1234567890&vehicle=123456'
	 * @param delivery ID da entrega
	 * @param vehicle ID do veiculo
	 * @return Retorna um json com as instrucoes ou uma mensagem de erro.
	 * Formato do json:
	 * 
	 * {
	 * 	"steps":[
	 * 		{"deliveryId":2,"from":"Zona de Abastecimento","step":1,"to":"Zona do Caminhao"},
	 * 		{"deliveryId":1,"from":"Zona de Abastecimento","step":2,"to":"Zona de Transferencia"},
	 * 		{"deliveryId":2,"from":"Zona do Caminhao","step":3,"to":"Zona de Transferencia"},
	 * 		{"deliveryId":3,"from":"Zona de Abastecimento","step":4,"to":"Zona do Caminhao"},
	 * 		{"deliveryId":2,"from":"Zona de Transferencia","step":5,"to":"Zona de Abastecimento"},
	 * 		{"deliveryId":1,"from":"Zona de Transferencia","step":6,"to":"Zona do Caminhao"},
	 * 		{"deliveryId":2,"from":"Zona de Abastecimento","step":7,"to":"Zona do Caminhao"}
	 * 	]
	 * }
	 */
	@GET
	@Path("/step")
	@Produces(MediaType.TEXT_PLAIN)
	public Response step(@DefaultValue("-1") @QueryParam("deliveryId") int delivery,
						 @DefaultValue("-1") @QueryParam("vehicle") int vehicle) {

		System.out.println("Data Received: deliveryId: " + delivery + " vehicle: " + vehicle);
		if (delivery == -1) {
			System.out.println("RESPOSTA step:\n" + "Parametro 'deliveryId' nao encontrado! Verifique,");
			return Response.status(200).entity("Parametro 'deliveryId' nao encontrado! Verifique.").build();
		}
		if (vehicle == -1) {
			System.out.println("RESPOSTA step:\n" + "Parametro 'vehicle' nao encontrado! Verifique,");
			return Response.status(200).entity("Parametro 'vehicle' nao encontrado! Verifique.").build();
		}
		DeliveryModel dm = new DeliveryModel(delivery, vehicle);
		if (dm.isHasError()) {
			System.out.println("RESPOSTA step:\n" + dm.getErrorMessage());
			return Response.status(200).entity(dm.getErrorMessage()).build();
		}
		String response = dm.getDeliverySteps();
		System.out.println("RESPOSTA step:\n" + response);
		
		return Response.status(200).entity(response).build();
	}
	
	/**
	 * Request POST para o cadastro de entrega
	 * @param incomingData Recebe um json com as informacoes da entrega
	 * LogisticAPIClient.java implementa um request com este json para referencia.
	 * Formato do json:
	 * 
	 * {
	 *	“vehicle” : “123456”,
	 *	“deliveryId” : “1234567890”
	 *	“packages” : [
	 *					{ "id": “1”, “weight”: “14.50”},
	 *					{ "id": “2”, “weight”: “12.15”},
	 *					{ "id": “3”, “weight”: “19.50”}
	 *				]
	 * }
	 * @return cria o arquivo json desta entrega no servidor. Retorna sucesso ou uma mensagem de erro
	 */
	@POST
	@Path("/delivery")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delivery(InputStream incomingData) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			
			JSONObject json  = new JSONObject(sb.toString());
			DeliveryModel dm = new DeliveryModel(json);
			
			
			if (dm.isHasError()) {
				System.out.println("RESPOSTA delivery:\n" + dm.getErrorMessage());
				return Response.status(200).entity(dm.getErrorMessage()).build();
			}

			/* Apos fazer o cadastro da entrega e criar o arquivo json no servidor, ja realiza o algoritmo para a
			 * criacao das intrucoes. Esse metodo executa em uma thread separada para agilizar o processo. */

			dm.makeSteps();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("RESPOSTA delivery:\nEntrega cadastrada com sucesso!");
		return Response.status(200).entity("Entrega cadastrada com sucesso!").build();
	}
}
