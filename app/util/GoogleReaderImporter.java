package util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import models.Feed;
import models.FeedCategory;
import models.User;
import models.UserFeed;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;


public class GoogleReaderImporter {

    public static void importFromGoogle(String account, String password) {
        String[] authInfo = loginGoogle(account, password);
        String jsonResult = getFeeds(authInfo);
        User user = User.findByEmail(account);
        if (user == null) {
            user = new User();
            user.email = account;
            user.create();
        }
        importFeeds(jsonResult, user);
    }
    
    public static void oAuthImportFromGoogle(String account, String token) {
        String jsonResult = getFeeds(token);
        User user = User.findByEmail(account);
        if (user == null) {
            user = new User();
            user.email = account;
            user.create();
        }
        importFeeds(jsonResult, user);
    }
    
    public static String getFeeds(String token) {
        HttpGet httpGet = new HttpGet("https://www.google.com/reader/api/0/subscription/list?output=json");
        httpGet.setHeader("Authorization","Bearer " + token);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpResponse response = httpclient.execute(httpGet);
            return EntityUtils.toString(response.getEntity());
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static String getFeeds(String[] authInfo) {
        HttpGet httpGet = new HttpGet("https://www.google.com/reader/api/0/subscription/list?output=json");
        httpGet.setHeader("Authorization","GoogleLogin auth=" + authInfo[0]);
        httpGet.setHeader("Cookie","SID=" + authInfo[1]);
        httpGet.setHeader("accept-encoding", "gzip, deflate");
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpResponse response = httpclient.execute(httpGet);
            return EntityUtils.toString(response.getEntity());
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void importFeeds(String jsonResult, User user) {
        Gson gson = new Gson();
        Map<String, List> feedMap = gson.fromJson(jsonResult, Map.class);
        List<Map> feeds = feedMap.get("subscriptions");
        Map<String, FeedCategory> categoryMap = new HashMap<String, FeedCategory>();
        for (Map feed : feeds) {
            String feedXmlUrl = ((String)feed.get("id")).substring("feed/".length());
            Feed feedEntity = Feed.findByXmlUrl(feedXmlUrl);
            if (feedEntity == null) {
                feedEntity = new Feed();
                feedEntity.title = (String)feed.get("title");
                feedEntity.htmlUrl = (String)feed.get("htmlUrl");
                feedEntity.xmlUrl = ((String)feed.get("id")).substring("feed/".length());
                feedEntity.create();
            }
            user.feeds.add(feedEntity);
            UserFeed userFeed = new UserFeed();
            userFeed.feed = feedEntity;
            userFeed.user = user;
            userFeed.create();
            user.userFeeds.add(userFeed);
            
            List<Map> categories = (List<Map>)feed.get("categories");
            for (Map category: categories) {
                String title = (String) category.get("label");
                if (!categoryMap.containsKey(title)) {
                    FeedCategory feedCategory = new FeedCategory();
                    feedCategory.name = title;
//                    feedCategory.user = user;
                    categoryMap.put(title, feedCategory);
                }
                FeedCategory feedCategory = categoryMap.get(title);
                feedCategory.userFeeds.add(userFeed);
            }
        }
        for (FeedCategory feedCategory : categoryMap.values()) {
            feedCategory.create();
            user.feedCategories.add(feedCategory);
        }
        user.update();
    }
    
    private static String[] loginGoogle(String account, String password) {
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
            String responseStr = EntityUtils.toString(response.getEntity());
            String[] responseComponents = responseStr.split("\n");
            String[] results = new String[2];
            results[0] = responseComponents[2].split("=")[1];
            results[1] = responseComponents[0].split("=")[1];
            return results;
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
