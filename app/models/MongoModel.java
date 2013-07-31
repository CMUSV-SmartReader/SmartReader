package models;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import util.ReaderDB;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public abstract class MongoModel {

    protected DBCollection getCollection() {
        String collectionName = this.getClass().getName();
        return ReaderDB.db.getCollection(collectionName);
    }

    public void create() {
        ReaderDB.datastore.save(this);
    }

    public void update() {
        ReaderDB.datastore.save(this);
    }

    public void delete() {
        ReaderDB.datastore.delete(this);
    }

    public MongoModel() {

    }

    public MongoModel(DBObject entityDB) {

    }

    public static <T extends MongoModel> T findEntity(String id, Class<T> clazz) {
        ObjectId objectId = new ObjectId(id);
        return ReaderDB.datastore.get(clazz, objectId);
    }

    public static <T extends MongoModel> T find(String id, Class<T> clazz) {
        DBObject entityDB = findDbObject(id, clazz);
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

    public static DBObject findDbObject(String id, Class<?> clazz) {
        DBCollection collection = ReaderDB.db.getCollection(clazz.getSimpleName());
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        return collection.findOne(query);
    }

    public static <T extends MongoModel> List<T> findAll(Class<T> clazz, int limit) {
        DBCollection collection = ReaderDB.db.getCollection(clazz.getSimpleName());
        DBCursor cursor = collection.find().limit(limit);
        List<T> models = new ArrayList<T>();
        try {
            Constructor<T> constructor = clazz.getConstructor(DBObject.class);
            while (cursor.hasNext()) {
                models.add(constructor.newInstance(cursor.next()));
            }

        } catch (Exception e) {
        }
        return models;
    }

    public static List<? extends MongoModel> all(Class<? extends MongoModel> klass, int limit) {
        if (ReaderDB.datastore != null) {
            return ReaderDB.datastore. find(klass).limit(limit).asList();
        } else {
            return new ArrayList<MongoModel>();
        }
    }

    public static List<? extends MongoModel> all(Class<? extends MongoModel> klass) {
        if (ReaderDB.datastore != null) {
            return ReaderDB.datastore.find(klass).asList();
        } else {
            return new ArrayList<MongoModel>();
        }
    }
}

