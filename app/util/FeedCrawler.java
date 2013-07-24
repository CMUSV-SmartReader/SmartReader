package util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import models.Feed;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;

public class FeedCrawler {

    private final List<Feed> feeds = new ArrayList<Feed>();

    public FeedCrawler() {
    }

    public void addFeed(Feed feed) {
        feeds.add(feed);
    }

    public void run() {
        Akka.system().scheduler().scheduleOnce(
                Duration.create(0, TimeUnit.SECONDS),
                    new Runnable() {
                        @Override
                        public void run() {
                            for (Feed feed : feeds) {
                                try {
                                    feed.crawl();
                                    Logger.info("parse[" + feed.xmlUrl + "] success");
                                } catch (Exception e) {
                                    Logger.warn("parse [" + feed.xmlUrl + "] fail : "
                                            + e.getMessage());
                                }
                            }
                        }
                    }, Akka.system().dispatcher());

    }
}
