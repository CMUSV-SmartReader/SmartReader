package util;

import models.User;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.google.gson.GsonBuilder;

public class SmartReaderUtils {

    public static User getCurrentUser() {
        Identity identity = SecureSocial.currentUser();
        return User.findByEmail(identity.email().get());
    }

    public static GsonBuilder builder = new GsonBuilder();

    private static Twitter twitter;

    public static Twitter getTwitter() {

        if (twitter == null) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setDebugEnabled(true);
            builder.setOAuthConsumerKey("BploO8qFE4tWwmdNNsE1g");
            builder.setOAuthConsumerSecret("lpcBV30Wrr1dN1RO9ehxYDhfrGUkJjqa7V0idWKcoM");
            Configuration configuration = builder.build();
            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();
        }
        return twitter;
    }

}
