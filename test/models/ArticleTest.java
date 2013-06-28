package models;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

public class ArticleTest {

    @Test
    public void getArticleFromCondition(){

        String link = "http://zreading.cn.feedsportal.com/c/35042/f/647833/s/2d5b084b/l/0L0Szreading0Bcn0Carchives0C3850A0Bhtml/story01.htm";
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("link", link);
        assertEquals("should be able to find the same article", true,Article.exists(condition, Article.class) );
    }
}
