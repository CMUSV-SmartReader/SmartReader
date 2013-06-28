package controllers;

import models.Article;
import models.MongoModel;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import util.SmartReaderUtils;

import com.google.gson.Gson;
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
        User user = SmartReaderUtils.getCurrentUser();
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(user.userArticles()));
    }

}
