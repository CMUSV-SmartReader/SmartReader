package models;


import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import play.Logger;
import util.FeedParser;
import util.MorphiaObject;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;

@Entity
public class Feed extends MongoModel {
    
    @Id
    public ObjectId id;
    
    public String title;
    
    public String type;
    
    public String xmlUrl;
    
    public String htmlUrl;
    
    public String author;
    

    public DateTime lastAccessedTime; 
    
    @Reference(lazy = true)
    public List<User> users = new ArrayList<User>();
    
    public void crawl() throws Exception{
    	//if being parsed 30 minutes ago, ignore it. 
    	if(lastAccessedTime.plusMinutes(30).isAfterNow()) return;
    	
    	lastAccessedTime = DateTime.now();
    	List<Article> articles = FeedParser.parseFeed(this);
    	for (Article article : articles) {
    		article.setReference(this.getClass(), this.id);
            article.create();
        }
        return;
    }
    
    public static void crawAll(){
    	MorphiaObject.setUp();
    	List<Feed> feeds = (List<Feed>) MongoModel.all(Feed.class);
        for (Feed feed : feeds) {
        	 try {
				feed.crawl();
				Logger.debug("parse[" + feed.xmlUrl + "] success");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Logger.warn("parse [" + feed.xmlUrl + "] fail : " + e.getMessage());
			}
        }
    }

}
