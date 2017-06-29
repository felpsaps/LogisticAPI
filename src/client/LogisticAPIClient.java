package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class LogisticAPIClient {
	
	public static void main(String[] args) {
		try {
			long t = System.currentTimeMillis();
			/* Request sem nenhum cadastro relaizado ainda. 
			 * Resultado esperado: Mensagem de nenhum cadastro*/
			makeGETRequest(1234567890, 123456);
			Thread.sleep(1000);

			String string = " { " +
					"\"deliveryId\" : \"1234567890\", " +
					"\"packages\" : [ " +
					"{ \"id\": \"1\", \"weight\": \"14.50\"}, " +
					"{ \"id\": \"2\", \"weight\": \"12.15\"}, " +
					"{ \"id\": \"3\", \"weight\": \"19.50\"} " +
					"]" +
					"}";

			/* Request para cadastro sem o parametro 'vehicle' 
			 * Resultado esperado: Mensagem de falta de parametro */
			makePOSTRequest(string);
			Thread.sleep(1000);

			string = " { " +
					"\"vehicle\" : \"123456\", " +
					"\"packages\" : [ " +
					"{ \"id\": \"1\", \"weight\": \"14.50\"}, " +
					"{ \"id\": \"2\", \"weight\": \"12.15\"}, " +
					"{ \"id\": \"3\", \"weight\": \"19.50\"} " +
					"]" +
					"}";

			/* Request para cadastro sem o parametro 'deliveryId' 
			 * Resultado esperado: Mensagem de falta de parametro */
			makePOSTRequest(string);
			Thread.sleep(1000);

			string = " { " +
					"\"vehicle\" : \"123456\", " +
					"\"deliveryId\" : \"1234567890\", " +
					"}";

			/* Request para cadastro sem o parametro 'packages' 
			 * Resultado esperado: Mensagem de falta de parametro */
			makePOSTRequest(string);
			Thread.sleep(1000);

			string = " { " +
					"\"vehicle\" : \"123456\", " +
					"\"deliveryId\" : \"1234567890\", " +
					"\"packages\" : [ " +
					"]" +
					"}";

			/* Request para cadastro com o parametro pakages, porem nenhum item 
			 * Resultado esperado: Mensagem de falta de entregas */
			makePOSTRequest(string);
			Thread.sleep(1000);

			string = " { " +
					"\"vehicle\" : \"123456\", " +
					"\"deliveryId\" : \"1234567890\", " +
					"\"packages\" : [ " +
					"{ \"id\": \"1\", \"weight\": \"14.50\"}, " +
					"{ \"id\": \"2\", \"weight\": \"12.15\"}, " +
					"{ \"id\": \"3\", \"weight\": \"19.50\"} " +
					"]" +
					"}";

			/* Request para cadastro. Todos os parametros corretos 
			 * Resultado esperado: Mensagem de cadastro realizado com sucesso */
			makePOSTRequest(string);
			Thread.sleep(1000);			 

			/* Request para cadastro. Todos os parametros corretos, porem, cadastro duplicado.
			 * Resultado esperado: Mensagem de cadastro ja existente */
			makePOSTRequest(string);
			Thread.sleep(1000);

			/*Request de pegar instrucoes com a entrega que foi cadastrada 
			 * Resultado esperado: Json com os steps */
			makeGETRequest(1234567890, 123456);			
			Thread.sleep(1000);

			string = " { " +
					"\"vehicle\" : \"123\", " +
					"\"deliveryId\" : \"123\", " +
					"\"packages\" : [ " +
					"{ \"id\": \"1\", \"weight\": \"14.50\"}, " +
					"{ \"id\": \"2\", \"weight\": \"12.15\"}, " +
					"{ \"id\": \"3\", \"weight\": \"22.15\"}, " +
					"{ \"id\": \"4\", \"weight\": \"14.17\"}, " +
					"{ \"id\": \"5\", \"weight\": \"7.10\"}, " +
					"{ \"id\": \"6\", \"weight\": \"18.15\"}, " +
					"{ \"id\": \"7\", \"weight\": \"32.95\"}, " +
					"{ \"id\": \"8\", \"weight\": \"27.33\"}, " +
					"{ \"id\": \"9\", \"weight\": \"14.15\"}, " +
					"{ \"id\": \"10\", \"weight\": \"17.50\"}, " +
					"{ \"id\": \"11\", \"weight\": \"12.75\"}, " +
					"{ \"id\": \"12\", \"weight\": \"12.76\"}, " +
					"{ \"id\": \"13\", \"weight\": \"12.77\"}, " +
					"{ \"id\": \"14\", \"weight\": \"91.50\"} " +
					"]" +
					"}";

			/* Request para cadastro. Todos os parametros corretos, cadastro com maior numero de entregas.
			 * Resultado esperado: Mensagem de cadastro realizado com sucesso */
			makePOSTRequest(string);
			/* Por se tratar de uma entrega com bastante itens, o algoritimo para a criacao dos
			 * steps ira demorar mais para concluir. Deste modo, o funcionario deve esperar um pouco
			 * e tentar novamente
			 * Resultado esperado: Mensagem para aguardar um tempo e tentar novamente*/
			makeGETRequest(123, 123);			
			Thread.sleep(5000);

			/* Apos esperar um tempo, realiza o request denovo para pegar a lista de instrucoes 
			 * Resultado esperado: Lista com as instrucoes */
			makeGETRequest(123, 123);			
			System.out.println("TEMPO TOTAL: " + (System.currentTimeMillis()-t)/1000);


		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void makePOSTRequest(String json) {
		try {
			
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			URL url = new URL("http://localhost:8080/logisticAPI/api/delivery");
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setConnectTimeout(2000);
			connection.setReadTimeout(2000);
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
			out.write(jsonObject.toString());
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String s;
			while ((s = in.readLine()) != null) {
				System.out.println(s);
			}
			System.out.println();
			in.close();
		} catch (Exception e) {
			
		}
	}
	
	private static void makeGETRequest(Integer deliveryId, Integer vehicle) {
		try {
			
			URL url = new URL("http://localhost:8080/logisticAPI/api/step?deliveryId=" + deliveryId + "&vehicle=" + vehicle);
			URLConnection connection = url.openConnection();
			
			connection.setDoOutput(true);
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String s;
			while ((s = in.readLine()) != null) {
				System.out.println(s);
			}
			System.out.println();
			in.close();
		} catch (Exception e) {
			
		}
	}

}
