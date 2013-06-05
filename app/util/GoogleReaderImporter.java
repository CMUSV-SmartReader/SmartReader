package util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;

public class GoogleReaderImporter {

    public static void importFeeds(String account, String password) {
        loginGoogle(account, password);
    }
    
    public static void importWithEmail(String email, InputSource inputSource) {
        MorphiaObject.setUp();
        
    }
    
    private static void loginGoogle(String account, String password) {
        HttpPost httpPost = new HttpPost("https://www.google.com/accounts/ClientLogin");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("accountType", "HOSTED_OR_GOOGLE"));
        params.add(new BasicNameValuePair("Email", account));
        params.add(new BasicNameValuePair("Passwd", password));
        params.add(new BasicNameValuePair("service", "reader"));
        params.add(new BasicNameValuePair("source", "SMARTREADER"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DefaultHttpClient httpclient = new DefaultHttpClient();  
        try {
            HttpResponse response = httpclient.execute(httpPost);
            System.out.println(EntityUtils.toString(response.getEntity()));
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
