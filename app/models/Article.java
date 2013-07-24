package models;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;

import scala.util.Random;
import twitter4j.Status;
import util.ReaderDB;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;

import facebook4j.Post;

@Entity
public class Article extends MongoModel {

    @Id
    public ObjectId id;

    @Reference
    public Feed feed;

    @Reference
    public SNSProvider provider;

    public String title;

    public String desc;

    public String contents = "";

    public String link;

    public Date publishDate;

    public Date updateDate;

    public String author;

    public boolean isRead;

    public int popularity;

    public Long twitterStatusId;

    public String facebookPostId;

    public List<String> categories = new ArrayList<String>();

    @Reference(lazy=true)
    public List<Article> dups = new ArrayList<Article>();

    @Reference(lazy=true)
    public List<Article> recommends = new ArrayList<Article>();

    public Article(SyndEntry entry) {
        Random rand = new Random();
        this.title = entry.getTitle();
        this.link = entry.getLink();
        this.desc = entry.getDescription() != null ? entry.getDescription().getValue() : null;
        if (entry.getContents() != null) {
            for (Object contentObj : entry.getContents()) {
                SyndContentImpl content = (SyndContentImpl)contentObj;
                contents += content.getValue();
            }
        }
        if (entry.getCategories() != null) {
            for (Object categoryObject : entry.getCategories()) {
                SyndCategoryImpl category = (SyndCategoryImpl)categoryObject;
                categories.add(category.getName());
            }
        }
        this.popularity = rand.nextInt(5) + 1;
        this.publishDate = entry.getPublishedDate();
        this.updateDate = entry.getUpdatedDate();
        this.author = entry.getAuthor();
    }

    public Article() {
    }

    public Article(Status status) {
        this.desc = status.getText();
        this.author = status.getSource();
        this.publishDate = status.getCreatedAt();
        this.link = status.getURLEntities()[0].getURL();
        this.twitterStatusId = status.getId();
    }

    public Article(Post post) {
        this.title = post.getCaption();
        this.desc = post.getMessage();
        this.author = post.getSource().toString();
        this.contents = post.getStory();
        this.publishDate = post.getCreatedTime();
        this.updateDate = post.getUpdatedTime();
        this.link = post.getLink().toString();
        this.facebookPostId = post.getId();
    }

    public Article(DBObject articleDB) {
        this.id = (ObjectId) articleDB.get("_id");
        if (articleDB.get("title") != null) {
            this.title = articleDB.get("title").toString();
        }
        if (articleDB.get("link") != null) {
            this.link = articleDB.get("link").toString();
        }
        if (articleDB.get("desc") != null) {
            this.desc = articleDB.get("desc").toString();
        }
        this.author = articleDB.get("author").toString();
        if (articleDB.get("publishDate") != null) {
            this.publishDate = (Date) articleDB.get("publishDate");
        }
        if (articleDB.get("updateDate") != null) {
            this.updateDate = (Date) articleDB.get("updateDate");
        }
    }

    public void loadFeed(DBObject articleDB) {
        DBRef feedRef = (DBRef) articleDB.get("feed");
        DBObject feedObj = feedRef.fetch();
        if (feedObj != null) {
            this.feed = new Feed(feedRef.fetch());
        }
    }

    public void loadRecommendation(DBObject articleDB) {
        BasicDBList recommeds = (BasicDBList) articleDB.get("recommends");
        if (recommeds != null) {
            for (int i = 0; i < recommeds.size(); i++) {
                DBRef ref = (DBRef) recommeds.get(i);
                DBObject recommendDB = ref.fetch();
                Article recommend = new Article(recommendDB);
                recommend.loadFeed(recommendDB);
                this.recommends.add(recommend);
            }
        }
    }

    public void loadIsRead(User user) {
        UserArticle userArticle = UserArticle.getUserArticle(user, this);
        if (userArticle == null) {
            this.isRead = false;
        }
        else {
            this.isRead = userArticle.isRead;
        }
    }

    public void loadDups(DBObject articleDB) {
        BasicDBList dups = (BasicDBList) articleDB.get("dups");
        if (dups != null) {
            for (int i = 0; i < dups.size(); i++) {
                DBRef ref = (DBRef) dups.get(i);
                DBObject dupDB = ref.fetch();
                Article dup = new Article(dupDB);
                dup.loadFeed(dupDB);
                this.dups.add(dup);
            }
        }
    }

    public void addRecommendation(Article article) {
        if (!article.id.equals(this.id)) {
            this.recommends.add(article);
            this.update();
        }
    }

    public void addDups(Article article) {
        if (!article.id.equals(this.id)) {
            this.dups.add(article);
            this.update();
        }
    }

    public boolean isReadBy(User user) {
        return false;
    }

    public Article createUnique(){
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("link", this.link);
        Article existingArticle = Article.existingArticle(condition);
        if (existingArticle != null){
            return existingArticle;
        }
        else {
            super.create();
            return this;
        }
    }

    public Article createTwitterArticle(SNSProvider provider) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("twitterStatusId", this.twitterStatusId);
        condition.put("provider.$id", provider.id);
        Article existingArticle = Article.existingArticle(condition);
        if (existingArticle != null){
            return null;
        }
        else {
            this.provider = provider;
            super.create();
            return this;
        }
    }

    public Article createFacebookArticle(SNSProvider provider) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("facebookPostId", this.facebookPostId);
        condition.put("provider.$id", provider.id);
        Article existingArticle = Article.existingArticle(condition);
        if (existingArticle != null){
            return null;
        }
        else {
            this.provider = provider;
            super.create();
            return this;
        }
    }

    public static Article existingArticle(HashMap<String, Object>condition){
        DBCollection collection = ReaderDB.getArticleCollection();
        BasicDBObject query = new BasicDBObject();
        query.putAll(condition);
        DBObject entityDB = collection.findOne(query);
        return entityDB != null ? new Article(entityDB) : null;
    }

    public static class Serializer implements JsonSerializer<Article> {
        @Override
        public JsonElement serialize(Article src, Type type,
                JsonSerializationContext ctx) {
            JsonObject article = new JsonObject();
            article.add("id", new JsonPrimitive(src.id.toString()));
            if (src.title != null) {
                article.add("title", new JsonPrimitive(src.title));
            }
            if (src.desc != null) {
                article.add("desc", new JsonPrimitive(src.desc));
            }
            else {
                article.add("desc", new JsonPrimitive(""));
            }
            if (src.link != null) {
                article.add("link", new JsonPrimitive(src.link));
            }
            else {
                article.add("link", new JsonPrimitive(""));
            }
            if (src.publishDate != null) {
                article.add("publishDate", ctx.serialize(src.publishDate));
            }
            if (src.updateDate != null) {
                article.add("updateDate", ctx.serialize(src.updateDate));
            }
            if (src.author != null) {
                article.add("author", new JsonPrimitive(src.author));
            }
            else {
                article.add("author", new JsonPrimitive(""));
            }
            if (src.feed != null) {
                article.add("feed", ctx.serialize(src.feed));
            }
            else {
                article.add("feed", new JsonPrimitive(""));
            }
            if (src.contents != null) {
                article.add("contents", new JsonPrimitive(src.contents));
            }
            else {
                article.add("content", new JsonPrimitive(""));
            }
            article.add("categories", ctx.serialize(src.categories));
            article.add("isRead", new JsonPrimitive(src.isRead));
            return article;
        }
    }

    public void increasePopularity() {
        this.popularity += 1;
        this.update();
    }

}
