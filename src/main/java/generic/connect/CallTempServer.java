package generic.connect;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class CallTempServer {

	// http://45.33.81.168:8000/api/v1/get/temp/baby
	
	public final static String SERVER = "http://45.33.81.168:8000/api/v1/get/temp/";
	
	
	
	public static int askTemperature(String iRoom){
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
	    try {
	        HttpGet httpget = new HttpGet(SERVER+iRoom);

	        System.out.println("Executing request " + httpget.getRequestLine());

	        // Create a custom response handler
	        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

	            @Override
	            public String handleResponse(
	                    final HttpResponse response) throws ClientProtocolException, IOException {
	                int status = response.getStatusLine().getStatusCode();
	                if (status >= 200 && status < 300) {
	                    HttpEntity entity = response.getEntity();
	                    return entity != null ? EntityUtils.toString(entity) : null;
	                } else {
	                    throw new ClientProtocolException("Unexpected response status: " + status);
	                }
	            }

	        };
	        String responseBody = httpclient.execute(httpget, responseHandler);
	        System.out.println("----------------------------------------");
	        System.out.println(responseBody);
	        
	        Gson aGson = new Gson();
	        
	        Response aResp = aGson.fromJson(responseBody, Response.class);
	        
	        if (aResp.error==null) {
	        	return aResp.temperature;
	        } else {
	        	return -1;
	        }
	        
	    } catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
		
	    return -1;
		
	}
	
	
}
