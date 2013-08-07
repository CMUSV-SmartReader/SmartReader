package util;

import models.User;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.google.gson.GsonBuilder;

import facebook4j.Facebook;
import facebook4j.FacebookFactory;

public class SmartReaderUtils {

    public static User getCurrentUser() {
        Identity identity = SecureSocial.currentUser();
        return User.findByEmail(identity.email().get());
    }

    public static GsonBuilder builder = new GsonBuilder();

    private static Twitter twitter;

    private static Facebook facebook;

    public static String getTwitterKey() {
        String key = System.getenv("TWITTER_KEY");
        return key != null ? key : "BploO8qFE4tWwmdNNsE1g";
    }

    public static String getTwitterSecret() {
        String secret = System.getenv("TWITTER_SECRET");
        return secret != null ? secret : "lpcBV30Wrr1dN1RO9ehxYDhfrGUkJjqa7V0idWKcoM";
    }

    public static String getFacebookKey() {
        String key = System.getenv("FACEBOOK_KEY");
        return key != null ? key : "421655734614850";
    }

    public static String getFacebookSecret() {
        String secret = System.getenv("FACEBOOK_SECRET");
        return secret != null ? secret : "4466551b2b5a71f8bc0ea17e0a5f8835";
    }

    public static Twitter getTwitter() {
        if (twitter == null) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setDebugEnabled(true);
            builder.setOAuthConsumerKey(getTwitterKey());
            builder.setOAuthConsumerSecret(getTwitterSecret());
            Configuration configuration = builder.build();
            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();
        }
        return twitter;
    }

    public static Facebook getFacebook() {
        if (facebook == null) {
            facebook = new FacebookFactory().getInstance();
            facebook.setOAuthAppId(getFacebookKey(), getFacebookSecret());
            facebook.setOAuthPermissions("read_stream");
        }
        return facebook;
    }


}
