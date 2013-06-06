import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.SerializationConfig;

import models.Article;
import models.Feed;
import models.FeedCategory;
import models.UserFeed;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import util.MorphiaObject;
import util.SmartReaderUtils;

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import com.google.gson.GsonBuilder;

public class Global extends GlobalSettings {

    @Override
    public void onStart(play.Application arg0) {
        super.beforeStart(arg0);
        initJsonSerializer();
        MorphiaLoggerFactory.reset();
        MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
        MorphiaObject.setUp();
    }
    
    private void initJsonSerializer() {
        SmartReaderUtils.builder.registerTypeAdapter(FeedCategory.class, new FeedCategory.Serializer());
        SmartReaderUtils.builder.registerTypeAdapter(UserFeed.class, new UserFeed.Serializer());
        SmartReaderUtils.builder.registerTypeAdapter(Feed.class, new Feed.Serializer());
        SmartReaderUtils.builder.registerTypeAdapter(Article.class, new Article.Serializer());
    }
}
