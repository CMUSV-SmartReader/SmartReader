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

import java.util.Arrays;

import models.SecurityRole;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;

import controllers.routes;

import play.Application;
import play.GlobalSettings;
import play.mvc.Call;

public class Global extends GlobalSettings {

    @Override
    public void onStart(play.Application arg0) {
        PlayAuthenticate.setResolver(new Resolver() {

            @Override
            public Call login() {
                // Your login page
                return routes.Application.login();
            }

            @Override
            public Call afterAuth() {
                // The user will be redirected to this page after authentication
                // if no original URL was saved
                return routes.Application.index();
            }

            @Override
            public Call afterLogout() {
                return routes.Application.index();
            }

            @Override
            public Call auth(final String provider) {
                // You can provide your own authentication implementation,
                // however the default should be sufficient for most cases
                return com.feth.play.module.pa.controllers.routes.Authenticate
                        .authenticate(provider);
            }

            @Override
            public Call askMerge() {
                return routes.Account.askMerge();
            }

            @Override
            public Call askLink() {
                return routes.Account.askLink();
            }

            @Override
            public Call onException(final AuthException e) {
                if (e instanceof AccessDeniedException) {
                    return routes.Signup
                            .oAuthDenied(((AccessDeniedException) e)
                                    .getProviderKey());
                }

                // more custom problem handling here...
                return super.onException(e);
            }
        });
        initialData();

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

    private void initialData() {
        if (SecurityRole.find.findRowCount() == 0) {
            for (final String roleName : Arrays
                    .asList(controllers.Application.USER_ROLE)) {
                final SecurityRole role = new SecurityRole();
                role.roleName = roleName;
                role.save();
            }
        }
    }
}
