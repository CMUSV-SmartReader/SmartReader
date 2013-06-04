package models;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.bson.types.ObjectId;

public class UserFeed extends MongoModel {
    
    ObjectId id;
    
    @ManyToOne
    public User user;
    
    @OneToOne
    public Feed feed;
}
