package models;

import java.util.ArrayList;
import java.util.List;

import util.MorphiaObject;

public class MongoModel {

    public void create() {
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
