import java.util.concurrent.TimeUnit;

import models.Feed;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import util.ReaderDB;

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;

public class Global extends GlobalSettings {

    @Override
    public void onStart(play.Application arg0) {
        super.beforeStart(arg0);
        MorphiaLoggerFactory.reset();
        MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
        ReaderDB.setUp();

        Akka.system().scheduler().schedule(
                Duration.create(0, TimeUnit.SECONDS),
                Duration.create(10, TimeUnit.MINUTES),
                new Runnable() {
                  @Override
                public void run() {
                      Logger.debug("Start Crawling");
                      Feed.crawAll();
                  }
                },
                Akka.system().dispatcher()
              );
    }

}
