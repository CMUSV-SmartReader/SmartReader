package util;

import java.util.List;

import models.User;

import org.junit.Test;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Post;
import facebook4j.ResponseList;

public class TwitterTest {

    @Test
    public void testTwitter() throws TwitterException {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
          .setOAuthConsumerKey("BploO8qFE4tWwmdNNsE1g")
          .setOAuthConsumerSecret("lpcBV30Wrr1dN1RO9ehxYDhfrGUkJjqa7V0idWKcoM")
          .setOAuthAccessToken("326628553-Ih2GjcjgLvB97BWc2JSmtABN82MflC2U48mbmnsD")
          .setOAuthAccessTokenSecret("PcHv0cjZPmfw6K4z0wgFaU05tVVXwJOjQRDtL6xaR4");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        List<Status> statuses = twitter.getHomeTimeline();
        for (Status status : statuses) {
            System.out.println(status.getSource());
            System.out.println(status.getUser().getName() + ":" +
                               status.getText());
        }

    }

    @Test
    public void testParseTwitter() {
        ReaderDB.setUp();
        User user = User.findByEmail("clyde1008li@gmail.com");
        user.crawlTwitter();
    }

    @Test
    public void testFacebook() throws FacebookException {
        ReaderDB.setUp();
        User user = User.findByEmail("seanlionheart@gmail.com");
        Facebook facebook = user.getFacebook();
        ResponseList<Post> feeds = facebook.getHome();
        for (Post post : feeds) {
            System.out.println(post.getCaption());
            System.out.println(post.getType());
            System.out.println(post.getMessage());
        }
    }
}
