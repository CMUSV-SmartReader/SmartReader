package models;


import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;

@Entity
public class Feed extends MongoModel {
    
    @Id
    public ObjectId id;
    
    public String title;
    
    public String type;
    
    public String xmlUrl;
    
    public String htmlUrl;
    
    @Reference(lazy = true)
    public List<User> users = new ArrayList<User>();
}
