package models;

import java.util.List;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;


import org.bson.types.ObjectId;

@Entity
public class UserCategory extends MongoModel {

    @Id
    public ObjectId id;
    
    public String name;
    
    @Reference
    public User user;
    
    @Reference
    public List<Feed> feeds;
    
}
