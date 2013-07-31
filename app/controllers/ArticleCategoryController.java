package controllers;

import java.util.Date;
import java.util.List;

import models.Article;
import models.ArticleCategory;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import util.SmartReaderUtils;

import com.google.gson.Gson;

public class ArticleCategoryController extends Controller {

    public static Result categoryArticles(String id) {
        String publishDateStr = request().getQueryString("publishDate");
        Gson gson = SmartReaderUtils.builder.create();
        if (publishDateStr == null) {
            List<Article> articles = ArticleCategory.articlesInCategory(id);
            return ok(gson.toJson(articles));
        }
        else {
            long publishDateLong = Long.parseLong(publishDateStr);
            Date publishDate = new Date(publishDateLong);
            List<Article> articles = ArticleCategory.articlesInCategory(id, publishDate);
            return ok(gson.toJson(articles));
        }
    }

    public static Result allCategories() {
        Gson gson = SmartReaderUtils.builder.create();
        User user = SmartReaderUtils.getCurrentUser();
        return ok(gson.toJson(user.allCategories()));
    }
}
