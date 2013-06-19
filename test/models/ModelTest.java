package models;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import util.MorphiaObject;
import util.SmartReaderUtils;

import com.google.gson.Gson;

public class ModelTest {

    @Before
    public void setUp() {
        MorphiaObject.setUp();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testList() {
        List<Feed> feeds = (List<Feed>) MongoModel.all(Feed.class);
        for (Feed feed : feeds) {
            System.out.println(feed.title);
        }
    }

    @Test
    public void testFeed() {
    }

    @Test
    public void testFeedArticle() {
        User user = User.findByEmail("seanlionheart@gmail.com");
        List<FeedCategory> userCategories = user.feedCategories;
        for (FeedCategory feedCategory : userCategories) {
            for (UserFeed userFeed : feedCategory.userFeeds) {
                System.out.println(userFeed.feed.xmlUrl);
                for (Article article : userFeed.feed.articles) {
                    System.out.println(article.title);
                }
            }
        }
    }

    @Test
    public void getUserCategory() {
        User user = User.findByEmail("seanlionheart@gmail.com");
        List<FeedCategory> userCategories = user.feedCategories;
        for (FeedCategory userCategory : userCategories) {
            for (UserFeed userFeed : userCategory.userFeeds) {
            }
        }
    }

    @Test
    public void getUserCategoryTest() {
        long time = System.currentTimeMillis();
        User user = User.findByEmail("seanlionheart@gmail.com");
        Gson gson = SmartReaderUtils.builder.create();
        System.out.println(gson.toJson(user.allFeedCategoriesWithFeed()));
        System.out.println(System.currentTimeMillis() - time);
    }

    @Test
    public void getFeed() {
        List<Feed> feeds = (List<Feed>) MongoModel.all(Feed.class);
        for (Feed feed : feeds) {
            System.err.println(feed.id.toString());
            List<Article> articles = Feed.articlesInFeed(feed.id.toString());
            Gson gson = SmartReaderUtils.builder.create();
            gson.toJson(articles);
            for (Article article : articles) {
              System.out.println(article.title);
            }
        }
    }

    @Test
    public void importDups() {
        List<Article> allArticles = (List<Article>) MongoModel.all(Article.class);
        Random random = new Random();
        for (Article article : allArticles) {
            for (int i = 0; i < 5; i++) {
                int index = random.nextInt(allArticles.size());
                article.addDups(allArticles.get(index));
            }
        }
    }

    @Test
    public void importRecommend() {
        List<Article> allArticles = (List<Article>) MongoModel.all(Article.class);
        Random random = new Random();
        for (Article article : allArticles) {
            for (int i = 0; i < 5; i++) {
                int index = random.nextInt(allArticles.size());
                article.addRecommendation(allArticles.get(index));
            }
        }
    }
}
