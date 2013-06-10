package controllers;

import java.io.IOException;
import java.util.List;

import models.Article;
import models.Feed;
import models.FeedCategory;
import models.User;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import play.mvc.Controller;
import play.mvc.Result;

import util.GoogleReaderImporter;
import util.SmartReaderUtils;
import views.html.main;

import com.google.gson.Gson;


import java.text.SimpleDateFormat;
import java.util.Date;

import models.User;
import play.Routes;
import play.data.Form;
import play.mvc.*;
import play.mvc.Http.Response;
import play.mvc.Http.Session;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;

import views.html.*;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.user.AuthUser;

public class Application extends Controller {

    /**
     * 旧login的尸骨
     *
     * @return
     */
//    @SecureSocial.SecuredAction
//    public static Result index() {
//        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
//        return ok(main.render());
//    }
    
    public static final String FLASH_MESSAGE_KEY = "message";
    public static final String FLASH_ERROR_KEY = "error";
    public static final String USER_ROLE = "user";

    public static Result index() {
        return ok(index.render());
    }

    public static User getLocalUser(final Session session) {
        final AuthUser currentAuthUser = PlayAuthenticate.getUser(session);
        final User localUser = User.findByAuthUserIdentity(currentAuthUser);
        return localUser;
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result restricted() {
        final User localUser = getLocalUser(session());
        return ok(restricted.render(localUser));
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result profile() {
        final User localUser = getLocalUser(session());
        return ok(profile.render(localUser));
    }

    public static Result login() {
        return ok(login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM));
    }

    public static Result doLogin() {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        final Form<MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM
                .bindFromRequest();
        if (filledForm.hasErrors()) {
            // User did not fill everything properly
            return badRequest(login.render(filledForm));
        } else {
            // Everything was filled
            return UsernamePasswordAuthProvider.handleLogin(ctx());
        }
    }

    public static Result signup() {
        return ok(signup.render(MyUsernamePasswordAuthProvider.SIGNUP_FORM));
    }

    public static Result jsRoutes() {
        return ok(
                Routes.javascriptRouter("jsRoutes",
                        controllers.routes.javascript.Signup.forgotPassword()))
                .as("text/javascript");
    }

    public static Result doSignup() {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        final Form<MySignup> filledForm = MyUsernamePasswordAuthProvider.SIGNUP_FORM
                .bindFromRequest();
        if (filledForm.hasErrors()) {
            // User did not fill everything properly
            return badRequest(signup.render(filledForm));
        } else {
            // Everything was filled
            // do something with your part of the form before handling the user
            // signup
            return UsernamePasswordAuthProvider.handleSignup(ctx());
        }
    }

    public static String formatTimestamp(final long t) {
        return new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date(t));
    }

//    public static Result getFeeds() throws JsonGenerationException, JsonMappingException, IOException {
//        Identity identity = SecureSocial.currentUser();
//        User user = User.findByEmail(identity.email().get());
//        return ok(new ObjectMapper().writeValueAsString(user.feeds));
//    }
//
//    public static Result getFeed(String id) throws JsonGenerationException, JsonMappingException, IOException {
//        Feed feed = Feed.find(id);
//        Gson gson = SmartReaderUtils.builder.create();
//        return ok(gson.toJson(feed));
//    }
//
//    public static Result getCategories() throws JsonGenerationException, JsonMappingException, IOException {
//        Identity identity = SecureSocial.currentUser();
//        User user = User.findByEmail("seanlionheart@gmail.com");
//        Gson gson = SmartReaderUtils.builder.create();
//        return ok(gson.toJson(user.feedCategories));
//    }
//
//    public static Result getCategory(String categoryId) throws JsonGenerationException, JsonMappingException, IOException {
//        return ok(new ObjectMapper().writeValueAsString(FeedCategory.find(categoryId)));
//    }
//    
//    public static Result getUserArticles(String categoryId) throws JsonGenerationException, JsonMappingException, IOException{
//        Identity identity = SecureSocial.currentUser();
//        User user = User.findByEmail(identity.email().get());
//        List<Article> articles = FeedCategory.find(categoryId).articles;
//        return ok(new ObjectMapper().writeValueAsString(articles));
//    }
//    public static Result getUserArticles() throws JsonGenerationException, JsonMappingException, IOException{
//        Identity identity = SecureSocial.currentUser();
//        User user = User.findByEmail(identity.email().get());
//        return ok(new ObjectMapper().writeValueAsString(user.articles));
//    }
}
