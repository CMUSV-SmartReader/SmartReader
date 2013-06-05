package models;


import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;

@Entity
public class UserFeed extends MongoModel {
    
    @Id
    public ObjectId id;
    
    @Reference(lazy = true)
    public User user;
    
    @Reference(lazy = true)
    public Feed feed;
}
