package models;

import org.bson.types.ObjectId;

public class User {

    public ObjectId id;
    
    public String title;
    
    public User() {
    	id = new ObjectId();
    	title = "user title";
    }
}
