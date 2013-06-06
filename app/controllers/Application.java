package controllers;

import java.io.IOException;

import models.FeedCategory;
import models.User;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;

import play.*;
import play.mvc.*;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import util.SmartReaderUtils;
import views.html.*;

public class Application extends Controller {


    // public static Result index() {
    //     return ok(main.render());
    // }

    /**
     * This action only gets called if the user is logged in.
     *
     * @return
     */
    @SecureSocial.SecuredAction
    public static Result index() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        return ok(main.render());
    }

    @SecureSocial.UserAwareAction
    public static Result userAware() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        final String userName = user != null ? user.fullName() : "guest";
        return ok("Hello " + userName + ", you are seeing a public page");
    }

    // @SecureSocial.SecuredAction(ajaxCall = true)
    // public static Result ajaxCall() {
    //     // return some json
    //     return null;
    // }
    
    // example for twitter
    @SecureSocial.SecuredAction( authorization = WithProvider.class, params = {"twitter"})
    public static Result onlyTwitter() {
        return ok("You are seeing this because you logged in using Twitter");
    }

    public static Result getFeeds() throws JsonGenerationException, JsonMappingException, IOException {
        Identity identity = SecureSocial.currentUser();
        User user = User.findByEmail(identity.email().get());
        return ok(new ObjectMapper().writeValueAsString(user.feeds));
    }

    public static Result getFeed(String id) throws JsonGenerationException, JsonMappingException, IOException {
        Identity identity = SecureSocial.currentUser();
        User user = User.findByEmail(identity.email().get());
        return ok(new ObjectMapper().writeValueAsString(user.feeds));
    }

    public static Result getCategories() throws JsonGenerationException, JsonMappingException, IOException {
        Identity identity = SecureSocial.currentUser();
        User user = User.findByEmail("seanlionheart@gmail.com");
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(user.feedCategories));
    }

    public static Result getCategory(String categoryId) throws JsonGenerationException, JsonMappingException, IOException {
        return ok(new ObjectMapper().writeValueAsString(FeedCategory.find(categoryId)));
    }

}
