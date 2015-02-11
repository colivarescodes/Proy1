package com.pyclimitada.pyc;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class httpHandler {
	public String post(String posturl){
		try{
			
			HttpClient httpclient  = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(posturl);
			
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity  = response.getEntity();
			
			String responseText = EntityUtils.toString(entity);
			return responseText;
			
		}catch(Exception e){
			return e.getMessage();
		}
	
	}
}
