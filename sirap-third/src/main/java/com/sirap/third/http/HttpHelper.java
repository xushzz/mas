package com.sirap.third.http;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexFile;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;

public class HttpHelper {
	public static final String URL_AKA10_REPO = "http://www.aka10.com/file/finals";
	public static final String URL_AKA10_PICKER = "http://www.aka10.com/sirap/picker";
	public static final String KEY_BALL = "ball";
	public static final String KEY_TITLE = "title";

	public static String postToAka10(String content) {
		return doPost(URL_AKA10_REPO, "ball", content);
	}
	public static String postToAka10(List<Object> items) {
		return null;
	}
	public static String sendStringsAndFiles(List<?> items) {
		return sendStringsAndFiles(items, URL_AKA10_REPO, null);
	}
	
	public static String sendStringsAndFiles(List<?> items, String url, String title) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String result = null;
        try {
    		ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName("utf-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);  
            D.list(items);
            for(Object obj : items) {
            	if(obj instanceof File) {
            		File file = (File)obj;
            		builder.addBinaryBody(file.getName(), file);
            	} else if(obj instanceof MexFile) {
            		File file = ((MexFile)obj).getFile();
            		builder.addBinaryBody(file.getName(), file);
            	} else {
            		builder.addTextBody(KEY_BALL, obj + "", contentType);
            	}
            }
            
            builder.addTextBody("title", title, contentType);
            
            httpPost.setEntity(builder.build());
            C.pl("Sending to " + url);
            response = httpclient.execute(httpPost);
            return EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            HttpClientUtils.closeQuietly(httpclient);
            HttpClientUtils.closeQuietly(response);
        }
        return result;
    }
	
	public static String doGet(String url) {  
		HttpGet perry = new HttpGet(url);
		CloseableHttpClient donald = HttpClients.createDefault();  
		try {
			CloseableHttpResponse james = donald.execute(perry);
			HttpEntity httpEntity = james.getEntity();  
            String result = EntityUtils.toString(httpEntity);

            return result;
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		
		return null;
	}
	
	public static String doPost(String url, String key, String value) {  
        String result = "";
        // 创建HttpPost对象  
        HttpPost perry = new HttpPost(url);  
  
        List<NameValuePair> params = new ArrayList<NameValuePair>();  
        params.add(new BasicNameValuePair(key, value));  
        CloseableHttpResponse httpResponse = null;  
          
        try {  
        	perry.setEntity(new UrlEncodedFormEntity(params, Konstants.CODE_UTF8));  
            CloseableHttpClient httpclient = HttpClients.createDefault();  
            httpResponse = httpclient.execute(perry);  
            D.pl(httpResponse);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {  
                HttpEntity httpEntity = httpResponse.getEntity();  
                result = EntityUtils.toString(httpEntity);
            }  
        } catch (Exception ex) {  
            throw new MexException(ex);
        } finally{  
        	try {
				httpResponse.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
        }
        
        return result;
    }
}
