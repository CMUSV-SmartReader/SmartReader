package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Article;
import models.MongoModel;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import util.ReaderDB;
import util.SmartReaderUtils;

import com.google.gson.Gson;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class ArticleController extends Controller {

    public static Result getDuplicatedArticles(String id) {
        DBObject articleDB = MongoModel.findDbObject(id, Article.class);
        Article article = new Article(articleDB);
        article.loadDups(articleDB);
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(article.dups));
    }

    public static Result getRecommendedArticles(String id) {
        DBObject articleDB = MongoModel.findDbObject(id, Article.class);
        Article article = new Article(articleDB);
        article.loadRecommendation(articleDB);
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(article.recommends));
    }

    public static Result read(String id) {
        Article article = MongoModel.find(id, Article.class);
        User user = SmartReaderUtils.getCurrentUser();
        user.read(article);
        return ok();
    }

    public static Result unread(String id) {
        Article article = MongoModel.find(id, Article.class);
        User user = SmartReaderUtils.getCurrentUser();
        user.unread(article);
        return ok();
    }

    public static Result allArticles() {
        List<Article> articles = new ArrayList<Article>();
        DBCollection collection = ReaderDB.getArticleCollection();
        DBCursor cursor = collection.find();
        int i = 0;
        while (cursor.hasNext() && i++ < 12) {
            DBObject object = cursor.next();
            Article article = new Article(object);
            article.loadFeed(object);
            articles.add(article);
        }
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(articles));
    }

}
