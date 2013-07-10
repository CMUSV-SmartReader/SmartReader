package util;

import java.util.ArrayList;
import java.util.List;

import models.Feed;
import play.Logger;

public class FeedCrawler implements Runnable {

    private final List<Feed> feeds = new ArrayList<Feed>();

    public FeedCrawler() {
    }

    public void addFeed(Feed feed) {
        feeds.add(feed);
    }

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
}
