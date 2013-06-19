package controllers;

import java.util.List;

import models.Article;
import models.MongoModel;
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

    public static Result allArticles() {
        List<Article> articles = (List<Article>) MongoModel.all(Article.class, 12);
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(articles));
    }

}
