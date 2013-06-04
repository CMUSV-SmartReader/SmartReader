package models;


import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity
public class Feed extends MongoModel {
    
    @Id
    public ObjectId id;
    
    public String title;
    
    public String type;
    
    public String xmlUrl;
    
    public String htmlUrl;
    
    public String author;
    
}
