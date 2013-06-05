package util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

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
            Feed feedEntity = new Feed();
            feedEntity.title = (String)feed.get("title");
            feedEntity.htmlUrl = (String)feed.get("htmlUrl");
            feedEntity.xmlUrl = ((String)feed.get("id")).substring("feed/".length());
            feedEntity.create();
            user.feeds.add(feedEntity);
            List<Map> categories = (List<Map>)feed.get("categories");
            for (Map category: categories) {
                String title = (String) category.get("label");
                if (!categoryMap.containsKey(title)) {
                    FeedCategory feedCategory = new FeedCategory();
                    feedCategory.name = title;
                    feedCategory.user = user;
                    categoryMap.put(title, feedCategory);
                }
                FeedCategory feedCategory = categoryMap.get(title);
                feedCategory.feeds.add(feedEntity);
            }
        }
        for (FeedCategory feedCategory : categoryMap.values()) {
            feedCategory.create();
            user.feedCategories.add(feedCategory);
        }
        user.create();
    }
    
    
    private static void storeData(User user, List<Map<String, String>> dataList) {
        MorphiaObject.setUp();
        FeedCategory feedCategory = null;
        for (Map<String, String> map : dataList) {
            if (!map.containsKey("xmlUrl")) {
                if (feedCategory != null) {
                    feedCategory.create();
                    user.feedCategories.add(feedCategory);
                }
                feedCategory = new FeedCategory();
                feedCategory.name = map.get("title");
                feedCategory.user = user;
            }
            else {
                Feed feed = new Feed();
                feed.htmlUrl = map.get("htmlUrl");
                feed.xmlUrl = map.get("xmlUrl");
                feed.title = map.get("title");
                feed.type = map.get("type");
                feed.users.add(user);
                feed.create();
                user.feeds.add(feed);
                UserFeed userFeed = new UserFeed();
                userFeed.user = user;
                userFeed.feed = feed;
                userFeed.create();
                feedCategory.feeds.add(feed);
            }
        }
        if (feedCategory != null) {
            feedCategory.create();
        }
        user.update();
    }
    
    public static void importWithEmail(User user, InputSource inputSource) {
        
        OPMLParser mRSSHandler = new OPMLParser();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            xmlReader.setContentHandler(mRSSHandler);
            xmlReader.parse(inputSource);
            List<Map<String, String>> dataList = mRSSHandler.getOutlineDataList();
            storeData(user, dataList);
        } catch (SAXException e) {
            
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
