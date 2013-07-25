package models;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity
public class ArticleCategory extends MongoModel {

    @Id
    public ObjectId id;

    public String name;
}
