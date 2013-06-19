package models;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;

import util.MorphiaObject;
import util.SmartReaderUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public abstract class MongoModel {

    protected DBCollection getCollection() {
        String collectionName = this.getClass().getName();
        return SmartReaderUtils.db.getCollection(collectionName);
    }

    public void create() {
        MorphiaObject.datastore.save(this);
    }

    public void update() {
        MorphiaObject.datastore.save(this);
    }

    public MongoModel() {

    }

    public MongoModel(DBObject entityDB) {

    }
    public static boolean exists(HashMap<String, Object>condition, Class clazz){
        DBCollection collection = SmartReaderUtils.db.getCollection(clazz.getSimpleName());
        BasicDBObject query = new BasicDBObject();
        query.putAll(condition);
        DBObject entityDB = collection.findOne(query);
        if (entityDB != null) {
            try {
               return true;
            }
            catch (Exception e) {
            }
        }
        return false;
    }
    
    public static <T extends MongoModel> T find(String id, Class<T> clazz) {
        DBCollection collection = SmartReaderUtils.db.getCollection(clazz.getName());
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        DBObject entityDB = collection.findOne(query);
        if (entityDB != null) {
            try {
                Constructor<T> constructor = clazz.getConstructor(DBObject.class);
                return constructor.newInstance(entityDB);
            }
            catch (Exception e) {
            }
        }
        return null;
    }

    public static List<? extends MongoModel> all(Class<? extends MongoModel> klass, int limit) {
        if (MorphiaObject.datastore != null) {
            return MorphiaObject.datastore.find(klass).limit(limit).asList();
        } else {
            return new ArrayList<MongoModel>();
        }
    }

    public static List<? extends MongoModel> all(Class<? extends MongoModel> klass) {
        if (MorphiaObject.datastore != null) {
            return MorphiaObject.datastore.find(klass).asList();
        } else {
            return new ArrayList<MongoModel>();
        }
    }
}

