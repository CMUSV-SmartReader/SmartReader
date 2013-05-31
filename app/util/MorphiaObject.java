package util;

import java.net.UnknownHostException;

import play.Logger;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

public class MorphiaObject {
    static public Mongo mongo;
    static public Morphia morphia;
    static public Datastore datastore;
    
    public static void setUp() {
        Logger.info("** onStart **");
        if (MorphiaObject.mongo == null) {
            try {
                MorphiaObject.mongo = new Mongo("127.0.0.1", 27017);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            MorphiaObject.morphia = new Morphia();
            MorphiaObject.datastore = MorphiaObject.morphia.createDatastore(MorphiaObject.mongo, "SmartReader");
            MorphiaObject.datastore.ensureIndexes();
            MorphiaObject.datastore.ensureCaps();
        }
        Logger.info("** Morphia datastore: " + MorphiaObject.datastore.getDB());
    }
}
