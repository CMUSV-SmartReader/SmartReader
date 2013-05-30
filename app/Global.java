import java.net.UnknownHostException;

import play.GlobalSettings;
import play.Logger;
import util.MorphiaObject;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

public class Global extends GlobalSettings {

    @Override
    public void onStart(play.Application arg0) {
        super.beforeStart(arg0);
        MorphiaObject.setUp();
    }
}
