package util;

import models.User;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;

import com.google.gson.GsonBuilder;

public class SmartReaderUtils {

    public static User getCurrentUser() {
        Identity identity = SecureSocial.currentUser();
        return User.findByEmail(identity.email().get());
    }

    public static GsonBuilder builder = new GsonBuilder();

}
