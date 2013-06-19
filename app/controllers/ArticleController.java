package controllers;

import java.util.List;

import models.Article;
import models.MongoModel;
import play.mvc.Controller;
import play.mvc.Result;
import util.SmartReaderUtils;

import com.google.gson.Gson;

public class ArticleController extends Controller {

    public static Result getDuplicatedArticles(String id) {
        Article article = MongoModel.find(id, Article.class);
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(article.dups));
    }

    public static Result getRecommendedArticles(String id) {
        Article article = MongoModel.find(id, Article.class);
        Gson gson = SmartReaderUtils.builder.create();
//        article.loadDups(articleDB)
        return ok(gson.toJson(article.dups));
    }

    public static Result allArticles() {
        List<Article> articles = (List<Article>) MongoModel.all(Article.class, 12);
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(articles));
    }

}
