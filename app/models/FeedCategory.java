package models;

import java.util.ArrayList;
import java.util.List;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;


import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;

import util.MorphiaObject;

@Entity
public class FeedCategory extends MongoModel {

    @Id
    public ObjectId id;
    
    public String name;
    
    @Reference(lazy = true)
    @JsonIgnore
    public User user;
    
    @Reference(concreteClass = ArrayList.class)
    @JsonIgnore
    public List<UserFeed> userFeeds = new ArrayList<UserFeed>();
    
    public static FeedCategory find(String categoryId) {
        return MorphiaObject.datastore.get(FeedCategory.class, categoryId);
    }
}
