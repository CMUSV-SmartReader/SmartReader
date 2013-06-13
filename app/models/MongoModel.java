package models;

import java.util.ArrayList;
import java.util.List;

import util.MorphiaObject;
import util.SmartReaderUtils;

import com.mongodb.DBCollection;

public class MongoModel {

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

    public static List<? extends MongoModel> all(Class<? extends MongoModel> klass) {
        if (MorphiaObject.datastore != null) {
            return MorphiaObject.datastore.find(klass).asList();
        } else {
            return new ArrayList<MongoModel>();
        }
    }
}
