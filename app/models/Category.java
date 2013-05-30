package models;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;

@Entity
public class Category extends MongoModel {

    public ObjectId id;
    
    public String name;
}
