package models;


import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;

@Entity
public class UserFeed extends MongoModel {
    
    @Id
    public ObjectId id;
    
    @Reference
    public User user;
    
    @Reference
    public Feed feed;
}
