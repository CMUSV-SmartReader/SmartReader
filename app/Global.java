import java.util.concurrent.TimeUnit;

import models.Feed;

import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import util.MorphiaObject;

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;

public class Global extends GlobalSettings {

    @Override
    public void onStart(play.Application arg0) {
        super.beforeStart(arg0);
        initJsonSerializer();
        MorphiaLoggerFactory.reset();
        MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
        MorphiaObject.setUp();
        
        Akka.system().scheduler().schedule(
                Duration.create(0, TimeUnit.SECONDS),
                Duration.create(10, TimeUnit.MINUTES),
                new Runnable() {
                  public void run() {
                      Logger.debug("Start Crawling");
                      Feed.crawAll();
                  }
                },
                Akka.system().dispatcher()
              ); 
        
    }

    private void initJsonSerializer() {

    }
}
