package models;

import org.bson.types.ObjectId;

import play.data.validation.Constraints.Required;

import com.google.code.morphia.annotations.Entity;

@Entity
public class Article {

    public ObjectId id;
    
    @Required
    public String title;
    
    public String summary;
}
