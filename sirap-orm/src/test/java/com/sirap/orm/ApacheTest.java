package com.sirap.orm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class ApacheTest {
	@Test  
    public void doPost() throws IOException {  
        String uriAPI = "http://localhost:8080/SpringMVC/databind/json";
        uriAPI = "http://localhost/file/throw";
        String result = "";
        // 创建HttpPost对象  
        HttpPost httpRequst = new HttpPost(uriAPI);  
  
        List<NameValuePair> params = new ArrayList<NameValuePair>();  
        params.add(new BasicNameValuePair("username", "admin"));  
        params.add(new BasicNameValuePair("password", "123456"));  
        params.add(new BasicNameValuePair("ball", "Nothing is more important"));  
        CloseableHttpResponse httpResponse = null;  
          
        try {  
            httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));  
            CloseableHttpClient httpclient = HttpClients.createDefault();  
            httpResponse = httpclient.execute(httpRequst);  
            if (httpResponse.getStatusLine().getStatusCode() == 200) {  
                HttpEntity httpEntity = httpResponse.getEntity();  
                result = EntityUtils.toString(httpEntity);// 取出应答字符串  
            }  
        } catch (Exception ex) {  
            ex.printStackTrace();
        } finally{  
            httpResponse.close();  
        }  
        System.out.println(result);  
    }  
}
