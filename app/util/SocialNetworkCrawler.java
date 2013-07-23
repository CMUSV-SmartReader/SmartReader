package util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import models.User;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;

public class SocialNetworkCrawler {

    private final List<User> users = new ArrayList<User>();

    public void addUser(User user) {
        users.add(user);
    }

    public void run() {
        Akka.system().scheduler().scheduleOnce(
            Duration.create(0, TimeUnit.SECONDS),
                new Runnable() {
                    @Override
                    public void run() {
                        for (User user : users) {
                            Logger.debug("Start Crawling twitter for " + user.email);
                            user.crawlTwitter();
                        }
                    }
                }, Akka.system().dispatcher());
    }
}
