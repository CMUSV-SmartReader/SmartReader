package models;

import java.lang.reflect.Type;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Entity
public class ArticleCategory extends MongoModel {

    @Id
    public ObjectId id;

    public String name;

    public String imgLink;

    @Reference(lazy=true)
    public User user;

    public static class Serializer implements JsonSerializer<ArticleCategory> {

        @Override
        public JsonElement serialize(ArticleCategory src, Type type,
                JsonSerializationContext ctx) {
            JsonObject articleCategoryObj = new JsonObject();
            articleCategoryObj.add("id", new JsonPrimitive(src.id.toString()));
            articleCategoryObj.add("name", new JsonPrimitive(src.name));
            articleCategoryObj.add("name", new JsonPrimitive(src.imgLink));
            return articleCategoryObj;
        }

    }
}
