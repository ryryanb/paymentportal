package tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;

/*
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.HttpEntity;
*/
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moadbus.web.biller.dto.SmsMessage;



public class SMSSenderClient {

	protected final Log logger = LogFactory.getLog(getClass());

	
	
	   //TODO: transfer to properties
        private String host = "127.0.0.1";
        private String port = "40001";
	
		protected HttpClient client= null;
		protected String baseURL = "http://" + host + ":" + port + "/";
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");	
	
		public HttpClient getCCRestClient() {
			if(client==null) {
			String timout="20000";

			int sockTimeout=Integer.parseInt(timout);
			
			 RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(sockTimeout).setConnectTimeout(sockTimeout)
		                .setSocketTimeout(sockTimeout).build();
		     SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true).build();
		     PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
		     poolingHttpClientConnectionManager.setMaxTotal(250);
		     poolingHttpClientConnectionManager.setDefaultMaxPerRoute(150);
		     client = HttpClientBuilder.create().setConnectionManager(poolingHttpClientConnectionManager)
		    		 .setDefaultSocketConfig(socketConfig).setDefaultRequestConfig(requestConfig).build();
			}
		     return client;
		}

		public String sendMessage(String phone, String message) throws RemoteException {
			SmsMessage sms = new SmsMessage();
			sms.setPhone(phone);
			sms.setMessage(message);
			return doPost("sendMessage" , gson.toJson(sms));			
		}




		private String doRequest(final HttpRequestBase request) throws RemoteException {

			HttpResponse response;
			BufferedReader rd = null;
			try {
				int hardTimeout = 20; // seconds
				TimerTask task = new TimerTask() {
				    @Override
				    public void run() {
				        if (request != null) {
				        	request.abort();
				        }
				    }
				};
				new Timer(true).schedule(task, hardTimeout * 1000);
				response = client.execute(request);
				rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				logger.info("Response JSON Data:" + result.toString());
				return result.toString();
			} catch (ClientProtocolException e) {
				throw new RemoteException(e.getMessage());
			} catch (IOException e) {
				throw new RemoteException(e.getMessage());
			} finally {
				if (rd != null) {
					try {
						rd.close();
					} catch (IOException e) {
					}
				}
			}
		}

		protected String doPost(String uri, String jsonContent) throws RemoteException {
			logger.info("POST request to " + baseURL + uri);
			HttpPost request = new HttpPost(baseURL + uri);
			request.addHeader("Content-Type", "application/json");

			if (StringUtils.isNotBlank(jsonContent)) {
				HttpEntity data = null;
				try {
					data = new StringEntity(jsonContent);
				} catch (UnsupportedEncodingException e) {
					throw new RemoteException(e.getMessage());
				}
				request.setEntity(data);
			}
			return doRequest(request);
		}

	}


