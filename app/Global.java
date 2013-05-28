import java.net.UnknownHostException;

import play.GlobalSettings;
import play.Logger;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

class MorphiaObject {
    static public Mongo mongo;
    static public Morphia morphia;
    static public Datastore datastore;
}

public class Global extends GlobalSettings {

    @Override
    public void onStart(play.Application arg0) {
        super.beforeStart(arg0);
        Logger.info("** onStart **");
        try {
            MorphiaObject.mongo = new Mongo("127.0.0.1", 27017);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        MorphiaObject.morphia = new Morphia();
        MorphiaObject.datastore = MorphiaObject.morphia.createDatastore(MorphiaObject.mongo, "SmartReader");
        MorphiaObject.datastore.ensureIndexes();
        MorphiaObject.datastore.ensureCaps();

        Logger.info("** Morphia datastore: " + MorphiaObject.datastore.getDB());
    }
}
