import java.util.List;
import java.util.concurrent.TimeUnit;

import models.Feed;
import models.MongoModel;
import models.User;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import util.FeedCrawler;
import util.ReaderDB;
import util.SocialNetworkCrawler;

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;

public class Global extends GlobalSettings {

    private static final int THREAD_COUNT = 5;

    @Override
    public void onStart(play.Application arg0) {
        super.beforeStart(arg0);
        MorphiaLoggerFactory.reset();
        MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
        ReaderDB.setUp();

        Akka.system().scheduler().schedule(Duration.create(0, TimeUnit.SECONDS),
                Duration.create(10, TimeUnit.MINUTES),
            new Runnable() {
                @SuppressWarnings("unchecked")
                @Override
                public void run() {
                    List<User> allUsers = (List<User>) MongoModel.all(User.class);
                    List<Feed> allFeeds = (List<Feed>) MongoModel.all(Feed.class);

                    int userIndex = 0;
                    for (int i = 0; i < THREAD_COUNT; i++) {
                        try {
                            SocialNetworkCrawler crawler = new SocialNetworkCrawler();
                            for (; userIndex < (i + 1) * allUsers.size() / THREAD_COUNT && userIndex < allUsers.size(); userIndex++) {
                                crawler.addUser(allUsers.get(userIndex));
                            }
                            crawler.run();
                        } catch (Exception e) {
                            Logger.debug("Exception Happens in Social Network " + e.getMessage());
                        }
                    }

                    int feedIndex = 0;
                    for (int i = 0; i < THREAD_COUNT; i++) {
                        try {
                            FeedCrawler crawler = new FeedCrawler();
                            for (; feedIndex < (i + 1) * allFeeds.size() / THREAD_COUNT && feedIndex < allFeeds.size(); feedIndex++) {
                                crawler.addFeed(allFeeds.get(feedIndex));
                            }
                            crawler.run();
                        } catch (Exception e) {
                            Logger.debug("Exception Happens in Feed " + e.getMessage());
                        }
                    }
                }
            },
            Akka.system().dispatcher()
        );
    }

}
