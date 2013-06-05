package models;


import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import play.libs.Scala;

import scala.Option;
import securesocial.core.AuthenticationMethod;
import securesocial.core.Identity;
import securesocial.core.OAuth1Info;
import securesocial.core.OAuth2Info;
import securesocial.core.PasswordInfo;
import util.MorphiaObject;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;

@Entity
public class User extends MongoModel {

    @Id
    public ObjectId id;
    
    public String lastName;

    public String userName;

    public String displayName;

    public String avatarUrl;

    @Indexed
    public String email;

    @Reference(concreteClass = ArrayList.class, lazy = true)
    public List<FeedCategory> feedCategories = new ArrayList<FeedCategory>();

    @Reference(concreteClass = ArrayList.class, lazy = true)
    public List<UserFeed> userFeeds = new ArrayList<UserFeed>();

    @Reference(concreteClass = ArrayList.class, lazy = true)
    public List<Feed> feeds = new ArrayList<Feed>();
    
    public static User findByEmail(String email) {
        return MorphiaObject.datastore.find(User.class).filter("email", email).get();
    }

    public User(Identity identity) {
        this.email = identity.email().get();
    }

    public User() {
    }

}
