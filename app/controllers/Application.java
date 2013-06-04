package controllers;

import models.User;
import play.*;
import play.api.libs.json.JacksonJson;
import play.mvc.*;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
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
    

}
