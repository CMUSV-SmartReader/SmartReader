package models;

import java.util.ArrayList;
import java.util.List;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;


import org.bson.types.ObjectId;

@Entity
public class FeedCategory extends MongoModel {

    @Id
    public ObjectId id;
    
    public String name;
    
    @Reference(lazy = true)
    public User user;
    
    @Reference(concreteClass = ArrayList.class)
    public List<UserFeed> userFeeds = new ArrayList<UserFeed>();
    
}
