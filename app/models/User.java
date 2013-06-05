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
import securesocial.core.UserId;
import util.MorphiaObject;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;

@Entity
public class User extends MongoModel implements Identity{

    @Id
    public ObjectId id;
    
    public String lastName;

    public String userName;

    public String displayName;

    public String avatarUrl;

    @Indexed
    public String email;
    
    @Override
    public securesocial.core.UserId id() {
    	return null;
    };
    
    @Override
    public String firstName() {
    	// TODO Auto-generated method stub
    	return null;
    }
    
    @Override
    public String lastName() {
    	// TODO Auto-generated method stub
    	return null;
    }
    
    @Override
    public String fullName() {
    	// TODO Auto-generated method stub
    	return null;
    }
    
    @Override
    public Option<PasswordInfo> passwordInfo() {
    	// TODO Auto-generated method stub
    	return null;
    }
    
    @Override
    public Option<String> avatarUrl() {
    	return Scala.Option(avatarUrl);
    }
    
    @Override
    public Option<String> email() {
    	return Scala.Option(email);
    }
    
    @Override
    public AuthenticationMethod authMethod() {
    	// TODO Auto-generated method stub
    	return null;
    }
    
    @Override
    public Option<OAuth1Info> oAuth1Info() {
    	// TODO Auto-generated method stub
    	return null;
    }
    
    @Override
    public Option<OAuth2Info> oAuth2Info() {
    	// TODO Auto-generated method stub
    	return null;
    }

    @Reference(concreteClass = ArrayList.class, lazy = true)
    public List<FeedCategory> feedCategories = new ArrayList<FeedCategory>();

    @Reference(concreteClass = ArrayList.class, lazy = true)
    public List<UserFeed> userFeeds = new ArrayList<UserFeed>();

    public static User findByEmail(String email) {
        return MorphiaObject.datastore.find(User.class).filter("email", email).get();
    }

    public User(Identity identity) {
        this.email = identity.email().get();
    }

    public User() {
    }

}
