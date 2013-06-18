package util;

import java.net.UnknownHostException;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

public class MorphiaObject {
    static public Mongo mongo;
    static public Morphia morphia;
    static public Datastore datastore;

    public static void setUp() {
        if (MorphiaObject.mongo == null) {
            try {
                MongoClient client = new MongoClient("ec2.lydian.tw", 27017);
                MorphiaObject.mongo = client;
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            MorphiaObject.morphia = new Morphia();
            MorphiaObject.datastore = MorphiaObject.morphia.createDatastore(MorphiaObject.mongo, "thermoreader");
            //MorphiaObject.datastore = MorphiaObject.morphia.createDatastore(MorphiaObject.mongo, "app15993858", "smartReader", "MonGoGo2013".toCharArray());
            MorphiaObject.datastore.ensureIndexes();
            MorphiaObject.datastore.ensureCaps();
        }
    }
}
